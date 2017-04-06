package io.snippy.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import java.io.File;


/**
 * A static class used to help with some of the JavaFX roughness
 *
 * Created by Ian on 2/16/2017.
 */
public final class UXUtils {

	// Private constructor to prevent instantiation
	private UXUtils() {}

	/**
	 * A utility method to instantiate an FXML file to Java
	 * @param filePath The location of an FXML layout file
	 * @return The inflated layout file as a Node object
	 */
	public static Parent inflate( String filePath ) {
		File f = new File( filePath );
		if ( !f.exists() || !f.isFile() ) {
			throw new IllegalArgumentException( "FXML file does not exist at " + filePath );
		}
		try {
			return FXMLLoader.load( f.toURI().toURL() );
		}
		catch ( Exception e ) {
			return new Pane();
		}
	}

	public static void loadCSS() {
		//TODO: Custom CSS loading to try out data-driven styling
		//this.getClass().getResource( R. )
	}
}
