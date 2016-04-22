/**
 * 
 */
package com.huestew.studio.model;

import java.awt.Color;

/**
 * @author Adam
 * @author Marcus
 *
 */
public class VirtualBulb {

	private double x = 0;
	private double y = 0;
	private Color color;

	public void setPosition(double d, double e) {
		this.x = d;
		this.y = e;
	}

	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}

	public Color getColor() {
		return new Color(color.getRGB());
	}

	public void setColor(Color color) {
		this.color = new Color(color.getRGB());
	}

}
