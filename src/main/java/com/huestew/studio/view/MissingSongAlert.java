package com.huestew.studio.view;

import javafx.scene.control.Alert;

/**
 * A custom FX alert for when a song file is missing
 * @author Patrik
 *
 */
public class MissingSongAlert extends Alert {
	public MissingSongAlert(String songPath) {
		super(AlertType.ERROR);
		setTitle("HueStew Error");
		setHeaderText("Missing song file");
		setContentText("The song file associated with this show could not be found in the specified location:"
				+ "\n\t" + songPath
				+ "\n\nYou will be prompted to browse for a replacement song.");
	}
}
