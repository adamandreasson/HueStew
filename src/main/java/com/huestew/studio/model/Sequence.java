/**
 * 
 */
package com.huestew.studio.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam
 *
 */
public class Sequence {

	private List<KeyFrame> frames;

	public Sequence(List<KeyFrame> originalFrames) {
		frames = new ArrayList<KeyFrame>();

		int startTime = Integer.MAX_VALUE;

		for (KeyFrame frame : originalFrames) {
			if (frame.getTimestamp() < startTime)
				startTime = frame.getTimestamp();
		}

		for (KeyFrame frame : originalFrames) {
			KeyFrame sequenceFrame = new KeyFrame(frame.getTimestamp() - startTime);
			sequenceFrame.setState(frame.getState());
			
			frames.add(sequenceFrame);
		}
		
	}

	/**
	 * @return the frames
	 */
	public List<KeyFrame> getFrames() {
		return frames;
	}

}
