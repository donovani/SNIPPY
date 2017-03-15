package io.snippy.util;

/**
 * An enumeration made to easily define our supported languages
 *
 * Created by Ian on 3/15/2017.
 */
public enum Language {
	RAW("Raw Text"), C("C"), CPP("C++"), CSHARP("C#"), JAVA("Java"),
	RUBY("Ruby"), JAVASCRIPT("Javascript"), PHP("PHP"), SQL("SQL"),
	HTML("HTML"), CSS("CSS"), XML("XML"), YAML("YAML"), JSON("JSON");

	public final String NAME;

	Language( String name ) {
		NAME = name;
	}
}
