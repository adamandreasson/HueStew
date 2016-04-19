package com.huestew.studio;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		MainViewController controller = (MainViewController) Util.loadFxml("main.fxml");
		primaryStage.setTitle("HueStew Studio");
		Scene scene = new Scene(controller.getView());
		primaryStage.setScene(scene);

		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("icon_16x16.png")));
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("icon_256x256.png")));
		primaryStage.sizeToScene();
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
