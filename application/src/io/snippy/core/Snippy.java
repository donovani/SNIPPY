package io.snippy.core;


import io.snippy.login.LoginScene;
import javafx.application.Application;
import javafx.stage.Stage;


/**
 * The main class to initialize the app
 * Created by Ian on 2/14/2017.
 */
public class Snippy extends Application {

	public static void main(String[] args) {
		Application.launch( Snippy.class );
	}

	@Override
	public void start( Stage stage ) throws Exception {
		stage.setTitle("SNIPPY");
		StageScene start = new LoginScene( stage );
		stage.setScene( start );
		start.onCreate();
		stage.show();
	}
}
