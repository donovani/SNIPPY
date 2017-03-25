package snippy;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class SQLUtils {

	private static Connection connection = null;

	public SQLUtils() {
		connect();
	}

	/*
	 * Method: connect
	 * Pre: none
	 * Post: connects to the database if there is not an active connection
	 */
	private static void connect() {

		if (connection == null) {
			String url = "jdbc:mysql://localhost:3306/snippy";
			String username = "root";
			String password = "password";
			try {
				connection = DriverManager.getConnection(url, username, password);
				println("Connected");
			} catch (Exception e) {
				printErr(e);
			}
		}
	}

	/*
	 * Method: executeQuery
	 * Pre: gets a sql query as a string
	 * Post: returns true if successful, false if not
	 */
	private static boolean executeQuery(String query) {
		try {
			connect();
			PreparedStatement preparedStmt = connection.prepareStatement(query);
			preparedStmt.execute();
			;
			return true;
		} catch (Exception e) {
			printErr(e);
			return false;
		}
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	/*
	 * Method: userExists
	 * Pre: Takes in an email as a string
	 * Post: Returns true if user's email exists, or false if not
	 */
	public static boolean userExists(String email) throws Exception {
		connect();

		String query = "SELECT ID FROM users WHERE Email LIKE '" + email + "';";
		ResultSet rs = connection.createStatement().executeQuery(query);

		String usr = null;
		while (rs.next()) {
			usr = rs.getString(1);
			break;
		}
		rs.close();
		;
		if (usr.equals("") || usr.equals(null)) {
			return false;
		} else {
			return true;
		}
	}

	/*
	 * Method: userExists
	 * Pre: Takes in an ID as int
	 * Post: Returns true if user's ID exists, or false if not
	 */
	public static boolean userExists(int ID) throws Exception {
		connect();

		String query = "SELECT ID FROM users WHERE ID LIKE '" + ID + "';";
		ResultSet rs = connection.createStatement().executeQuery(query);

		String usr = null;
		while (rs.next()) {
			usr = rs.getString(1);
			break;
		}
		rs.close();
		;
		if (usr.equals("") || usr.equals(null)) {
			return false;
		} else {
			return true;
		}
	}

	/*
	 * Method: removeUser
	 * Pre: Takes in an email as a string
	 * Post: Returns true if the user was deleted, or false if not
	 */
	public static boolean removeUser(String email) throws Exception {
		connect();
		String query = "SELECT ID FROM users WHERE Email LIKE '" + email + "';";
		ResultSet rs = connection.createStatement().executeQuery(query);

		int ID = -1;
		while (rs.next()) {
			ID = rs.getInt(1);
			break;
		}
		if (ID < 0) {
			return false;
		}
		rs.close();
		;

		return removeUser(ID);
	}

	/*
	 * Method: removeUser
	 * Pre: Takes in an ID as int
	 * Post: Returns true if was deleted, or false if not
	 */
	public static boolean removeUser(int ID) {
		// delete all user snips where snips dont have a groupID assigned
		// remove user from all groups
		// if user is only one in group - remove group and all snips with group
		// id matching
		// else make someone else group owner
		// delete user

		// DELETE FROM table_name
		// WHERE some_column=some_value;
		return false;
	}
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	/*
	 * Method: createUser
	 * Pre: Takes in at minimum a password, email, and salt.
	 * Overloads add in security questions/answers and name
	 * Post: returns true if successful, or false if issue
	 */
	public static boolean createUser(String pass, String email, String salt) {

		String query = "INSERT INTO `snippy`.`users` (`Password`, `Email`, `s`) VALUES ('" + pass + "','" + email
				+ "','" + salt + "');";

		return executeQuery(query);
	}

	public static boolean createUser(String pass, String email, String name, String salt) {

		String query = "INSERT INTO `snippy`.`users` (`Password`, `Email`, `FName`, `s`) VALUES ('" + pass + "', '"
				+ email + "', '" + name + "', '" + salt + "');";

		return executeQuery(query);
	}

	public static boolean createUser(String pass, String email, String name, String sQ, String sA, String salt)
			throws Exception {

		String query = "INSERT INTO `snippy`.`users` (`Password`, `Email`, `FName, `SecQ1`, `SecA1`, `s`) VALUES ('"
				+ pass + "', '" + email + "', '" + name + "', " + sQ + ", " + sA + ", '" + salt + "');";

		return executeQuery(query);
	}

	public static boolean createUser(String pass, String email, String name, String sQ1, String sA1, String sQ2,
			String sA2, String salt) throws Exception {

		String query = "INSERT INTO `snippy`.`users` (`Password`, `Email`, `FName, `SecQ1`, `SecA1`, `SecQ2`, `SecA1`, `s`) VALUES ('"
				+ pass + "', '" + email + "', '" + name + "', " + sQ1 + ", " + sA1 + ", " + sQ2 + ", " + sA2 + ", '"
				+ salt + "');";

		return executeQuery(query);
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	/*
	 * Method: createGroup
	 * Pre: takes in a groupname and the user's id
	 * Post: return's the created group number or -1 if there is an issue
	 */
	public static int createGroup(String groupName, int ownerID) {
		String query = "";
		executeQuery(query);

		return -1;
	}

	/*
	 * Method: joinGroup
	 * Pre: takes in group id and user id
	 * Post: returns true if successful in joining or false if not
	 */
	public static boolean joinGroup(int groupID, int userID) {
		String query = "";
		return executeQuery(query);
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	/*
	 * Method: getUserSnips
	 * Pre: takes in the user's id
	 * Post: returns all of the user's snips as a string (` delim) or null if
	 * error
	 * ID`UserID`Title`Desc`Tags`Lang`Code
	 */
	public static ArrayList<String> getUserSnips(String userID) {
		return null;
	}

	/*
	 * Method: getGroupSnips
	 * Pre: takes in a group id
	 * Post: returns all snips part of that group as a string (` delim) or null
	 * if error
	 * ID`UserID`Title`Desc`Tags`Lang`Code
	 */
	public static ArrayList<String> getGroupSnips(String groupID) {

		return null;
	}

	/*
	 * Method: createSnip
	 * Pre: Takes in at least a UserID, title, and code (overloads give more
	 * options)
	 * Post: returns true if successful, false if not
	 */
	public static boolean createSnip(int userID, String title, String code) {
		String query = "";
		return executeQuery(query);
	}

	public static boolean createSnip(int userID, String title, String desc, String code) {
		String query = "";
		return executeQuery(query);
	}

	public static boolean createSnip(int userID, String title, String desc, ArrayList<String> tags, String code) {
		String query = "";
		return executeQuery(query);
	}

	public static boolean createSnip(int userID, String title, String desc, ArrayList<String> tags, String lang,
			String code) {
		String query = "";
		return executeQuery(query);
	}

	/*
	 * Method: removeSnip
	 * Pre: takes in a snipID
	 * Post: returns true if the snip was successfully removed or false on error
	 */
	public static boolean removeSnip(int SnipID) {
		String query = "";
		return executeQuery(query);
	}

	/*
	 * Method: shareSnip
	 * Pre: takes in snip and group to share it to
	 * Post: returns true if successfully shared or false if error
	 */
	public static boolean shareSnip(int snipId, int groupID) {
		String query = "";
		return executeQuery(query);
	}
	// ===========================ENCRYPTION=============================

	/*
	 * Method: hash
	 * Pre: Take in a password as a string, and salt as a string
	 * Post: Return a SHA256 hashed password as a string, or null if error
	 */
	public static String hash(String password, String salt) {
		try {
			MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
			password = password + salt;
			byte[] passBytes = password.getBytes();
			byte[] passHash = sha256.digest(passBytes);
			return new String(passHash);
		} catch (Exception e) {
			printErr(e);
			return null;
		}

	}

	/*
	 * Method: generateSalt
	 * Pre: none
	 * Post: returns a 20 byte string of salt
	 */
	public static String generateSalt() {
		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[20];
		random.nextBytes(bytes);
		return new String(bytes);
	}

	// ==========================DEBUG STUFF=============================

	private static boolean debug = true;

	/*
	 * Method: println
	 * Pre: a string to post
	 * Post: outputs the string if debug is enabled
	 */
	private static void println(String line) {
		if (debug)
			System.out.println(line);
	}

	/*
	 * Method: printErr
	 * Pre: takes in an exception
	 * Post: outputs the exception and stack trace if debug is enabled
	 */
	private static void printErr(Exception e) {
		if (debug) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	// ==================================================================
}
