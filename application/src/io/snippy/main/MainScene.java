package io.snippy.main;

import io.snippy.core.StageScene;
import io.snippy.util.UXUtils;
import javafx.scene.Parent;
import javafx.stage.Stage;


/**
 * The root scene for most of SNIPPY.
 * Created by Ian on 2/18/2017.
 */
public class MainScene extends StageScene {

	public MainScene( Stage primaryStage ) {
		super( primaryStage );
	}

	@Override
	public Parent inflateLayout() {
		return UXUtils.inflate( "assets/layout/main_base.fxml" );
	}

	@Override
	public void onCreate() {

	}

	@Override
	public void onDispose() {

	}
}
