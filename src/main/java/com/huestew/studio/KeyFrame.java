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
	 * Method for getting the timestamp of the keyframe, will be zero or greater
	 * @return the timestamp of the keyframe
	 */
	public int getTimestamp() {
		return this.timestamp;
	}
	
	/**
	 * Method for setting the value of timestamp. The new value must be greater than zero.
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
	
	/**
	 * Method for getting the rgb value of a keyframe.
	 * @return the color of the keyframe as a rgb value
	 */
	public Color getRGBColor() {
		int rgb = this.rgbColor.getRGB();
		return new Color(rgb);
	}
	
	/**
	 * Method for setting the value of rgb value of a keyframe.
	 * @param color the new rgb value for the keyframe.
	 */
	public void setRGBColor(Color color) {
		Color temp = new Color(color.getRGB());
		this.rgbColor = temp;
	}
	
	/**
	 * Method for getting the hue of the keyframe.
	 * @return the hue of the keyframe
	 */
	public int getHue() {
		return this.hue;
	}
	
	/**
	 * Method for setting the hue of the keyframe
	 * @param hue must be greater than or equal to;
	 */
	public void setHue(int hue) {
		if ( hue >= 0) {
			this.hue = hue;
		} else {
			throw new IllegalArgumentException("Hue must be greater than or equal to 0");
		}
	}
	
	/**
	 * Method for getting the brightness of a keyframe.
	 * @return the brightness of the keyframe
	 */
	public short getBrightness() {
		return this.brightness;
	}
	
	/**
	 * Method for setting the brightness of a keyframe.
	 * @param brightness must be greater than -1 and less than 256.
	 */
	public void setBrightness(short brightness) {
		if (brightness > -1 && brightness < 256) {
			this.brightness = brightness;
		} else {
			throw new IllegalArgumentException(
					"Brightness must be greater than -1 and less than 256");
		}
	}
	
	
}
