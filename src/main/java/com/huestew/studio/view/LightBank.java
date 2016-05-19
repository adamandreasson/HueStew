/**
 * 
 */
package com.huestew.studio.view;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection of currently available lights.
 * 
 * @author Adam Andreasson
 */
public class LightBank {
	
	private static LightBank instance = null;

	/** An arraylist of lights currently being available to be used **/
	private List<Light> lights;
	
	private LightBank() {
		lights = new ArrayList<>();
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
	public List<Light> getLights() {
		return new ArrayList<Light>(lights);
	}
	
	public void addLight(Light light) {
		lights.add(light);
	}
	
	public void update() {
		//TODO update all the available
	}

}
