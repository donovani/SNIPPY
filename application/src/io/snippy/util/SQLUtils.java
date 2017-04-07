package io.snippy.util;

import io.snippy.core.Snip;
import sun.dc.pr.PRError;

import java.awt.*;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


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

            String url = "jdbc:mysql://35.162.241.216:3306/snippy";
            String username = "jimmr";
            String password = "Software171!";

            /*
            String url = "jdbc:mysql://localhost/snippy";
            String username = "root";
            String password = "password";*/
            try {
                connection = DriverManager.getConnection(url, username, password);
                println("Connected");
            } catch (Exception e) {
                printErr(e);
            }
        }
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /*done
     * Method: userExists
     * Pre: Takes in an email as a string
     * Post: Returns true if user's email exists, or false if not
     */
    public static boolean userExists(String email) {
        connect();

        try {
            String query = "SELECT ID FROM users WHERE Email LIKE ?;";
            PreparedStatement stmnt = connection.prepareStatement(query);
            stmnt.setString(1, email);
            ResultSet rs = stmnt.executeQuery();

            int usr = -1;
            while (rs.next()) {
                usr = rs.getInt(1);
                break;
            }
            rs.close();
            if (usr == -1) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            printErr(e);
            return false;
        }
    }

    /*done
     * Method: userExists
     * Pre: Takes in an ID as int
     * Post: Returns true if user's ID exists, or false if not
     */
    public static boolean userExists(int ID) {
        connect();

        try {
            String query = "SELECT ID FROM users WHERE ID LIKE ?;";
            PreparedStatement stmnt = connection.prepareStatement(query);
            stmnt.setInt(1, ID);
            ResultSet rs = stmnt.executeQuery();

            String usr = null;
            while (rs.next()) {
                usr = rs.getString(1);
                break;
            }
            rs.close();

            if (usr.equals("") || usr.equals(null)) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            printErr(e);
            return false;
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
        connect();
        try {
            String query = "INSERT INTO `snippy`.`users` (`Password`, `Email`, `s`) VALUES (?,?,?);";
            PreparedStatement stmnt = connection.prepareStatement(query);
            stmnt.setString(1, pass);
            stmnt.setString(2, email);
            stmnt.setString(3, salt);
            stmnt.execute();
            return true;
        } catch (Exception e) {
            printErr(e);
            return false;
        }
    }

    public static boolean createUser(String pass, String email, String name, String salt) {
        connect();
        try {
            String query = "INSERT INTO `snippy`.`users` (`Password`, `Email`, `FName`, `s`) VALUES (?,?,?,?);";
            PreparedStatement stmnt = connection.prepareStatement(query);
            stmnt.setString(1, pass);
            stmnt.setString(2, email);
            stmnt.setString(3, name);
            stmnt.setString(4, salt);
            stmnt.execute();
            return true;
        } catch (Exception e) {
            printErr(e);
            return false;
        }
    }

    public static boolean createUser(String pass, String email, String name, String sQ, String sA, String salt) {
        connect();
        try {
            String query = "INSERT INTO `snippy`.`users` (`Password`, `Email`, `FName`, `SecQ1`, `SecA1`, `s`) VALUES (?,?,?,?,?,?);";
            PreparedStatement stmnt = connection.prepareStatement(query);
            stmnt.setString(1, pass);
            stmnt.setString(2, email);
            stmnt.setString(3, name);
            stmnt.setString(4, sQ);
            stmnt.setString(5, sA);
            stmnt.setString(6, salt);
            stmnt.execute();
            return true;
        } catch (Exception e) {
            printErr(e);
            return false;
        }
    }

    public static boolean createUser(String pass, String email, String name, String sQ1, String sA1, String sQ2, String sA2, String salt) {
        connect();
        try {
            String query = "INSERT INTO `snippy`.`users` (`Password`, `Email`, `FName`, `SecQ1`, `SecA1`,`SecQ2`, `SecA2`, `s`) VALUES (?,?,?,?,?,?,?,?);";
            PreparedStatement stmnt = connection.prepareStatement(query);
            stmnt.setString(1, pass);
            stmnt.setString(2, email);
            stmnt.setString(3, name);
            stmnt.setString(4, sQ1);
            stmnt.setString(5, sA1);
            stmnt.setString(6, sQ2);
            stmnt.setString(7, sA2);
            stmnt.setString(8, salt);
            stmnt.execute();
            return true;
        } catch (Exception e) {
            printErr(e);
            return false;
        }
    }

    /* done
     * Method: getUserID
     * Pre: takes in a username
     * Post: returns user id if ok, or -1 if error
     */
    public static int getUserID(String username) {
        connect();

        try {
            String query = "SELECT `ID` FROM `users` WHERE `Email` LIKE ?";
            PreparedStatement stmnt = connection.prepareStatement(query);
            stmnt.setString(1, username);

            ResultSet rs = stmnt.executeQuery();
            rs.next();
            return rs.getInt(1);

        } catch (Exception e) {
            printErr(e);
            return -1;
        }
    }

    /* done
     * Method: getUser
     * Pre: takes in a user's ID
     * Post: returns user as a string (delim `) or null if error
     */
    public static String getUser(int ID) {
        connect();

        String usr = "";
        String query = "SELECT * FROM `users` WHERE `ID` LIKE ?";
        try {
            PreparedStatement stmnt = connection.prepareStatement(query);
            stmnt.setInt(1, ID);

            ResultSet rs = stmnt.executeQuery();
            while (rs.next()) {
                usr = usr + rs.getInt(1) + "`";
                usr = usr + rs.getString(3) + "`";
                usr = usr + rs.getString(4) + "`";
                usr = usr + rs.getString(5) + "`";
                usr = usr + rs.getString(6) + "`";
                usr = usr + rs.getString(7) + "`";
                usr = usr + rs.getString(8) + "`";
                usr = usr + rs.getString(9);
            }
            return usr;
        } catch (Exception e) {
            printErr(e);
            return null;
        }
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /* done - fixed
     * Method: login
	 * Pre: takes in a username and password
	 * Post: returns user id if ok, or -1 if error
	 */
    public static int login(String username, String password) {
        connect();
        try {
            String query = "SELECT `Password`, `s` FROM `users` WHERE `Email` = ?";
            PreparedStatement stmnt = connection.prepareStatement(query);
            stmnt.setString(1, username);

            ResultSet rs = stmnt.executeQuery();

            String dbPass = "";
            String dbSalt = "";
            while (rs.next()) {
                dbPass = rs.getString(1);
                dbSalt = rs.getString(2);
            }

            String hash = new String(hash(password, dbSalt));

            query = "INSERT INTO `safe` (`str`) VALUES (?)";
            stmnt = connection.prepareStatement(query);

            stmnt.setString(1, hash);
            stmnt.execute();

            query = "SELECT * FROM `safe`";
            stmnt = connection.prepareStatement(query);

            rs = stmnt.executeQuery();

            while (rs.next()) {
                hash = rs.getString(1);
                break;
            }

            connection.prepareStatement("TRUNCATE `safe`").execute();

            //hash.replaceAll("�", "\u009D"); //sql sanitize);

            if (hash.equals(dbPass)) {
                int id = getUserID(username);
                println("Id: " + id);
                return id;
            } else {
                return -1;
            }
        } catch (Exception e) {
            printErr(e);
            return -1;
        }
    }

    /* done
     * Method: changePass
     * Pre: takes in a username and password
     * Post: returns true if password was successfully chanced or false if error
     */
    public static boolean changePass(String username, String pass) {
        connect();

        try {
            String query = "SELECT `s` FROM `users` WHERE `Email` LIKE ?"; //get the user's salt
            PreparedStatement stmnt = connection.prepareStatement(query);
            stmnt.setString(1, username);

            ResultSet rs = stmnt.executeQuery();

            String salt = "";
            while (rs.next()) {
                salt = rs.getString(1); //save the salt
            }

            pass = hash(pass, salt); //hash the new password

            query = "UPDATE `users` SET `Password` = ? WHERE `Email` LIKE ?"; //change password
            stmnt = connection.prepareStatement(query);
            stmnt.setString(1, pass);
            stmnt.setString(2, username);

            stmnt.execute();

            return true;
        } catch (Exception e) {
            printErr(e);
            return false;
        }
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /* done
     * Method: createGroup
	 * Pre: takes in a groupname and the user's id
	 * Post: return's the created group number or -1 if there is an issue
	 */
    public static int createGroup(String groupName, int ownerID) {
        connect();
        try {
            String query = "INSERT INTO `groups` (`GName`, `OwnerID`) Values(?, ?)";
            PreparedStatement stmnt = connection.prepareStatement(query);
            stmnt.setString(1, groupName);
            stmnt.setInt(2, ownerID);
            stmnt.execute();

            query = "SELECT `ID` FROM `groups`";
            stmnt = connection.prepareStatement(query);
            ResultSet rs = stmnt.executeQuery();
            int group = -1;
            try {
                while (rs.next()) {
                    group = rs.getInt(1);
                }
                return group;
            } catch (Exception e) {
                printErr(e);
                return -2;
            }
        } catch (Exception e) {
            printErr(e);
            return -1;
        }
    }

    /*done
     * Method: joinGroup
     * Pre: takes in group id and user id
     * Post: returns true if successful in joining or false if not
     */
    public static boolean joinGroup(int groupID, int userID) {
        connect();
        try {
            String queryG = "SELECT `userID` FROM `groupmembers` WHERE `groupID` LIKE ?";
            PreparedStatement stmnt = connection.prepareStatement(queryG);
            stmnt.setInt(1, groupID);
            ResultSet rs = stmnt.executeQuery();

            try {
                while (rs.next()) {
                    int usr = rs.getInt(1);

                    if (usr == userID) { //User is already part of the group
                        return false;
                    }
                    break;
                }
            } catch (Exception e) {
                printErr(e);
                return false;
            }

            queryG = "SELECT `ID` FROM `groups` WHERE `ID` LIKE ?";
            stmnt = connection.prepareStatement(queryG);
            stmnt.setInt(1, groupID);
            rs = stmnt.executeQuery();

            try {
                boolean exists = false;

                while (rs.next()) {
                    int g = rs.getInt(1);

                    if (g == groupID) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    return false;
                }
            } catch (Exception e) {
                printErr(e);
                return false;
            }

            String query = "INSERT INTO `groupmembers` (`userID`, `groupID`) VALUES(?,?)";
            stmnt = connection.prepareStatement(query);
            stmnt.setInt(1, userID);
            stmnt.setInt(2, groupID);
            stmnt.execute();
            return true;
        } catch (Exception e) {
            printErr(e);
            return false;
        }
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /* done
     * Method: getUserSnips
     * Pre: takes in the user's id
     * Post: returns all of the user's snips as an arrayList of Snips or null if error
     */
    public static ArrayList<Snip> getUserSnips(int userID) {
        connect();
        try {
            ArrayList<Snip> snips = new ArrayList<Snip>();

            String query = "SELECT * FROM `snips` WHERE `userID` like ?";
            PreparedStatement stmnt = connection.prepareStatement(query);
            stmnt.setInt(1, userID);
            ResultSet rs = stmnt.executeQuery();

            try {
                while (rs.next()) {
                    int id = rs.getInt(1);
                    int uId = rs.getInt(2);
                    String title = rs.getString(3);
                    String tags = rs.getString(4);
                    String lang = rs.getString(5);
                    String code = rs.getString(6);

                    String[] tgs = null;
                    if (tags != null) {
                        tgs = tags.split("~");
                    }
                    Snip tmp = new Snip(id, uId, title, tgs, lang, code);
                    snips.add(tmp);
                }

            } catch (Exception e) {
                printErr(e);
                return null;
            }
            return snips;
        } catch (Exception e) {
            printErr(e);
            return null;
        }
    }

    /* done
     * Method: getUsersLastFiftySnips
     * Pre: takes in the user's id
     * Post: returns all of the user's last 50 snips as a ArrayList of Snips (latest to newest) (` delim) or null if error
     */
    public static ArrayList<Snip> getUsersLastFiftySnips(int userID) {
        connect();
        try {
            ArrayList<Snip> snips = new ArrayList<Snip>();

            String query = "SELECT * FROM `snips` WHERE `userID` like ? ORDER BY `ID` DESC LIMIT 50";
            PreparedStatement stmnt = connection.prepareStatement(query);
            stmnt.setInt(1, userID);
            ResultSet rs = stmnt.executeQuery();

            try {
                while (rs.next()) {
                    int id = rs.getInt(1);
                    int uId = rs.getInt(2);
                    String title = rs.getString(3);
                    String tags = rs.getString(4);
                    String lang = rs.getString(5);
                    String code = rs.getString(6);

                    String[] tgs = null;
                    if (tags != null) {
                        tgs = tags.split("~");
                    }
                    Snip tmp = new Snip(id, uId, title, tgs, lang, code);
                    snips.add(tmp);
                }

            } catch (Exception e) {
                printErr(e);
                return null;
            }
            return snips;
        } catch (Exception e) {
            printErr(e);
            return null;
        }
    }

    /*done
     * Method: getGroupSnips
     * Pre: takes in a group id
     * Post: returns all UNIQUE snips part of that group as ArrayList of Snips or null if error
     */
    public static ArrayList<Snip> getGroupSnips(int userID, int groupID) {
        connect();
        try {
            ArrayList<Snip> snips = new ArrayList<Snip>();

            String query = "SELECT s.* FROM snips s, snipgroups sg, groups g WHERE s.ID = sg.snipID and sg.groupID = g.ID and g.ID = ?";
            PreparedStatement stmnt = connection.prepareStatement(query);
            stmnt.setInt(1, groupID);
            ResultSet rs = stmnt.executeQuery();

            try {
                while (rs.next()) {
                    int id = rs.getInt(1);
                    int uId = rs.getInt(2);
                    String title = rs.getString(3);
                    String tags = rs.getString(4);
                    String lang = rs.getString(5);
                    String code = rs.getString(6);

                    if (uId == userID) {//Do not add if user's snip
                    } else {
                        String[] tgs = null;
                        if (tags != null) {
                            tgs = tags.split("~");
                        }
                        Snip tmp = new Snip(id, uId, title, tgs, lang, code);
                        snips.add(tmp);
                    }
                }
            } catch (Exception e) {
                printErr(e);
                return null;
            }
            return snips;
        } catch (Exception e) {
            printErr(e);
            return null;
        }
    }

    /*done
     * Method: createSnip
	 * Pre: Takes in at least a UserID, title, and code (overloads give more
	 * options)
	 * Post: returns ID if successful, -1 if not
	 * Note: if tags, tags are seperated with '~' in the DB
	 */

    public static int createSnip(int userID, String title, String code) {
        connect();
        try {
            String query = "INSERT INTO `snippy`.`snips` (`UserID`, `Title`, `Code`) VALUES (?,?,?);";
            PreparedStatement stmnt = connection.prepareStatement(query);
            stmnt.setInt(1, userID);
            stmnt.setString(2, title);
            stmnt.setString(3, code);
            stmnt.execute();

            query = "SELECT `ID` FROM `snips` WHERE `UserID` LIKE ? ORDER BY `ID` DESC LIMIT 1";
            stmnt = connection.prepareStatement(query);
            stmnt.setInt(1, userID);
            ResultSet rs = stmnt.executeQuery();

            while (rs.next()) {
                return rs.getInt(1);
            }
            return -1;
        } catch (Exception e) {
            printErr(e);
            return -1;
        }
    }

    public static int createSnip(int userID, String title, String lang, String code) {
        connect();
        try {
            String query = "INSERT INTO `snippy`.`snips` (`UserID`, `Title`, `Lang`, `Code`) VALUES (?,?,?,?);";
            PreparedStatement stmnt = connection.prepareStatement(query);
            stmnt.setInt(1, userID);
            stmnt.setString(2, title);
            stmnt.setString(3, lang);
            stmnt.setString(4, code);
            stmnt.execute();

            query = "SELECT `ID` FROM `snips` WHERE `UserID` LIKE ? ORDER BY `ID` DESC LIMIT 1";
            stmnt = connection.prepareStatement(query);
            stmnt.setInt(1, userID);
            ResultSet rs = stmnt.executeQuery();

            while (rs.next()) {
                return rs.getInt(1);
            }
            return -1;
        } catch (Exception e) {
            printErr(e);
            return -1;
        }
    }

    public static int createSnip(int userID, String title, ArrayList<String> tags, String code) {
        String tgs = "";
        for (int i = 0; i < tags.size(); i++) {
            tgs = tags.get(i) + "~";
        }
        tgs = tgs.substring(0, tgs.length() - 1);

        connect();
        try {
            String query = "INSERT INTO `snippy`.`snips` (`UserID`, `Title`, `Tags`, `Code`) VALUES (?,?,?,?);";
            PreparedStatement stmnt = connection.prepareStatement(query);
            stmnt.setInt(1, userID);
            stmnt.setString(2, title);
            stmnt.setString(3, tgs);
            stmnt.setString(4, code);
            stmnt.execute();

            query = "SELECT `ID` FROM `snips` WHERE `UserID` LIKE ? ORDER BY `ID` DESC LIMIT 1";
            stmnt = connection.prepareStatement(query);
            stmnt.setInt(1, userID);
            ResultSet rs = stmnt.executeQuery();

            while (rs.next()) {
                return rs.getInt(1);
            }
            return -1;
        } catch (Exception e) {
            printErr(e);
            return -1;
        }
    }

    public static int createSnip(int userID, String title, ArrayList<String> tags, String lang, String code) {
        String tgs = "";
        for (int i = 0; i < tags.size(); i++) {
            tgs = tags.get(i) + "~";
        }
        tgs = tgs.substring(0, tgs.length() - 1);

        connect();
        try {
            String query = "INSERT INTO `snippy`.`snips` (`UserID`, `Title`, `Tags`, `Lang`, `Code`) VALUES (?,?,?,?,?);";
            PreparedStatement stmnt = connection.prepareStatement(query);
            stmnt.setInt(1, userID);
            stmnt.setString(2, title);
            stmnt.setString(3, tgs);
            stmnt.setString(4, lang);
            stmnt.setString(5, code);
            stmnt.execute();

            query = "SELECT `ID` FROM `snips` WHERE `UserID` LIKE ? ORDER BY `ID` DESC LIMIT 1";
            stmnt = connection.prepareStatement(query);
            stmnt.setInt(1, userID);
            ResultSet rs = stmnt.executeQuery();

            while (rs.next()) {
                return rs.getInt(1);
            }
            return -1;
        } catch (Exception e) {
            printErr(e);
            return -1;
        }
    }

    /*done
     * Method: removeSnip
     * Pre: takes in a snipID
     * Post: returns true if the snip was successfully removed or false on error
     */
    public static boolean removeSnip(int snipID) {
        connect();
        try {
            String query = "DELETE FROM `snips` WHERE `ID` LIKE ?";
            PreparedStatement stmnt = connection.prepareStatement(query);
            stmnt.setInt(1, snipID);
            stmnt.execute();

            query = "DELETE FROM `snipgroups` WHERE `snipID` LIKE ?";
            stmnt = connection.prepareStatement(query);
            stmnt.setInt(1, snipID);
            stmnt.execute();
            return true;
        } catch (Exception e) {
            printErr(e);
            return false;
        }
    }

    /*done
     * Method: shareSnip
     * Pre: takes in snip and group to share it to
     * Post: returns true if successfully shared or false if error
     */
    public static boolean shareSnip(int snipId, int groupID) {
        connect();
        try {
            String query = "INSERT INTO `snipgroups` (`snipID`, `groupID`) VALUES (?,?)";
            PreparedStatement stmnt = connection.prepareStatement(query);
            stmnt.setInt(1, snipId);
            stmnt.setInt(2, groupID);
            stmnt.execute();
            return true;
        } catch (Exception e) {
            printErr(e);
            return false;
        }
    }

    /*done
     * Method: unshareSnip
     * Pre: takes in a snip and a group its a part of and unshare it.
     * Post: returns true if successfully unshared or false if error
     */
    public static boolean unshareSnip(int snipId, int groupID) {
        connect();
        try {
            boolean partOfGroup = false;
            String query = "Select * FROM `snipgroups` WHERE `groupID` LIKE ?";
            PreparedStatement stmnt = connection.prepareStatement(query);
            stmnt.setInt(1, groupID);
            ResultSet rs = stmnt.executeQuery();

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

            query = "Delete FROM `snipgroups` WHERE `snipID` LIKE ?";
            stmnt = connection.prepareStatement(query);
            stmnt.setInt(1, snipId);
            stmnt.execute();
            return true;

        } catch (Exception e) {
            printErr(e);
            return false;
        }
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

            String hash = new String(passHash);

            return hash;
        } catch (Exception e) {
            printErr(e);
            return null;
        }

    }

    /*done
     * Method: generateSalt
     * Pre: none
     * Post: returns a 20 int string of salt
     */
    public static String generateSalt() {
        Random rand = new Random();

        String salt = "";

        for (int i = 0; i < 20; i++) {
            salt = salt + rand.nextInt(10);
        }
        return salt;
    }

    // ==========================DEBUG STUFF=============================

    private static boolean debug = false;
    private static boolean print = false;

    /*done
     * Method: println
     * Pre: a string to post
     * Post: outputs the string if debug is enabled
     */
    private static void println(String line) {
        if (debug && print)
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

    /*done
     * Method: clear
     * Pre: none
     * Post: all snippy DB tables are cleared
     */
    private static void clear() {
        if (debug) {
            try {
                connect();
                connection.prepareStatement("TRUNCATE `groupmembers`").execute();
                connection.prepareStatement("TRUNCATE `groups`").execute();
                connection.prepareStatement("TRUNCATE `snipgroups`").execute();
                connection.prepareStatement("TRUNCATE `snips`").execute();
                connection.prepareStatement("TRUNCATE `users`").execute();
            } catch (Exception e) {
                printErr(e);
            }
        }
    }

    public static void main(String[] args) { //FOR TESTING ONLY!
        if (debug) {
            clear();
            System.out.println("Clearing all data");

            String salt = generateSalt();
            String pass = hash("password", salt);
            System.out.println("Create User 1: " + createUser(pass, "hallja99@gmail.com", salt));

            salt = generateSalt();
            pass = hash("password", salt);
            System.out.println("Create User 2: " + createUser(pass, "hallja98@gmail.com", "Jake", salt));

            salt = generateSalt();
            pass = hash("password", salt);
            System.out.println("Create User 3: " + createUser(pass, "hallja97@gmail.com", "Joe", "Mother's Maiden Name", "Mills", salt));

            salt = generateSalt();
            pass = hash("password", salt);
            System.out.println("Create User 4: " + createUser(pass, "hallja96@gmail.com", "Jake", "Mother's Maiden Name", "Mills", "City Born In", "Boston", salt));

            System.out.println("==========");

            System.out.println("User 1's id: " + getUserID("hallja99@gmail.com"));
            System.out.println("User 1: " + getUser(getUserID("hallja99@gmail.com")));

            System.out.println("==========");

            System.out.println("User Exists (y): " + userExists("hallja99@gmail.com"));
            System.out.println("User Exists (N): " + userExists("hallj@gmail.com"));
            System.out.println("User Exists (Y): " + userExists(1));
            System.out.println("User Exists (N): " + userExists(0));

            System.out.println("==========");

            System.out.println("User 1's Password was changed: " + changePass("hallja99@gmail.com", "TEST"));
            System.out.println("User'1 can login: " + login("hallja99@gmail.com", "TEST"));
            System.out.println("User 1: " + getUser(getUserID("hallja99@gmail.com")));

            System.out.println("==========");

            System.out.println("Create Group (1): " + createGroup("Test Group 1", 1));
            System.out.println("Create Group (3): " + createGroup("Test Group 2", 3));

            System.out.println("Join Group (2 into 1): " + joinGroup(1, 2));
            System.out.println("Join Group (4 into 2): " + joinGroup(2, 4));

            System.out.println("==========");

            System.out.println("Create Snip (1): " + createSnip(1, "Test 1", "System.out.println(\"Snip #1\");"));
            System.out.println("Create Snip (2): " + createSnip(2, "Test 2", "Java", "System.out.println(\"Snip #2\");"));

            ArrayList<String> tags = new ArrayList<>();
            tags.add("Debug");
            tags.add("Testing");
            tags.add("Tags");

            System.out.println("Create Snip (3): " + createSnip(3, "Test 3", tags, "System.out.println(\"Snip #3\");"));
            System.out.println("Create Snip (4): " + createSnip(4, "Test 4", tags, "Java", "System.out.println(\"Snip #4\");"));

            System.out.println("==========");

            System.out.println("Sharing Snip (1): " + shareSnip(1, 1));
            System.out.println("Sharing Snip (2): " + shareSnip(2, 1));
            System.out.println("Sharing Snip (3): " + shareSnip(3, 2));

            System.out.println("Removing Snip (4): " + removeSnip(4));
            System.out.println("UnSharing Snip (2): " + unshareSnip(2, 1));

            System.out.println("==========");

            System.out.println("User 1's Last 50 Snips: " + getUsersLastFiftySnips(1).get(0).toString());
            System.out.println("User 2 Snips: " + getUserSnips(2).get(0).toString());
            System.out.println("User 3 Snips: " + getUserSnips(3).get(0).toString());
            System.out.println("User 4 Snips: " + getUserSnips(4).size());

            System.out.println("==========");

            System.out.println("Group 1 Snips (User 1): " + Arrays.toString(getGroupSnips(1, 1).toArray()));
            System.out.println("Group 1 Snips (User 2): " + Arrays.toString(getGroupSnips(2, 1).toArray()));
            System.out.println("Group 2 Snips (User 3): " + Arrays.toString(getGroupSnips(3, 2).toArray()));
            System.out.println("Group 2 Snips (User 4): " + Arrays.toString(getGroupSnips(4, 2).toArray()));
        }
    }
    // ==================================================================
}
