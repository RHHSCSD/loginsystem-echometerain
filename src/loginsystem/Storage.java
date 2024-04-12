/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loginsystem;

import java.io.*;
import java.util.*;

/**
 *
 * @author hhwl
 */
public final class Storage {

	/**
	 * Data file delimiter
	 */
	public static final String DELIM = ";";

	/**
	 * Number of data points stored in user data file
	 */
	public static final int NUM_DATA = 8;
	private String dataFile = "./data";
	private String badPassFile = "./dictbadpass.txt";
	private HashSet<String> badPass = new HashSet<>();
	private PrintWriter write = null;
	private Scanner s;
	private TreeMap<String, User> users = new TreeMap<>();

	/**
	 * Load data from disk
	 */
	public Storage() {
		loadUsers();
		try {
			loadBadPass();
		} catch (FileNotFoundException ex) {
			// quit if cannot find bad passwords file
			System.err.println("Bad passwords file not found, exiting!!!");
			System.exit(1);
		}
	}

	/**
	 * Store users into data file
	 */
	public void storeUsers() {
		try {
			// point printWriter to data file
			setWrite(new PrintWriter(getDataFile()));
			for (var e : getUsers().values()) {
				// each line must have this schema
				getWrite().print(e.getUsername() + DELIM);
				getWrite().print(e.getPWHash() + DELIM);
				getWrite().print(e.getPWSalt() + DELIM);
				getWrite().print(e.getSecret2FA() + DELIM);
				getWrite().print(e.getEmail() + DELIM);
				getWrite().print(e.getPhone() + DELIM);
				getWrite().print(e.getSecurityQuestion() + DELIM);
				getWrite().print(e.getSecurityAnswer() + "\n");
			}
			getWrite().close();
		} catch (FileNotFoundException ex) {
			System.err.println("Cannot access user file path, exiting!!!");
			System.exit(1);
		}
	}

	/**
	 * Load all the users from file
	 */
	public void loadUsers() {
		try {
			// point scanner to data file
			setS(new Scanner(new File(getDataFile())));
			while (getS().hasNextLine()) {
				String[] data = getS().nextLine().split(DELIM);
				// quit if file doesn't follow schema
				if (data.length != NUM_DATA || data[0].equals("")) {
					return;
				}
				getUsers().put(data[0], new User(data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7]));
			}
			getS().close();
		} catch (FileNotFoundException ex) {
			System.out.println("No user data found");
		}
	}

	/**
	 * Load bad passwords file
	 *
	 * @throws FileNotFoundException
	 */
	public void loadBadPass() throws FileNotFoundException {
		setS(new Scanner(new File(getBadPassFile())));
		// scan all lines for passwords and put in set
		while (getS().hasNextLine()) {
			getBadPass().add(getS().nextLine());
		}
		getS().close();
	}

	/**
	 * @return the outFile
	 */
	public String getDataFile() {
		return dataFile;
	}

	/**
	 * @param dataFile the outFile to set
	 */
	public void setDataFile(String dataFile) {
		this.dataFile = dataFile;
	}

	/**
	 * @return the write
	 */
	private PrintWriter getWrite() {
		return write;
	}

	/**
	 * @param write the write to set
	 */
	private void setWrite(PrintWriter write) {
		this.write = write;
	}

	/**
	 * @return the s
	 */
	private Scanner getS() {
		return s;
	}

	/**
	 * @param s the s to set
	 */
	private void setS(Scanner s) {
		this.s = s;
	}

	/**
	 * @return the users
	 */
	public TreeMap<String, User> getUsers() {
		return users;
	}

	/**
	 * @param users the users to set
	 */
	public void setUsers(TreeMap<String, User> users) {
		this.users = users;
	}

	/**
	 * @return the badPassFile
	 */
	public String getBadPassFile() {
		return badPassFile;
	}

	/**
	 * @param badPassFile the badPassFile to set
	 */
	public void setBadPassFile(String badPassFile) {
		this.badPassFile = badPassFile;
	}

	/**
	 * @return the badPass
	 */
	public HashSet<String> getBadPass() {
		return badPass;
	}

	/**
	 * @param badPass the badPass to set
	 */
	public void setBadPass(HashSet<String> badPass) {
		this.badPass = badPass;
	}
}
