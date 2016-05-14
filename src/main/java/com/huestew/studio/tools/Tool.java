package com.huestew.studio.tools;

import java.util.Set;

import com.huestew.studio.Toolbox;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightTrack;

import javafx.scene.Cursor;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * A tool that manipulates lighttracks.
 * 
 * @author Daniel Illipe
 * @author Patrik Olson
 * @author Marcus Randevik
 * @author Adam Andreasson
 */

public abstract class Tool {
	private Toolbox toolbox;
	
	public Tool(Toolbox toolbox) {
		this.toolbox = toolbox;
	}
	
	public void select() {
		toolbox.setSelectedTool(this);
	}
	
	public void setActive() {
		toolbox.setActiveTool(this);
	}

	/**
	 * Manipulates a light track based on a mouse event.
	 * 
	 * @param event
	 *            The mouse event that was called.
	 * @param lightTrack
	 *            The light track that is being manipulated.
	 * @param keyFrame
	 *            The key frame that is being manipulated. Can be null.
	 * @param timestamp
	 *            The corresponding timestamp of where the event occurred on the
	 *            light track.
	 * @param normalizedY
	 *            The normalized y coordinate of where the interaction occurred.
	 *
	 */
	public abstract void doAction(MouseEvent event, LightTrack lightTrack, KeyFrame keyFrame, Set<KeyFrame> selectedKeyFrames, int timestamp, double normalizedY);

	/**
	 * Changes the behavior of the tool based on a key event.
	 * 
	 * @param event
	 *            The key event that was called.
	 */
	public abstract void doAction(KeyEvent event);

	/**
	 * Returns the desired cursor depending on mouse position and state.
	 * 
	 * @param hoveringKeyFrame
	 *            Is the mouse cursor hovering a keyframe on the trackview?
	 * @param isMouseDown
	 *            Is the left mouse button down?
	 * 
	 */
	public abstract Cursor getCursor(boolean hoveringKeyFrame, boolean isMouseDown);
}