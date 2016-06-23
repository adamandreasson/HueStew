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


import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller that handles all interaction with drums and the drum kit pane in
 * the main view
 * 
 * @author Adam
 *
 */
public class DrumKitController {

	MainViewController controller;
	AnchorPane drumKitPane;
	DrumKit drumKit;
	VBox grid;
	List<DrumConfigController> drumConfigs;

	/**
	 * Create a new instance of the controller
	 * 
	 * @param drumkitPane
	 *            The pane for the controller to work with
	 * @param controller
	 *            Parent controller
	 */
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

	/**
	 * Called when user presses the "add sequence button" in the main view
	 * 
	 * @param frames
	 *            Currently selected frames to create a drum sequence from
	 */
	public void addButtonPressed(List<KeyFrame> frames) {

		Sequence sequence = new Sequence(frames);

		addDrum(sequence);

	}

	/**
	 * Add a new drum and create the interface for it
	 * 
	 * @param sequence
	 *            The sequence for the new drum
	 */
	private void addDrum(Sequence sequence) {

		Drum drum = drumKit.addDrum(sequence);

		DrumConfigController drumConfigController = (DrumConfigController) ViewController.loadFxml("/drumtile.fxml");
		drumConfigController.setDrumKitController(this);
		drumConfigController.setWidth(drumKitPane.getWidth());
		drumConfigController.initDrum(drum, controller.getShow().getLightTracks());

		drumConfigs.add(drumConfigController);
		grid.getChildren().add(drumConfigController.getParent());

	}

	/**
	 * Create a copy of an existing drum (duplicate)
	 * 
	 * @param drum
	 *            The drum to copy
	 */
	public void createDrumCopy(Drum drum) {

		addDrum(drum.getSequence());

	}

	/**
	 * Remove a drum and its' interface
	 * 
	 * @param drumConfigController
	 *            The controller for the drum's interface
	 * @param drum
	 *            The drum to be removed
	 */
	public void removeDrum(DrumConfigController drumConfigController, Drum drum) {
		drumConfigs.remove(drumConfigController);
		grid.getChildren().remove(drumConfigController.getParent());

		drumKit.removeDrum(drum);
	}

	/**
	 * Is the key pressed a valid drum key? Redirected to {@link DrumKit}
	 * 
	 * @param key
	 *            The key pressed
	 * @return Whether the key press is linked to a drum
	 */
	public boolean isValidDrumKey(KeyCode key) {

		return drumKit.isValidKey(key);
	}

	public void keyboardEvent(KeyEvent event) {

		// If the key pressed has a drum assigned, create a snapshot before
		// hitting the drum
		if (drumKit.isValidKey(event.getCode())) {
			//SnapshotManager.getInstance().commandIssued();
		}

		boolean wasDrumBeat = drumKit.beat(event.getCode(), controller.getShow());

		// if the drum was successfully beat, update track view
		if (wasDrumBeat) {
			controller.updateTrackView();
		}
	}

	public void updateSize() {
		grid.setMaxWidth(drumKitPane.getWidth());
		for (DrumConfigController config : drumConfigs) {
			config.updateSize(drumKitPane.getWidth());
		}
	}

}
