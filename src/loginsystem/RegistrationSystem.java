/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loginsystem;

import java.security.*;
import java.util.*;
import java.nio.charset.*;
import com.j256.twofactorauth.TimeBasedOneTimePasswordUtil;
import java.net.URL;
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
	private static final int SALT_LENGTH = 128;

	/**
	 * Use UTF-8 to encode hash and salt
	 */
	private static final Charset CHARSET = StandardCharsets.UTF_8;

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
	private JFrame currentFrame = null;

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
		if (!validateUsername(username) || !validatePW(password)) {
			return false;
		}
		if (securityQuestion.contains(Storage.DELIM) || securityAnswer.contains(Storage.DELIM)) {
			System.out.println("Fields cannot contain \"" + Storage.DELIM + "\"!!!");
			return false;
		}
		// create user
		User user = new User(username, email, phone, securityQuestion, securityAnswer);
		hashAndSalt(user, password);
		// save to disk
		store.storeUsers();
		return true;
	}

	/**
	 * Logs a user in
	 *
	 * @param username
	 * @param password
	 * @return true if successful
	 */
	public boolean login(String username, String password) {
		// find user
		HashMap<String, User> users = store.getUsers();
		if (!users.containsKey(username)) {
			System.out.println("User not found!!!");
			return false;
		}
		// check password
		User user = users.get(username);
		if (!checkPassword(user, password)) {
			System.out.println("Incorrect password!!!");
			return false;
		}
		return true;
	}

	/**
	 * Check if username is valid at account creation
	 *
	 * @param username
	 * @return true if successful
	 */
	public boolean validateUsername(String username) {
		if (username.contains(Storage.DELIM)) {
			System.out.println("Username cannot contain \"" + Storage.DELIM + "\"!!!");
			return false;
		}
		if (!store.getUsers().containsKey(username)) {
			System.out.println("Username already exists!!!");
			return false;
		}
		return true;
	}

	/**
	 * Check if password is valid at account creation
	 *
	 * @param password
	 * @return true if successful
	 */
	public boolean validatePW(String password) {
		// basic checks
		if (password.contains(Storage.DELIM)) {
			System.out.println("Password cannot contain \"" + Storage.DELIM + "\"!!!");
			return false;
		}
		if (password.length() < MIN_PW_LENGTH) {
			System.out.println("Password too short!!! Must be longer than " + MIN_PW_LENGTH + " characters!!!");
			return false;
		}
		if (store.getBadPass().contains(password)) {
			System.out.println("Common password detected!!! Choose a stronger password!!!");
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
			System.out.println("Must contain a lowercase character!!!");
			return false;
		} else if (!hasUpperCase) {
			System.out.println("Must contain a uppercase character!!!");
			return false;
		} else if (!hasNumber) {
			System.out.println("Must contain a number!!!");
			return false;
		} else if (!hasSpecial) {
			System.out.println("Must contain a special character!!!");
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
	 * @throws java.security.GeneralSecurityException
	 */
	public boolean check2FA(String secret, int code) throws GeneralSecurityException {
		if (TimeBasedOneTimePasswordUtil.validateCurrentNumber(secret, code, TWOFA_WINDOW)) {
			System.out.println("Incorrect 2FA validation code!!!");
			return false;
		}
		return true;
	}

	/**
	 * Generate 2FA code seed
	 *
	 * @param user
	 */
	public void generate2FA(User user) {
		// generate secret
		String secret = TimeBasedOneTimePasswordUtil.generateBase32Secret();
		System.out.println("Your secret is: " + secret);
		System.out.println("(You do not need to store this if you are able to scan the QR code)");
		// get qr code url
		String url = TimeBasedOneTimePasswordUtil.qrImageUrl("LoginSystem", secret);
		try {
			// qr code image dialog
			JDialog dialog = new JDialog();
			dialog.setUndecorated(true);
			JLabel label = new JLabel(new ImageIcon(ImageIO.read(new URL(url))));
			dialog.add(label);
			dialog.pack();
			dialog.setVisible(true);
		} catch (IOException ex) {
			System.out.println("Image loading failed!!!");
		}
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
			byte[] hashBytes = md.digest(password.getBytes(CHARSET));
			return new String(hashBytes, CHARSET);
		} catch (NoSuchAlgorithmException ex) {
			System.out.println("This computer can't run SHA-512!!!");
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
		String salt = new String(saltBytes, CHARSET);
		// hash contains delimiter edge case
		if (hash.contains(Storage.DELIM) || salt.contains(Storage.DELIM)) {
			hashAndSalt(user, password);
			return;
		}

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
		String inputHash = getHash(salt.getBytes(CHARSET), input);
		if (!hash.equals(inputHash)) {
			System.out.println("Wrong password!!!");
			return false;
		}
		return true;
	}

	/**
	 *
	 * @param user
	 * @param newPass
	 * @param phone
	 * @param email
	 * @param securityAnswer
	 * @return true if successful
	 */
	public boolean resetPassword(User user, String newPass, String phone, String email, String securityAnswer) {
		if (validatePW(newPass) && user.getPhone().equals(phone) && user.getEmail().equals(email)
				&& user.getSecurityAnswer().equals(securityAnswer)) {
			hashAndSalt(user, newPass);
			generate2FA(user);
			return true;
		}
		return false;

	}

	/**
	 * @return the currentFrame
	 */
	public JFrame getCurrentFrame() {
		return currentFrame;
	}

	/**
	 * @param currentFrame the currentFrame to set
	 */
	public void setCurrentFrame(JFrame currentFrame) {
		this.currentFrame = currentFrame;
	}
}
