package com.huestew.studio.tools;

import java.awt.Color;

import com.huestew.studio.KeyFrame;
import com.huestew.studio.LightState;
import com.huestew.studio.LightTrack;
import com.huestew.studio.Tool;

import javafx.event.Event;

public class PopulateTool implements Tool {
	@Override
	public void doAction(Event event, LightTrack lightTrack, int timestamp, double normalizedY) {
		lightTrack.addKeyFrame(new KeyFrame(timestamp, new LightState(Color.green, (short) (255*normalizedY), (short) 0)));
	}
}
