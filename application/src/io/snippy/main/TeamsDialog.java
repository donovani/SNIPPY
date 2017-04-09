package io.snippy.main;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import io.snippy.core.R;
import io.snippy.util.UXUtils;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;


/**
 * Created by Ian on 4/9/2017.
 */
public class TeamsDialog extends JFXDialog {
	private static JFXDialog dialog;
	private final MainScene scene;
	private final StackPane stackPane;
	private JFXDialogLayout layout;
	private JFXButton doneButton;

	private TeamsDialog(MainScene scene, StackPane stackPane) {
		this.stackPane = stackPane;
		this.layout = new JFXDialogLayout();
		this.scene = scene;
		create();
	}

	private final void create() {
		doneButton = new JFXButton( R.strings( "teams.confirm"));
		doneButton.setStyle("-fx-text-fill: WHITE; -fx-background-color: #00aaaa");

		layout.setHeading(new Label( R.strings( "teams.header")));
		layout.setBody( UXUtils.inflate( "assets/layouts/layout_teams.fxml" ) );
		layout.setActions( doneButton );

		doneButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
			@Override
			public void handle(javafx.event.ActionEvent event) {
				dialog.close();
				scene.clearDisplayedSnip();
				scene.update();
			}
		});

		this.setTransitionType(DialogTransition.CENTER);
		this.setContent(layout);
	}

	public static final JFXDialog createAndShow(MainScene s, StackPane sp) {
		JFXDialog d = new TeamsDialog(s, sp);
		dialog = d;

		d.show(sp);
		return d;
	}
}