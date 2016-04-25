package com.huestew.studio.tools;

import java.awt.Color;

import com.huestew.studio.controller.Tool;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightState;
import com.huestew.studio.model.LightTrack;

import javafx.event.Event;
import javafx.scene.input.MouseEvent;

public class PopulateTool implements Tool {
	@Override
	public void doAction(Event event, LightTrack lightTrack, KeyFrame keyFrame, int timestamp, double normalizedY) {
		if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
			lightTrack.addKeyFrame(new KeyFrame(timestamp, new LightState(Color.green, (int) (255 * normalizedY), 0)));
		}
	}
}
