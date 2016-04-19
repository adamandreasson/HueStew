package com.huestew.studio;

/**
 * A representation of a generic light that can receive commands in the form of
 * key frames.
 * 
 * @author Patrik Olson
 */
public class Light {
	
	private LightState state;
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

	public LightState getState() {
		return state;
	}

	public void setState(LightState state) {
		this.state = state;
	}

	public LightController getController() {
		return controller;
	}

	public void setController(LightController controller) {
		this.controller = controller;
	}
}
