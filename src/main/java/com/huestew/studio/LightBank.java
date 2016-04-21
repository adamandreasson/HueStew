/**
 * 
 */
package com.huestew.studio;

import java.util.ArrayList;
import java.util.List;

/**
 * A bank of lights.
 * 
 * @author Adam Andreasson
 */
public class LightBank {

	private List<Light> lights;

	public LightBank() {
		this.lights = new ArrayList<Light>();
	}

	public List<Light> getLights() {
		return lights;
	}

}
