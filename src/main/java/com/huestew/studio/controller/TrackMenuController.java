package com.huestew.studio.controller;

import com.huestew.studio.model.LightTrack;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class TrackMenuController extends ViewController {
	@FXML
	private AnchorPane pane;

	public void openFor(LightTrack track) {
		// TODO change contents
		pane.toFront();
	}
	
	public void close() {
		pane.toBack();
	}
}
