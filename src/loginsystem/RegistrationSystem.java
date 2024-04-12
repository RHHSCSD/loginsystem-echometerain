/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loginsystem;

import java.security.*;
import java.util.*;
import com.j256.twofactorauth.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

/**
 *
 * @author hhwl
 */
public class RegistrationSystem {

	/**
	 * Length of password salt in bytes
	 */
	private static final int SALT_LENGTH = 32;

	/**
	 * Enforce minimum password length
	 */
	private static final int MIN_PW_LENGTH = 5;

	/**
	 * 2FA code acceptance time window
	 */
	private static final int TWOFA_WINDOW = 30000;

	// access storage
	private final Storage store = new Storage();

	// needs parent frame to open dialogs
	private JFrame frame;

	/**
	 *
	 * @param frame
	 */
	public RegistrationSystem(JFrame frame) {
		this.frame = frame;
	}

	/**
	 * Registers a user
	 *
	 * @param username
	 * @param password
	 * @param email
	 * @param phone
	 * @param securityQuestion
	 * @param securityAnswer
	 * @return true if successful
	 */
	public boolean register(String username, String password, String email, String phone, String securityQuestion, String securityAnswer) {
		// check if fields are valid
		if (!validateUsername(username) || !validatePW(password) || !validatePhone(phone)) {
			return false;
		}
		if (email.contains(Storage.DELIM) || securityQuestion.contains(Storage.DELIM) || securityAnswer.contains(Storage.DELIM)) {
			showError("Fields cannot contain \"" + Storage.DELIM + "\"!!!");
			return false;
		}
		// create user
		User user = new User(username, email, phone, securityQuestion, securityAnswer);
		hashAndSalt(user, password);
		generate2FA(user);
		// save to disk
		store.getUsers().put(username, user);
		store.storeUsers();
		showInfo("Account created!");
		return true;
	}

	/**
	 * Logs a user in
	 *
	 * @param username
	 * @param password
	 * @param twoFACode
	 * @return true if successful
	 */
	public boolean login(String username, String password, String twoFACode) {
		// find user
		TreeMap<String, User> users = store.getUsers();
		if (!users.containsKey(username)) {
			showError("User not found!!!");
			return false;
		}
		// check password
		User user = users.get(username);
		if (!checkPassword(user, password)) {
			return false;
		}
		// check 2fa token
		try {
			if (!check2FA(user.getSecret2FA(), Integer.parseInt(twoFACode))) {
				return false;
			}
		} catch (NumberFormatException ex) { // if 2fa code not integer
			showError("2FA code must be a number!!!");
			return false;
		}
		showInfo("Logged in!");
		return true;
	}

	/**
	 * Check if username is valid at account creation
	 *
	 * @param username
	 * @return true if valid
	 */
	public boolean validateUsername(String username) {
		if (username.contains(Storage.DELIM)) {
			showError("Username cannot contain \"" + Storage.DELIM + "\"!!!");
			return false;
		}
		if (store.getUsers().containsKey(username)) {
			showError("Username already exists!!!");
			return false;
		}
		return true;
	}

	/**
	 * Check if password is valid at account creation
	 *
	 * @param password
	 * @return true if valid
	 */
	public boolean validatePW(String password) {
		// basic checks
		if (password.contains(Storage.DELIM)) {
			showError("Password cannot contain \"" + Storage.DELIM + "\"!!!");
			return false;
		}
		if (password.length() < MIN_PW_LENGTH) {
			showError("Password too short!!! Must be longer than " + MIN_PW_LENGTH + " characters!!!");
			return false;
		}
		if (store.getBadPass().contains(password)) {
			showError("Common password detected!!! Choose a stronger password!!!");
			return false;
		}

		// loop through string to check for necessary character types
		boolean hasLowerCase = false;
		boolean hasUpperCase = false;
		boolean hasNumber = false;
		boolean hasSpecial = false;
		for (char e : password.toCharArray()) {
			if (!hasLowerCase && Character.isLowerCase(e)) {
				hasLowerCase = true;
			} else if (!hasUpperCase && Character.isUpperCase(e)) {
				hasUpperCase = true;
			} else if (!hasNumber && Character.isDigit(e)) {
				hasNumber = true;
			} else if (!hasSpecial) {
				hasSpecial = true;
			}
		}
		if (!hasLowerCase) {
			showError("Must contain a lowercase character!!!");
			return false;
		} else if (!hasUpperCase) {
			showError("Must contain a uppercase character!!!");
			return false;
		} else if (!hasNumber) {
			showError("Must contain a number!!!");
			return false;
		} else if (!hasSpecial) {
			showError("Must contain a special character!!!");
			return false;
		}
		return true;
	}

	/**
	 * Check if phone number is a number
	 *
	 * @param phone
	 * @return true if is a number
	 */
	public boolean validatePhone(String phone) {
		try {
			Long.parseLong(phone);
		} catch (NumberFormatException ex) { // if phone number is not a number
			showError("Phone number must be a number!!!");
			return false;
		}
		return true;
	}

	/**
	 * Check if correct 2FA code
	 *
	 * @param secret
	 * @param code
	 * @return true if successful
	 */
	public boolean check2FA(String secret, int code) {
		try {
			if (!TimeBasedOneTimePasswordUtil.validateCurrentNumber(secret, code, TWOFA_WINDOW)) {
				showError("Incorrect 2FA verification code!!!");
				return false;
			}
		} catch (GeneralSecurityException ex) {
			showError("2FA library error!!!");
		}
		return true;
	}

