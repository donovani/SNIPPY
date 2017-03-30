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
                String email = "";
                String name = "";
                String pass1 = "";
                String pass2 = "";
                String sec1 = "";
                String seca1 = "";
                String sec2 = "";
                String seca2 = "";
                try {
                    email = ((JFXTextField) lookup("#register_email")).getText();
                    name = ((JFXTextField) lookup("#register_name")).getText();
                    pass1 = ((JFXPasswordField) lookup("#register_password")).getText();
                    pass2 = ((JFXPasswordField) lookup("#register_confirm")).getText();
                    sec1 = ((JFXComboBox) lookup("#register_question1")).getValue().toString();
                    seca1 = ((JFXTextField) lookup("#register_answer1")).getText();
                    sec2 = ((JFXComboBox) lookup("#register_question2")).getValue().toString();
                    seca2 = ((JFXTextField) lookup("#register_answer2")).getText();
                } catch (Exception e) {

                }
                if (email.equals("")) { //no email visible
                    hideErrors();
                    lookup("#register_error1").setVisible(true);
                } else if (SQLUtils.userExists(email)) {
                    hideErrors();
                    lookup("#register_error5").setVisible(true);
                } else if (pass1.equals("") || pass2.equals("")) { //a password was empty
                    hideErrors();
                    lookup("#register_error2").setVisible(true);
                } else if (!pass1.equals(pass2)) { //passwords dont match
                    hideErrors();
                    lookup("#register_error3").setVisible(true);
                } else if ((sec1.equals("") && !seca1.equals("")) || (!sec1.equals("") && seca1.equals(""))) {//if sec q or answer are empty while the other is filled out
                    hideErrors();
                    lookup("#register_error4").setVisible(true);
                } else if ((sec2.equals("") && !seca2.equals("")) || (!sec2.equals("") && seca2.equals(""))) {//if sec q or answer are empty while the other is filled out
                    hideErrors();
                    lookup("#register_error4").setVisible(true);
                } else {
                    String salt = SQLUtils.generateSalt();
                    String pass = SQLUtils.hash(pass1, salt);

                    System.out.println("HELLO");
                    boolean success = SQLUtils.createUser(pass, email, name, sec1, seca1, sec2, seca2, salt);

                    if (success) {
                        switchScreen(LoginScene.class);
                    } else {
                        hideErrors();
                        lookup("#register_error6").setVisible(true);
                    }
                }
            }
        });

    }

    public void hideErrors() {
        lookup("#register_error1").setVisible(false);
        lookup("#register_error2").setVisible(false);
        lookup("#register_error3").setVisible(false);
        lookup("#register_error4").setVisible(false);
        lookup("#register_error5").setVisible(false);
        lookup("#register_error6").setVisible(false);
    }

    @Override
    public void onDispose() {

    }
}
