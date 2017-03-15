package io.snippy.main;

import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import io.snippy.core.StageScene;
import io.snippy.util.UXUtils;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


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
		JFXListView< SnipListItem > snips = (JFXListView< SnipListItem >) lookup( "#base_selections" );
	}

	@Override
	public void onDispose() {

	}
}
