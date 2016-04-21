/**
 * 
 */
package com.huestew.studio.model;

import java.util.ArrayList;
import java.util.List;

import com.huestew.studio.LightController;

/**
 * A bank of lights.
 * 
 * @author Adam Andreasson
 */
public class LightBank {

	private List<LightController> lights = new ArrayList<>();

	public List<LightController> getLights() {
		return lights;
	}

}
