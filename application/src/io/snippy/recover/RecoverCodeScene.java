package io.snippy.recover;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import io.snippy.core.StageScene;
import io.snippy.login.LoginScene;
import io.snippy.util.SQLUtils;
import io.snippy.util.UXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.util.Date;


/**
 * The third recovery scene: entering emailed recovery code
 * Created by Ian on 3/2/2017.
 */
public class RecoverCodeScene extends StageScene {

    public RecoverCodeScene(Stage primaryStage) {
        super(primaryStage);
    }

    @Override
    public Parent inflateLayout() {
        return UXUtils.inflate("assets/layouts/layout_recover3.fxml");
    }

    @Override
    public void onCreate() {
        String sentCode = new SendEmail().send(RecoverEmailScene.username); //send an email to the user
        long startTime = System.currentTimeMillis(); //log the time

        JFXButton cancelButton = (JFXButton) lookup("#recover_cancel"); //cancel button
        cancelButton.setOnAction(event -> switchScreen(LoginScene.class));

        JFXButton nextButton = (JFXButton) lookup("#recover_next");
        nextButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                String code = ((JFXTextField) lookup("#recover_answer")).getText();

                int minsTillTimeout = 5;// timeout after 5 mines
                if (System.currentTimeMillis() - startTime > (minsTillTimeout * 60 * 1000)) { //if time out
                    lookup("#login_error1").setVisible(false); //show timeout error
                    lookup("#login_error2").setVisible(true);
                    switchScreen(RecoverCodeScene.class); //resend code
                } else if (code.equals(sentCode)) { //if codes match
                    lookup("#login_error1").setVisible(false);
                    lookup("#login_error2").setVisible(false);
                    switchScreen(RecoverResetScene.class);
                } else { //show error
                    lookup("#login_error2").setVisible(false);
                    lookup("#login_error1").setVisible(true);
                }
            }
        });
    }

    @Override
    public void onDispose() {

    }
}
