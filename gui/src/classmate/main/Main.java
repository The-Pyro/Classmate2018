package classmate.main;

import java.io.InputStream;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import edu.wpi.first.networktables.NetworkTableInstance;

public class Main extends Application {
	public static void main(String args[]) {
		/* Configure Network Table protocol */
		NetworkTableInstance defaultNT = NetworkTableInstance.getDefault();
		defaultNT.setNetworkIdentity("Classmate");
		defaultNT.startClientTeam(871);

		launch(args);
	}

	@Override
	public void start(Stage stage) throws IOException{
		InputStream fxml = getClass().getResourceAsStream("/res/GUI.fxml");
		FXMLLoader loader = new FXMLLoader();

		BorderPane root = (BorderPane)loader.load(fxml);

		Scene scene = new Scene(root);
		stage.setScene(scene);

		stage.setTitle("Dashboard");
		stage.initStyle(StageStyle.UNDECORATED);
		stage.setWidth(800);
		stage.setHeight(400);
		stage.setX(0);
		stage.setY(0);

		stage.show();
	}
}
