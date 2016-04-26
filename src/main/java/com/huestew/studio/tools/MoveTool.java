package com.huestew.studio.tools;

import java.util.TreeSet;

import com.huestew.studio.HueStew;
import com.huestew.studio.controller.Tool;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightTrack;

import javafx.event.Event;
import javafx.scene.input.MouseEvent;

public class MoveTool implements Tool {
	private KeyFrame selectedKeyFrame;
	private int floor;
	private int ceiling;
	
	@Override
	public void doAction(Event event, LightTrack lightTrack, KeyFrame keyFrame, int timestamp, double normalizedY) {
		if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
			// Store selected key frame
			selectedKeyFrame = keyFrame;
			
			if (keyFrame == null) {
				return;
			}
			
			TreeSet<KeyFrame> keyFrames = lightTrack.getKeyFrames();
			
			// Determine lower timestamp limit
			KeyFrame lower = keyFrames.lower(keyFrame);
			floor = lower == null ? 0 : lower.getTimestamp() + HueStew.getInstance().getTickDuration();
			
			// Determine upper timestamp limit
			KeyFrame higher = keyFrames.higher(keyFrame);
			ceiling = higher == null ? Integer.MAX_VALUE : higher.getTimestamp() - HueStew.getInstance().getTickDuration();
		} else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED && selectedKeyFrame != null) {
			// Update timestamp
			selectedKeyFrame.setTimestamp(Math.max(floor, Math.min(ceiling, timestamp)));
		}
	}
}
