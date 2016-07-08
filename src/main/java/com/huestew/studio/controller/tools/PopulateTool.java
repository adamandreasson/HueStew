package com.huestew.studio.controller.tools;

import java.util.List;
import java.util.TreeSet;

import com.huestew.studio.command.Command;
import com.huestew.studio.command.CommandManager;
import com.huestew.studio.model.Color;
import com.huestew.studio.model.HueStewConfig;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightState;
import com.huestew.studio.model.LightTrack;

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
 * 
 */
public class PopulateTool extends Tool {
	
	
	private Cursor cursor = new ImageCursor(new Image("/cursor_pencil_add.png"), 4, 0);

	public PopulateTool(Toolbox toolbox) {
		super(toolbox);
	}

	@Override
	public void doAction(MouseEvent event, LightTrack lightTrack, KeyFrame keyFrame, List<KeyFrame> selectedKeyFrames,
			int timestamp, double normalizedY) {
		if (keyFrame != null) {
			return;
			
		}

		if (event.getEventType() == MouseEvent.MOUSE_PRESSED && event.getButton() == MouseButton.PRIMARY) {
			if (canPlace(lightTrack, timestamp)) {
				CommandManager.getInstance().executeCmd(new addKeyFrameCommand(lightTrack, 
						selectedKeyFrames, timestamp, normalizedY));
			}
				
		}
	}
	
	/**
	 * 
	 * 
	 *
	 */
	private class addKeyFrameCommand implements Command{
		
		private LightTrack lightTrack;
		private List<KeyFrame> selectedKeyFrames;
		private int timestamp;
		private double normalizedY;
		
		private addKeyFrameCommand(LightTrack lightTrack, 
						List<KeyFrame> selectedKeyFrames, int timestamp, double normalizedY){
			this.lightTrack = lightTrack;
			this.selectedKeyFrames = selectedKeyFrames;
			this.timestamp = timestamp;
			this.normalizedY = normalizedY;
		}
		
		@Override
		public void execute() {
			lightTrack.addKeyFrame(new KeyFrame(timestamp,
					new LightState(new Color(1, 1, 1), (int) (255 * normalizedY), 255), lightTrack));
			selectedKeyFrames.clear();
			System.out.println("exec");
		}

		@Override
		public void undo() {
			System.out.println("undo");
			lightTrack.removeKeyFrame(new KeyFrame(timestamp,
					new LightState(new Color(1, 1, 1), (int) (255 * normalizedY), 255), lightTrack));
			
		}

		@Override
		public void redo() {
			lightTrack.addKeyFrame(new KeyFrame(timestamp,
					new LightState(new Color(1, 1, 1), (int) (255 * normalizedY), 255), lightTrack));
			
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

	/**
	 * Determine whether a new {@link KeyFrame} can be placed at the timestamp
	 * 
	 * @param track
	 *            The desired track to place the frame on
	 * @param timestamp
	 *            The desired timestamp in milliseconds
	 * @return Can a new {@link KeyFrame} be placed
	 */
	private boolean canPlace(LightTrack track, int timestamp) {
		TreeSet<KeyFrame> keyFrames = track.getKeyFrames();
		KeyFrame target = new KeyFrame(timestamp);

		KeyFrame left = keyFrames.floor(target);
		KeyFrame right = keyFrames.ceiling(target);

		return !(left != null && timestamp - left.getTimestamp() < HueStewConfig.getInstance().getMinFrameDistance()
				|| right != null
						&& right.getTimestamp() - timestamp < HueStewConfig.getInstance().getMinFrameDistance());
	}
}
