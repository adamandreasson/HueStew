package com.huestew.studio.controller.tools;

import java.util.List;

import com.huestew.studio.model.HueStewConfig;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightState;
import com.huestew.studio.model.LightTrack;
import com.huestew.studio.model.SnapshotManager;

import javafx.scene.Cursor;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * A combined tool for selecting, moving and deleting keyframes.
 * 
 * @author Marcus
 * @author Adam
 *
 */
public class SelectTool extends Tool {

	private List<KeyFrame> selectedKeyFrames;
	private boolean ctrlDown = false;
	private boolean shiftDown = false;
	private boolean takeSnapshot = false;

	public SelectTool(Toolbox toolbox) {
		super(toolbox);
	}

	/**
	 * Delete all selected frames and update the track view
	 */
	private void deleteSelectedKeyFrames() {
		if (selectedKeyFrames == null || selectedKeyFrames.isEmpty()) {
			return;
		}

		SnapshotManager.getInstance().commandIssued();

		for (KeyFrame frame : selectedKeyFrames) {
			frame.remove();
		}

		selectedKeyFrames.clear();

		toolbox.getController().updateTrackView();

	}

	@Override
	public void doAction(MouseEvent event, LightTrack lightTrack, KeyFrame keyFrame, List<KeyFrame> keyFramesSelected,
			int timestamp, double normalizedY) {

		// Always update list of selected key frames with the one given by
		// TrackView selection
		selectedKeyFrames = keyFramesSelected;

		// Stop if the mouse is not on a keyframe
		if (keyFrame == null) {
			return;
		}

		if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {

			takeSnapshot = true;

			if (keyFramesSelected.isEmpty()) {

				// If there are no selected key frames and we press a keyframe,
				// select it
				keyFramesSelected.add(keyFrame);

			} else {

				// If you click a frame that is not in the current selection
				if (!keyFramesSelected.contains(keyFrame)) {

					// if ctrl is not down, clear the selection
					if (!ctrlDown)
						keyFramesSelected.clear();

					// add the clicked frame to selection
					keyFramesSelected.add(keyFrame);
				}

			}

		} else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED && !selectedKeyFrames.isEmpty()) {

			if (takeSnapshot) {
				SnapshotManager.getInstance().commandIssued();
				takeSnapshot = false;
			}

			if (!shiftDown) {

				// calculate difference between mouse current timestamp and the
				// clicked frame's timestamp
				int delta = timestamp - keyFrame.getTimestamp();

				// by default the move action is allowed
				boolean allowedMove = true;

				/*
				 * this entire loop has the single purpose of finding out
				 * whether the move should be allowed we loop through each frame
				 * and look at the distance to the next and previous frame. if
				 * they are too close together, do not allow the move
				 */
				for (KeyFrame keyframe : selectedKeyFrames) {

					int maxTimestamp = Integer.MAX_VALUE;
					int minTimestamp = 0;

					KeyFrame nextFrame = keyframe.next();
					if (nextFrame != null && !selectedKeyFrames.contains(nextFrame))
						maxTimestamp = nextFrame.getTimestamp() - HueStewConfig.getInstance().getMinFrameDistance();

					KeyFrame prevFrame = keyframe.previous();
					if (prevFrame != null && !selectedKeyFrames.contains(prevFrame))
						minTimestamp = prevFrame.getTimestamp() + HueStewConfig.getInstance().getMinFrameDistance();

					int newTimestamp = keyframe.getTimestamp() + delta;

					if (newTimestamp < minTimestamp || newTimestamp > maxTimestamp || newTimestamp <= 0) {
						allowedMove = false;
						break;
					}

				}

				if (allowedMove) {

					// Move each selected frame
					for (KeyFrame keyframe : selectedKeyFrames) {

						// the new timestamp of the frame is calculated by
						// adding the delta to it's current timestamp
						int newTimestamp = keyframe.getTimestamp() + delta;
						keyframe.setTimestamp(newTimestamp);

					}

				}
			}

			if (!ctrlDown) {

				// calculate difference between current brightness and the
				// proposed brightness as given by the normalized y value
				int delta = ((int) (normalizedY * 255)) - keyFrame.getState().getBrightness();

				for (KeyFrame frame : selectedKeyFrames) {

					int newBrightness = frame.getState().getBrightness() + delta;
					LightState newState = new LightState(frame.getState());

					// no fancy checks, just skip the frame if brightness value
					// is out of bounds
					try {
						newState.setBrightness(newBrightness);
					} catch (IllegalArgumentException e) {
						continue;
					}

					// if the new state is valid, we can update the frame's
					// state
					frame.setState(newState);

				}

			}
		}

	}

	@Override
	public void doAction(KeyEvent event) {
		boolean pressed = event.getEventType() == KeyEvent.KEY_PRESSED;

		switch (event.getCode()) {
		case CONTROL:
			ctrlDown = pressed;
			break;
		case SHIFT:
			shiftDown = pressed;
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
