package io.snippy.teams;

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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.stage.Stage;

/**
 * Created by agressm on 4/8/2017.
 */
public class Groups extends StageScene {

    public static User currentUser;

    public Groups(Stage stage) {
        super(stage);
    }

    @Override
    public Parent inflateLayout() {
        return UXUtils.inflate("assets/layouts/layout_teams.fxml");
    }//Change to use join groups

    @Override
    public void onCreate() {
        JFXButton teamsCreateButton = (JFXButton) lookup("#create_team");
        JFXButton teamsJoinButton = (JFXButton) lookup("#teams_code");

        teamsCreateButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                String groupname = ((JFXTextField) lookup("#teams_create")).getText(); //get group name
                int userID = currentUser.getUserId(); //get user's id

                if(SQLUtils.groupExists(groupname) == false) {
                    SQLUtils.createGroup(groupname, userID); //creates group with ID of the user as owner
                   }


                }
            });

        teamsJoinButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                String groupname = ((JFXTextField) lookup("#teams_code")).getText(); //get group name
                int userID = currentUser.getUserId(); //get user's id
                int groupID = SQLUtils.getGroupID(groupname);

                if(SQLUtils.groupExists(groupname) == false) {
                    SQLUtils.joinGroup(groupID, userID); //joins group with group ID of groupID and user ID of userID
                }

            }
        });




    }

    @Override
    public void onDispose() {

    }



}
