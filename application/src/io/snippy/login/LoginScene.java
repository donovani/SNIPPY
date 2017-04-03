package io.snippy.login;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import io.snippy.core.StageScene;
import io.snippy.main.MainScene;
import io.snippy.recover.RecoverEmailScene;
import io.snippy.register.RegisterScene;
import io.snippy.util.SQLUtils;
import io.snippy.util.UXUtils;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.ActionEvent;


/**
 * The scene representing the user login prompt.
 * Created by Ian on 2/15/2017.
 */
public class LoginScene extends StageScene {

    public LoginScene(Stage stage) {
        super(stage);
    }

    @Override
    public Parent inflateLayout() {
        return UXUtils.inflate("assets/layouts/layout_login.fxml");
    }

    @Override
    public void onCreate() {
        JFXButton loginButton = (JFXButton) lookup("#login_submit");

        loginButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                String username = ((JFXTextField) lookup("#login_email")).getText();
                String password = ((JFXPasswordField) lookup("#login_password")).getText();

                int userID = SQLUtils.login(username, password);
                if (userID != -1) {
                    switchScreen(MainScene.class);
                } else {
                    lookup("#login_error").setVisible(true);
                }
            }
        });

        JFXButton registerButton = (JFXButton) lookup("#login_register");
        registerButton.setOnAction(event -> switchScreen(RegisterScene.class));
        JFXButton forgotButton = (JFXButton) lookup("#login_forgot");
        forgotButton.setOnAction(event -> switchScreen(RecoverEmailScene.class));
    }

    @Override
    public void onDispose() {

    }
}
