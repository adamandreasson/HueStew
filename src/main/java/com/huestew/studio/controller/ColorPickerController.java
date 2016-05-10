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
	private void changeLightStateButtonPressed() {
		updateKeyFrame();
	
		
		
	}
	
		
	private void updateKeyFrame(){
		
		Color c = colorPicker.getValue();
		
		keyframe.getState().setColor(c);
		keyframe.setTimestamp(Integer.parseInt(newTimestamp.getText()));
		
		
		/*
        .setColor(c);
        .setBrightness((int)c.getBrightness()*255);
        .setSaturation((int)c.getSaturation()*255);
			*/
		
	}
	
	public void setKeyFrame(KeyFrame keyframe){
		this.keyframe = keyframe;
		
		newTimestamp.setText(keyframe.getTimestamp() + "");
		brightnessSlider.setValue(keyframe.getState().getBrightness());
		
	}
	
	
	

}
