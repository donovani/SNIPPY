package io.snippy.core;

import org.pmw.tinylog.Logger;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * Based on the Android resources class R. Reads in basic data to be used elsewhere.
 * Provides a syntax like 'R.colors("accent")'
 *
 * Created by Ian on 3/16/2017.
 */
public class R {

	private static Map<String, Properties> sets;

	private static final String DIR = "assets/values";
	private static final String[] FILES = new String[] {
		"colors", "config", "dimens",
		"files", "strings"
	};

	static {
		loadProperties();
	}

	//Cannot instantiate this class
	private R() { }


	public static final Color color( String col ) {
		return Color.decode( sets.get( FILES[0] ).getProperty( col ) );
	}

	public static final Object config( String cfg ) {
		return sets.get( FILES[1] ).get( cfg );
	}

	public static final double dimens( String dim ) {
		return Double.parseDouble( sets.get( FILES[2] ).getProperty( dim ) );
	}

	public static final File files( String file ) {
		return new File( sets.get( FILES[3] ).getProperty( file ) );
	}

	public static final String strings( String str ) {
		return sets.get( FILES[4] ).getProperty( str );
	}


	protected static void loadProperties() {
		if ( sets == null )
			sets = new HashMap<>();
		for (String set : FILES) {
			try {
				sets.put( set, loadFile( DIR, set ) );
			}
			catch ( Exception e ) {
				Logger.warn( "Couldn't load properties files.\n\t{} : {}", e.getClass().getSimpleName(), e.getMessage() );
			}
		}
	}

	private static Properties loadFile(String directory, String fileName) throws FileNotFoundException, IOException {
		File f = new File( directory+'/'+fileName+".properties" );
		FileInputStream fin = new FileInputStream( f );
		Properties p = new Properties( );
		p.load( fin );
		fin.close();
		return p;
	}
}
