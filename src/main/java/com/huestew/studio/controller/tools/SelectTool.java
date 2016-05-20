package com.huestew.studio.controller.tools;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.huestew.studio.HueStew;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightState;
import com.huestew.studio.model.LightTrack;

import javafx.scene.Cursor;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * A combined tool for selecting, moving and deleting keyframes.
 * 
 * @author Marcus
 *
 */
public class SelectTool extends Tool {

	private KeyFrame selectedKeyFrame;
	private LightTrack selectedLightTrack;
	private Set<KeyFrame> selectedKeyFrames;
	private int floor;
	private int ceiling;
	private boolean moveHorizontally = true;
	private boolean moveVertically = true;
	private Set<KeyFrame> withoutSelectedKeyFrame;

	public SelectTool(Toolbox toolbox) {
		super(toolbox);
	}

	private void deleteSelectedKeyFrames() {
		if (!selectedKeyFrames.isEmpty()) {
			Iterator<KeyFrame> deleter = selectedKeyFrames.iterator();

			while (deleter.hasNext()) {
				KeyFrame temp = deleter.next();
				temp.remove();
				HueStew.getInstance().getView().updateTrackView();
			}
			// selectedLightTrack.removeKeyFrame(selectedKeyFrame);
			// selectedKeyFrame = null;
		}
	}

	@Override
	public void doAction(MouseEvent event, LightTrack lightTrack, KeyFrame keyFrame, Set<KeyFrame> keyFramesSelected,
			int timestamp, double normalizedY) {
		// Store selected key frame/s and light track
		selectedKeyFrames = keyFramesSelected;
		selectedKeyFrame = keyFrame;
		selectedLightTrack = lightTrack;

		if (event.getEventType() == MouseEvent.MOUSE_RELEASED && keyFrame != null) {
			if (!selectedKeyFrames.contains(keyFrame)) {
				selectedKeyFrames.add(keyFrame);

			}
		}

		else {
			selectedKeyFrames.remove(keyFrame);
		}

		if (event.getEventType() == MouseEvent.MOUSE_RELEASED && keyFrame == null) {
			if (!selectedKeyFrames.isEmpty()) {
				return;
			} else {
				selectedKeyFrames.clear();
			}

		}

		if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {

			if (keyFrame == null) {
				return;
			}
			withoutSelectedKeyFrame = new HashSet<KeyFrame>(selectedKeyFrames);
			withoutSelectedKeyFrame.remove(selectedKeyFrame);

			TreeSet<KeyFrame> keyFrames = lightTrack.getKeyFrames();

			// Determine lower timestamp limit
			KeyFrame lower = keyFrames.lower(keyFrame);
			floor = lower == null ? 0 : lower.getTimestamp() + HueStew.getInstance().getTickDuration();

			// Determine upper timestamp limit
			KeyFrame higher = keyFrames.higher(keyFrame);
			ceiling = higher == null ? Integer.MAX_VALUE
					: higher.getTimestamp() - HueStew.getInstance().getTickDuration();
		}

		else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED && selectedKeyFrame != null) {
			// Save previous timestamp/brightness
			int tempStamp = selectedKeyFrame.getTimestamp();
			LightState tempState = selectedKeyFrame.getState();
			int tempBrightness = tempState.getBrightness();

			if (moveHorizontally) {
				// Update timestamp

				selectedKeyFrame.setTimestamp(timestamp);

				for (KeyFrame keyframe : withoutSelectedKeyFrame) {

					keyframe.setTimestamp(keyframe.getTimestamp() + (selectedKeyFrame.getTimestamp() - tempStamp));
					// keyframe.setTimestamp(Math.max(keyframe.findLeftmostTimestamp(),
					// Math.min(keyframe.findRightmostTimestamp(),
					// keyframe.getTimestamp() +
					// (selectedKeyFrame.getTimestamp() - tempStamp))));

				}

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

				if (selectedKeyFrame == null) {
					return;
				}

				// Update brightness
				LightState state = selectedKeyFrame.getState();
				state.setBrightness((int) (255 * normalizedY));
				selectedKeyFrame.setState(state);

				for (KeyFrame keyframe : withoutSelectedKeyFrame) {
					LightState stateToSet = keyframe.getState();
					stateToSet.setBrightness(stateToSet.getBrightness() + (state.getBrightness() - tempBrightness));
				}

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
		case DELETE:
			deleteSelectedKeyFrames();
			break;
		default:
			break;
		}

	}

	@Override
	public Cursor getCursor(boolean hoveringKeyFrame, boolean isMouseDown) {
		if (hoveringKeyFrame)
			return Cursor.HAND;
		else
			return Cursor.DEFAULT;
	}

}
