package com.huestew.studio.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A light show is a collection of lighttracks and may also have an audiotrack aswell.
 * 
 * @author Adam Andreasson
 */
public class Show {

	/** the collection of lighttracks in this show **/
	private List<LightTrack> lightTracks = new ArrayList<>();

	/**
	 * Add a new lighttrack to the show.
	 * @param track 
	 * 				the new lighttrack to be added.
	 */
	public void addLightTrack(LightTrack track) {
		lightTracks.add(track);
	}

	/**
	 * Remove a lighttrack from the show.
	 * @param track
	 * 				the lighttrack to remove.
	 */
	public void removeLightTrack(LightTrack track) {
		lightTracks.remove(track);
	}

	/**
	 * @return
	 * 			a list of all the lighttracks.
	 */
	public List<LightTrack> getLightTracks(){
		//TODO
		return new ArrayList<>(lightTracks);
	}
	
	/**
	 * Update the cursor position in all of the ligthtracks.
	 * @param cursor
	 * 				the timestamp at which the cursor is currently at.
	 */
	public void updateCursor(int cursor) {
		// Update cursor in each light track
		for (LightTrack track : lightTracks) {
			track.updateCursor(cursor);
		}
	}
}
