package com.huestew.studio.tools;

import java.util.List;
import java.util.TreeSet;

import com.huestew.studio.HueStew;
import com.huestew.studio.controller.Tool;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightState;
import com.huestew.studio.model.LightTrack;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class MoveTool implements Tool {
	private KeyFrame selectedKeyFrame;
	private LightTrack selectedLightTrack;
	private int floor;
	private int ceiling;
	private boolean moveHorizontally = true;
	private boolean moveVertically = true;

	@Override
	public void doAction(MouseEvent event, LightTrack lightTrack, KeyFrame keyFrame, int timestamp, double normalizedY) {
		if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
			// Store selected key frame and light track
			selectedKeyFrame = keyFrame;
			selectedLightTrack = lightTrack;
			
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
			if (moveHorizontally) {
				// Update timestamp
				selectedKeyFrame.setTimestamp(Math.max(floor, Math.min(ceiling, timestamp)));
			}
			
			if (moveVertically) {
				if (lightTrack != selectedLightTrack) {
					// Correct the normalized Y value
					List<LightTrack> lightTracks = HueStew.getInstance().getShow().getLightTracks();
					if (lightTracks.indexOf(lightTrack) > lightTracks.indexOf(selectedLightTrack)) {
						normalizedY = 0;
					} else {
						normalizedY = 1;
					}
				}
				
				// Update brightness
				LightState state = selectedKeyFrame.getState();
				state.setBrightness((int) (255 * normalizedY));
				selectedKeyFrame.setState(state);
			}
		}
	}

	@Override
	public void doAction(KeyEvent event) {
		boolean pressed = event.getEventType() == KeyEvent.KEY_PRESSED;
		
		switch (event.getCode()) {
		case CONTROL:
			moveVertically = !pressed;
			break;
		case SHIFT:
			moveHorizontally = !pressed;
			break;
		default:
			break;
		}
	}
}
