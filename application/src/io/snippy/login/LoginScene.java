package io.snippy.login;

import com.jfoenix.controls.JFXButton;
import io.snippy.core.StageScene;
import io.snippy.main.MainScene;
import io.snippy.recover.RecoverEmailScene;
import io.snippy.register.RegisterScene;
import io.snippy.util.UXUtils;
import javafx.scene.Parent;
import javafx.stage.Stage;


/**
 * The scene representing the user login prompt.
 * Created by Ian on 2/15/2017.
 */
public class LoginScene extends StageScene {

	public LoginScene( Stage stage ) {
		super( stage );
	}

	@Override
	public Parent inflateLayout() {
		return UXUtils.inflate( "assets/layouts/layout_login.fxml" );
	}

	@Override
	public void onCreate() {
		JFXButton loginButton = (JFXButton) lookup( "#login_submit" );
		loginButton.setOnAction( event -> switchScreen( MainScene.class ) );
		JFXButton registerButton = (JFXButton) lookup( "#login_register" );
		registerButton.setOnAction( event -> switchScreen( RegisterScene.class ) );
		JFXButton forgotButton = (JFXButton) lookup( "#login_forgot" );
		forgotButton.setOnAction( event -> switchScreen( RecoverEmailScene.class ) );
	}

	@Override
	public void onDispose() {

	}
}
