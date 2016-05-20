package com.huestew.studio.controller.tools;

import java.util.Iterator;
import java.util.Set;
import com.huestew.studio.HueStew;
import com.huestew.studio.model.KeyFrame;
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

	private Set<KeyFrame> selectedKeyFrames;
	private boolean moveHorizontally = true;
	private boolean moveVertically = true;

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

		if (keyFrame == null) {
			return;
		}

		if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {

			if (keyFramesSelected.isEmpty()) {

				keyFramesSelected.add(keyFrame);

			} else {

				if (!keyFramesSelected.contains(keyFrame)) {
					keyFramesSelected.clear();
					keyFramesSelected.add(keyFrame);
				}

			}

			selectedKeyFrames = keyFramesSelected;
			keyFrame.getTimestamp();

		} else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED && !selectedKeyFrames.isEmpty()) {

			if (moveHorizontally) {

				int delta = timestamp - keyFrame.getTimestamp();

				for (KeyFrame keyframe : selectedKeyFrames) {

					int maxTimestamp = Integer.MAX_VALUE;
					int minTimestamp = 0;

					KeyFrame nextFrame = keyframe.next();
					if (nextFrame != null)
						maxTimestamp = nextFrame.getTimestamp() - 10;

					KeyFrame prevFrame = keyframe.previous();
					if (prevFrame != null)
						minTimestamp = prevFrame.getTimestamp() + 10;

					int newTimestamp = keyframe.getTimestamp() + delta;

					if (newTimestamp < maxTimestamp && newTimestamp > minTimestamp)
						keyframe.setTimestamp(newTimestamp);

				}

			}

			if (moveVertically) {
				/*
				 * if (lightTrack != selectedLightTrack) { // Correct the
				 * normalized Y value List<LightTrack> lightTracks =
				 * HueStew.getInstance().getShow().getLightTracks(); if
				 * (lightTracks.indexOf(lightTrack) >
				 * lightTracks.indexOf(selectedLightTrack)) { normalizedY = 0; }
				 * else { normalizedY = 1; } }
				 * 
				 * if (selectedKeyFrame == null) { return; }
				 * 
				 * // Update brightness LightState state =
				 * selectedKeyFrame.getState(); state.setBrightness((int) (255 *
				 * normalizedY)); selectedKeyFrame.setState(state);
				 * 
				 * for (KeyFrame keyframe : withoutSelectedKeyFrame) {
				 * LightState stateToSet = keyframe.getState();
				 * stateToSet.setBrightness(stateToSet.getBrightness() +
				 * (state.getBrightness() - tempBrightness)); }
				 */
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
