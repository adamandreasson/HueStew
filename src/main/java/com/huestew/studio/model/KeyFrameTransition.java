/**
 * 
 */
package com.huestew.studio.model;

/**
 * @author Adam
 *
 */
public class KeyFrameTransition {

	private KeyFrame from;
	private KeyFrame to;
	
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
