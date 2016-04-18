package com.huestew.studio;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

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
		FXMLLoader loader = new FXMLLoader(Util.class.getResource(path));
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
}
