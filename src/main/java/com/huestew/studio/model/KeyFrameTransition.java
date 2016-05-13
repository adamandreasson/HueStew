/**
 * 
 */
package com.huestew.studio.model;

/**
 * A keyframe holder class used for determination of color transitioning.
 * Stores the keyframe from which the transition will begin from and the
 * keyframe to which the transition will end at.
 * @author Adam
 *
 */
public class KeyFrameTransition {

	private KeyFrame from;
	private KeyFrame to;
	
	/**
	 * Creates a new keyframetransition object with a starting keyframe as well as a
	 * ending keyframe.
	 * @param from
	 * 				the keyframe at which the transition will start from.
	 * @param to
	 * 				the keyframe at which the transition will move to.
	 */
	public KeyFrameTransition(KeyFrame from, KeyFrame to) {
		this.from = from;
		this.to = to;
	}

	/**
	 * @return the from
	 */
	public KeyFrame getFrom() {
		return from;
	}

	/**
	 * @param from the from to set
	 */
	public void setFrom(KeyFrame from) {
		this.from = from;
	}

	/**
	 * @return the to
	 */
	public KeyFrame getTo() {
		return to;
	}

	/**
	 * @param to the to to set
	 */
	public void setTo(KeyFrame to) {
		this.to = to;
	}
	
	

}
