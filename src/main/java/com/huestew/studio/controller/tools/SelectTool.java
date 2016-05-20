package com.huestew.studio.controller.tools;

import java.util.List;

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

	private List<KeyFrame> selectedKeyFrames;
	private boolean ctrlDown = false;
	private boolean shiftDown = false;

	public SelectTool(Toolbox toolbox) {
		super(toolbox);
	}

	private void deleteSelectedKeyFrames() {
		if (selectedKeyFrames != null && !selectedKeyFrames.isEmpty()) {
			
			for(KeyFrame frame : selectedKeyFrames){
				frame.remove();
			}
			
			HueStew.getInstance().getView().updateTrackView();
			
		}
	}

	@Override
	public void doAction(MouseEvent event, LightTrack lightTrack, KeyFrame keyFrame, List<KeyFrame> keyFramesSelected,
			int timestamp, double normalizedY) {

		selectedKeyFrames = keyFramesSelected;
		
		if (keyFrame == null) {
			return;
		}

		if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {

			if (keyFramesSelected.isEmpty()) {

				keyFramesSelected.add(keyFrame);

			} else {

				if (!keyFramesSelected.contains(keyFrame)) {

					// ctrl down means user wants to add frame to selection
					if(!ctrlDown)
						keyFramesSelected.clear();
					
					keyFramesSelected.add(keyFrame);
				}

			}

			keyFrame.getTimestamp();

		} else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED && !selectedKeyFrames.isEmpty()) {

			if (!shiftDown) {

				int delta = timestamp - keyFrame.getTimestamp();
				boolean allowedMove = true;

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

					if (newTimestamp < minTimestamp || newTimestamp > maxTimestamp)
						allowedMove = false;

				}

				if(allowedMove){

					for (KeyFrame keyframe : selectedKeyFrames) {

						int newTimestamp = keyframe.getTimestamp() + delta;
						keyframe.setTimestamp(newTimestamp);

					}

				}
			}

			if (!ctrlDown) {

				int delta = ((int)(normalizedY*255)) - keyFrame.getState().getBrightness();


				for (KeyFrame frame : selectedKeyFrames) {

					int newBrightness = frame.getState().getBrightness() + delta;
					LightState newState = new LightState(frame.getState());
					try{
						newState.setBrightness(newBrightness);
					}catch(IllegalArgumentException e){
						continue;
					}
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
