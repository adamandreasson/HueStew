/**
 * 
 */
package com.huestew.studio.view;

import com.huestew.studio.model.LightTrack;

import javafx.scene.control.Button;

/**
 * @author Adam
 *
 */
public class TrackActionButton extends Button {

	LightTrack track;
	
	public TrackActionButton(LightTrack track) {
		this.track = track;
	}

	public LightTrack getTrack() {
		return track;
	}
	
}
