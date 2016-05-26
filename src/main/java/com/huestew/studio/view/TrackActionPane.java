package com.huestew.studio.view;

import com.huestew.studio.model.LightTrack;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * @author Adam
 *
 */
public class TrackActionPane extends VBox {

	private LightTrack track;
	
	private ToggleButton trackBtn;
	private Button removeBtn;
	
	public TrackActionPane(LightTrack track, ToggleGroup actionGroup) {
		this.track = track;

		Image lightImg = new Image("icon_light.png");
		Image eyeImg = new Image("icon_eye.png");
		Image trashImg = new Image("icon_trash.png");
		
		trackBtn = new ToggleButton();
		trackBtn.setToggleGroup(actionGroup);
		trackBtn.setGraphic(new ImageView(lightImg));
		trackBtn.setTooltip(new Tooltip("Configure lights"));
		this.getChildren().add(trackBtn);
		
		Button hideBtn = new Button();
		hideBtn.setGraphic(new ImageView(eyeImg));
		hideBtn.setTooltip(new Tooltip("Hide track"));
		hideBtn.setOnAction((e) -> {
			System.out.println("todohehe");
		});
		this.getChildren().add(hideBtn);
		
		removeBtn = new Button();
		removeBtn.setGraphic(new ImageView(trashImg));
		removeBtn.setTooltip(new Tooltip("Remove track"));
		removeBtn.setDisable(true);
		this.getChildren().add(removeBtn);
	}

	public void setOnRemove(EventHandler<ActionEvent> handler) {
		removeBtn.setOnAction(handler);
		removeBtn.setDisable(false);
	}

	public LightTrack getTrack() {
		return track;
	}
	
	public ToggleButton getTrackBtn() {
		return trackBtn;
	}
	
}
