package com.huestew.studio.controller;

import javafx.scene.Parent;

/**
 * An abstract JavaFX controller class.
 */
public abstract class ViewController {
	private Parent view;

	/**
	 * @return The view associated with this controller.
	 */
	public Parent getView() {
		return view;
	}

	/**
	 * Set the view.
	 * 
	 * @param view
	 *            The view associated with this controller.
	 */
	public void setView(Parent view) {
		this.view = view;
	}

	/**
	 * Show the view associated with this controller.
	 */
	public void show() {
		view.toFront();
	}

	/**
	 * Hide the view associated with this controller.
	 */
	public void hide() {
		view.toBack();
	}

	/**
	 * Invoked after the view has been set.
	 */
	public void init() {}
}
