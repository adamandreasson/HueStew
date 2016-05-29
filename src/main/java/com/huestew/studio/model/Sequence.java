/**
 * 
 */
package com.huestew.studio.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A sequence of key frames
 * 
 * @author Adam
 *
 */
public class Sequence {

	private List<KeyFrame> frames;

	/**
	 * Create a new sequence with given frames. This will calculate new time
	 * stamps for all frames with 0 at the first time stamp.
	 * 
	 * @param originalFrames
	 *            The frames to base the selection on
	 */
	public Sequence(List<KeyFrame> originalFrames) {
		frames = new ArrayList<KeyFrame>();

		int startTime = Integer.MAX_VALUE;

		for (KeyFrame frame : originalFrames) {
			if (frame.getTimestamp() < startTime)
				startTime = frame.getTimestamp();
		}

		for (KeyFrame frame : originalFrames) {
			KeyFrame sequenceFrame = new KeyFrame(frame);
			sequenceFrame.setTimestamp(frame.getTimestamp() - startTime);
			sequenceFrame.setState(frame.getState());

			frames.add(sequenceFrame);
		}

	}

	/**
	 * @return the frames in the sequence
	 */
	public List<KeyFrame> getFrames() {
		return frames;
	}

}
