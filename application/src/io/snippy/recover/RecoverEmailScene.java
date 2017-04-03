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
        JFXButton cancelButton = (JFXButton) lookup("#recover_cancel");
        cancelButton.setOnAction(event -> switchScreen(LoginScene.class));

        JFXButton nextButton = (JFXButton) lookup("#recover_next");
        nextButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                username = ((JFXTextField) lookup("#recover_email")).getText();

                if (SQLUtils.userExists(username)) {
                    lookup("#login_error").setVisible(false);
                    switchScreen(RecoverQuestionScene.class);
                } else {
                    lookup("#login_error").setVisible(true);
                }
            }
        });
    }

    @Override
    public void onDispose() {

    }
}
