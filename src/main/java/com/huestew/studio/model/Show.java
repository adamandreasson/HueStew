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
	private List<LightTrack> lightTracks;

	private int duration;

	private Audio audio;

	private int cursor;

	public Show() {
		this.lightTracks = new ArrayList<LightTrack>();
	}

	public Show(Show other) {
		this.lightTracks = new ArrayList<>();
		List<LightTrack> temp = other.getLightTracks();

		for (LightTrack lt : temp) {
			this.lightTracks.add(new LightTrack(lt));
		}

		this.duration = other.getDuration();
		this.audio = other.getAudio();
	}

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
	 *            the lighttrack to remove
	 * @throws IllegalStateException
	 *             if this is the only light track in the show
	 */
	public void removeLightTrack(LightTrack track) throws IllegalStateException {
		if (lightTracks.size() <= 1) {
			throw new IllegalStateException("Cannot remove the last light track.");
		}
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

		this.cursor = cursor;

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
	 * @param duration
	 *            the duration to set
	 * @throws IllegalArgumentException
	 *             if duration is negative
	 */
	public void setDuration(int duration) {
		if (duration < 0) {
			throw new IllegalArgumentException("Duration cannot be negative");
		}
		this.duration = duration;
	}

	/**
	 * @return the cursor
	 */
	public int getCursor() {
		return cursor;
	}
}
