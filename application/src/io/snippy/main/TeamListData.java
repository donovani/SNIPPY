package io.snippy.main;


import com.jfoenix.controls.JFXButton;
import io.snippy.core.Group;
import io.snippy.util.SQLUtils;
import io.snippy.util.UXUtils;
import javafx.scene.Parent;
import javafx.scene.text.Text;


/**
 * Created by Ian on 4/9/2017.
 */
public class TeamListData {

	private int userID;
	private Group nodeGroup;

	public Parent toNode(Group nodeGroup, int userID) {
		this.nodeGroup = nodeGroup;
		Parent node = UXUtils.inflate( "assets/layouts/list_item.fxml" );
		Text titleText = (Text) node.lookup( "#team_name");
		JFXButton leaveButton = (JFXButton) node.lookup( "#team_leave" );
		JFXButton disbandButton = (JFXButton) node.lookup( "#team_disband" );

		titleText.setText(nodeGroup.getName());
		if ( userID == nodeGroup.getGroupOwnerID() ) {
			leaveButton.setVisible( false );
			//TODO: Delete group & remove any users who joined it.
		}
		else {
			disbandButton.setVisible( false );
			//TODO: Remove user with userID from group.
		}
		return node;
	}
}
