package com.huestew.studio.model;

import javafx.scene.paint.Color;

/**
 * A light state. Self-explanatory.
 * 
 * @author Adam
 * @author Marcus
 */
public class LightState {

	/** the color of the keyframe defined as a rgb value **/
	private Color color;

	/** the brightness of the keyframe **/
	private short brightness;

	/** the saturation of the keyframe **/
	private short saturation;
	
	/**
	 * Copies the values of a LightState
	 * @param state values which is to be copied.
	 */
	public LightState(LightState state) {
		this(state.getColor(), state.getBrightness(), state.getSaturation());
	}

	/**
	 * Creates a new KeyFrame object with the specified values.
	 * 
	 * @param color
	 * @param brightness
	 * @param saturation
	 */
	public LightState(Color color, int brightness, int saturation) {
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
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), 1);
	}

	/**
	 * Method for setting the value of rgb value of a keyframe.
	 * 
	 * @param c
	 *            the new rgb value for the keyframe.
	 */
	public void setColor(Color c) {
		Color temp = new Color(c.getRed(), c.getGreen(), c.getBlue(), 1);
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
	public void setBrightness(int brightness) {
		if (brightness > -1 && brightness < 256) {
			this.brightness = (short) brightness;
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
	public void setSaturation(int saturation) {
		if (saturation > -1 && saturation < 256) {
			this.saturation = (short) saturation;
		} else {
			throw new IllegalArgumentException("Saturation must be greater than -1 and less than 256");
		}
	}

}
