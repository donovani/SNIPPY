package io.snippy.recover;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import io.snippy.core.StageScene;
import io.snippy.login.LoginScene;
import io.snippy.util.SQLUtils;
import io.snippy.util.UXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.stage.Stage;


/**
 * The fourth recovery scene: resetting your password
 * Created by Ian on 3/2/2017.
 */
public class RecoverResetScene extends StageScene {

    public RecoverResetScene(Stage primaryStage) {
        super(primaryStage);
    }

    @Override
    public Parent inflateLayout() {
        return UXUtils.inflate("assets/layouts/layout_recover4.fxml");
    }

    @Override
    public void onCreate() {
        JFXButton cancelButton = (JFXButton) lookup("#recover_cancel");
        cancelButton.setOnAction(event -> switchScreen(LoginScene.class));

        JFXButton submitButton = (JFXButton) lookup("#recover_submit");
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                String pass1 = ((JFXPasswordField) lookup("#recover_password")).getText(); //grab password
                String pass2 = ((JFXPasswordField) lookup("#recover_confirm")).getText(); //grab password confirmation

                if (pass1.equals(pass2)) { //if passwords match
                    lookup("#login_error1").setVisible(false); //hide errors
                    lookup("#login_error2").setVisible(false);

                    boolean changed = SQLUtils.changePass(RecoverEmailScene.username, pass1); //try to change password

                    if (changed) { //if successful
                        switchScreen(LoginScene.class);
                    } else { //else error
                        lookup("#login_error1").setVisible(false);
                        lookup("#login_error2").setVisible(true);
                    }
                } else {//else error
                    lookup("#login_error1").setVisible(true);
                    lookup("#login_error2").setVisible(false);
                }
            }
        });
    }

    @Override
    public void onDispose() {

    }
}
