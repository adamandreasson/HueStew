/**
 * 
 */
package com.huestew.studio.model;

/**
 * @author Adam
 *
 */
public class Color {

	private double red;
	private double green;
	private double blue;
	
	public Color(double red, double green, double blue){
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public Color(Color color){
		this.red = color.getRed();
		this.green = color.getGreen();
		this.blue = color.getBlue();
	}
	
	public Color(javafx.scene.paint.Color color){
		this.red = color.getRed();
		this.green = color.getGreen();
		this.blue = color.getBlue();
	}
	
	public javafx.scene.paint.Color toFxColor(){
		return new javafx.scene.paint.Color(red, green, blue, 1);
	}
	
	/**
	 * @return the red
	 */
	public double getRed() {
		return red;
	}
	/**
	 * @param red the red to set
	 */
	public void setRed(double red) {
		this.red = red;
	}
	/**
	 * @return the green
	 */
	public double getGreen() {
		return green;
	}
	/**
	 * @param green the green to set
	 */
	public void setGreen(double green) {
		this.green = green;
	}
	/**
	 * @return the blue
	 */
	public double getBlue() {
		return blue;
	}
	/**
	 * @param blue the blue to set
	 */
	public void setBlue(double blue) {
		this.blue = blue;
	}

	@Override
	/**
	 * @super
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int)red*73 + (int)green*13 + (int)blue*11;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Color))
			return false;
		Color other = (Color) obj;
		if (red != other.red)
			return false;
		if (green != other.green)
			return false;
		if (blue != other.blue)
			return false;
		return true;
	}
	
}
