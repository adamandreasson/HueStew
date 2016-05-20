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

	private LightTrack track;

	/**
	 * Creates a new KeyFrame object with the specified values.
	 * 
	 * @param timestamp
	 * @param lightState
	 */
	public KeyFrame(int timestamp, LightState state, LightTrack track) {
		this.setTimestamp(timestamp);
		this.setState(state);
		this.track = track;
	}

	public KeyFrame(int timestamp) {
		this.setTimestamp(timestamp);
	}

	public KeyFrame(KeyFrame other) {
		this(other.timestamp, other.state, other.track);
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
	 * Remove key frame from the light track.
	 */
	public void remove() {
		track.removeKeyFrame(this);
	}

	/**
	 * Method to find the previous keyframe in the track
	 * @return previous KeyFrame
	 */
	public KeyFrame previous() {
		KeyFrame temp = new KeyFrame(timestamp - 1);
		return track.getKeyFrames().floor(temp);
	}

	/**
	 * Method to find the following keyframe in the track
	 * @return next KeyFrame
	 */
	public KeyFrame next() {
		KeyFrame temp = new KeyFrame(timestamp + 1);
		return track.getKeyFrames().ceiling(temp);
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
		return new LightState(state);
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(LightState state) {
		this.state = new LightState(state);
	}

	@Override
	/**
	 * Compares two keyframes based on their timestamp.
	 */
	public int compareTo(KeyFrame o) {
		return this.timestamp - o.getTimestamp();
	}

	@Override
	/**
	 * Compares to keyframes based on their timestamp.
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof KeyFrame))
			return false;
		KeyFrame other = (KeyFrame) obj;
		if (timestamp != other.timestamp)
			return false;
		if (track != other.track)
			return false;
		return true;
	}

}
