package com.huestew.studio.controller;

import java.util.TreeMap;

import com.huestew.studio.model.LightTrack;
import com.huestew.studio.view.Light;

/**
 * A collection of currently available lights.
 * 
 * @author Adam Andreasson
 */
public enum LightBank {
	INSTANCE;

	private TreeMap<Light, LightTrack> lights;

	private LightBank() {
		lights = new TreeMap<Light, LightTrack>((light1, light2) -> light1.getName().compareTo(light2.getName()));
	}

	public static LightBank getInstance() {
		return INSTANCE;
	}

	/**
	 * @return a list of currently available lights
	 */
	public TreeMap<Light, LightTrack> getLights() {
		return new TreeMap<>(lights);
	}

	public void addLight(Light light) {
		lights.put(light, null);
	}

	public void addLight(Light light, LightTrack track) {
		lights.put(light, track);
	}

	public void update() {
		// TODO update all the available
	}

	public void updateLight(Light light, LightTrack track) {
		lights.replace(light, track);
	}

}
