package com.huestew.studio.tools;

import java.awt.Color;

import com.huestew.studio.controller.Tool;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightState;
import com.huestew.studio.model.LightTrack;

import javafx.event.Event;

public class RemoveTool implements Tool {

	@Override
	public void doAction(Event event, LightTrack lightTrack, int timestamp, double normalizedY) {
		lightTrack.removeKeyFrame(new KeyFrame(timestamp, new LightState(Color.green, (short) (255*normalizedY), (short) 0)));

	}

}
