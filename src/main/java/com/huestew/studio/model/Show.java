package com.huestew.studio.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A show is a collection of light tracks and an audio.
 * 
 * @author Adam Andreasson
 */
public class Show {

	/** The collection of lighttracks in this show */
	private List<LightTrack> lightTracks = new ArrayList<>();

	private int duration;
	
	private Audio audio;

	/**
	 * Add a new lighttrack to the show.
	 * 
	 * @param track
	 *            the new lighttrack to be added.
	 */
	public void addLightTrack(LightTrack track) {
		lightTracks.add(track);
	}

	/**
	 * Remove a lighttrack from the show.
	 * 
	 * @param track
	 *            the lighttrack to remove.
	 */
	public void removeLightTrack(LightTrack track) {
		lightTracks.remove(track);
	}

	/**
	 * @return a list of all the lighttracks.
	 */
	public List<LightTrack> getLightTracks() {
		// TODO
		return new ArrayList<>(lightTracks);
	}

	/**
	 * @return the audio associated with the show
	 */
	public Audio getAudio() {
		return audio;
	}

	/**
	 * Change the audio of the show.
	 * 
	 * @param audio
	 *            the new audio reference
	 */
	public void setAudio(Audio audio) {
		this.audio = audio;
	}

	/**
	 * Update the cursor position in all of the ligthtracks.
	 * 
	 * @param cursor
	 *            the timestamp at which the cursor is currently at.
	 */
	public void updateCursor(int cursor) {
		// Update cursor in each light track
		for (LightTrack track : lightTracks) {
			track.updateCursor(cursor);
		}
	}

	/**
	 * @return the duration
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}
}
