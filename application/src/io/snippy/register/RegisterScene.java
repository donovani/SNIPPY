package io.snippy.register;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import io.snippy.core.StageScene;
import io.snippy.login.LoginScene;
import io.snippy.main.MainScene;
import io.snippy.util.SQLUtils;
import io.snippy.util.UXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import jdk.nashorn.internal.ir.LabelNode;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


/**
 * Created by Ian on 2/18/2017.
 */
public class RegisterScene extends StageScene {
    public static final String EMAIL_REGEX = "^([\\w\\-\\.])+@(([\\d\\w\\-])+\\.)+[a-zA-Z]{2,}$";

    public RegisterScene(Stage primaryStage) {
        super(primaryStage);
    }

    @Override
    public Parent inflateLayout() {
        return UXUtils.inflate("assets/layouts/layout_register.fxml");
    }

    @Override
    public void onCreate() {
        JFXButton cancelButton = (JFXButton) lookup("#register_cancel");
        cancelButton.setOnAction(event -> switchScreen(LoginScene.class));

        ArrayList<String> comboItems = new ArrayList<String>();
        comboItems.add("");
        comboItems.add("In what city were you born?");
        comboItems.add("What is the name of your first school?");
        comboItems.add("What high school did you attend?");
        comboItems.add("What is your favorite movie?");
        comboItems.add("What street did you grow up on?");
        comboItems.add("What is your father's middle name?");
        comboItems.add("What is your favorite color?");
        comboItems.add("Which is your favorite web browser?");
        comboItems.add("What was the make of your first car?");
        comboItems.add("Who is your favorite actor, musician, or artist?");

        JFXComboBox q1 = (JFXComboBox) lookup("#register_question1");
        JFXComboBox q2 = (JFXComboBox) lookup("#register_question2");
        q1.getItems().addAll(comboItems);
        q2.getItems().addAll(comboItems);

        JFXButton submitButton = (JFXButton) lookup("#register_submit");
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {

                String email = getValue((JFXTextField) lookup("#register_email"));
                String name = getValue((JFXTextField) lookup("#register_name"));
                String pass1 = getValue((JFXPasswordField) lookup("#register_password"));
                String pass2 = getValue((JFXPasswordField) lookup("#register_confirm"));
                String sec1 = getValue((JFXComboBox) lookup("#register_question1"));
                String seca1 = getValue((JFXTextField) lookup("#register_answer1"));
                String sec2 = getValue((JFXComboBox) lookup("#register_question2"));
                String seca2 = getValue((JFXTextField) lookup("#register_answer2"));

                //DEBUG
                println("\n\n==========");
                println("Email: " + email);
                println("Name: " + name);
                println("Password: " + pass1);
                println("Password (Conf): " + pass2);
                println("Security Question 1: " + sec1);
                println("Security Answer 1: " + seca1);
                println("Security Question 2: " + sec2);
                println("Security Answer 2: " + seca2);
                println("==========\n\n");
                //END DEBUG

                if (!email.matches(EMAIL_REGEX) || email.equals("")) { //no email visible
                    hideErrors();
                    lookup("#register_error1").setVisible(true);

                    printErr("Email issue");
                } else if (SQLUtils.userExists(email)) {
                    hideErrors();
                    lookup("#register_error5").setVisible(true);

                    println("Email has already been registered");
                } else if (pass1.equals("") || pass2.equals("")) { //a password was empty
                    hideErrors();
                    lookup("#register_error2").setVisible(true);

                    println("A password field was blank");
                } else if (!pass1.equals(pass2)) { //passwords dont match
                    hideErrors();
                    lookup("#register_error3").setVisible(true);

                    println("Passwords do not match");
                } else if (sec1.equals("") && !seca1.equals("")) {//if sec q or answer are empty while the other is filled out
                    hideErrors();
                    lookup("#register_error4").setVisible(true);

                    println("Security question 1 is blank or response is blank");
                } else if (sec2.equals("") && !seca2.equals("")) {//if sec q or answer are empty while the other is filled out
                    hideErrors();
                    lookup("#register_error4").setVisible(true);

                    println("Security question 2 is blank or response is blank");
                } else {

                    if (!sec1.equals("") && seca1.equals("")) {//allow the user to still register if they decided not to pic a sec question
                        sec1 = "";
                        seca1 = "";
                    }
                    if (!sec2.equals("") && seca2.equals("")) {//allow the user to still register if they decided not to pic a sec question
                        sec2 = "";
                        seca2 = "";
                    }
                    String salt = SQLUtils.generateSalt(); //generate salt
                    String pass = SQLUtils.hash(pass1, salt); //hash the user's password

                    println("Salt: " + salt);
                    println("Hashed and Salted: " + pass);

                    boolean success = SQLUtils.createUser(pass, email, name, sec1, seca1, sec2, seca2, salt); //try creating an account

                    if (success) {
                        hideErrors();
                        lookup("#register_success").setVisible(true);

                        switchScreen(LoginScene.class); //go back to login scene
                    } else { //cannot create the account
                        hideErrors();
                        lookup("#register_error6").setVisible(true);

                        println("There was an issue creating the account");
                    }
                }
            }
        });
    }

    private String getValue(JFXTextField field) {//look up the value
        String val = "";
        try {//set the values to ones in the fields
            val = field.getText();
        } catch (Exception e) {
            printErr(e);
        }
        return val;
    }

    private String getValue(JFXComboBox field) {//look up the value
        String val = "";
        try {//set the values to ones in the fields
            val = field.getValue().toString();
        } catch (Exception e) {
            printErr(e);
        }
        return val;
    }

    private String getValue(JFXPasswordField field) {//look up the value
        String val = "";
        try {//set the values to ones in the fields
            val = field.getText();
        } catch (Exception e) {
            printErr(e);
        }
        return val;
    }

    public void hideErrors() { //make all error messages invisible
        lookup("#register_error1").setVisible(false);
        lookup("#register_error2").setVisible(false);
        lookup("#register_error3").setVisible(false);
        lookup("#register_error4").setVisible(false);
        lookup("#register_error5").setVisible(false);
        lookup("#register_error6").setVisible(false);
        lookup("#register_success").setVisible(false);
    }

    @Override
    public void onDispose() {

    }

    //============DEBUG=============================
    private boolean debug = false;

    private void print(String val) {
        if (debug) {
            System.out.print(val);
        }
    }

    private void println(String val) {
        if (debug) {
            print(val + "\n");
        }
    }

    private void printErr(String err) {
        if (debug) {
            System.err.println(err);
        }
    }

    private void printErr(Exception e) {
        if (debug) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
