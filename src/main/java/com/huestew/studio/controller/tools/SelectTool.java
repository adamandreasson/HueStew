package com.huestew.studio.controller.tools;

import java.util.ArrayList;
import java.util.List;

import com.huestew.studio.command.Command;
import com.huestew.studio.command.CommandManager;
import com.huestew.studio.model.HueStewConfig;
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
 * @author Adam
 *
 */
public class SelectTool extends Tool {

	private List<KeyFrame> selectedKeyFrames;
	private boolean ctrlDown = false;
	private boolean shiftDown = false;

	public SelectTool(Toolbox toolbox) {
		super(toolbox);

	}

	/**
	 * Delete all selected frames and update the track view
	 */
	public void deleteSelectedKeyFrames() {
		if (selectedKeyFrames == null || selectedKeyFrames.isEmpty()) {
			return;
		}
		CommandManager.getInstance().executeCmd(new deleteKeyFramesCommand());
	}

	private class deleteKeyFramesCommand implements Command {

		List<KeyFrame> removedKeyFrames = new ArrayList<KeyFrame>();

		@Override
		public void execute() {

			// A copy of the keyframes to be removed whilst removing
			for (KeyFrame frame : selectedKeyFrames) {
				removedKeyFrames.add(new KeyFrame(frame));
				frame.remove();
				System.out.println("deleteCommand");
			}

			selectedKeyFrames.clear();

		}

		@Override
		public void undo() {
			LightTrack selected = removedKeyFrames.get(0).track();
			for (KeyFrame frame : removedKeyFrames) {
				selected.addKeyFrame(frame);
			}

		}

		@Override
		public void redo() {
			for (KeyFrame frame : removedKeyFrames) {
				frame.remove();
			}
		}

	}

	@Override
	public void doAction(MouseEvent event, LightTrack lightTrack, KeyFrame keyFrame, List<KeyFrame> keyFramesSelected,
			int timestamp, double normalizedY) {

		// Always update list of selected key frames with the one given by
		// TrackView selection
		selectedKeyFrames = keyFramesSelected;

		// Variables used later to determine the change of the keyframes.
		int timeDelta = 0;
		int brightnessDelta = 0;

		// Stop if the mouse is not on a keyframe
		if (keyFrame == null) {
			return;
		}

		if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {

			if (keyFramesSelected.isEmpty()) {

				// if there are no selected keyframes and we press a keyframe,
				// select it
				keyFramesSelected.add(keyFrame);

			} else {

				// if you click a keyframe that is not in the current selection
				if (!keyFramesSelected.contains(keyFrame)) {

					// if ctrl is not down, clear the selection
					if (!ctrlDown) {
						keyFramesSelected.clear();
					}

					// add the clicked keyframe to the selection
					keyFramesSelected.add(keyFrame);

				}
			}
		} else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED && !selectedKeyFrames.isEmpty()) {

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

				// If the move is allowed, set the time delta.
				if (allowedMove) {

					timeDelta = delta;

				}

			}

			if (!ctrlDown) {

				// calculate difference between current brightness and the
				// proposed brightness as given by the normalized y value
				brightnessDelta = ((int) (normalizedY * 255)) - keyFrame.getState().getBrightness();

			}

			CommandManager.getInstance().executeCmd(new moveKeyFramesCommand(selectedKeyFrames, 
					timeDelta, brightnessDelta));
		}

	}

	private class moveKeyFramesCommand implements Command {

		private List<KeyFrame> keyFramesSelected;
		private int timeDelta;
		private int brightnessDelta;

		private moveKeyFramesCommand(List<KeyFrame> keyFramesSelected, int timeDelta, int brightnessDelta) {
			this.keyFramesSelected = keyFramesSelected;
			this.timeDelta = timeDelta;
			this.brightnessDelta = brightnessDelta;
		}

		@Override
		public void execute() {
			//Change values of the keyframes according to the delta values
			for (KeyFrame frame : keyFramesSelected) {
				int newTimestamp = frame.getTimestamp() + timeDelta;
				frame.setTimestamp(newTimestamp);
				
				int newBrightness = frame.getState().getBrightness() + brightnessDelta;
				LightState newState = new LightState(frame.getState());
				
				//No checks, if the value is out of bounds the frame will be skipped
				try {
					newState.setBrightness(newBrightness);
				} catch (IllegalArgumentException e) {
					continue;
				}
				
				//if the state was valid, the frame will be updated.
				frame.setState(newState);
			}
		}

		@Override
		public void undo() {
			//Change values of the keyframes according to the delta values
			for (KeyFrame frame : keyFramesSelected) {
				int newTimestamp = frame.getTimestamp() - timeDelta;
				frame.setTimestamp(newTimestamp);
				
				int newBrightness = frame.getState().getBrightness() - brightnessDelta;
				LightState newState = new LightState(frame.getState());
				
				//No checks, if the value is out of bounds the frame will be skipped
				try {
					newState.setBrightness(newBrightness);
				} catch (IllegalArgumentException e) {
					continue;
				}
				
				//if the state was valid, the frame will be updated.
				frame.setState(newState);
			}

		}

		@Override
		public void redo() {
			//Change values of the keyframes according to the delta values
			for (KeyFrame frame : keyFramesSelected) {
				int newTimestamp = frame.getTimestamp() + timeDelta;
				frame.setTimestamp(newTimestamp);
				
				int newBrightness = frame.getState().getBrightness() + brightnessDelta;
				LightState newState = new LightState(frame.getState());
				
				//No checks, if the value is out of bounds the frame will be skipped
				try {
					newState.setBrightness(newBrightness);
				} catch (IllegalArgumentException e) {
					continue;
				}
				
				//if the state was valid, the frame will be updated.
				frame.setState(newState);
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
