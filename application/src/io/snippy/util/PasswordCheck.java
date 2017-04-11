package io.snippy.util;

/**
 * Created by Jake on 4/7/2017.
 */
public class PasswordCheck {
    public PasswordCheck() {

    }

    /*
     * Pre: Takes in a password
     * Post: returns true if valid or false if not
     */
    public boolean checkPass(String pass) {
        if (pass.length() < 6) {
            println("Too short");
            return false;
        } else if (!pass.matches("^(?=.*[A-Z]).+$")) {
            println("No Upper Case");
            return false;
        } else if (!pass.matches("^(?=.*[a-z]).+$")) {
            println("No Lower Case");
            return false;
        } else if (!pass.matches("^(?=.*\\d).+$")) {
            println("No Number");
            return false;
        } else {
            return true;
        }

    }

    //=======DEBUG=========
    private boolean debug = false;

    private void println(String val) {
        if (debug) {
            System.out.println(val);
        }
    }
}
