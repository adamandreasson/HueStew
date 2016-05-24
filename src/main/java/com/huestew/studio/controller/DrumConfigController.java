/**
 * 
 */
package com.huestew.studio.controller;

import java.util.ArrayList;
import java.util.List;

import com.huestew.studio.model.Drum;
import com.huestew.studio.model.LightTrack;

import javafx.collections.FXCollections;
/**
 * @author Adam
 *
 */
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

public class DrumConfigController extends ViewController {

	@FXML
	private AnchorPane wrapPane;
	
	@FXML
	private ChoiceBox<String> trackDropdown;

	@FXML
	private TextField keyField;

	@FXML
	private Label label;

	@FXML
	private Button copyButton;

	@FXML
	private Button removeButton;

	private DrumKitController controller;
	private List<LightTrack> tracks;
	private Drum drum;

	@Override
	public void init() {

		copyButton.setGraphic(new ImageView(new Image("icon_copy.png")));
		copyButton.setTooltip(new Tooltip("Create copy"));

		removeButton.setGraphic(new ImageView(new Image("icon_trash.png")));
		removeButton.setTooltip(new Tooltip("Remove"));
		
	}

	@FXML
	private void onCopyPressed() {
		System.out.println("coppy");
	}

	@FXML
	private void onRemovePressed() {
		System.out.println("remove");
	}

	public void setDrumKitController(DrumKitController controller) {
		this.controller = controller;
	}

	public void initDrum(Drum drum, List<LightTrack> tracks) {
		this.drum = drum;

		label.setText(drum.getName());

		keyField.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
			KeyCode code = event.getCode();
			if (code == KeyCode.UNDEFINED)
				return;
			keyField.setText(code.toString());
			drum.setKey(code);

			event.consume();
		});
		
		trackDropdown.getSelectionModel().selectedIndexProperty().addListener((ov, oldValue, newValue) -> {
			drum.setTrack(tracks.get(newValue.intValue()));
		});

		updateTrackDropdown(tracks);
	}

	public void updateTrackDropdown(List<LightTrack> tracks) {
		this.tracks = tracks;

		ArrayList<String> trackNames = new ArrayList<String>();
		
		for (int i = 0; i < tracks.size(); i++) {
			trackNames.add("Track " + i);
		}

		trackDropdown.setItems(FXCollections.observableArrayList(trackNames));
	}

	public void updateSize(double width) {
		wrapPane.setMaxWidth(width);
	}

	public void setWidth(double width) {
		wrapPane.setMaxWidth(width);
	}

}
