package com.huestew.studio.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A light track representing a timeline of key frames.
 * 
 * @author Patrik Olson
 */
public class LightTrack {
	private SortedSet<KeyFrame> keyFrames = new TreeSet<>();
	private Set<Light> lights = new HashSet<>();

	/**
	 * Add a key frame to this light track.
	 * 
	 * @param keyFrame
	 *            The key frame that should be added.
	 * @throws IllegalArgumentException
	 *             If a key frame with an identical timestamp already exists in
	 *             this track.
	 */
	public void addKeyFrame(KeyFrame keyFrame) {
		if (keyFrames.contains(keyFrame)) {
			throw new IllegalArgumentException("There cannot be two key frames with the same timestamp.");
		}

		keyFrames.add(keyFrame);
	}

	public Iterator<KeyFrame> getKeyFrames() {
		return keyFrames.iterator();
	}
	
	/**
	 * Remove a key frame from this light track.
	 * 
	 * @param keyFrame
	 *            The key frame that should be removed.
	 */
	public void removeKeyFrame(KeyFrame keyFrame) {
		keyFrames.remove(keyFrame);
	}

	/**
	 * Connect a light to this light track.
	 * 
	 * @param light
	 *            The light that should be connected to this light track.
	 */
	public void addLight(Light light) {
		lights.add(light);
	}

	/**
	 * Disconnect a light from this light track.
	 * 
	 * @param light
	 *            The light that should be disconnected from this light track.
	 */
	public void removeLight(Light light) {
		lights.add(light);
	}
}
