package com.huestew.studio.controller;

import java.util.HashMap;
import java.util.Map.Entry;

import com.huestew.studio.model.LightTrack;
import com.huestew.studio.view.Light;
import com.huestew.studio.view.LightBank;
import com.huestew.studio.view.TrackActionButton;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class TrackMenuController extends ViewController {

	@FXML
	private AnchorPane pane;

	public void openFor(TrackActionButton trackBtn) {
		HashMap<Light, LightTrack> lights = LightBank.getInstance().getLights();
		
		VBox box = new VBox();

		for (Entry<Light, LightTrack> entry : lights.entrySet()) {
			Light light = entry.getKey();
			LightTrack track = entry.getValue();
			
			AnchorPane listEntryPane = new AnchorPane();
			listEntryPane.setPrefHeight(25.0);
			listEntryPane.setPrefWidth(150.0);
			Label label = new Label(light.getName());
			CheckBox check = new CheckBox();
			check.setSelected(trackBtn.getTrack() != null && trackBtn.getTrack() == track);

			listEntryPane.getChildren().add(label);
			listEntryPane.getChildren().add(check);
			AnchorPane.setLeftAnchor(label, 4.0);
			AnchorPane.setRightAnchor(check, 4.0);
			box.getChildren().add(listEntryPane);
		}
		
		pane.getChildren().add(box);
		pane.autosize();
		pane.setLayoutY(trackBtn.getLayoutY());
		pane.setLayoutX(trackBtn.getLayoutX() + trackBtn.getWidth());
		pane.toFront();
		
		trackBtn.setSelected(true);
	}

	public void close() {
		pane.toBack();
	}
}
