package com.huestew.studio.controller;

import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightTrack;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * A tool that manipulates lighttracks.
 * 
 * @author Daniel Illipe
 * @author Patrik Olson
 * @author Marcus Randevik
 * @author Adam Andreasson
 */

public interface Tool {

	/**
	 * Manipulates a light track based on a mouse event.
	 * 
	 * @param event
	 *            The mouse event that was called.
	 * @param lightTrack
	 *            The light track that is being manipulated.
	 * @param keyFrame
	 *            The key frame that is being manipulated. Can be null.
	 * @param timestamp
	 *            The corresponding timestamp of where the event occurred on the
	 *            light track.
	 * @param normalizedY
	 *            The normalized y coordinate of where the interaction occurred.
	 *
	 */
	void doAction(MouseEvent event, LightTrack lightTrack, KeyFrame keyFrame, int timestamp, double normalizedY);

	/**
	 * Changes the behavior of the tool based on a key event.
	 * 
	 * @param event
	 *            The key event that was called.
	 */
	void doAction(KeyEvent event);
}