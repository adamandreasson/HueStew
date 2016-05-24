/**
 * 
 */
package com.huestew.studio.controller;

import java.util.List;

import com.huestew.studio.model.Drum;
import com.huestew.studio.model.DrumKit;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.Sequence;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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

		DrumConfigController drumConfigController = (DrumConfigController) ViewController.loadFxml("/drumtile.fxml");
		drumConfigController.setDrumKitController(this);
		drumConfigController.initDrum(drum, controller.getShow().getLightTracks());
		grid.getChildren().add(drumConfigController.getParent());
				
	}

	public boolean isValidDrumKey(KeyCode key){
		
		return drumKit.isValidKey(key);
	}
	
	public void keyboardEvent(KeyEvent event) {
		boolean wasDrumBeat = drumKit.beat(event.getCode(), controller.getShow());
	
		if(wasDrumBeat){
			System.out.println("omg drum was beat");
			controller.updateTrackView();
		}
	}

}
