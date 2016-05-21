/**
 * 
 */
package com.huestew.studio.view;

import com.huestew.studio.controller.TrackViewController;

import javafx.scene.input.KeyEvent;

/**
 * @author Adam Andreasson
 *
 */
public class HueStewView {

	private VirtualRoom virtualRoom;
	private TrackViewController trackView;

	public HueStewView() {
		this.virtualRoom = new VirtualRoom();
	}

	public VirtualRoom getVirtualRoom() {
		return virtualRoom;
	}

	public void setVirtualRoom(VirtualRoom virtualRoom) {
		this.virtualRoom = virtualRoom;
	}

	/**
	 * @param trackView
	 *            the trackView to set
	 */
	public void setTrackView(TrackViewController trackView) {
		this.trackView = trackView;
	}

	public void handleKeyboardEvent(KeyEvent event) {
		trackView.keyboardEvent(event);
	}

	public TrackViewController getTrackView() {
		return trackView;
	}
}
