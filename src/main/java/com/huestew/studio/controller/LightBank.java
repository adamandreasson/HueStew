/**
 * 
 */
package com.huestew.studio.controller;

import java.util.HashMap;
import com.huestew.studio.model.LightTrack;
import com.huestew.studio.view.Light;

/**
 * A collection of currently available lights.
 * 
 * @author Adam Andreasson
 */
public class LightBank {
	
	private static LightBank instance = null;

	private HashMap<Light, LightTrack> lights;
	
	private LightBank() {
		lights = new HashMap<>();
	}
	
	public static synchronized LightBank getInstance() {
		if (instance == null) {
			instance = new LightBank();
		}
		return instance;
	}

	/**
	 * @return a list of currently available lights
	 */
	public HashMap<Light, LightTrack> getLights() {
		return new HashMap<Light, LightTrack>(lights);
	}
	
	public void addLight(Light light) {
		lights.put(light, null);
	}
	
	public void addLight(Light light, LightTrack track) {
		lights.put(light, track);
	}
	
	public void update() {
		//TODO update all the available
	}

	public void updateLight(Light light, LightTrack track) {
		lights.replace(light, track);
	}

}
