package io.snippy.core;

import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.pmw.tinylog.Logger;

import java.lang.reflect.Constructor;


/**
 * An extension to the JavaFX Scene class to allow passing/control of the primary stage
 * Created by Ian on 2/17/2017.
 */
public abstract class StageScene extends Scene {

	private Stage primaryStage;

	public StageScene( Stage primaryStage ) {
		super( new Group() );
		this.primaryStage = primaryStage;
		((Group) this.getRoot()).getChildren().add( inflateLayout() );
	}


	//Used to instantiate the layout your scene is using
	public abstract Parent inflateLayout();

	//For general setup, collecting elements, & adding of listeners
	public abstract void onCreate();

	//Any code that should execute when you swap to a new scene
	public abstract void onDispose();



	public final <T extends StageScene> void switchScreen( Class<T> type ) {
		Constructor<?> struct;
		Object[] args;
		//First step when using reflection to do this is to get the constructor and arguments
		try {
			//Tries to find a standard constructor
			struct = type.getConstructor( Stage.class );
			args = new Object[]{ primaryStage };
		}
		catch ( NoSuchMethodException e ) {
			struct = type.getConstructors()[0];
			Class<?>[] params = struct.getParameterTypes();
			args = new Object[params.length];
			for (int i=0; i<args.length; i++) {
				if (params[i].equals( Stage.class )) {
					args[i] = primaryStage;
				}
			}
		}
		//After that, we just need to try to instantiate it
		try {
			T newScreen = (T) struct.newInstance( args );
			switchScreen( newScreen );
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	public final void switchScreen( StageScene scene ) {
		this.onDispose();
		((Group) this.getRoot()).getChildren().clear();
		primaryStage.setScene( scene );
		scene.onCreate();
		primaryStage.show();
		Logger.info( "Switching to scene {}", scene );
	}

	public final Stage getPrimaryStage() {
		return primaryStage;
	}
}
