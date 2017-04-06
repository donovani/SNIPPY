package io.snippy.main;

import io.snippy.util.Language;
import io.snippy.util.UXUtils;
import javafx.scene.Parent;
import javafx.scene.text.Text;


/**
 * Created by Ian on 3/15/2017.
 */
public class SnipListData {

	private String uuid;
	private String title = "Untitled Snip";
	private Language language = Language.PLAINTEXT;
	private boolean favorite = false;
	private boolean selected, hovered;

	public Parent toNode() {
		Parent node = UXUtils.inflate( "assets/layouts/list_item.fxml" );
		Text titleText = (Text) node.lookup( "#list_item_title" );
		Text languageText = (Text) node.lookup( "#list_item_written" );
		titleText.setText( title );
		languageText.setText( "Written in " + language.NAME );
		return node;
	}
}
