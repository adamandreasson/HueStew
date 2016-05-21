/**
 * 
 */
package com.huestew.studio.controller;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 * @author Adam
 *
 */
public class DrumKitController {

	MainViewController mvc;
	AnchorPane drumKitPane;
	
	public DrumKitController(AnchorPane drumkitPane, MainViewController mvc) {
		this.drumKitPane = drumkitPane;
		this.mvc = mvc;
	
		Label label = new Label("drum kit pane");
		drumKitPane.getChildren().add(label);
	}

	public void addButtonPressed() {
		
		System.out.println("time to create sequence");
		
	}

}
