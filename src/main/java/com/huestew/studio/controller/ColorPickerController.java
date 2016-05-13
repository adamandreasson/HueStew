package com.huestew.studio.controller;

import com.huestew.studio.HueStew;
import com.huestew.studio.model.KeyFrame;

import javafx.fxml.FXML;

import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

/**
 * A controller classed used to controll the colorpicker element in the gui. 
 * Uses the javafx colorpicker as well as a textfield for timestamp and a slider for birghtness.
 * @author Daniel
 *
 */
public class ColorPickerController extends ViewController {
			
	
	@FXML ColorPicker colorPicker;
		
	@FXML TextField newTimestamp;
		
	@FXML Slider brightnessSlider;
	
	@FXML AnchorPane colorPickerPane;
	
	private KeyFrame keyframe; 
			
	@Override
	public void init() {
		
		/*
		brightnessSlider.setMajorTickUnit(1);
		brightnessSlider.setMinorTickCount(0);
		brightnessSlider.setSnapToTicks(true);
		*/
		

		brightnessSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
			keyframe.getState().setBrightness(newValue.intValue());
			HueStew.getInstance().getView().updateTrackView();
		});
		
	}
	
	
	@FXML
	private void colorPickerOnAction() {
		Color c = colorPicker.getValue();
		keyframe.getState().setColor(c);
	
	}
/*
	/*so fakkin laggy
	@FXML
	private void brightnessSliderOnDragDetected() {
		keyframe.getState().setBrightness((int)(brightnessSlider.getValue()));
		
		
	}
	*/
	@FXML
	private void newTimestampOnAction(){
		keyframe.setTimestamp(Integer.parseInt(newTimestamp.getText()));
	}
	
	/**
	 * Sets the keyframe to be changed based on user input.
	 * @param keyframe
	 * 					the keyframe to be changed.
	 */
	public void setKeyFrame(KeyFrame keyframe){
		this.keyframe = keyframe;
		
		colorPicker.setValue(keyframe.getState().getColor());
		newTimestamp.setText(keyframe.getTimestamp() + "");
		brightnessSlider.setValue(keyframe.getState().getBrightness());
	}
	
	
}
