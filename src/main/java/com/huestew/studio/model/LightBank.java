/**
 * 
 */
package com.huestew.studio.model;

import java.util.ArrayList;
import java.util.List;

import com.huestew.studio.view.Light;

/**
 * A collection of currently available lights.
 * 
 * @author Adam Andreasson
 */
public class LightBank {

	/** An arraylist of lights currently being available to be used **/
	private List<Light> lights = new ArrayList<>();

	/**
	 * @return a list of currently available lights
	 */
	public List<Light> getLights() {
		return lights;
	}

}
