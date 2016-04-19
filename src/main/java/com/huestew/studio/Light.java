package com.huestew.studio;

/**
 * A representation of a generic light that can receive commands in the form of
 * key frames.
 * 
 * @author Patrik Olson
 */
public class Light {
	
	private LightController controller;
	private String id;

	/**
	 * Get the identifier of this light.
	 * 
	 * @return An identifier that is unique to this light.
	 */
	String getId() {
		return id;
	}

	public LightController getController() {
		return controller;
	}

	public void setController(LightController controller) {
		this.controller = controller;
	}
}
