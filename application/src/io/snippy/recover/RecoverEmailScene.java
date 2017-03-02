package io.snippy.recover;

import com.jfoenix.controls.JFXButton;
import io.snippy.core.StageScene;
import io.snippy.login.LoginScene;
import io.snippy.util.UXUtils;
import javafx.scene.Parent;
import javafx.stage.Stage;


/**
 * The first recovery scene: entering account email
 * Created by Ian on 3/1/2017.
 */
public class RecoverEmailScene extends StageScene {

	public RecoverEmailScene( Stage primaryStage ) {
		super( primaryStage );
	}

	@Override
	public Parent inflateLayout() {
		return UXUtils.inflate( "assets/layouts/layout_recover1.fxml" );
	}

	@Override
	public void onCreate() {
		JFXButton cancelButton = (JFXButton) lookup( "#recover_cancel" );
		cancelButton.setOnAction( event -> switchScreen( LoginScene.class ) );
		JFXButton nextButton = (JFXButton) lookup( "#recover_next" );
		nextButton.setOnAction( event -> switchScreen( RecoverQuestionScene.class ) );
	}

	@Override
	public void onDispose() {

	}
}
