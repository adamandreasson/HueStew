package com.huestew.studio.controller;

import java.util.TreeSet;

import com.huestew.studio.HueStew;
import com.huestew.studio.model.Color;
import com.huestew.studio.model.KeyFrame;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

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
	
	@FXML TextField brightnessTextField;
	
	@FXML TextField timestampTextField;
	
	private KeyFrame keyFrame;
	
	// I need this
	private TreeSet<KeyFrame> selectedKeyFrames;
		
	@Override
	public void init() {
			
			brightnessSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
			keyFrame.getState().setBrightness(newValue.intValue());
			brightnessTextField.setText(keyFrame.getState().getBrightness() + "");
			HueStew.getInstance().getView().updateTrackView();
		});
		
	}
	
	@FXML
	private void colorPickerOnAction() {
		keyFrame.getState().setColor(new Color(colorPicker.getValue()));
	
	}

	@FXML
	private void timestampTextFieldOnAction(){
		keyFrame.setTimestamp(Integer.parseInt(timestampTextField.getText()));
		HueStew.getInstance().getView().updateTrackView();
	}
	@FXML
	private void brightnessTextFieldOnAction(){
		keyFrame.getState().setBrightness(Integer.parseInt(brightnessTextField.getText()));
		brightnessSlider.setValue(Integer.parseInt(brightnessTextField.getText()));
		HueStew.getInstance().getView().updateTrackView();
	}
	
	/**
	 * Sets the keyframe to be changed based on user input.
	 * @param keyframe
	 * 					the keyframe to be changed.
	 */
	public void setKeyFrame(KeyFrame keyframe){
		this.keyFrame = keyframe;
		
		colorPicker.setValue(keyframe.getState().getColor().toFxColor());
		timestampTextField.setText(keyframe.getTimestamp() + "");
		brightnessSlider.setValue(keyframe.getState().getBrightness());
		brightnessTextField.setText(keyframe.getState().getBrightness() + "");
	}
	

	
	
	
}
