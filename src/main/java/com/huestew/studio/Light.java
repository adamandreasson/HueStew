package com.huestew.studio;

/**
 * A representation of a generic light that can receive commands in the form of
 * key frames.
 * 
 * @author Patrik Olson
 */
public interface Light {
	/**
	 * Get the identifier of this light.
	 * 
	 * @return An identifier that is unique to this light.
	 */
	String getId();

	/**
	 * Send a key frame to this light.
	 * 
	 * @param keyFrame
	 *            The key frame that should be sent to this light.
	 */
	void send(KeyFrame keyFrame);
}
