package io.snippy.main;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import io.snippy.core.R;
import io.snippy.util.UXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;


/**
 * Created by Ian on 4/9/2017.
 */
public class ShareDialog extends JFXDialog {
	private static JFXDialog dialog;
	private final MainScene scene;
	private final StackPane stackPane;
	private JFXDialogLayout layout;
	private JFXButton doneButton;

	private ShareDialog(MainScene scene, StackPane stackPane) {
		this.stackPane = stackPane;
		this.layout = new JFXDialogLayout();
		this.scene = scene;
		create();
	}

	private final void create() {
		doneButton = new JFXButton( R.strings( "common.done"));
		doneButton.setStyle("-fx-text-fill: WHITE; -fx-background-color: #00aaaa");

		layout.setHeading(new Label( R.strings( "share.header")));
		layout.setBody( UXUtils.inflate( "assets/layouts/layout_share.fxml" ) );
		layout.setActions( doneButton );

		doneButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(javafx.event.ActionEvent event) {
				dialog.close();
				scene.clearDisplayedSnip();
				scene.update();
			}
		});

		this.setTransitionType( JFXDialog.DialogTransition.CENTER);
		this.setContent(layout);
	}

	public static final JFXDialog createAndShow(MainScene s, StackPane sp) {
		JFXDialog d = new ShareDialog(s, sp);
		dialog = d;

		d.show(sp);
		return d;
	}
}
