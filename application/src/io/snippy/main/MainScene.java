package io.snippy.main;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import io.snippy.core.*;
import io.snippy.login.LoginScene;
import io.snippy.util.Language;
import io.snippy.util.SQLUtils;
import io.snippy.util.UXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;

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

        JFXButton teamsButton = (JFXButton) this.lookup("#base_teams");
        teamsButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                JFXDialog temp = TeamsDialog.createAndShow(scene, overlay);
            }
        });

        //TODO, change main_share to a JFXButton if you want the fancy dialog (works well now, just for future)
        /*
        JFXButton shareButton = (JFXButton) this.lookup( "#main_share");
        shareButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                JFXDialog temp = ShareDialog.createAndShow(scene, overlay);
            }
        });
*/
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

    /*
     * Method: update
     * Pre: None
     * Post: This method updates the main screen and the side bar. Used primarily to reset the screen.
     */
    public void update() {
        clearDisplayedSnip();
        getUserSnips();
        displayMainSnip();
        displaySideSnips();
    }

    /*
     * Method: getUserSnips
     * Pre: None
     * Post: Adds all snips that the user has access to to the *userSnips* ArrayList
     * - Adds the snips that the user has created first.
     * - Adds the snips that belong to groups the user is a member of.
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

    /*
     * Method: clearSearch
     * Pre: None
     * Post: Clears the search bar and dispalys all the snips in the *userSnips* ArrayList.
     */
    private void clearSearch() {
        if (userSnips.size() >= 2 && searching) {
            searching = false;
            sideSnips.getItems().clear();
            displaySideSnips();
        }
        JFXTextField searchBar = (JFXTextField) lookup("#base_searchbar");
        searchBar.clear();
    }

    /*
     * Method: searchSnips
     * Pre: None
     * Post: Searches through the *userSnips* ArrayList. This ArrayList contains all the snips that a user has access to.
     * Searches can match the following:
     *  - Snip titles or sub strings within a snip title
     *  - Tag names
     *  - Languages
     */
    private void searchSnips(JFXTextField searchBar) {
        searching = true;
        String search = searchBar.getText();
        ArrayList<Snip> searchedSnips = new ArrayList<Snip>();
        for (Snip s : userSnips) {
            if (s.getTitle().contains(search) || s.getTags().contains(search) || s.getLanguage().equalsIgnoreCase(search)) {
                searchedSnips.add(s);
            }
        }
        updateSideSnips(searchedSnips);
    }

    /*
     * Method: displaySelectedSideSnip
     * Pre: None
     * Post: Clears the currently displayed side snip and displays the snip that the user has selected in the side bar.
     *  - This will also add the currently displayed snip to the userSnips arraylist. This would only occur in situations where a snip
     *  created in the current session is being displayed for the first time. Will only happen once per new snip.
     */

    private void displaySelectedSideSnip() {
        editSnip();
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
        if (selectedSideSnip.getTags() != null) {
            Pane tagList = (Pane) lookup("#main_taglist");
            for (String tag : selectedSideSnip.getTags()) {
                tagList.getChildren().add(new TagListData().toNode(tag));
            }
        }
        displayedSnip = selectedSideSnip;
    }

    /*
     * Method: displayMainSnip
     * Pre: None
     * Post: This method displays the users most recently created snip that a user has created.
     *      - If the user does not have any snips created yet it will display the create new snip page.
     */

    private void displayMainSnip() {
        if (userSnips != null && userSnips.size() != 0) {
            displayedSnip = userSnips.get(0);
            ((JFXTextField) lookup("#main_title")).setText(displayedSnip.getTitle());
            ((TextArea) lookup("#main_code")).setText(displayedSnip.getCodeSnippet());
            ((JFXComboBox) lookup("#main_language")).getSelectionModel().select(displayedSnip.getLanguage());
            Pane tagList = (Pane) lookup("#main_taglist");
            JFXTextField tagTextBox = (JFXTextField) lookup("#main_addtag");
            tagTextBox.clear();
            if (displayedSnip.getTags() != null) {
                for (String tag : displayedSnip.getTags()) {
                    tagList.getChildren().add(new TagListData().toNode(tag));
                }
            }
            editSnip();
        } else {
            displayedSnip = null;
            JFXButton newButton = (JFXButton) lookup("#base_new");
            createNewSnip();
        }
    }

    /*
      This method will display all of the user snips along the side bar.
     */
    private void displaySideSnips() {
        sideSnips = (JFXListView<Parent>) lookup("#base_selections");
        sideSnips.getItems().clear();
        for (Snip s : userSnips) {
            sideSnips.getItems().add(new SnipListData().toNode(s));
        }
    }

    /*
      This method adds the given Snip *s* to the top of the side bar display. This is supposed to be the most recently created snip.
     */
    private void updateSideSnips(Snip s) {
        sideSnips.getItems().add(0, new SnipListData().toNode(s));
    }

    /*
      This method clears the currently displayed sidebar and displays the side snip with the passed *snips* ArrayList.
     */
    private void updateSideSnips(ArrayList<Snip> snips) {
        sideSnips.getItems().clear();
        for (Snip s : snips) {
            sideSnips.getItems().add(new SnipListData().toNode(s));
        }
    }

    /*
     * Method: editSNip
     * Pre: None
     * Post: This method handles editing an already created snip.
     *  - The method will validate that the title and code of the snip are not empty.
     *  - The method will update the displayedSnip object and update it in the database as well
     */
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

        JFXTextField tagTextBox = (JFXTextField) lookup("#main_addtag");
        tagTextBox.setFocusTraversable(false);
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
                    tags.add(tagName);

                    Parent node = new TagListData().toNode(tagName);
                    node.setLayoutX(node.getLayoutX() + offset);

                    //TODO: FIND BETTER OFFSET
                    offset = offset + (node.toString().length() * 1.25) + 3;

                    tagList.getChildren().add(node);
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

        boolean readyToEdit = true;
        //Checking for empty title
        if (!(newTitle != null && !newTitle.equals(""))) {
            ((JFXTextField) lookup("#main_title")).setStyle("-fx-prompt-text-fill: rgba(255, 0, 0, 1)");
            readyToEdit = false;
        }
        //Checking for empty code snippet
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
    }

    /*
     * Method: clearDisplayedSnip
     * Pre: None
     * Post: This method handles clearing the currently displayed snip. It will clear all textfields, dropdowns and tags.
     */
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
        tagTextbox.setText("");
        Pane tagList = (Pane) lookup("#main_taglist");
        for (int i = 1; i < tagList.getChildren().size(); ) {
            tagList.getChildren().remove(i);
        }
    }

    private double offset = 0;

    /*
     * Method: createNewSnip
     * Pre: None
     * Post: Creates a new snip that is added to the userSnips arraylist.
     *      - Snip title and snip code is required to create a new snip, all other fields are optional.
            - Appropriate error messages are displayed for empty fields that are required.
            - Tags are displayed as the user adds them
     */
    private void createNewSnip() {
        offset = 0;
        ArrayList<String> tags = new ArrayList<String>();
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
                    tags.add(tagName);

                    Parent node = new TagListData().toNode(tagName);
                    node.setLayoutX(node.getLayoutX() + offset);
                    node.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            tags.remove(tagToDelete);
                            tagList.getChildren().remove(node);
                        }
                    });

                    //TODO: FIND BETTER OFFSET
                    offset = offset + (node.toString().length() * 1.25) + 3;

                    tagList.getChildren().add(node);
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
                    /*MenuButton share = (MenuButton) lookup("#main_share");
                    share.setStyle("-fx-background-color: #44aaff");
                    share.setDisable(false);
                    enableShareDel();*/
                    saveButton.setOnAction(edit -> editSnip());
                    update();
                }
            }
        });
    }

    /*done
     * Method: setupShare()
     * Pre: takes in no value
     * Post: fills in the share button with groups the user is a part of
     */
    private void setupShare() {
        try {
            MenuButton share = (MenuButton) lookup("#main_share"); //grab the share button
            if (displayedSnip.getID() != -1) { //if there is a displayed snip
                User user = LoginScene.currentUser; //grab the current user
                ArrayList<Group> usersGroups = SQLUtils.getUserGroups(user.getUserId()); //get an arraylist of all the user's groups

                if (usersGroups.size() == 0) { //if the user doesnt have any
                    share.getItems().add(new MenuItem("Do not Share"));
                } else {
                    ArrayList<MenuItem> items = new ArrayList<MenuItem>();
                    share.getItems().add(new MenuItem("Do not Share"));

                    for (int i = 0; i < usersGroups.size(); i++) {// loop through user groups

                        String tmp = usersGroups.get(i).getName();
                        MenuItem item = new MenuItem(tmp);
                        int id = usersGroups.get(i).getGroupID();

                        item.setOnAction(event -> {
                            share(id);
                        }); //set a handler to share on click
                        items.add(item); //add to the group list
                    }
                    share.getItems().addAll(items);// add all to the menubutton
                }
            }
        } catch (Exception e) { //user has no groups
            MenuButton share = (MenuButton) lookup("#main_share");
            share.getItems().add(new MenuItem("Do not Share")); //set to default
        }
    }

    /*done
     * Method: share
     * Pre: takes in a groupID
     * Post: shares the displayed snip with the groupID
     */
    private void share(int id) {
        SQLUtils.shareSnip(displayedSnip.getID(), id);
    }

    /*done
     * Method: enableShareDel
     * Pre: takes in nothing
     * Post: enables the share and del buttons
     */
    private void enableShareDel() {
        JFXButton del = (JFXButton) lookup("#main_delete");
        MenuButton share = (MenuButton) lookup("#main_share");

        del.setDisable(false);
        share.setDisable(false);
    }

    /*done
     * Method: disableShareDel
     * Pre: takes in nothing
     * Post: disables the share and del buttons
     */
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