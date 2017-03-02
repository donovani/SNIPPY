package io.snippy.recover;

import com.jfoenix.controls.JFXButton;
import io.snippy.core.StageScene;
import io.snippy.login.LoginScene;
import io.snippy.util.UXUtils;
import javafx.scene.Parent;
import javafx.stage.Stage;


/**
 * The fourth recovery scene: resetting your password
 * Created by Ian on 3/2/2017.
 */
public class RecoverResetScene extends StageScene {

	public RecoverResetScene( Stage primaryStage ) {
		super( primaryStage );
	}

	@Override
	public Parent inflateLayout() {
		return UXUtils.inflate( "assets/layouts/layout_recover4.fxml" );
	}

	@Override
	public void onCreate() {
		JFXButton cancelButton = (JFXButton) lookup( "#recover_cancel" );
		cancelButton.setOnAction( event -> switchScreen( LoginScene.class ) );
		JFXButton submitButton = (JFXButton) lookup( "#recover_submit" );
		submitButton.setOnAction( event -> switchScreen( LoginScene.class ) );
	}

	@Override
	public void onDispose() {

	}
}
