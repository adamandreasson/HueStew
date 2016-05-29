package com.huestew.studio.view;

import javafx.scene.control.Alert;

/**
 * General error alert when an exception occurs that couldn't be dealt with.
 * @author Patrik
 *
 */
public class ErrorAlert extends Alert {
	public ErrorAlert(String message) {
		super(AlertType.ERROR);
		setTitle("HueStew Error");
		setHeaderText(message);
	}
}
