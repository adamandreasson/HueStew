package com.huestew.studio.model;

import com.huestew.studio.LightController;

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

	/**
	 * Get the current state of this light
	 * @return The state which the light is currently representing
	 */
	public LightState getState() {
		return state;
	}

	/**
	 * Change the state of this light
	 * @param state
	 */
	public void setState(LightState state) {
		this.state = new LightState(state);
	}

	public LightController getController() {
		return controller;
	}

	public void setController(LightController controller) {
		this.controller = controller;
	}
}
