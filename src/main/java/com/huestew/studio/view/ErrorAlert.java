package com.huestew.studio.view;

import javafx.scene.control.Alert;

public class ErrorAlert extends Alert {
	public ErrorAlert(String message) {
		super(AlertType.ERROR);
		setTitle("HueStew Error");
		setHeaderText(message);
	}
}
