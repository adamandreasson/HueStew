/**
 * 
 */
package com.huestew.studio.model;

import java.awt.Color;

/**
 * A class for graphical representation of virtual bulbs.
 * @author Adam
 * @author Marcus
 *
 */
public class VirtualBulb {

	/** the coordinates of the virtual bulb in the virtual room. **/
	private double x = 0;
	private double y = 0;
	/** the color of the virtual bulb. **/
	private Color color;

	/**
	 * Sets the new position for the virtual bulb.
	 * @param x 
	 * 			the new x coordinate of the bulb.
	 * @param y
	 * 			the new y coordinate of the bulb.
	 */
	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Get the x coordinate of the bulb.
	 * @return
	 * 			the x coordinate of the bulb.
	 */
	public double getX() {
		return x;
	}
	
	/**
	 * Get the y coordinate of the bulb.
	 * @return
	 * 			the y coordinate of the bulb.
	 */
	public double getY() {
		return y;
	}

	/**
	 * Get the color of the bulb.
	 * @return
	 * 			the color of the bulb as an RGB value.
	 */
	public Color getColor() {
		return new Color(color.getRGB());
	}

	/**
	 * Change the color of the bulb.
	 * @param color
	 * 				the new color of the bulb as an RGB value.
	 */
	public void setColor(Color color) {
		this.color = new Color(color.getRGB());
	}

}
