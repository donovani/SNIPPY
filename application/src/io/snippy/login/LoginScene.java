package io.snippy.login;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import io.snippy.core.StageScene;
import io.snippy.core.User;
import io.snippy.main.MainScene;
import io.snippy.recover.RecoverEmailScene;
import io.snippy.register.RegisterScene;
import io.snippy.util.SQLUtils;
import io.snippy.util.UXUtils;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.ActionEvent;


/**
 * The scene representing the user login prompt.
 * Created by Ian on 2/15/2017.
 */
public class LoginScene extends StageScene {

    public static User currentUser;

    public LoginScene(Stage stage) {
        super(stage);
    }

    @Override
    public Parent inflateLayout() {
        return UXUtils.inflate("assets/layouts/layout_login.fxml");
    }

    @Override
    public void onCreate() {
        //boolean debug = true; //NEEDS TO BE REMOVED
        JFXButton loginButton = (JFXButton) lookup("#login_submit");

        JFXPasswordField pass = (JFXPasswordField) lookup("#login_password");

        pass.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    login();
                }
            }
        });

        loginButton.setOnAction(event -> login());

        JFXButton registerButton = (JFXButton) lookup("#login_register");
        registerButton.setOnAction(event -> switchScreen(RegisterScene.class));
        JFXButton forgotButton = (JFXButton) lookup("#login_forgot");
        forgotButton.setOnAction(event -> switchScreen(RecoverEmailScene.class));
    }

    private void login() {
        String username = ((JFXTextField) lookup("#login_email")).getText(); //get username
        String password = ((JFXPasswordField) lookup("#login_password")).getText();// get password

        int userID = SQLUtils.login(username, password); //get user's id
        if (userID != -1) {//if the id isnt -1
            currentUser = new User(userID);
            switchScreen(MainScene.class); //good to go
        } else { //else error
            lookup("#login_error").setVisible(true);
        }
    }

    @Override
    public void onDispose() {

    }
}
