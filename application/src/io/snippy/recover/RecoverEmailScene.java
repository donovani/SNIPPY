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


/**
 * The first recovery scene: entering account email
 * Created by Ian on 3/1/2017.
 */
public class RecoverEmailScene extends StageScene {
    public static final String EMAIL_REGEX = "^([\\w\\-\\.])+@(([\\d\\w\\-])+\\.)+[a-zA-Z]{2,}$";
    public static String username = "";

    public RecoverEmailScene(Stage primaryStage) {
        super(primaryStage);
    }

    @Override
    public Parent inflateLayout() {
        return UXUtils.inflate("assets/layouts/layout_recover1.fxml");
    }

    @Override
    public void onCreate() {
        username = "";
        JFXButton cancelButton = (JFXButton) lookup("#recover_cancel"); //cancel
        cancelButton.setOnAction(event -> switchScreen(LoginScene.class));

        JFXButton nextButton = (JFXButton) lookup("#recover_next");
        nextButton.setOnAction(new EventHandler<ActionEvent>() { //on button click
            @Override
            public void handle(javafx.event.ActionEvent event) {
                username = ((JFXTextField) lookup("#recover_email")).getText(); //save the username

                if (username.matches(EMAIL_REGEX)) { //make sure its an email
                    if (SQLUtils.userExists(username)) { //double check it exists
                        lookup("#login_error").setVisible(false); //hide error messages
                        switchScreen(RecoverQuestionScene.class); //go to next recovery step
                    } else {
                        lookup("#login_error").setVisible(true); //show error
                    }
                } else {
                    lookup("#login_error").setVisible(true);//show error
                }
            }
        });
    }

    @Override
    public void onDispose() {

    }
}
