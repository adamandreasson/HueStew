/**
 * 
 */
package com.huestew.studio.model;

/**
 * A custom class for storing color. Stores the values based on the RGB scale
 * with minimum value 0 and maximum value 1 for each of the colors.
 * 
 * @author Adam
 *
 */
public class Color {

	private double red;
	private double green;
	private double blue;
	private int hue = -1;

	/**
	 * Creates a new color object with the specified values.
	 * 
	 * @param red
	 *            the amount of red.
	 * @param green
	 *            the amount of green.
	 * @param blue
	 *            the amount of blue.
	 */
	public Color(double red, double green, double blue) {
		setRed(red);
		setGreen(green);
		setBlue(blue);
	}

	/**
	 * Creates a new color object based on the values of another.
	 * 
	 * @param color
	 *            the color to be copied.
	 */
	public Color(Color color) {
		this.red = color.getRed();
		this.green = color.getGreen();
		this.blue = color.getBlue();
		this.hue = color.getHue();
	}

	/**
	 * Creates a new color object based on the values of a JavaFX color object.
	 * 
	 * @param color
	 *            the JavaFX color object to be copied.
	 */
	public Color(javafx.scene.paint.Color color) {
		this.red = color.getRed();
		this.green = color.getGreen();
		this.blue = color.getBlue();
		this.hue = (int)((color.getHue()/360.0)*65535.0);
	}

	public Color(double red, double green, double blue, int hue) {
		setRed(red);
		setGreen(green);
		setBlue(blue);
		setHue(hue);
	}

	/**
	 * Converts the color to a JavaFX color with the opacity 1.
	 * 
	 * @return the color as a JavaFx color
	 */
	public javafx.scene.paint.Color toFxColor() {
		return new javafx.scene.paint.Color(red, green, blue, 1);
	}

	/**
	 * @return the red
	 */
	public double getRed() {
		return red;
	}

	/**
	 * @param red
	 *            the red to set
	 */
	public void setRed(double red) {
		if (!(red < 0 || red > 1)) {
			this.red = red;
		} else {
			throw new IllegalArgumentException("Red must be between 0-1");
		}
	}

	/**
	 * @return the green
	 */
	public double getGreen() {
		return green;
	}

	/**
	 * @param green
	 *            the green to set
	 */
	public void setGreen(double green) {
		if (!(green < 0 || green > 1)) {
			this.green = green;
		} else {
			throw new IllegalArgumentException("Green must be between 0-1");
		}
	}

	/**
	 * @return the blue
	 */
	public double getBlue() {
		return blue;
	}

	/**
	 * @param blue
	 *            the blue to set
	 */
	public void setBlue(double blue) {
		if (!(blue < 0 || blue > 1)) {
			this.blue = blue;
		} else {
			throw new IllegalArgumentException("Blue must be between 0-1");
		}
	}

	public void setHue(int hue) {
		this.hue = hue;
	}

	public int getHue() {
		return hue;
	}
	
	@Override
	public String toString() {
		return "Color [red=" + red + ", green=" + green + ", blue=" + blue + "]";
	}

	/**
	 * @super
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (red * 269) + (int) (green * 263) + (int) (blue * 257);
		return result;
	}

	/**
	 * Compares two color objects based on their RGB values.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Color))
			return false;
		Color other = (Color) obj;
		return Math.abs(red - other.red) < 0.0001 && Math.abs(green - other.green) < 0.0001 && Math.abs(blue - other.blue) < 0.0001;
	}

}
