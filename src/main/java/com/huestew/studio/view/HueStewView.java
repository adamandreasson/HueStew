/**
 * 
 */
package com.huestew.studio.view;

import java.util.List;

import com.huestew.studio.HueStew;
import com.huestew.studio.controller.MainViewController;
import com.huestew.studio.model.KeyFrame;

import javafx.application.Platform;
import javafx.scene.input.KeyEvent;

/**
 * @author Adam Andreasson
 *
 */
public class HueStewView {

	private VirtualRoom virtualRoom;
	private TrackView trackView;
	private MainViewController mvc;

	public HueStewView() {
		this.virtualRoom = new VirtualRoom();
	}

	public VirtualRoom getVirtualRoom() {
		return virtualRoom;
	}

	public void setVirtualRoom(VirtualRoom virtualRoom) {
		this.virtualRoom = virtualRoom;
	}

	public void setMainViewController(MainViewController mvc) {
		this.mvc = mvc;

		mvc.setVolume(HueStew.getInstance().getConfig().getVolume());
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

	public void updateWaveImage(List<String> filePaths) {
		trackView.loadWaves(filePaths);
	}

	public void handleKeyboardEvent(KeyEvent event) {
		trackView.keyboardEvent(event);
	}

	/**
	 * Update the footer status text
	 * @param msg
	 */
	public void updateFooterStatus(String msg){
		if(mvc == null)
			return;
		mvc.updateFooterStatus(msg);
	}

	public void updateTitle(String string) {
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				mvc.updateTitle(string);
			}
			
		});
	}

	public void openColorPickerPane(KeyFrame hoveringKeyFrame) {
		mvc.openColorPickerPane(hoveringKeyFrame);
	}
	
	public String getWindowDimensions(){
		return mvc.getStageSize();
	}
	
	public void setWindowDimensions(String dimensions){
		mvc.setStageSize(dimensions);
	}
}
