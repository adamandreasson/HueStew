package com.huestew.studio;

import java.awt.Color;

/**
 * The class for a keyframe which is an event in a lighttrack
 * @author Marcus Randevik
 *
 */
public class KeyFrame {

	/** the timestamp at which the keyframe occurs **/
	private int timestamp;
	
	/** the color of the keyframe defined as a rgb value **/
	private Color rgbColor;
	
	/** the color of the keyframe as a hue value **/
	private int hue;
	
	/** the brightness of the keyframe **/
	private short brightness;
	
	/** the saturation of the keyframe **/
	private short saturation;
	
	/**
	 * Method for returning the timestamp of the keyframe, will be zero or greater
	 * @return the timestamp of the keyframe
	 */
	public int getTimestamp() {
		return this.timestamp;
	}
	
	/**
	 * Method for changning the value of timestamp. The new value must be greater than zero.
	 * @param timestamp must be greater than zero.
	 * @throws IllegalArgumentException if timestamp is less than zero.
	 */
	public void setTimestamp(int timestamp) {
		if (timestamp >= 0) {
			this.timestamp = timestamp;
		} else {
			throw new IllegalArgumentException("Timestamp cannot be less than zero");
		}
	}
	
}
