package io.snippy.main;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import io.snippy.core.R;
import io.snippy.core.StageScene;
import io.snippy.util.UXUtils;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.pmw.tinylog.Logger;


/**
 * The root scene for most of SNIPPY.
 * Created by Ian on 2/18/2017.
 */
public class MainScene extends StageScene {

	private JFXHamburger menuButton;
	private HamburgerBasicCloseTransition closeTransition;

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

		StackPane overlay = (StackPane) this.lookup( "#base_stack" );
		JFXButton deleteButton = (JFXButton) this.lookup( "#main_delete" );
		String snipID = "<snip uuid goes here>";
		Logger.info( "Creating deletion dialog for SnipID {}", snipID);
		deleteButton.setOnAction( event -> DeleteDialog.createAndShow( overlay, snipID ) );

		//Lastly we load the data of the app
		JFXListView< SnipListItem > snips = (JFXListView< SnipListItem >) lookup( "#base_selections" );
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
		JFXDialog d = new DeleteDialog( sp, id );
		d.show( sp );
		return d;
	}
}
