package classmate.gui;

import java.text.DecimalFormat;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.TableEntryListener;

public class GUI {
	@FXML
	BorderPane rootNode;

	@FXML
	Controller blackMotor, redMotor;

	@FXML
	ChoiceBox relay;

	@FXML
	HBox dioBox, analogBox;

	/*
	The classmate connects to a Network Table called "classmate".  The table
	will have the the following keys:
		-blackMotor:  A subtable used by the black motor.  See Controller.java.
		-redMotor:  See Controller.java
		-dios:  A boolean array storing the states of all digital inputs.
		-analogs:  A number array storing the values of all analog inputs.
		-relay:  A string storing the state of the relay.
	*/
	final static String DIO_KEY = "dios";
	final static String ANALOG_KEY = "analogs";
	final static String RELAY_KEY = "relay";

	Circle dios[];

	Label analogs[];

	@FXML
	public void initialize() {
		NetworkTable ourTable = NetworkTableInstance.getDefault()
			.getTable("classmate");

		/* Set up motor controllers */
		blackMotor.setTable(ourTable.getSubTable("blackMotor"));
		redMotor.setTable(ourTable.getSubTable("redMotor"));

		/* Create dio display and register table listener */
		dios = new Circle[10];
		for (int i = 0; i < 10; i++) {
			dios[i] = new Circle(10.0, Color.RED);
			dios[i].setStroke(Color.GREY);
		}
		dioBox.getChildren().addAll(dios);

		ourTable.addEntryListener(DIO_KEY,
			(table, key, entry, value, flags) -> {
				boolean dioVals[] = entry.getBooleanArray(new boolean[10]);
				Platform.runLater(() -> {
					for (int i = 0; i < 10; i++) {
						dios[i].setFill(dioVals[i] ? Color.GREEN : Color.RED);
					}
				});
			}, TableEntryListener.kNew | TableEntryListener.kUpdate);

		/* Create analog displays and register table listener */
		analogs = new Label[4];
		for (int i = 0; i < 4; i++) {
			analogs[i] = new Label("Analog!");
		}
		analogBox.getChildren().addAll(analogs);

		ourTable.addEntryListener(ANALOG_KEY,
			(table, key, entry, value, flags) -> {
				double analogVals[] = entry.getDoubleArray(new double[4]);
				DecimalFormat f = new DecimalFormat("+0.0000;-0.0000");
				Platform.runLater(() -> {
					for (int i = 0; i < 4; i++) {
						analogs[i].setText(f.format(analogVals[i]));
					}
				});
			}, TableEntryListener.kNew | TableEntryListener.kUpdate);
	}

	@FXML
	private void updateRelay(ActionEvent e) {
		NetworkTableInstance.getDefault().getTable("classmate")
			.getEntry(RELAY_KEY).setString(relay.getValue().toString());
	}
}