	/**
	 * Generate 2FA code seed
	 *
	 * The library uses SHA-1 to generate codes (your authenticator app will ask
	 * for this)
	 *
	 * @param user
	 */
	public void generate2FA(User user) {
		// generate secret
		String secret = TimeBasedOneTimePasswordUtil.generateBase32Secret();
		// qr code dialog
		JDialog dialog = new JDialog();
		JTextField textLabel = new JTextField("Your secret is: " + secret);
		textLabel.setEditable(false);
		dialog.add(textLabel, BorderLayout.NORTH);

		// get qr code address
		String otpURL = TimeBasedOneTimePasswordUtil.generateOtpAuthUrl(user.getUsername(), secret);
		String webAddress = "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=" + otpURL;
		try {
			URL url = URI.create(webAddress).toURL();
			JLabel imgLabel = new JLabel(new ImageIcon(ImageIO.read(url)));
			dialog.add(imgLabel, BorderLayout.SOUTH);
		} catch (IOException ex) {
			// add url link in case image doesn't load
			JTextField linkLabel = new JTextField("\nQR code URL: " + webAddress);
			textLabel.setEditable(false);
			dialog.add(linkLabel, BorderLayout.CENTER);
			dialog.repaint();
		}
		dialog.pack();
		dialog.setAlwaysOnTop(true);
		dialog.setVisible(true);
		// store secret
		user.setSecret2FA(secret);
	}

	/**
	 * Get hash from password and salt
	 *
	 * @param salt
	 * @param password
	 * @return true if successful
	 */
	private String getHash(byte[] salt, String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			// this salts the algorithm appearently
			md.update(salt);
			// generate hash and return its string
			byte[] hashBytes = md.digest(password.getBytes());
			return encodeB64(hashBytes);
		} catch (NoSuchAlgorithmException ex) {
			showError("This computer can't run SHA-512, exiting!!!");
			ex.getStackTrace();
			System.exit(1);
			return "";
		}
	}

	/**
	 * Generates the hash and salt, stores it in a user object
	 *
	 * @param user
	 * @param password
	 */
	public void hashAndSalt(User user, String password) {
		// generate salt
		SecureRandom sr = new SecureRandom();
		byte[] saltBytes = new byte[SALT_LENGTH];
		sr.nextBytes(saltBytes);

		// generate hash and salt strings
		String hash = getHash(saltBytes, password);
		String salt = encodeB64(saltBytes);

		// store them
		user.setPWHash(hash);
		user.setPWSalt(salt);
	}

	/**
	 * Checks if password correct
	 *
	 * @param user
	 * @param input
	 * @return true if password correct
	 */
	public boolean checkPassword(User user, String input) {
		String hash = user.getPWHash();
		String salt = user.getPWSalt();
		// check input against hash and salt
		String inputHash = getHash(decodeB64(salt), input);
		if (!hash.equals(inputHash)) {
			showError("Wrong password!!!");
			return false;
		}
		return true;
	}

	/**
	 * Perform a password reset
	 *
	 * @param username
	 * @param newPass
	 * @param phone
	 * @param email
	 * @param securityAnswer
	 * @return true if successful
	 */
	public boolean resetPassword(String username, String newPass, String phone, String email, String securityAnswer) {
		if (!store.getUsers().containsKey(username)) {
			showError("User not found!!!");
			return false;
		}
		User user = store.getUsers().get(username);
		// check if info is correct
		if (validatePhone(phone) && validatePW(newPass) && user.getPhone().equals(phone)
				&& user.getEmail().equals(email) && user.getSecurityAnswer().equals(securityAnswer)) {
			// generate new hash, salt, and 2fa token
			hashAndSalt(user, newPass);
			generate2FA(user);
			store.storeUsers();
			showInfo("Password reset successful!");
			return true;
		}
		showError("Password reset failed!!!");
		return false;

	}

	/**
	 * Retrieves security question for the label in the reset tab
	 *
	 * @param username
	 * @param email
	 * @param phone
	 * @return
	 */
	public String retrieveSecurityQuestion(String username, String email, String phone) {
		if (!store.getUsers().containsKey(username)) {
			return "";
		}
		// only give security question if phone and email is right
		User user = store.getUsers().get(username);
		if (user.getPhone().equals(phone) && user.getEmail().equals(email)) {
			return user.getSecurityQuestion();
		}
		return "";
	}

	/**
	 * Dialog to show error
	 *
	 * @param message
	 */
	public void showError(String message) {
		JOptionPane.showMessageDialog(frame, message,
				"Swing Tester", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Dialog to show info message
	 *
	 * @param message
	 */
	public void showInfo(String message) {
		JOptionPane.showMessageDialog(frame, message,
				"Swing Tester", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * @return the currentFrame
	 */
	public JFrame getFrame() {
		return frame;
	}

	/**
	 * @param frame the currentFrame to set
	 */
	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	/**
	 * Encode base 64
	 *
	 * @param input
	 * @return encoded string
	 */
	public static String encodeB64(byte[] input) {
		return Base64.getEncoder().encodeToString(input);
	}

	/**
	 * Decode base 64
	 *
	 * @param input
	 * @return decoded byte array
	 */
	public static byte[] decodeB64(String input) {
		return Base64.getDecoder().decode(input);
	}
}
