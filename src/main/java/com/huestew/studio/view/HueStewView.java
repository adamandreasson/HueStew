/**
 * 
 */
package com.huestew.studio.view;

/**
 * @author Adam Andreasson
 *
 */
public class HueStewView {

	private VirtualRoom virtualRoom;
	private TrackView trackView;

	public HueStewView() {
		this.virtualRoom = new VirtualRoom();
	}

	public VirtualRoom getVirtualRoom() {
		return virtualRoom;
	}

	public void setVirtualRoom(VirtualRoom virtualRoom) {
		this.virtualRoom = virtualRoom;
	}

	public void updateTrackView() {
		trackView.redraw();
	}

	/**
	 * @param trackView
	 *            the trackView to set
	 */
	public void setTrackView(TrackView trackView) {
		this.trackView = trackView;
	}

	public void updateWaveImage(String filePath) {
		trackView.loadWave(filePath);
	}
}
