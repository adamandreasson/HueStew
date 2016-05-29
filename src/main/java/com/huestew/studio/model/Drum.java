/**
 * 
 */
package com.huestew.studio.model;

import javafx.scene.input.KeyCode;

/**
 * A drum for the show, has a key, sequence and track. Can be used to rapidly
 * add new frames to a light track using the keyboard
 * 
 * @author Adam
 *
 */
public class Drum {

	private KeyCode key;
	private Sequence sequence;
	private LightTrack track;
	private String name;

	public Drum(Sequence sequence, String name) {
		this.sequence = sequence;
		this.name = name;
	}

	/**
	 * @return the key
	 */
	public KeyCode getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(KeyCode key) {
		this.key = key;
	}

	/**
	 * @return the sequence
	 */
	public Sequence getSequence() {
		return sequence;
	}

	/**
	 * @param sequence
	 *            the sequence to set
	 */
	public void setSequence(Sequence sequence) {
		this.sequence = sequence;
	}

	/**
	 * @return the track
	 */
	public LightTrack getTrack() {
		return track;
	}

	/**
	 * @param track
	 *            the track to set
	 */
	public void setTrack(LightTrack track) {
		this.track = track;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Beat the drum!
	 * 
	 * @param show
	 *            The show to beat the drum on
	 * @return Whether the beat was successful. Will return false if the drum
	 *         does not have a track assigned
	 */
	public boolean beat(Show show) {
		if (track == null)
			return false;

		int cursor = show.getCursor();

		for (KeyFrame frame : sequence.getFrames()) {
			KeyFrame pastedFrame = new KeyFrame(cursor + frame.getTimestamp(), frame.getState(), track);
			try {
				track.addKeyFrame(pastedFrame);
			} catch (IllegalArgumentException e) {
				// Fail silently
			}
		}

		return true;
	}

}
