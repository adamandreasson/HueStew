package com.huestew.studio.view;

import java.util.Arrays;

import javafx.scene.control.Alert;

public class ErrorAlert extends Alert {
	public ErrorAlert(Exception e) {
		super(AlertType.ERROR);
		setTitle("HueStew Error");
		setHeaderText(e.getMessage());
		setContentText(Arrays.deepToString(e.getStackTrace()));
	}
}
