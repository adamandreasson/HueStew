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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class DrumConfigController extends ViewController {

    @FXML
    private ChoiceBox<String> trackDropdown;

    @FXML
    private TextField keyField;

    @FXML
    private Label label;
    
    private DrumKitController controller;
    private List<LightTrack> tracks;
    private Drum drum;

	public void setDrumKitController(DrumKitController controller) {
		this.controller = controller;
	}
	
	public void initDrum(Drum drum, List<LightTrack> tracks) {
		this.drum = drum;
		
		label.setText(drum.getName());
		
		keyField.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
			KeyCode code = event.getCode();
			if(code == KeyCode.UNDEFINED)
				return;
			keyField.setText(code.toString());
			drum.setKey(code);
			
			event.consume();
		});
		trackDropdown.getSelectionModel().selectedIndexProperty().addListener((ov, oldValue, newValue) -> {

			System.out.println("chaning drum track to " + newValue.intValue());
			drum.setTrack(tracks.get(newValue.intValue()));

		});
		
		updateTrackDropdown(tracks);
	}
	
	public void updateTrackDropdown(List<LightTrack> tracks){
		this.tracks = tracks;
		
		ArrayList<String> trackNames = new ArrayList<String>();	
		int i = 1;
		for(LightTrack track : tracks){
			trackNames.add("Track " + i);
			i++;
		}
		
		trackDropdown.setItems(FXCollections.observableArrayList(trackNames));
	}

}
