package com.huestew.studio.controller;

import com.huestew.studio.Toolbox;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightState;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;


import javafx.scene.paint.Color;

public class ColorPickerController extends ViewController {
			
	
	@FXML ColorPicker colorPicker;
		
	@FXML TextField newTimestamp;
		
	@FXML Button changeLightStateButton;
		
	@FXML Slider brightnessSlider;
	
	private KeyFrame keyframe; 
	
	
	@FXML
	private void colorPickerOnAction() {
		Color c = colorPicker.getValue();
		keyframe.getState().setColor(c);
	
		
		
	}
	
	
	@FXML
	private void changeLightStateButtonPressed() {
		updateKeyFrame();
	
		
		
	}
	
		
		
	private void updateKeyFrame(){
							
		keyframe.setTimestamp(Integer.parseInt(newTimestamp.getText()));
		keyframe.getState().setBrightness((int)(brightnessSlider.getValue()));
		
		/*
        .setColor(c);
        .setBrightness((int)c.getBrightness()*255);
        .setSaturation((int)c.getSaturation()*255);
			*/
		
	}
	
	public void setKeyFrame(KeyFrame keyframe){
		this.keyframe = keyframe;
		
		colorPicker.setValue(keyframe.getState().getColor());
		newTimestamp.setText(keyframe.getTimestamp() + "");
		brightnessSlider.setValue(keyframe.getState().getBrightness());
		
	}
	
	
	

}
