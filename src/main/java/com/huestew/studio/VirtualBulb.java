/**
 * 
 */
package com.huestew.studio;

import java.awt.Color;
import java.awt.geom.Point2D;

/**
 * @author Adam
 * @author Marcus
 *
 */
public class VirtualBulb {

	private Point2D point;
	private Color color;

	public Point2D getPoint() {
		return point;
	}

	public void setPoint(Point2D point) {
		this.point = point;
	}

	public Color getColor() {
		return new Color(color.getRGB());
	}

	public void setColor(Color color) {
		this.color = new Color(color.getRGB());
	}

}
