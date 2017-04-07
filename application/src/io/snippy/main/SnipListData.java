package io.snippy.main;

import io.snippy.core.Snip;
import io.snippy.util.Language;
import io.snippy.util.UXUtils;
import javafx.scene.Parent;
import javafx.scene.text.Text;


/**
 * Created by Ian on 3/15/2017.
 */
public class SnipListData {

	private String uuid;
	private boolean favorite = false;
	private boolean selected, hovered;

	public Parent toNode(Snip nodeSnip) {
		Parent node = UXUtils.inflate( "assets/layouts/list_item.fxml" );
		Text titleText = (Text) node.lookup( "#list_item_title" );
		Text languageText = (Text) node.lookup( "#list_item_written" );
		titleText.setText(nodeSnip.getTitle() );
		languageText.setText( "Written in " + nodeSnip.getLanguage() );
		return node;
	}
}
