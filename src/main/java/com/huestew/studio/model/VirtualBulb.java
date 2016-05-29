package com.huestew.studio.model;

/**
 * A class for graphical representation of virtual bulbs.
 * @author Adam
 * @author Marcus
 *
 */
public class VirtualBulb {

	// the coordinates of the virtual bulb in the virtual room.
	private double x = 0;
	private double y = 0;
	private LightState state;

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
	 * @return the state
	 */
	public LightState getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(LightState state) {
		this.state = state;
	}

}
