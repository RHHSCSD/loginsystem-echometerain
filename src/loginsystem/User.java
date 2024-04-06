/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loginsystem;

/**
 *
 * @author hhwl
 */
public class User {

	private String username;
	private String PWHash;
	private String PWSalt;
	private String secret2FA;
	private String email;
	private String phone;
	private String securityQuestion;
	private String securityAnswer;

	/**
	 * User register constructor
	 *
	 * @param username
	 * @param email
	 * @param phone
	 * @param securityQuestion
	 * @param securityAnswer
	 */
	public User(String username, String email, String phone, String securityQuestion, String securityAnswer) {
		this.username = username;
		this.email = email;
		this.phone = phone;
		this.securityQuestion = securityQuestion;
		this.securityAnswer = securityAnswer;
	}

	/**
	 * User load constructor
	 *
	 * @param username
	 * @param PWHash
	 * @param PWSalt
	 * @param secret2FA
	 * @param email
	 * @param phone
	 * @param securityQuestion
	 * @param securityAnswer
	 */
	public User(String username, String PWHash, String PWSalt, String secret2FA, String email, String phone, String securityQuestion, String securityAnswer) {
		this.username = username;
		this.PWHash = PWHash;
		this.PWSalt = PWSalt;
		this.secret2FA = secret2FA;
		this.email = email;
		this.phone = phone;
		this.securityQuestion = securityQuestion;
		this.securityAnswer = securityAnswer;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the PWHash
	 */
	public String getPWHash() {
		return PWHash;
	}

	/**
	 * @param PWHash the PWHash to set
	 */
	public void setPWHash(String PWHash) {
		this.PWHash = PWHash;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the securityQuestion
	 */
	public String getSecurityQuestion() {
		return securityQuestion;
	}

	/**
	 * @param securityQuestion the securityQuestion to set
	 */
	public void setSecurityQuestion(String securityQuestion) {
		this.securityQuestion = securityQuestion;
	}

	/**
	 * @return the securityAnswer
	 */
	public String getSecurityAnswer() {
		return securityAnswer;
	}

	/**
	 * @param securityAnswer the securityAnswer to set
	 */
	public void setSecurityAnswer(String securityAnswer) {
		this.securityAnswer = securityAnswer;
	}

	/**
	 * @return the PWSalt
	 */
	public String getPWSalt() {
		return PWSalt;
	}

	/**
	 * @param PWSalt the PWSalt to set
	 */
	public void setPWSalt(String PWSalt) {
		this.PWSalt = PWSalt;
	}

	/**
	 * @return the secret2FA
	 */
	public String getSecret2FA() {
		return secret2FA;
	}

	/**
	 * @param secret2FA the secret2FA to set
	 */
	public void setSecret2FA(String secret2FA) {
		this.secret2FA = secret2FA;
	}

}
