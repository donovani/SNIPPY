package io.snippy.main;


import com.jfoenix.controls.*;
import io.snippy.core.Group;
import io.snippy.core.R;
import io.snippy.util.SQLUtils;
import io.snippy.util.UXUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;

import static io.snippy.login.LoginScene.currentUser;

/**
 * Created by Ian on 4/9/2017.
 */
public class TeamsDialog extends JFXDialog {
    private static JFXDialog dialog;
    private final MainScene scene;
    private final StackPane stackPane;
    private JFXDialogLayout layout;
    private JFXButton doneButton;

    private TeamsDialog(MainScene scene, StackPane stackPane) {
        this.stackPane = stackPane;
        this.layout = new JFXDialogLayout();
        this.scene = scene;
        create();
    }

    private final void create() {

        doneButton = new JFXButton(R.strings("common.done"));
        doneButton.setStyle("-fx-text-fill: WHITE; -fx-background-color: #00aaaa");

        layout.setHeading(new Label(R.strings("teams.header")));
        layout.setBody(UXUtils.inflate("assets/layouts/layout_teams.fxml"));
        layout.setActions(doneButton);

        doneButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                dialog.close();
                scene.clearDisplayedSnip();
                scene.update();
            }
        });

        this.setTransitionType(DialogTransition.CENTER);
        this.setContent(layout);

        //==============================================================================

        JFXButton teamsCreateButton = (JFXButton) lookup("#teams_create");
        JFXButton teamsJoinButton = (JFXButton) lookup("#teams_join");
        JFXButton teamsLeaveButton = (JFXButton) lookup("#teams_leave");
        JFXButton teamsDeleteButton = (JFXButton) lookup("#teams_delete");

        teamsCreateButton.setOnAction(event -> createGroup());
        teamsJoinButton.setOnAction(event -> joinGroup());
        teamsLeaveButton.setOnAction(event -> leaveGroup());
        teamsDeleteButton.setOnAction(event -> deleteGroup());

        //==============================================================================

        JFXTextArea myGroups = (JFXTextArea) lookup("#teams_list");
        String text = "Groups you are a part of:\n";

        ArrayList<Group> groups = SQLUtils.getUserGroups(currentUser.getUserId());

        for (int i = 0; i < groups.size(); i++) {
            text = text + " " + (i + 1) + ".   " + groups.get(i).getName() + "\n";
        }
        myGroups.setText(text);
    }

    private void createGroup() {
        String groupname = ((JFXTextField) lookup("#teams_name")).getText(); //get group name
        int userID = currentUser.getUserId(); //get user's id

        if ((!groupname.equals("") && groupname != null) && SQLUtils.groupExists(groupname) == false) {
            int success = SQLUtils.createGroup(groupname, userID); //creates a group with group name and user ID
            if (success > 0) {
                dialog.close();
                scene.clearDisplayedSnip();
                scene.update();
            } else {
                ((JFXTextField) lookup("#teams_name")).clear();
                ((JFXTextField) lookup("#teams_name")).setPromptText("E   R   R   O   R");
            }
        } else {
            ((JFXTextField) lookup("#teams_name")).clear();
            ((JFXTextField) lookup("#teams_name")).setPromptText("t h a t  g r o u p  a l r e a d y  e x i s t s");
        }
    }

    private void joinGroup() {
        String groupname = ((JFXTextField) lookup("#teams_code")).getText(); //get group name
        int userID = currentUser.getUserId(); //get user's id
        int groupID = SQLUtils.getGroupId(groupname);

        if ((!groupname.equals("") && groupname != null) && SQLUtils.groupExists(groupname) == true) {
            boolean success = SQLUtils.joinGroup(groupID, userID); //joins a group with groupID
            if (success) {
                dialog.close();
                scene.clearDisplayedSnip();
                scene.update();
            } else {
                ((JFXTextField) lookup("#teams_code")).clear();
                ((JFXTextField) lookup("#teams_code")).setPromptText("E   R   R   O   R");
            }
        } else {
            ((JFXTextField) lookup("#teams_code")).clear();
            ((JFXTextField) lookup("#teams_code")).setPromptText("t h a t  g r o u p  d o e s  n o t  e x i s t");
        }
    }

    private void leaveGroup() {
        String groupname = ((JFXTextField) lookup("#teams_leave_name")).getText(); //get group name
        int userID = currentUser.getUserId(); //get user's id
        int groupID = SQLUtils.getGroupId(groupname);

        if ((!groupname.equals("") && groupname != null) && SQLUtils.groupExists(groupname) != false) {
            boolean success = SQLUtils.leaveGroup(groupID, userID); //leaves the group with GroupID
            if (success) {
                dialog.close();
                scene.clearDisplayedSnip();
                scene.update();
            } else {
                ((JFXTextField) lookup("#teams_leave_name")).clear();
                ((JFXTextField) lookup("#teams_leave_name")).setPromptText("E   R   R   O   R");
            }
        } else {
            ((JFXTextField) lookup("#teams_leave_name")).clear();
            ((JFXTextField) lookup("#teams_leave_name")).setPromptText("t h a t  g r o u p  d o e s  n o t  e x i s t");
        }
    }

    private void deleteGroup() {
        String groupname = ((JFXTextField) lookup("#teams_delete_name")).getText(); //get group name
        int userID = currentUser.getUserId(); //get user's id

        if ((!groupname.equals("") && groupname != null) && SQLUtils.groupExists(groupname) != false) {
            boolean success = SQLUtils.deleteGroup(SQLUtils.getGroupId(groupname), userID); //creates a group with group name and user ID
            if (success) {
                dialog.close();
                scene.clearDisplayedSnip();
                scene.update();
            } else {
                ((JFXTextField) lookup("#teams_delete_name")).clear();
                ((JFXTextField) lookup("#teams_delete_name")).setPromptText("E   R   R   O   R");
            }
        } else {
            ((JFXTextField) lookup("#teams_delete_name")).clear();
            ((JFXTextField) lookup("#teams_delete_name")).setPromptText("t h a t  g r o u p  d o e s  n o t  e x i s t");
        }
    }

    public static final JFXDialog createAndShow(MainScene s, StackPane sp) {
        JFXDialog d = new TeamsDialog(s, sp);
        dialog = d;

        d.show(sp);
        return d;
    }
}