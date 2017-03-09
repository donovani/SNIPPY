package io.snippy.main;

import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import io.snippy.core.StageScene;
import io.snippy.util.UXUtils;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.beans.EventHandler;


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
		return UXUtils.inflate( "assets/layouts/base_layout.fxml" );
	}

	@Override
	public void onCreate() {
		menuButton = (JFXHamburger) this.lookup( "#base_menu" );
		closeTransition = new HamburgerBasicCloseTransition( menuButton );
		closeTransition.setRate( -3 );
		menuButton.addEventHandler( MouseEvent.MOUSE_PRESSED, (e)->{
			closeTransition.setRate(closeTransition.getRate() * -1);
			closeTransition.play();
		});
	}

	@Override
	public void onDispose() {

	}
}
