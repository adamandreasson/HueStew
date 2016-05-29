/**
 * 
 */
package com.huestew.studio.controller;

import java.util.ArrayList;
import java.util.List;

import com.huestew.studio.model.Drum;
import com.huestew.studio.model.DrumKit;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.Sequence;
import com.huestew.studio.model.SnapshotManager;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * @author Adam
 *
 */
public class DrumKitController {

	MainViewController controller;
	AnchorPane drumKitPane;
	DrumKit drumKit;
	VBox grid;
	List<DrumConfigController> drumConfigs;

	public DrumKitController(AnchorPane drumkitPane, MainViewController controller) {
		this.drumKitPane = drumkitPane;
		this.controller = controller;
		this.drumKit = new DrumKit();
		this.drumConfigs = new ArrayList<DrumConfigController>();

		this.grid = new VBox();
		AnchorPane.setTopAnchor(grid, 0.0);
		AnchorPane.setLeftAnchor(grid, 0.0);
		AnchorPane.setRightAnchor(grid, 0.0);
		AnchorPane.setBottomAnchor(grid, 0.0);

		drumKitPane.getChildren().add(grid);
	}

	public void addButtonPressed(List<KeyFrame> frames) {

		Sequence sequence = new Sequence(frames);

		addDrum(sequence);

	}

	private void addDrum(Sequence sequence) {

		Drum drum = drumKit.addDrum(sequence);

		DrumConfigController drumConfigController = (DrumConfigController) ViewController.loadFxml("/drumtile.fxml");
		drumConfigController.setDrumKitController(this);
		drumConfigController.setWidth(drumKitPane.getWidth());
		drumConfigController.initDrum(drum, controller.getShow().getLightTracks());
		
		drumConfigs.add(drumConfigController);
		grid.getChildren().add(drumConfigController.getParent());

	}

	public void createDrumCopy(Drum drum) {
		
		addDrum(drum.getSequence());
		
	}

	public void removeDrum(DrumConfigController drumConfigController, Drum drum) {
		drumConfigs.remove(drumConfigController);
		grid.getChildren().remove(drumConfigController.getParent());
		
		drumKit.removeDrum(drum);
	}

	public boolean isValidDrumKey(KeyCode key) {

		return drumKit.isValidKey(key);
	}

	public void keyboardEvent(KeyEvent event) {

		// If the key pressed has a drum assigned, create a snapshot before hitting the drum
		if(drumKit.isValidKey(event.getCode())){
			SnapshotManager.getInstance().commandIssued();
		}

		boolean wasDrumBeat = drumKit.beat(event.getCode(), controller.getShow());
		
		// if the drum was successfully beat, update track view
		if (wasDrumBeat) {
			controller.updateTrackView();
		}
	}

	public void updateSize() {
		grid.setMaxWidth(drumKitPane.getWidth());
		for(DrumConfigController config : drumConfigs){
			config.updateSize(drumKitPane.getWidth());
		}
	}

}
