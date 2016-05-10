package com.huestew.studio.tools;

import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightTrack;

import javafx.scene.Cursor;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class RemoveTool implements Tool {
	@Override
	public void doAction(MouseEvent event, LightTrack lightTrack, KeyFrame keyFrame, int timestamp, double normalizedY) {
		if (event.getEventType() == MouseEvent.MOUSE_CLICKED && keyFrame != null) {
			lightTrack.removeKeyFrame(keyFrame);
		}
	}

	@Override
	public void doAction(KeyEvent event) {
		// TODO Should this do anything?
	}

	@Override
	public Cursor getCursor(boolean hoveringKeyFrame, boolean isMouseDown) {
		return Cursor.CROSSHAIR;
	}
}
