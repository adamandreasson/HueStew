package com.huestew.studio;

import java.awt.Color;

/**
 * A light state. Self-explanatory.
 * 
 * @author Adam
 *
 */
public class LightState {

	/** the color of the keyframe defined as a rgb value **/
	private Color color;

	/** the brightness of the keyframe **/
	private short brightness;

	/** the saturation of the keyframe **/
	private short saturation;

	/**
	 * Creates a new KeyFrame object with the specified values.
	 * 
	 * @param color
	 * @param brightness
	 * @param saturation
	 */
	public LightState(Color color, short brightness, short saturation) {
		this.setColor(color);
		this.setBrightness(brightness);
		this.setSaturation(saturation);
	}

	/**
	 * Method for getting the rgb value of a keyframe.
	 * 
	 * @return the color of the keyframe as a rgb value
	 */
	public Color getColor() {
		return new Color(color.getRGB());
	}

	/**
	 * Method for setting the value of rgb value of a keyframe.
	 * 
	 * @param color
	 *            the new rgb value for the keyframe.
	 */
	public void setColor(Color color) {
		Color temp = new Color(color.getRGB());
		this.color = temp;
	}

	/**
	 * Method for getting the brightness of a keyframe.
	 * 
	 * @return the brightness of the keyframe
	 */
	public short getBrightness() {
		return this.brightness;
	}

	/**
	 * Method for setting the brightness of a keyframe.
	 * 
	 * @param brightness
	 *            must be greater than -1 and less than 256.
	 */
	public void setBrightness(short brightness) {
		if (brightness > -1 && brightness < 256) {
			this.brightness = brightness;
		} else {
			throw new IllegalArgumentException("Brightness must be greater than -1 and less than 256");
		}
	}

	/**
	 * Method for getting the saturation of a keyframe.
	 * 
	 * @return the saturation of the keyframe.
	 */
	public short getSaturation() {
		return this.saturation;
	}

	/**
	 * Method for setting the saturation of a keyframe.
	 * 
	 * @param saturation
	 *            must be greater than -1 and less than 256.
	 */
	public void setSaturation(short saturation) {
		if (saturation > -1 && saturation < 256) {
			this.saturation = saturation;
		} else {
			throw new IllegalArgumentException("Saturation must be greater than -1 and less than 256");
		}
	}

}
