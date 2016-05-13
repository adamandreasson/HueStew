package com.huestew.studio.tools;




import com.huestew.studio.model.Color;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightState;
import com.huestew.studio.model.LightTrack;

import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class PopulateTool implements Tool {

	private Cursor cursor = new ImageCursor(new Image("cursor_pencil_add.png"), 4, 0);

	@Override
	public void doAction(MouseEvent event, LightTrack lightTrack, KeyFrame keyFrame, int timestamp, double normalizedY) {
		if(keyFrame != null)
			return;

		if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {

			lightTrack.addKeyFrame(new KeyFrame(timestamp, new LightState(new Color(1,1,1), (int) (255 * normalizedY), 0), lightTrack));
		}
	}

	@Override
	public void doAction(KeyEvent event) {
		// TODO Should this do anything?
	}

	@Override
	public Cursor getCursor(boolean hoveringKeyFrame, boolean isMouseDown) {
		if(hoveringKeyFrame)
			return Cursor.HAND;
		else
			return cursor;
	}
}
