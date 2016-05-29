package com.huestew.studio.controller;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * An abstract JavaFX controller class.
 */
public abstract class ViewController {
	private Parent parent;

	/**
	 * @return The view associated with this controller.
	 */
	public Parent getParent() {
		return parent;
	}

	/**
	 * Set the view.
	 * 
	 * @param view
	 *            The view associated with this controller.
	 */
	public void setParent(Parent parent) {
		this.parent = parent;
	}

	/**
	 * Invoked after the view has been set.
	 */
	public void init() {}
	

	/**
	 * Load a JavaFX view and get its controller.
	 * 
	 * @param path
	 *            The path to the fxml file that defines the view.
	 * @return The controller of the view.
	 */
	public static ViewController loadFxml(String path) {
		FXMLLoader loader = new FXMLLoader(ViewController.class.getResource(path));
		Parent view;

		try {
			view = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		ViewController controller = loader.getController();
		controller.setParent(view);
		controller.init();
		return controller;
	}
}
