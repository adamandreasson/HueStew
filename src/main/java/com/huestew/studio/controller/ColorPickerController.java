package com.huestew.studio.controller;

import com.huestew.studio.model.KeyFrame;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;

import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class ColorPickerController extends ViewController {
			
	
	@FXML ColorPicker colorPicker;
		
	@FXML TextField newTimestamp;
		
	@FXML Slider brightnessSlider;
	
	@FXML AnchorPane colorPickerPane;
	
	@FXML Label brightnessLabel;
	
	private KeyFrame keyframe; 
			
	@Override
	public void init() {
		
		/*
		brightnessSlider.setMajorTickUnit(1);
		brightnessSlider.setMinorTickCount(0);
		brightnessSlider.setSnapToTicks(true);
		*/
		brightnessSlider.valueProperty().addListener(new ChangeListener<Object>(){

			@Override
			public void changed(ObservableValue<?> arg0, Object arg1, Object arg2) {
				brightnessLabel.textProperty().setValue(String.valueOf((int)brightnessSlider.getValue()));
				keyframe.getState().setBrightness((int)(brightnessSlider.getValue()));
			}
			          
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
	
		
	public void setKeyFrame(KeyFrame keyframe){
		this.keyframe = keyframe;
		
		colorPicker.setValue(keyframe.getState().getColor());
		newTimestamp.setText(keyframe.getTimestamp() + "");
		brightnessSlider.setValue(keyframe.getState().getBrightness());
		brightnessLabel.textProperty().setValue(keyframe.getState().getBrightness() + "");
	}
	
	
}
