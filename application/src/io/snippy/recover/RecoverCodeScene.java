package io.snippy.recover;

import com.jfoenix.controls.JFXButton;
import io.snippy.core.StageScene;
import io.snippy.login.LoginScene;
import io.snippy.util.UXUtils;
import javafx.scene.Parent;
import javafx.stage.Stage;


/**
 * The third recovery scene: entering emailed recovery code
 * Created by Ian on 3/2/2017.
 */
public class RecoverCodeScene extends StageScene {

	public RecoverCodeScene( Stage primaryStage ) {
		super( primaryStage );
	}

	@Override
	public Parent inflateLayout() {
		return UXUtils.inflate( "assets/layouts/layout_recover3.fxml" );
	}

	@Override
	public void onCreate() {
		JFXButton cancelButton = (JFXButton) lookup( "#recover_cancel" );
		cancelButton.setOnAction( event -> switchScreen( LoginScene.class ) );
		JFXButton nextButton = (JFXButton) lookup( "#recover_next" );
		nextButton.setOnAction( event -> switchScreen( RecoverResetScene.class ) );
	}

	@Override
	public void onDispose() {

	}
}
