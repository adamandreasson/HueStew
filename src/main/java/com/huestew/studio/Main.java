package com.huestew.studio;

import java.io.IOException;

import com.huestew.studio.controller.MainViewController;
import com.huestew.studio.controller.ViewController;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {

		MainViewController controller = (MainViewController) ViewController.loadFxml("/main.fxml");
		primaryStage.setTitle("HueStew Studio");
		controller.setStage(primaryStage);

		Scene scene = new Scene(controller.getParent(), 1280, 720);
		scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

		primaryStage.setScene(scene);
		primaryStage.getIcons().add(new Image("/icon_256x256.png"));
		primaryStage.show();

		// Register global key listeners
		primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, event -> controller.handleKeyboardEvent(event));
		primaryStage.addEventFilter(KeyEvent.KEY_RELEASED, event -> controller.handleKeyboardEvent(event));

		// Register closing listener
		primaryStage.setOnCloseRequest((e) -> {
			try {
				controller.shutdown();
				controller.getFileHandler().clean();
			} catch (IOException e1) {
				// Nothing to do here really
				e1.printStackTrace();
			}
			System.exit(0);
		});

	}

	public static void main(String[] args) {
		launch(args);
	}
}
