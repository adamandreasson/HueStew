package com.huestew.studio.util;

import java.io.IOException;

import com.huestew.studio.HueStew;
import com.huestew.studio.controller.ViewController;

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
		System.out.println(HueStew.class.getResource(path));
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
}
