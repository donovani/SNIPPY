package io.snippy.register;

import com.jfoenix.controls.JFXButton;
import io.snippy.core.StageScene;
import io.snippy.login.LoginScene;
import io.snippy.util.UXUtils;
import javafx.scene.Parent;
import javafx.stage.Stage;


/**
 * Created by Ian on 2/18/2017.
 */
public class RegisterScene extends StageScene {

	public RegisterScene( Stage primaryStage ) {
		super( primaryStage );
	}

	@Override
	public Parent inflateLayout() {
		return UXUtils.inflate( "assets/layouts/layout_register.fxml" );
	}

	@Override
	public void onCreate() {
		JFXButton loginButton = (JFXButton) lookup( "#register_cancel" );
		loginButton.setOnAction( event -> switchScreen( LoginScene.class ) );
	}

	@Override
	public void onDispose() {

	}
}
