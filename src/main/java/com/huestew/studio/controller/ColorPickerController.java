package com.huestew.studio.controller;

import com.huestew.studio.Toolbox;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightState;

import javafx.fxml.FXML;

import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class ColorPickerController extends ViewController {
			
	
	@FXML ColorPicker colorPicker;
		
	@FXML TextField newTimestamp;
		
	@FXML Slider brightnessSlider;
	
	@FXML AnchorPane colorPickerPane;
	
	
	private KeyFrame keyframe; 
	
	
	@FXML
	private void colorPickerOnAction() {
		Color c = colorPicker.getValue();
		keyframe.getState().setColor(c);
	
		
		
	}
	
	@FXML
	private void brightnessSliderOnDragDetected() {
		keyframe.getState().setBrightness((int)(brightnessSlider.getValue()));
		
		
	}
	
	@FXML
	private void newTimestampOnAction(){
		keyframe.setTimestamp(Integer.parseInt(newTimestamp.getText()));
	}
	
	
		
	public void setKeyFrame(KeyFrame keyframe){
		this.keyframe = keyframe;
		
		colorPicker.setValue(keyframe.getState().getColor());
		newTimestamp.setText(keyframe.getTimestamp() + "");
		brightnessSlider.setValue(keyframe.getState().getBrightness());
		
	}
	
	
	

}
