package com.huestew.studio.tools;

import com.huestew.studio.controller.Tool;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightTrack;

import javafx.event.Event;
import javafx.scene.input.MouseEvent;

public class RemoveTool implements Tool {
	@Override
	public void doAction(Event event, LightTrack lightTrack, KeyFrame keyFrame, int timestamp, double normalizedY) {
		if (event.getEventType() == MouseEvent.MOUSE_CLICKED && keyFrame != null) {
			lightTrack.removeKeyFrame(keyFrame);
		}
	}
}
