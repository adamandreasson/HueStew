/**
 * 
 */
package com.huestew.studio.model;

import javafx.scene.input.KeyCode;

/**
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
	 * @param key the key to set
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
	 * @param sequence the sequence to set
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
	 * @param track the track to set
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
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
