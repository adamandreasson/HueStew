package com.huestew.studio.util;

import java.io.IOException;

import com.huestew.studio.HueStew;
import com.huestew.studio.controller.ViewController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Various utilities.
 */
public class Util {
	/**
	 * Load a JavaFX view and get its controller.
	 * 
	 * @param path
	 *            The path to the fxml file that defines the view.
	 * @return The controller of the view.
	 */
	public static ViewController loadFxml(String path) {
		FXMLLoader loader = new FXMLLoader(HueStew.class.getResource(path));
		Parent view;

		try {
			view = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		ViewController controller = loader.getController();
		controller.setView(view);
		controller.init();
		return controller;
	}

	public static Stage createStage() {
		Stage stage = new Stage();
		stage.getIcons().add(new Image("icon_256x256.png"));
		return stage;
	}
	
	private static String formatTime(int i){
		if(i >= 10)
			return String.valueOf(i);
		else
			return "0"+i;
	}

	public static String formatTimestamp(int i) {
		int minutes = Math.floorDiv(i, 60 * 1000);
		i -= minutes * 60 * 1000;
		int seconds = Math.floorDiv(i, 1000);
		i -= seconds * 1000;
		return formatTime(minutes) + ":" + formatTime(seconds);
	}
}
