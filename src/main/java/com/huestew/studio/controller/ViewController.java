package com.huestew.studio.controller;

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
}
