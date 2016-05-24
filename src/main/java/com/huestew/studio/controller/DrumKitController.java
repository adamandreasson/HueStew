/**
 * 
 */
package com.huestew.studio.controller;

import java.util.List;

import com.huestew.studio.model.Drum;
import com.huestew.studio.model.DrumKit;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.Sequence;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;

/**
 * @author Adam
 *
 */
public class DrumKitController {

	MainViewController controller;
	AnchorPane drumKitPane;
	DrumKit drumKit;
	TilePane grid;
	
	public DrumKitController(AnchorPane drumkitPane, MainViewController controller) {
		this.drumKitPane = drumkitPane;
		this.controller = controller;
		this.drumKit = new DrumKit();
	
		this.grid = new TilePane();
		AnchorPane.setTopAnchor(grid, 0.0);
		AnchorPane.setLeftAnchor(grid, 0.0);
		AnchorPane.setRightAnchor(grid, 0.0);
		AnchorPane.setBottomAnchor(grid, 0.0);
		grid.setPrefColumns(2);
		
		drumKitPane.getChildren().add(grid);
	}

	public void addButtonPressed(List<KeyFrame> frames) {
		
		System.out.println("time to create sequence");
		Sequence sequence = new Sequence(frames);
		
		System.out.println("CReasted sequence wirth " + sequence.getFrames().size() + " frames");
		
		for(KeyFrame frame : sequence.getFrames()){
			System.out.println("Frame: " + frame.getTimestamp());
		}
		
		addDrum(sequence);
	}

	private void addDrum(Sequence sequence) {
		
		Drum drum = drumKit.addDrum(sequence);

		Label label = new Label(drum.getName());
		grid.getChildren().add(label);
		
	}

}
