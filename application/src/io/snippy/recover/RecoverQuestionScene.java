package io.snippy.recover;

import com.jfoenix.controls.JFXButton;
import io.snippy.core.StageScene;
import io.snippy.login.LoginScene;
import io.snippy.util.UXUtils;
import javafx.scene.Parent;
import javafx.stage.Stage;


/**
 * The second recovery scene: answering security question
 * Created by Ian on 3/1/2017.
 */
public class RecoverQuestionScene extends StageScene {

	public RecoverQuestionScene( Stage primaryStage ) {
		super( primaryStage );
	}

	@Override
	public Parent inflateLayout() {
		return UXUtils.inflate( "assets/layouts/layout_recover2.fxml" );
	}

	@Override
	public void onCreate() {
		JFXButton cancelButton = (JFXButton) lookup( "#recover_cancel" );
		cancelButton.setOnAction( event -> switchScreen( LoginScene.class ) );
		JFXButton nextButton = (JFXButton) lookup( "#recover_next" );
		nextButton.setOnAction( event -> switchScreen( RecoverCodeScene.class ) );
	}

	@Override
	public void onDispose() {

	}
}
