package com.huestew.studio.controller;

import com.huestew.studio.Toolbox;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightState;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import javafx.scene.paint.Color;

public class ColorPickerController extends ViewController {
			
	
	@FXML ColorPicker colorPicker;
		
	@FXML TextField timestamp;
		
	@FXML Button changeLightStateButton;
		
	private LightState state;
		          
	@FXML
	private void colorPickerOnAction(){
		Color c = colorPicker.getValue();
		
        System.out.println("New Color's RGB = "+c.getRed()+" "+c.getGreen()+" "+c.getBlue());
	}
	
	@FXML
	private void changeLightStateButtonPressed() {
		setLightState();
		//hoveringKeyFrame.setState(state);
	
	}
	
		
	private void setLightState(){
		Color c = colorPicker.getValue();
		
		
		state.setColor(c);
		state.setBrightness((int)c.getBrightness()*255);
		state.setSaturation((int)c.getSaturation()*255);
		
	}
	
	
	

}
