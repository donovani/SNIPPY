package io.snippy.main;


import com.jfoenix.controls.*;
import io.snippy.core.R;
import io.snippy.util.SQLUtils;
import io.snippy.util.UXUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

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

        JFXButton teamsCreateButton = (JFXButton) lookup("#teams_create");
        JFXButton teamsJoinButton = (JFXButton) lookup("#teams_join");

        teamsCreateButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                String groupname = ((JFXTextField) lookup("#teams_name")).getText(); //get group name
                int userID = currentUser.getUserId(); //get user's id

                System.out.println("userID: " + userID);

                if (SQLUtils.groupExists(groupname) == false) {
                    SQLUtils.createGroup(groupname, userID); //creates group with ID of the user as owner
                    dialog.close();
                    scene.clearDisplayedSnip();
                    scene.update();
                } else {
                    ((JFXTextField) lookup("#teams_name")).clear();
                    ((JFXTextField) lookup("#teams_name")).setPromptText("t h a t  g r o u p  a l r e a d y  e x i s t s");
                }
            }
        });

        teamsJoinButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                String groupname = ((JFXTextField) lookup("#teams_code")).getText(); //get group name
                int userID = currentUser.getUserId(); //get user's id
                int groupID = SQLUtils.getGroupId(groupname);

                if (SQLUtils.groupExists(groupname) == true) {
                    SQLUtils.joinGroup(groupID, userID); //joins group with group ID of groupID and user ID of userID
                    dialog.close();
                    scene.clearDisplayedSnip();
                    scene.update();
                } else {
                    ((JFXTextField) lookup("#teams_code")).clear();
                    ((JFXTextField) lookup("#teams_code")).setPromptText("t h a t  g r o u p  d o e s  n o t  e x i s t");
                }

            }
        });

        JFXListView teams = (JFXListView) lookup("#team_list");
        ObservableList<String> items = FXCollections.observableArrayList();

    }

    public static final JFXDialog createAndShow(MainScene s, StackPane sp) {
        JFXDialog d = new TeamsDialog(s, sp);
        dialog = d;

        d.show(sp);
        return d;
    }
}