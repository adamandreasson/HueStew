package com.huestew.studio.controller;

import java.util.HashMap;
import java.util.Map.Entry;

import com.huestew.studio.model.LightTrack;
import com.huestew.studio.view.Light;
import com.huestew.studio.view.LightBank;
import com.huestew.studio.view.TrackActionButton;

import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class TrackMenuController extends ViewController {

	@FXML
	private AnchorPane pane;

	@FXML
	private AnchorPane coverPane;
	
	TrackActionButton activeButton;
	
	public void openFor(TrackActionButton trackBtn) {
		
		activeButton = trackBtn;
		
		AnchorPane.setTopAnchor(coverPane, 0.0);
		AnchorPane.setLeftAnchor(coverPane, 0.0);
		AnchorPane.setRightAnchor(coverPane, 0.0);
		AnchorPane.setBottomAnchor(coverPane, 0.0);
		
		// TODO move this to close
		pane.getChildren().clear();
		
		HashMap<Light, LightTrack> lights = LightBank.getInstance().getLights();
		
		VBox box = new VBox();
		AnchorPane.setTopAnchor(box, 8.0);
		AnchorPane.setLeftAnchor(box, 8.0);
		AnchorPane.setRightAnchor(box, 8.0);
		AnchorPane.setBottomAnchor(box, 0.0);

		for (Entry<Light, LightTrack> entry : lights.entrySet()) {
			Light light = entry.getKey();
			LightTrack track = entry.getValue();
			
			AnchorPane listEntryPane = new AnchorPane();
			listEntryPane.setPrefHeight(28.0);
			listEntryPane.setPrefWidth(150.0);
			Label label = new Label(light.getName());
			CheckBox check = new CheckBox();
			check.setSelected(trackBtn.getTrack() != null && trackBtn.getTrack() == track);

			check.setOnAction((e)->{
				
				if(check.isSelected()){
					trackBtn.getTrack().addListener(light);
					LightBank.getInstance().updateLight(light, trackBtn.getTrack());
				}else{
					trackBtn.getTrack().removeListener(light);
					LightBank.getInstance().updateLight(light, null);
				}
				
			});

			if(track != null && track != trackBtn.getTrack())
				check.setDisable(true);
			
			listEntryPane.getChildren().add(label);
			listEntryPane.getChildren().add(check);
			AnchorPane.setTopAnchor(label, 0.0);
			AnchorPane.setLeftAnchor(label, 0.0);
			AnchorPane.setTopAnchor(check, 0.0);
			AnchorPane.setRightAnchor(check, 0.0);
			box.getChildren().add(listEntryPane);
		}
		
		pane.getChildren().add(box);
		pane.autosize();
		
		Bounds boundsInScene = trackBtn.localToScene(trackBtn.getBoundsInLocal());
		
		pane.setLayoutY(boundsInScene.getMinY() - pane.getHeight() + 32.0);
		pane.setLayoutX(boundsInScene.getMaxX());
		coverPane.toFront();
		
		trackBtn.setSelected(true);
	}

	public void close() {
		coverPane.toBack();
		activeButton.setSelected(false);
	}

    @FXML
    void mouseDownCover() {
    	System.out.println("mouse down on cover");
    	close();
    }
}
