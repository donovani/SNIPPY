package io.snippy.util;

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

    /*done
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

    /*done
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

    /*done
     * Method: executeRetQuery
     * Pre: gets a sql query as a string
     * Post: returns the results
     */
    private static ResultSet executeRetQuery(String query) {
        connect();
        ResultSet rs = null;
        try {
            rs = connection.createStatement().executeQuery(query);
        } catch (Exception e) {
            return null;
        }
        if (rs == null) {
            return null;
        } else {
            return rs;
        }
    }
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /*done
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

    /*done
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

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /* done
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

    public static boolean createUser(String pass, String email, String name, String sQ, String sA, String salt) {

        String query = "INSERT INTO `snippy`.`users` (`Password`, `Email`, `FName, `SecQ1`, `SecA1`, `s`) VALUES ('"
                + pass + "', '" + email + "', '" + name + "', " + sQ + ", " + sA + ", '" + salt + "');";

        return executeQuery(query);
    }

    public static boolean createUser(String pass, String email, String name, String sQ1, String sA1, String sQ2,
                                     String sA2, String salt) {

        String query = "INSERT INTO `snippy`.`users` (`Password`, `Email`, `FName, `SecQ1`, `SecA1`, `SecQ2`, `SecA1`, `s`) VALUES ('"
                + pass + "', '" + email + "', '" + name + "', " + sQ1 + ", " + sA1 + ", " + sQ2 + ", " + sA2 + ", '"
                + salt + "');";

        return executeQuery(query);
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /* done
     * Method: createGroup
	 * Pre: takes in a groupname and the user's id
	 * Post: return's the created group number or -1 if there is an issue
	 */
    public static int createGroup(String groupName, int ownerID) {
        String query = "INSERT INTO `groups` (`GName`, `OwnerID`) Values(\"" + groupName + "\"," + ownerID + ")";
        executeQuery(query);

        return -1;
    }

    /*done
     * Method: joinGroup
     * Pre: takes in group id and user id
     * Post: returns true if successful in joining or false if not
     */
    public static boolean joinGroup(int groupID, int userID) {
        String queryG = "SELECT `userID` FROM `groupmembers` WHERE `groupID` LIKE " + groupID + ";";
        ResultSet rs = executeRetQuery(queryG);

        try {
            while (rs.next()) {
                int usr = rs.getInt(1);

                if (usr == userID) { //User is already part of the group
                    return false;
                }
                break;
            }
        } catch (Exception e) {
            return false;
        }
        String query = "INSERT INTO `groupmembers` (`userID`, `groupID`) VALUES(\"" + groupID + "\"," + userID + ")";
        return executeQuery(query);
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /* done
     * Method: getUserSnips
     * Pre: takes in the user's id
     * Post: returns all of the user's snips as a string (` delim) or null if
     * error
     * ID`UserID`Title`Desc`Tags`Lang`Code
     */
    public static ArrayList<String> getUserSnips(String userID) {
        ArrayList<String> snips = new ArrayList<String>();

        String query = "SELECT * FROM `snips` WHERE `userID` like " + userID;
        ResultSet rs = executeRetQuery(query);

        try {
            while (rs.next()) {
                int id = rs.getInt(1);
                int uId = rs.getInt(2);
                String title = rs.getString(3);
                String desc = rs.getString(4);
                String tags = rs.getString(5);
                String lang = rs.getString(6);
                String code = rs.getString(7);

                String tmp = id + "`" + uId + "`" + title + "`" + desc + "`" + tags + "`" + lang + "`" + code;
                snips.add(tmp);
            }

        } catch (Exception e) {
            return null;
        }
        return snips;
    }

    /*done
     * Method: getGroupSnips
     * Pre: takes in a group id
     * Post: returns all UNIQUE snips part of that group as a string (` delim) or null
     * if error
     * ID`UserID`Title`Desc`Tags`Lang`Code
     */
    public static ArrayList<String> getGroupSnips(int userID, int groupID) {
        ArrayList<String> snips = new ArrayList<String>();

        String query = "select s.*\n" +
                "from snips s, snipgroups sg, groups g\n" +
                "where s.ID = sg.snipID and sg.groupID = g.ID and g.ID = " + groupID + ";";
        executeRetQuery(query);

        ResultSet rs = null;
        try {
            while (rs.next()) {
                int id = rs.getInt(1);
                int uId = rs.getInt(2);
                String title = rs.getString(3);
                String desc = rs.getString(4);
                String tags = rs.getString(5);
                String lang = rs.getString(6);
                String code = rs.getString(7);

                String tmp = id + "`" + uId + "`" + title + "`" + desc + "`" + tags + "`" + lang + "`" + code;
                if (uId == userID) {//Do not add if user's snip
                } else {
                    snips.add(tmp);
                }
            }
        } catch (Exception e) {
            return null;
        }
        return snips;
    }

	/*done
     * Method: createSnip
	 * Pre: Takes in at least a UserID, title, and code (overloads give more
	 * options)
	 * Post: returns true if successful, false if not
	 * Note: if tags, tags are seperated with '~' in the DB
	 */

    public static boolean createSnip(int userID, String title, String code) {
        String query = "INSERT INTO `snippy`.`snips` (`UserID`, `Title`, `Code`) VALUES ('" + userID + "','" + title
                + "','" + code + "');";
        return executeQuery(query);
    }

    public static boolean createSnip(int userID, String title, String desc, String code) {
        String query = "INSERT INTO `snippy`.`snips` (`UserID`, `Title`, `Desc`, `Code`) VALUES ('" + userID + "','" + title
                + "','" + desc + "'," + code + "');";
        return executeQuery(query);
    }

    public static boolean createSnip(int userID, String title, String desc, ArrayList<String> tags, String code) {
        String tgs = "";
        for (int i = 0; i < tags.size(); i++) {
            tgs = tags.get(i) + "~";
        }
        tgs = tgs.substring(0, tgs.length() - 1);

        String query = "INSERT INTO `snippy`.`snips` (`UserID`, `Title`, `Desc`, `Tags`, `Code`) VALUES ('" + userID + "','" + title
                + "','" + desc + "'," + tgs + "'," + code + "');";
        return executeQuery(query);
    }

    public static boolean createSnip(int userID, String title, String desc, ArrayList<String> tags, String lang,
                                     String code) {
        String tgs = "";
        for (int i = 0; i < tags.size(); i++) {
            tgs = tags.get(i) + "~";
        }
        tgs = tgs.substring(0, tgs.length() - 1);

        String query = "INSERT INTO `snippy`.`snips` (`UserID`, `Title`, `Desc`, `Tags`, `Lang`, `Code`) VALUES ('" + userID + "','" + title
                + "','" + desc + "'," + tgs + "'," + lang + "'," + code + "');";

        return executeQuery(query);
    }

    /*done
     * Method: removeSnip
     * Pre: takes in a snipID
     * Post: returns true if the snip was successfully removed or false on error
     */
    public static boolean removeSnip(int snipID) {
        String query = "DELETE FROM `snips` WHERE `snipID` LIKE " + snipID;

        boolean a = executeQuery(query);
        if (!a)
            return false;

        query = "DELETE FROM `snipgroups` WHERE `snipID` LIKE " + snipID;

        return executeQuery(query);
    }

    /*done
     * Method: shareSnip
     * Pre: takes in snip and group to share it to
     * Post: returns true if successfully shared or false if error
     */
    public static boolean shareSnip(int snipId, int groupID) {
        String query = "INSERT INTO `snipgroups` (`snipID`, `groupID`) VALUES ('" + snipId + "','" + groupID + "')";
        return executeQuery(query);
    }

    /*done
     * Method: unshareSnip
     * Pre: takes in a snip and a group its a part of and unshare it.
     * Post: returns true if successfully unshared or false if error
     */
    public static boolean unshareSnip(int snipId, int groupID) {
        try {
            boolean partOfGroup = false;
            String query = "Select * FROM `snipgroups` WHERE `groupID` LIKE " + groupID;
            ResultSet rs = executeRetQuery(query);

            while (rs.next()) {
                int v = rs.getInt(1);
                if (v == snipId) {
                    partOfGroup = true;
                    break;
                }
            }

            if (!partOfGroup) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        String query = "Delete FROM `snipgroups` WHERE `snipID` LIKE " + snipId;
        return executeQuery(query);
    }
    // ===========================ENCRYPTION=============================

    /*done
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

    /*done
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

    /*done
     * Method: println
     * Pre: a string to post
     * Post: outputs the string if debug is enabled
     */
    private static void println(String line) {
        if (debug)
            System.out.println(line);
    }

    /*done
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


    public static void main(String[] args) { //FOR TESTING ONLY!
        if (debug) {

        }
    }
    // ==================================================================
}
