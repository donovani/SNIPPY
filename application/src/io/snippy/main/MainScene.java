package io.snippy.main;

import com.jfoenix.controls.*;
import com.jfoenix.controls.events.JFXDialogEvent;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import com.sun.deploy.util.ArrayUtil;
import io.snippy.core.*;
import io.snippy.login.LoginScene;
import io.snippy.util.Language;
import io.snippy.util.SQLUtils;
import io.snippy.util.UXUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.pmw.tinylog.Logger;
import sun.applet.Main;
import sun.rmi.runtime.Log;

import javax.swing.text.html.HTML;
import javax.xml.soap.Text;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * The root scene for most of SNIPPY.
 * Created by Ian on 2/18/2017.
 */
public class MainScene extends StageScene {
    private MainScene scene;

    private JFXHamburger menuButton;
    private HamburgerBasicCloseTransition closeTransition;

    private JFXListView<Parent> sideSnips;

    public ArrayList<Snip> userSnips;
    public static Snip displayedSnip;
    public static Snip selectedSideSnip;
    public static String tagToDelete;
    public static boolean searching = false;

    public MainScene(Stage primaryStage) {
        super(primaryStage);
    }

    @Override
    public Parent inflateLayout() {
        return UXUtils.inflate("assets/layouts/layout_base.fxml");
    }

