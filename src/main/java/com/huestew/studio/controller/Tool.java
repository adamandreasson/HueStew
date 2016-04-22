package com.huestew.studio.controller;

import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightTrack;

import javafx.event.Event;

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
	 * Manipulates a lighttrack depending on the event.
	 * 
	 * @param event
	 *            The event to be performed.
	 * @param lightTrack
	 *            The lighttrack that is being manipulated.
	 * @param keyFrame
	 *            The key frame that is being manipulated. Can be null.
	 * @param timestamp
	 *            At which time the event should occur on the lighttrack.
	 * @param normalizedY
	 *            The normalized y coordinate of where the interaction occurred.
	 *
	 */
	void doAction(Event event, LightTrack lightTrack, KeyFrame keyFrame, int timestamp, double normalizedY);

}