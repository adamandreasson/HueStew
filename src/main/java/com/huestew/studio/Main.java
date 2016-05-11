package com.huestew.studio;

import com.huestew.studio.controller.MainViewController;
import com.huestew.studio.util.Util;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		MainViewController controller = (MainViewController) Util.loadFxml("main.fxml");
		primaryStage.setTitle("HueStew Studio");
		Scene scene = new Scene(controller.getView(),1280,720);
		primaryStage.setScene(scene);

		primaryStage.getIcons().add(new Image("/icon_256x256.png"));
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
