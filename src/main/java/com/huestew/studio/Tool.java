package com.huestew.studio;

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
	 * @param timestamp
	 *            At which time the event should occur on the lighttrack.
	 * @param normalizedY 
	 *
	 */
	void doAction(Event event, LightTrack lightTrack, int timestamp, double normalizedY);

}