    @Override
    public void onCreate() {
        scene = this;

        //First we want to load the rest of the main scene
        Pane contentRoot = (Pane) lookup("#base_content");
        contentRoot.getChildren().add(UXUtils.inflate("assets/layouts/main_content.fxml"));

        //Then we add some listener junk
        menuButton = (JFXHamburger) this.lookup("#base_menu");
        closeTransition = new HamburgerBasicCloseTransition(menuButton);
        closeTransition.setRate(-3);
        menuButton.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            closeTransition.setRate(closeTransition.getRate() * -1);
            closeTransition.play();
        });

        //Now we setup events for dialogs
        StackPane overlay = (StackPane) this.lookup("#base_stack");

        //Instantiating the Language Combobox
        JFXComboBox languageDropdown = ((JFXComboBox) lookup("#main_language"));
        ArrayList<String> languageOptions = new ArrayList<String>();
        for (Language l : Language.values()) {
            languageOptions.add(l.NAME);
        }
        languageDropdown.getItems().addAll(languageOptions);

        JFXButton deleteButton = (JFXButton) this.lookup("#main_delete");
        deleteButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                JFXDialog temp = DeleteDialog.createAndShow(scene, overlay, displayedSnip.getID());
            }
        });

        //Searching
        JFXTextField searchBar = (JFXTextField) lookup("#base_searchbar");
        searchBar.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    searchSnips(searchBar);
                }
            }
        });
        JFXButton clearSearch = (JFXButton) lookup("#base_searchclear");
        clearSearch.setOnAction(event -> clearSearch());

        //Setup sharing
        setupShare();

        //Create a new snip
        JFXButton newButton = (JFXButton) lookup("#base_new");
        newButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                createNewSnip();
            }
        });
        //Save an edited snip
        JFXButton saveButton = (JFXButton) lookup("#main_save");
        saveButton.setOnAction(event -> editSnip());

        //show all snips
        update();

        //Select a snip from sidebar
        sideSnips.setOnMouseClicked(event -> displaySelectedSideSnip());
    }

    public void update() {
        clearDisplayedSnip();
        getUserSnips();
        displayMainSnip();
        displaySideSnips();
    }

    /*
        Gets all snips created by the user first. Then ads the snips of the groups they are in.
     */
    private void getUserSnips() {
        if (userSnips != null && userSnips.size() > 0) {
            userSnips.clear();
        }

        userSnips = SQLUtils.getUserSnips(LoginScene.currentUser.getUserId());
        ArrayList<Group> userGroups = SQLUtils.getUserGroups(LoginScene.currentUser.getUserId());
        for (Group g : userGroups) {
            userSnips.addAll(SQLUtils.getGroupSnips(LoginScene.currentUser.getUserId(), g.getGroupID()));
        }
    }

    private void clearSearch() {
        if (userSnips.size() >= 2 && searching) {
            searching = false;
            sideSnips.getItems().clear();
            displaySideSnips();
        }
        JFXTextField searchBar = (JFXTextField) lookup("#base_searchbar");
        searchBar.clear();
    }

    private void searchSnips(JFXTextField searchBar) {
        searching = true;
        String search = searchBar.getText();
        ArrayList<Snip> searchedSnips = new ArrayList<Snip>();
        for (Snip s : userSnips) {
            if (s.getTitle().contains(search)) {
                searchedSnips.add(s);
            }
        }
        updateSideSnips(searchedSnips);
    }

    private void displaySelectedSideSnip() {
        clearDisplayedSnip();
        enableShareDel();
        MenuButton share = (MenuButton) lookup("#main_share");
        share.setStyle("-fx-background-color: #44aaff");
        share.setDisable(false);

        ((JFXTextField) lookup("#main_title")).setText(selectedSideSnip.getTitle());
        ((TextArea) lookup("#main_code")).setText(selectedSideSnip.getCodeSnippet());
        ((JFXComboBox) lookup("#main_language")).getSelectionModel().select(selectedSideSnip.getLanguage());
        int index = sideSnips.getSelectionModel().getSelectedIndex();
        if (!userSnips.contains(displayedSnip) && (userSnips.size() != 0 || displayedSnip != null)) {
            updateSideSnips(displayedSnip);
            userSnips.add(displayedSnip);
        }
        if (displayedSnip.getTags()!=null) {
            Pane tagList = (Pane) lookup("#main_taglist");
            for (String tag : displayedSnip.getTags()) {
                tagList.getChildren().add(new TagListData().toNode(tag));
            }
        }
        displayedSnip = selectedSideSnip;
    }

    private void displayMainSnip() {
        if (userSnips != null && userSnips.size() != 0) {
            displayedSnip = userSnips.get(0);
            ((JFXTextField) lookup("#main_title")).setText(displayedSnip.getTitle());
            ((TextArea) lookup("#main_code")).setText(displayedSnip.getCodeSnippet());
            ((JFXComboBox) lookup("#main_language")).getSelectionModel().select(displayedSnip.getLanguage());
            Pane tagList = (Pane) lookup("#main_taglist");
            if (displayedSnip.getTags()!=null) {
                for (String tag : displayedSnip.getTags()) {
                    tagList.getChildren().add(new TagListData().toNode(tag));
                }
            }
        } else {
            displayedSnip = null;
            JFXButton newButton = (JFXButton) lookup("#base_new");
            createNewSnip();
        }
    }

    private void displaySideSnips() {
        sideSnips = (JFXListView<Parent>) lookup("#base_selections");
        sideSnips.getItems().clear();
        for (Snip s : userSnips) {
            sideSnips.getItems().add(new SnipListData().toNode(s));
        }
    }

    private void updateSideSnips(Snip s) {
        sideSnips.getItems().add(0, new SnipListData().toNode(s));
    }

    private void updateSideSnips(ArrayList<Snip> snips) {
        sideSnips.getItems().clear();
        for (Snip s : snips) {
            sideSnips.getItems().add(new SnipListData().toNode(s));
        }
    }

    private void editSnip() {
        ArrayList<String> tags = displayedSnip.getTags();

        JFXButton newButton = (JFXButton) lookup("#base_new");
        newButton.setOnAction(event -> createNewSnip());
        if (displayedSnip == null) {
            return;
        }

        String newTitle = ((JFXTextField) lookup("#main_title")).getText();
        String newCode = ((TextArea) lookup("#main_code")).getText();
        String newLanguage = ((JFXComboBox) lookup("#main_language")).getSelectionModel().getSelectedItem().toString();

        boolean readyToEdit = true;

        JFXTextField tagTextBox = (JFXTextField) lookup("#main_addtag");
        tagTextBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    String tagName = tagTextBox.getText();
                    if (!(tagName != null && !tagName.equals("")) || tags.contains(tagName)) {
                        return;
                    }
                    tagTextBox.clear();
                    Pane tagList = (Pane) lookup("#main_taglist");
                    //System.out.println(tagList.getChildren().toString());
                    tags.add(tagName);

                    Parent node = new TagListData().toNode(tagName);
                    node.setLayoutX(node.getLayoutX() + offset);

                    //TODO: FIND BETTER OFFSET
                    offset = offset + (node.toString().length() * 1.25) + 3;

                    tagList.getChildren().add(node);
                    System.out.println(tags);
                    tagList.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            tags.remove(tagToDelete);
                            tagList.getChildren().remove(tagToDelete);
                        }
                    });
                }
            }
        });

        if (!(newTitle != null && !newTitle.equals(""))) {
            ((JFXTextField) lookup("#main_title")).setStyle("-fx-prompt-text-fill: rgba(255, 0, 0, 1)");
            readyToEdit = false;
        }
        if (!(newCode != null && !newCode.equals(""))) {
            ((TextArea) lookup("#main_code")).setStyle("-fx-prompt-text-fill: rgba(255, 0, 0, 1)");
            readyToEdit = false;
        }

        if ((!newTitle.equals(displayedSnip.getTitle()) || !newCode.equals(displayedSnip.getCodeSnippet()) || !newLanguage.equals(displayedSnip.getLanguage()) || !tags.equals(displayedSnip.getTags())) && readyToEdit) {
            ((JFXTextField) lookup("#main_title")).setStyle("-fx-prompt-text-fill: rgba(0, 0, 0, 1)");
            ((TextArea) lookup("#main_code")).setStyle("-fx-prompt-text-fill: rgba(0, 0, 0, 1)");
            displayedSnip.setTitle(newTitle);
            displayedSnip.setLanguage(newLanguage);
            displayedSnip.setCodeSnippet(newCode);
            SQLUtils.editSnip(displayedSnip.getID(), newTitle, tags, newLanguage, newCode);
        }

        update();
    }

    public void clearDisplayedSnip() {
        if (userSnips != null) {
            if (!userSnips.contains(displayedSnip) && (userSnips.size() != 0 || displayedSnip != null)) {
                if (!searching) {
                    updateSideSnips(displayedSnip);
                }
                userSnips.add(displayedSnip);
            }
        }
        //Clear title and add prompt text
        JFXTextField snipTitle = ((JFXTextField) lookup("#main_title"));
        snipTitle.setText(null);
        snipTitle.setPromptText("Enter Snip Title");

        //Clear tags

        //Clear code area and add prompt test
        TextArea codeText = ((TextArea) lookup("#main_code"));
        codeText.setText(null);
        codeText.setPromptText("Enter your code here.");

        //Clear dropdown and add language options
        JFXComboBox languageDropdown = ((JFXComboBox) lookup("#main_language"));
        languageDropdown.getSelectionModel().select(0);

        //Clear tag textbox and list
        JFXTextField tagTextbox = (JFXTextField) lookup("#main_addtag");
        tagTextbox.clear();
        Pane tagList = (Pane) lookup("#main_taglist");
        for (int i=1; i<tagList.getChildren().size();){
            tagList.getChildren().remove(i);
        }
    }

    private double offset = 0;

    private void createNewSnip() {
        offset = 0;
        ArrayList<String> tags = new ArrayList<String>();
        System.out.println(tags);
        disableShareDel();
        clearDisplayedSnip();

        JFXButton newButton = (JFXButton) lookup("#base_new");
        newButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ((JFXTextField) lookup("#main_title")).setStyle("-fx-prompt-text-fill: rgba(0, 0, 0, 1)");
                ((TextArea) lookup("#main_code")).setStyle("-fx-prompt-text-fill: rgba(0, 0, 0, 1)");
                createNewSnip();
            }
        });

        JFXTextField tagTextBox = (JFXTextField) lookup("#main_addtag");
        tagTextBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    String tagName = tagTextBox.getText();
                    if (!(tagName != null && !tagName.equals("")) || tags.contains(tagName)) {
                        return;
                    }
                    tagTextBox.clear();
                    Pane tagList = (Pane) lookup("#main_taglist");
                    //System.out.println(tagList.getChildren().toString());
                    tags.add(tagName);

                    Parent node = new TagListData().toNode(tagName);
                    node.setLayoutX(node.getLayoutX() + offset);

                    //TODO: FIND BETTER OFFSET
                    offset = offset + (node.toString().length() * 1.25) + 3;

                    tagList.getChildren().add(node);
                    System.out.println(tags);
                    tagList.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            tags.remove(tagToDelete);
                            tagList.getChildren().remove(tagToDelete);
                        }
                    });
                }
            }
        });

        JFXButton saveButton = (JFXButton) lookup("#main_save");
        saveButton.setOnAction(new EventHandler<ActionEvent>() { // on click
            @Override
            public void handle(ActionEvent event) {
                String snipTitle = ((JFXTextField) lookup("#main_title")).getText();
                String snipCode = ((TextArea) lookup("#main_code")).getText();
                String snipLanguage = ((JFXComboBox) lookup("#main_language")).getSelectionModel().getSelectedItem().toString();
                boolean readyToCreate = true;

                if (!(snipTitle != null && !snipTitle.equals(""))) {
                    ((JFXTextField) lookup("#main_title")).setStyle("-fx-prompt-text-fill: rgba(255, 0, 0, 1)");
                    readyToCreate = false;
                }
                if (!(snipCode != null && !snipCode.equals(""))) {
                    ((TextArea) lookup("#main_code")).setStyle("-fx-prompt-text-fill: rgba(255, 0, 0, 1)");
                    readyToCreate = false;
                }
                if (readyToCreate) {
                    ((JFXTextField) lookup("#main_title")).setStyle("-fx-prompt-text-fill: rgba(0, 0, 0, 1)");
                    ((TextArea) lookup("#main_code")).setStyle("-fx-prompt-text-fill: rgba(0, 0, 0, 1)");
                    int snipID = SQLUtils.createSnip(LoginScene.currentUser.getUserId(), snipTitle, tags, snipLanguage, snipCode);
                    displayedSnip = new Snip(snipID, LoginScene.currentUser.getUserId(), snipTitle, tags, snipLanguage, snipCode);
                    System.out.println(displayedSnip);
                    MenuButton share = (MenuButton) lookup("#main_share");
                    share.setStyle("-fx-background-color: #44aaff");
                    share.setDisable(false);
                    saveButton.setOnAction(edit -> editSnip());
                    enableShareDel();
                    update();
                }
            }
        });
    }

    private void setupShare() {
        try {
            MenuButton share = (MenuButton) lookup("#main_share");
            if (displayedSnip.getID() != -1) {
                User user = LoginScene.currentUser;
                ArrayList<Group> usersGroups = SQLUtils.getUserGroups(user.getUserId());

                if (usersGroups.size() == 0) {
                    share.getItems().add(new MenuItem("Do not Share"));
                } else {
                    ArrayList<MenuItem> items = new ArrayList<MenuItem>();
                    share.getItems().add(new MenuItem("Do not Share"));
                    for (int i = 0; i < usersGroups.size(); i++) {
                        String tmp = usersGroups.get(i).getName();
                        MenuItem item = new MenuItem(tmp);
                        int id = usersGroups.get(i).getGroupID();
                        item.setOnAction(event -> {
                            share(id);
                        });
                        items.add(item);
                    }
                    share.getItems().addAll(items);
                }
            }
        } catch (Exception e) {
            MenuButton share = (MenuButton) lookup("#main_share");
            share.getItems().add(new MenuItem("Do not Share"));
        }
    }

    private void share(int id) {
        SQLUtils.shareSnip(displayedSnip.getID(), id);
    }

    private void enableShareDel() {
        JFXButton del = (JFXButton) lookup("#main_delete");
        MenuButton share = (MenuButton) lookup("#main_share");

        del.setDisable(false);
        share.setDisable(false);
    }

    private void disableShareDel() {
        JFXButton del = (JFXButton) lookup("#main_delete");
        MenuButton share = (MenuButton) lookup("#main_share");

        del.setDisable(true);
        share.setDisable(true);
    }

    @Override
    public void onDispose() {
    }
}


