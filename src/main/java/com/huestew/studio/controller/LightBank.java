package com.huestew.studio.controller;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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

	public void updateAvailableLights(List<LightTrack> tracks) {
		for (Entry<Light, LightTrack> entry : lights.entrySet()) {
			if (entry.getValue() != null && !tracks.contains(entry.getValue())) {
				entry.setValue(null);
			}
		}
	}

	public void updateLight(Light light, LightTrack track) {
		lights.replace(light, track);
	}

	public Light getLight(String name) {
		for (Light light : lights.keySet()) {
			if (light.getName().equals(name)) {
				return light;
			}
		}
		return null;
	}

	public Set<Light> getLights(LightTrack track) {
		Set<Light> lightSet = new TreeSet<Light>((light1, light2) -> light1.getName().compareTo(light2.getName()));

		for (Entry<Light, LightTrack> entry : lights.entrySet()) {
			// TODO undo/redo will probably fuck this up, need to implement equals in LightTrack or just replace keyframes in lighttracks
			if (track.equals(entry.getValue())) {
				lightSet.add(entry.getKey());
			}
		}

		return lightSet;
	}

	public void removeLight(Light light) {
		lights.remove(light);
	}
}
