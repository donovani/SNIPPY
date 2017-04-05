package io.snippy.main;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import io.snippy.core.R;
import io.snippy.core.StageScene;
import io.snippy.util.Language;
import io.snippy.util.UXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.pmw.tinylog.Logger;

import java.util.ArrayList;


/**
 * The root scene for most of SNIPPY.
 * Created by Ian on 2/18/2017.
 */
public class MainScene extends StageScene {

	private JFXHamburger menuButton;
	private HamburgerBasicCloseTransition closeTransition;

	private JFXListView<Parent> snips;

	public MainScene( Stage primaryStage ) {
		super( primaryStage );
	}

	@Override
	public Parent inflateLayout() {
		return UXUtils.inflate( "assets/layouts/layout_base.fxml" );
	}

	@Override
	public void onCreate() {
		//First we want to load the rest of the main scene
		Pane contentRoot = (Pane) lookup( "#base_content" );
		contentRoot.getChildren().add( UXUtils.inflate( "assets/layouts/main_content.fxml" ) );

		//Then we add some listener junk
		menuButton = (JFXHamburger) this.lookup( "#base_menu" );
		closeTransition = new HamburgerBasicCloseTransition( menuButton );
		closeTransition.setRate( -3 );
		menuButton.addEventHandler( MouseEvent.MOUSE_PRESSED, (e)->{
			closeTransition.setRate(closeTransition.getRate() * -1);
			closeTransition.play();
		});

		//Now we setup events for dialogs
		StackPane overlay = (StackPane) this.lookup( "#base_stack" );
		JFXButton deleteButton = (JFXButton) this.lookup( "#main_delete" );
		String snipID = "<snip uuid goes here>";
		Logger.info( "Creating deletion dialog for SnipID {}", snipID);
		deleteButton.setOnAction( event -> DeleteDialog.createAndShow( overlay, snipID ) );

		//Instantiating the Lnaguage Combobox
		JFXComboBox languageDropdown = ((JFXComboBox) lookup("#main_language"));
		ArrayList<String> languageOptions = new ArrayList<String>();
		for (Language l : Language.values()){
			languageOptions.add(l.NAME);
		}
		languageDropdown.getItems().addAll(languageOptions);

		//Lastly we load the data of the app
		//TODO: Here's where you load info about snips.
		snips = (JFXListView< Parent >) lookup( "#base_selections" );
		for (int i=0; i<50; i++)
			snips.getItems().add(new SnipListData().toNode());

		JFXButton newButton = (JFXButton) lookup("#base_new");
		newButton.setOnAction(event -> createSnip());

		JFXButton teamsButton = (JFXButton) lookup ("#base_teams");
		teamsButton.setOnAction(event -> manageTeams());
	}

	public void manageTeams(){
		//Implement
	}
	public void createSnip() {
		//Clear title and add prompt text
		JFXTextField snipTitle = ((JFXTextField) lookup("#main_title"));
		snipTitle.setText(null);
		snipTitle.setPromptText("Enter Snip Title");

		//Clear code area and add prompt text
		TextArea codeText = ((TextArea) lookup("#main_code"));
		codeText.setText(null);
		codeText.setPromptText("Enter your code here.");

		//Clear dropdown and add language options
		JFXComboBox languageDropdown = ((JFXComboBox) lookup("#main_language"));
		languageDropdown.getSelectionModel().select(0);


		JFXButton saveButton = (JFXButton) lookup("#main_save");
		saveButton.setOnAction(new EventHandler<ActionEvent>() { // on click
			@Override
			public void handle(ActionEvent event) {

				String snipTitle = ((JFXTextField) lookup("#main_title")).getText();
				String snipCode = ((TextArea) lookup ("#main_code")).getText();
				String language = ((JFXComboBox) lookup("#main_language")).getSelectionModel().getSelectedItem().toString();


				if (snipTitle != null && !snipTitle.equals("")) {

				}
				else{
					((JFXTextField) lookup("#main_title")).setStyle("-fx-prompt-text-fill: rgba(255, 0, 0, 1)");
				}
				if (snipCode != null && !snipCode.equals("")) {

				}
				else{
					((TextArea) lookup("#main_code")).setStyle("-fx-prompt-text-fill: rgba(255, 0, 0, 1)");
				}

			}
		});

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

	private DeleteDialog( StackPane stackPane, String snipID ) {
		this.stackPane = stackPane;
		this.snipID = snipID;
		this.layout = new JFXDialogLayout();
		create( );
	}

	private final void create( ) {
		cancelButton = new JFXButton( R.strings( "common.cancel" ) );
		confirmButton = new JFXButton( R.strings( "delete.confirm" ) );
		confirmButton.setStyle( "-fx-text-fill: WHITE; -fx-background-color: RED;" );

		layout.setHeading( new Label( R.strings( "delete.header" ) ) );
		layout.setBody( new Label( R.strings( "delete.body" ) ) );
		layout.setActions( cancelButton, confirmButton );

		this.setTransitionType( DialogTransition.CENTER );
		this.setContent( layout );
	}

	public static final JFXDialog createAndShow( StackPane sp, String id ) {
		JFXDialog d = new DeleteDialog(sp, id);
		d.show(sp);
		return d;
	}
}
