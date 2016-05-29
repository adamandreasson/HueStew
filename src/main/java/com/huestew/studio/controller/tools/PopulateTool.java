package com.huestew.studio.controller.tools;

import java.util.List;
import java.util.TreeSet;

import com.huestew.studio.model.Color;
import com.huestew.studio.model.HueStewConfig;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightState;
import com.huestew.studio.model.LightTrack;
import com.huestew.studio.model.SnapshotManager;

import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * A tool for adding new key frames to a light track.
 * 
 * @author Patrik
 */
public class PopulateTool extends Tool {

	private Cursor cursor = new ImageCursor(new Image("/cursor_pencil_add.png"), 4, 0);

	public PopulateTool(Toolbox toolbox) {
		super(toolbox);
	}

	@Override
	public void doAction(MouseEvent event, LightTrack lightTrack, KeyFrame keyFrame, List<KeyFrame> selectedKeyFrames, int timestamp, double normalizedY) {
		if (keyFrame != null){
			return;
		}

		if (event.getEventType() == MouseEvent.MOUSE_PRESSED && event.getButton() == MouseButton.PRIMARY) {
			if (canPlace(lightTrack, timestamp)) {
				SnapshotManager.getInstance().commandIssued();

				lightTrack.addKeyFrame(new KeyFrame(timestamp, new LightState(new Color(1, 1, 1), (int) (255 * normalizedY), 255), lightTrack));
				selectedKeyFrames.clear();
			}
		
		}
	}

	@Override
	public void doAction(KeyEvent event) {
		// TODO Should this do anything?
	}

	@Override
	public Cursor getCursor(boolean hoveringKeyFrame, boolean isMouseDown) {
		if (hoveringKeyFrame)
			return Cursor.HAND;
		else
			return cursor;
	}
	
	private boolean canPlace(LightTrack track, int timestamp) {
		TreeSet<KeyFrame> keyFrames = track.getKeyFrames();
		KeyFrame target = new KeyFrame(timestamp);
		
		KeyFrame left = keyFrames.floor(target);
		KeyFrame right = keyFrames.ceiling(target);
		
		return !(left != null && timestamp - left.getTimestamp() < HueStewConfig.getInstance().getMinFrameDistance()
				|| right != null && right.getTimestamp() - timestamp < HueStewConfig.getInstance().getMinFrameDistance());
	}
}
