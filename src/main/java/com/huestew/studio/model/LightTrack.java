package com.huestew.studio.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A light track representing a timeline of key frames.
 * 
 * @author Patrik Olson
 */
public class LightTrack {
	private TreeSet<KeyFrame> keyFrames = new TreeSet<>();
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

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

	/**
	 * 
	 * @return the iterator of the list of keyframes. 
	 */
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
	 * Add a listener.
	 * 
	 * @param listener
	 *            The listener that should be added to this light track.
	 */
	public void addListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener("latestKeyFrame", listener);
	}

	/**
	 * Remove a listener.
	 * 
	 * @param light
	 *            The listener that should be removed from this light track.
	 */
	public void removeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener("latestKeyFrame", listener);
	}

	/**
	 * Update the cursor.
	 * 
	 * @param timestamp
	 *            The new timestamp of the cursor.
	 */
	public void updateCursor(int timestamp) {
		// Find latest key frame
		KeyFrame latestKeyFrame = keyFrames.floor(new KeyFrame(timestamp, null));

		// TODO should null key frames be allowed?
		// Notify listeners
		pcs.firePropertyChange("latestKeyFrame", null, latestKeyFrame);
	}
}
