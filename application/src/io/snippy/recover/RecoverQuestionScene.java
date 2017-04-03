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
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Arrays;


/**
 * The second recovery scene: answering security question
 * Created by Ian on 3/1/2017.
 */
public class RecoverQuestionScene extends StageScene {
    private static int qNum = 1;
    private static String[] user = null;

    public RecoverQuestionScene(Stage primaryStage) {
        super(primaryStage);
    }

    @Override
    public Parent inflateLayout() {
        return UXUtils.inflate("assets/layouts/layout_recover2.fxml");
    }

    @Override
    public void onCreate() {
        if (user == null) { //generate a user
            String tmp = SQLUtils.getUser(SQLUtils.getUserID(RecoverEmailScene.username));
            user = tmp.split("`");
        }

        if ((user[3] == null || user[3].equals("") || user[3].equals("null")) && (user[5] == null || user[5].equals("") || user[5].equals("null"))) {
            println(user[3]);
            println(user[5]);
            println(((user[3] == null || user[3].equals("") || user[3].equals("null")) && (user[5] == null || user[5].equals("") || user[5].equals("null"))) + "");
            qNum = 1;
            user = null;
            switchScreen(RecoverCodeScene.class); //send code
        } else {// if the user did security questions
            JFXButton cancelButton = (JFXButton) lookup("#recover_cancel");
            cancelButton.setOnAction(event -> switchScreen(LoginScene.class));

            if (user[3].equals("null")) { //if user only selected second question, use that
                qNum = 2;
            }

            if (qNum == 1) { //question 1
                ((Text) lookup("#recover_question")).setText(user[3]);
            } else {//question 2
                ((Text) lookup("#recover_question")).setText(user[5]);
            }

            JFXButton nextButton = (JFXButton) lookup("#recover_next");
            nextButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(javafx.event.ActionEvent event) {
                    String answer = ((JFXTextField) lookup("#recover_answer")).getText();

                    if (qNum == 1 && answer.equalsIgnoreCase(user[4])) { //if answer to q1 is correct
                        lookup("#login_error").setVisible(false);
                        if (user[5] != null) { //see if there is a second question
                            user[3] = "null";
                            qNum = 2;
                            switchScreen(RecoverQuestionScene.class);//ask that question
                        } else { //only one question
                            qNum = 1;
                            user = null;
                            switchScreen(RecoverCodeScene.class);//send code
                        }
                    } else if (qNum == 2 && answer.equalsIgnoreCase(user[6])) {//if answer to q2 is correct
                        lookup("#login_error").setVisible(false);
                        qNum = 1;
                        user = null;
                        switchScreen(RecoverCodeScene.class); //send code
                    } else { //incorrect password
                        lookup("#login_error").setVisible(true);
                    }
                }
            });
        }
    }

    @Override
    public void onDispose() {

    }

    //================DEBUG===========
    private boolean debug = false;

    private void println(String val) {
        if (debug) {
            System.out.println(val);
        }
    }
}
