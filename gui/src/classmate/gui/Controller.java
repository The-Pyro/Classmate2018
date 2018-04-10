package classmate.gui;

import java.io.InputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.TableEntryListener;

public class Controller extends VBox {
	@FXML
	Label speed;

	@FXML
	ToggleGroup controllerGroup;

	@FXML
	RadioButton none, cont0, cont1, analog;

	@FXML
	ToggleButton invert;

	/*
	ITable used by the class to communicate with the robot.  The table will
	contain two keys:
		-speed:  A double representing the current motor speed.  Set by the
		robot.
		-config:  A string containing the controller configuration.  Set by
		the GUI.
	*/
	final static String SPEED_KEY = "speed";
	final static String CONFIG_KEY = "config";
	NetworkTable motorTable;

	public Controller() throws IOException {
		super();
		InputStream fxml =
			getClass().getResourceAsStream("/res/Controller.fxml");

		FXMLLoader loader = new FXMLLoader();

		loader.setRoot(this);
		loader.setController(this);
		loader.load(fxml);
	}

	@Override
	public String toString() {
		return controllerGroup.getSelectedToggle().getUserData().toString()
			+ ":" + invert.isSelected();
	}

	@FXML
	protected void updateConfig(ActionEvent e) {
		if (motorTable != null) {
			motorTable.getEntry(CONFIG_KEY).setString(toString());
		}
	}

	public void setTable(NetworkTable t) {
		motorTable = t;

		motorTable.addEntryListener(SPEED_KEY,
				(table, key, entry, value, flags) -> {
					double speedVal = value.getDouble();
					DecimalFormat f = new DecimalFormat("+0.0000;-0.0000");
					Platform.runLater(() -> {
						speed.setText(f.format(speedVal));
					});
			}, TableEntryListener.kNew | TableEntryListener.kUpdate);
	}
}
