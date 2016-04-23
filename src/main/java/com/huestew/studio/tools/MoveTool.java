package com.huestew.studio.tools;

import com.huestew.studio.controller.Tool;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightTrack;

import javafx.event.Event;
import javafx.scene.input.MouseEvent;

public class MoveTool implements Tool {
	private LightTrack selectedTrack;
	private KeyFrame selectedKeyFrame;
	
	@Override
	public void doAction(Event event, LightTrack lightTrack, KeyFrame keyFrame, int timestamp, double normalizedY) {
		if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
			selectedTrack = lightTrack;
			selectedKeyFrame = keyFrame;
		} else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED && selectedKeyFrame != null) {
			// Remove old key frame
			selectedTrack.removeKeyFrame(selectedKeyFrame);
			
			// Add new key frame
			selectedKeyFrame = new KeyFrame(timestamp, selectedKeyFrame.getState());
			selectedTrack.addKeyFrame(selectedKeyFrame);
		}
	}
}
