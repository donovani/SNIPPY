package io.snippy.main;

import com.jfoenix.controls.JFXButton;
import io.snippy.util.UXUtils;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 * Created by Ryan on 4/9/2017.
 */
public class TagListData {

    private String tagName;
    private boolean favorite = false;
    private boolean selected, hovered;

    public Parent toNode(String tagName) {
        this.tagName = tagName;
        Parent node = UXUtils.inflate( "assets/layouts/tag_item.fxml" );
        JFXButton tagButton = (JFXButton) node.lookup("#main_tagname");
        tagButton.setText(tagName);
        tagButton.setOnMouseClicked(event -> setTagToDelete());
        return node;
    }
    public void setTagToDelete(){
        MainScene.tagToDelete = tagName;
    }



}
