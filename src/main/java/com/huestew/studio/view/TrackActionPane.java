package com.huestew.studio.view;

import com.huestew.studio.model.LightTrack;
import com.huestew.studio.util.Task;

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
	
	ToggleButton trackBtn;
	private Button removeBtn;
	private Task<Void> removeTask;
	
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
		removeBtn.setOnAction((e) -> removeTask.execute());
		this.getChildren().add(removeBtn);
	}

	public void setOnRemove(Task<Void> task) {
		removeTask = task;
		removeBtn.setDisable(false);
	}

	public LightTrack getTrack() {
		return track;
	}
	
	public ToggleButton getTrackBtn() {
		return trackBtn;
	}
	
}
