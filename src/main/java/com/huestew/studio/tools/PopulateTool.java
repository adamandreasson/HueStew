package com.huestew.studio.tools;

import java.awt.Color;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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
		if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
			// TODO let user pick a color
			Random random = ThreadLocalRandom.current();
			Color color = new Color(128 + random.nextInt(128), 128 + random.nextInt(128), 128 + random.nextInt(128));

			lightTrack.addKeyFrame(new KeyFrame(timestamp, new LightState(color, (int) (255 * normalizedY), 0)));
		}
	}

	@Override
	public void doAction(KeyEvent event) {
		// TODO Should this do anything?
	}

	@Override
	public Cursor getCursor(boolean hoveringKeyFrame, boolean isMouseDown) {
		return cursor;
	}
}
