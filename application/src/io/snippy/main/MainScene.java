package io.snippy.main;

import com.jfoenix.controls.*;
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
import sun.rmi.runtime.Log;

import javax.xml.soap.Text;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * The root scene for most of SNIPPY.
 * Created by Ian on 2/18/2017.
 */
public class MainScene extends StageScene {

    private JFXHamburger menuButton;
    private HamburgerBasicCloseTransition closeTransition;

    private JFXListView<Parent> sideSnips;

    private ArrayList<Snip> userSnips;
    public static Snip displayedSnip;
    public static Snip selectedSideSnip;

    public MainScene(Stage primaryStage) {
        super(primaryStage);
    }

    @Override
    public Parent inflateLayout() {
        return UXUtils.inflate("assets/layouts/layout_base.fxml");
    }

    @Override
    public void onCreate() {
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
        JFXButton deleteButton = (JFXButton) this.lookup("#main_delete");
        String snipID = "<snip uuid goes here>";
        Logger.info("Creating deletion dialog for SnipID {}", snipID);
        deleteButton.setOnAction(event -> DeleteDialog.createAndShow(overlay, snipID));

        //Instantiating the Language Combobox
        JFXComboBox languageDropdown = ((JFXComboBox) lookup("#main_language"));
        ArrayList<String> languageOptions = new ArrayList<String>();
        for (Language l : Language.values()) {
            languageOptions.add(l.NAME);
        }
        languageDropdown.getItems().addAll(languageOptions);

        //Creating some dummy snips for testing, will delete later
        for (int i = 0; i <= 100; i++) {
            //userSnips.add(new Snip("Snip"+i, "test", "Python"));
            //SQLUtils.createSnip(LoginScene.currentUser.getUserId(), "Snip"+i, "Python",  "test");
        }

		getUserSnips();
        displayMainSnip();
        displaySideSnips();

        //Create a new snip
        MenuButton share = (MenuButton) this.lookup("#main_share");
        setupShare();
        JFXButton newButton = (JFXButton) lookup("#base_new");
        newButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                share.setStyle("-fx-background-color: #A9A9A9");
                share.setDisable(true);
                createNewSnip();
            }
        });
        //Save an edited snip
        JFXButton saveButton = (JFXButton) lookup("#main_save");
        saveButton.setOnAction(event -> editSnip());

        //Select a snip from sidebar
        sideSnips.setOnMouseClicked(event -> displaySelectedSideSnip());
    }
	/*
		Gets all snips created by the user first. Then ads the snips of the groups they are in.
	 */
    public void getUserSnips(){
		userSnips = SQLUtils.getUserSnips(LoginScene.currentUser.getUserId());
		ArrayList<Group> userGroups = SQLUtils.getUserGroups(LoginScene.currentUser.getUserId());
		for (Group g : userGroups){
			userSnips.addAll(SQLUtils.getGroupSnips(LoginScene.currentUser.getUserId(), g.getGroupID()));
		}
	}


    public void displaySelectedSideSnip() {
        MenuButton share = (MenuButton) lookup("#main_share");
        share.setStyle("-fx-background-color: #44aaff");
        share.setDisable(false);
        
        ((JFXTextField) lookup("#main_title")).setText(selectedSideSnip.getTitle());
        ((TextArea) lookup("#main_code")).setText(selectedSideSnip.getCodeSnippet());
        ((JFXComboBox) lookup("#main_language")).getSelectionModel().select(selectedSideSnip.getLanguage());
        int index = sideSnips.getSelectionModel().getSelectedIndex();
        if (!userSnips.contains(displayedSnip) && (userSnips.size()!=0 || displayedSnip != null)) {
            updateSideSnips(displayedSnip);
            userSnips.add(displayedSnip);
        }
        displayedSnip = selectedSideSnip;
    }

    public void displayMainSnip() {
    	if (userSnips.size() != 0) {
			displayedSnip = userSnips.get(0);
			userSnips.remove(0);
			((JFXTextField) lookup("#main_title")).setText(displayedSnip.getTitle());
			((TextArea) lookup("#main_code")).setText(displayedSnip.getCodeSnippet());
			((JFXComboBox) lookup("#main_language")).getSelectionModel().select(displayedSnip.getLanguage());
		}
		else{
    		displayedSnip = null;
		}
    }

    public void displaySideSnips() {
        sideSnips = (JFXListView<Parent>) lookup("#base_selections");
        for (Snip s : userSnips) {
            sideSnips.getItems().add(new SnipListData().toNode(s));
        }
    }

    public void updateSideSnips(Snip s) {
        sideSnips.getItems().add(0, new SnipListData().toNode(s));
    }

    public void editSnip() {

		String newTitle = ((JFXTextField) lookup("#main_title")).getText();
		String newCode = ((TextArea) lookup("#main_code")).getText();
		String newLanguage = ((JFXComboBox) lookup("#main_language")).getSelectionModel().getSelectedItem().toString();

		if (!newTitle.equals(displayedSnip.getTitle()) || !newCode.equals(displayedSnip.getCodeSnippet()) || !newLanguage.equals(displayedSnip.getLanguage())){
			System.out.println("editing");
			displayedSnip.setTitle(newTitle);
			displayedSnip.setLanguage(newLanguage);
			displayedSnip.setCodeSnippet(newCode);
			SQLUtils.editSnip(displayedSnip.getID(), newTitle, null, newLanguage, newCode);
		}
		else{
			System.out.println("not editing");
		}

	}

    public void clearDisplayedSnip() {

		if (!userSnips.contains(displayedSnip) && (userSnips.size()!=0 || displayedSnip != null)) {
			System.out.println("test");
			updateSideSnips(displayedSnip);
			userSnips.add(displayedSnip);
		}
        //Clear title and add prompt text
        JFXTextField snipTitle = ((JFXTextField) lookup("#main_title"));
        snipTitle.setText(null);
        snipTitle.setPromptText("Enter Snip Title");

        //Clear code area and add prompt test
        TextArea codeText = ((TextArea) lookup("#main_code"));
        codeText.setText(null);
        codeText.setPromptText("Enter your code here.");

        //Clear dropdown and add language options
        JFXComboBox languageDropdown = ((JFXComboBox) lookup("#main_language"));
        languageDropdown.getSelectionModel().select(0);
    }

    public void createNewSnip() {

        clearDisplayedSnip();

        JFXButton newButton = (JFXButton) lookup("#base_new");
        newButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clearDisplayedSnip();
            }
        });

        JFXTextField tagTextbox = (JFXTextField) lookup("#main_addtag");
        tagTextbox.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    String tagName = tagTextbox.getText();
                    if (!(tagName != null && !tagName.equals(""))) {
                        return;
                    }
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
                    SQLUtils.createSnip(LoginScene.currentUser.getUserId(), snipTitle,snipLanguage, snipCode);
                    displayedSnip = new Snip(snipTitle, snipCode, snipLanguage);
                    MenuButton share = (MenuButton) lookup("#main_share");
                    share.setStyle("-fx-background-color: #44aaff");
                    share.setDisable(false);
					saveButton.setOnAction(edit -> editSnip());
                }
            }
        });
    }

    public void setupShare() {
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
                        item.setOnAction(event -> {
                            share(tmp);
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

    public void share(String val) {
        int id = Integer.parseInt(val.substring(0, val.indexOf("|")));

        SQLUtils.shareSnip(displayedSnip.getID(), id);
    }

    @Override
    public void onDispose() {
    }
}


class DeleteDialog extends JFXDialog {

    private final StackPane stackPane;
    private final String snipID;
    private JFXDialogLayout layout;
    private JFXButton cancelButton, confirmButton;

    private DeleteDialog(StackPane stackPane, String snipID) {
        this.stackPane = stackPane;
        this.snipID = snipID;
        this.layout = new JFXDialogLayout();
        create();
    }

    private final void create() {
        cancelButton = new JFXButton(R.strings("common.cancel"));
        confirmButton = new JFXButton(R.strings("delete.confirm"));
        confirmButton.setStyle("-fx-text-fill: WHITE; -fx-background-color: RED;");

        layout.setHeading(new Label(R.strings("delete.header")));
        layout.setBody(new Label(R.strings("delete.body")));
        layout.setActions(cancelButton, confirmButton);

        this.setTransitionType(DialogTransition.CENTER);
        this.setContent(layout);
    }

    public static final JFXDialog createAndShow(StackPane sp, String id) {
        JFXDialog d = new DeleteDialog(sp, id);
        d.show(sp);
        return d;
    }
}
