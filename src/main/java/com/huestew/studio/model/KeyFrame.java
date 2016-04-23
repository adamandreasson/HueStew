package com.huestew.studio.model;

/**
 * The class for a keyframe which is an event in a lighttrack
 * 
 * @author Marcus Randevik
 *
 */
public class KeyFrame implements Comparable<KeyFrame> {

	/** the timestamp at which the keyframe occurs **/
	private int timestamp;

	private LightState state;

	/**
	 * Creates a new KeyFrame object with the specified values.
	 * 
	 * @param timestamp
	 * @param lightState
	 */
	public KeyFrame(int timestamp, LightState state) {
		this.setTimestamp(timestamp);
		this.setState(state);
	}

	/**
	 * Method for getting the timestamp of the keyframe, will be zero or greater
	 * 
	 * @return the timestamp of the keyframe
	 */
	public int getTimestamp() {
		return this.timestamp;
	}

	/**
	 * Method for setting the value of timestamp. The new value must be greater
	 * than zero.
	 * 
	 * @param timestamp
	 *            must be greater than zero.
	 * @throws IllegalArgumentException
	 *             if timestamp is less than zero.
	 */
	public void setTimestamp(int timestamp) {
		if (timestamp >= 0) {
			this.timestamp = timestamp;
		} else {
			throw new IllegalArgumentException("Timestamp cannot be less than zero");
		}
	}

	/**
	 * @return the state
	 */
	public LightState getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(LightState state) {
		this.state = state;
	}

	@Override
	/**
	 * Compares two keyframes based on their timestamp.
	 */
	public int compareTo(KeyFrame o) {
		return this.timestamp - o.getTimestamp();
	}

}