class DeleteDialog extends JFXDialog {
    private static JFXDialog dialog;
    private final MainScene scene;
    private final StackPane stackPane;
    private final int snipID;
    private JFXDialogLayout layout;
    private JFXButton cancelButton, confirmButton;

    private DeleteDialog(MainScene scene, StackPane stackPane, int snipID) {
        this.stackPane = stackPane;
        this.snipID = snipID;
        this.layout = new JFXDialogLayout();
        this.scene = scene;
        create();
    }

    private final void create() {
        cancelButton = new JFXButton(R.strings("common.cancel"));
        confirmButton = new JFXButton(R.strings("delete.confirm"));
        confirmButton.setStyle("-fx-text-fill: WHITE; -fx-background-color: RED;");

        layout.setHeading(new Label(R.strings("delete.header")));
        layout.setBody(new Label(R.strings("delete.body")));
        layout.setActions(cancelButton, confirmButton);

        cancelButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                dialog.close();
            }
        });

        confirmButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                SQLUtils.removeSnip(snipID);
                dialog.close();
                scene.clearDisplayedSnip();
                scene.update();
            }
        });

        this.setTransitionType(DialogTransition.CENTER);
        this.setContent(layout);
    }

    public static final JFXDialog createAndShow(MainScene s, StackPane sp, int id) {
        JFXDialog d = new DeleteDialog(s, sp, id);
        dialog = d;

        d.show(sp);
        return d;
    }
}
