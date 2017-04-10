package io.snippy.main;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import io.snippy.core.R;
import io.snippy.util.SQLUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;


/**
 * Created by Ian on 3/24/2017.
 */
public class DeleteDialog extends JFXDialog {
	private static JFXDialog dialog;
	private final MainScene scene;
	private final StackPane stackPane;
	private final int snipID;
	private JFXDialogLayout layout;
	private JFXButton cancelButton, confirmButton;

	private DeleteDialog(MainScene scene, StackPane stackPane, int snipID) {
		this.stackPane = stackPane;
		this.snipID = snipID;
		this.layout = new JFXDialogLayout();
		this.scene = scene;
		create();
	}

	private final void create() {
		cancelButton = new JFXButton( R.strings( "common.cancel"));
		confirmButton = new JFXButton(R.strings("delete.confirm"));
		confirmButton.setStyle("-fx-text-fill: WHITE; -fx-background-color: RED;");

		layout.setHeading(new Label( R.strings( "delete.header")));
		layout.setBody(new Label(R.strings("delete.body")));
		layout.setActions(cancelButton, confirmButton);

		cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(javafx.event.ActionEvent event) {
				dialog.close();
			}
		});

		confirmButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
			@Override
			public void handle(javafx.event.ActionEvent event) {
				SQLUtils.removeSnip( snipID);
				dialog.close();
				scene.clearDisplayedSnip();
				scene.update();
			}
		});

		this.setTransitionType(DialogTransition.CENTER);
		this.setContent(layout);
	}

	public static final JFXDialog createAndShow(MainScene s, StackPane sp, int id) {
		JFXDialog d = new DeleteDialog(s, sp, id);
		dialog = d;

		d.show(sp);
		return d;
	}
}
