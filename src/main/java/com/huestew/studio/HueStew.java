/**
 * 
 */
package com.huestew.studio;

import java.awt.Color;

import com.huestew.studio.model.LightBank;
import com.huestew.studio.model.LightState;
import com.huestew.studio.model.LightTrack;
import com.huestew.studio.model.Show;
import com.huestew.studio.model.VirtualBulb;
import com.huestew.studio.view.Light;
import com.huestew.studio.view.VirtualLight;
import com.huestew.studio.view.VirtualRoom;

/**
 * Main class for the HueStew Studio model
 * 
 * @author Adam Andreasson
 */
public class HueStew {

	private static HueStew instance = null;

	private VirtualRoom virtualRoom;
	private LightBank lightBank;
	private Show show;
	private int cursor;

	private HueStew() {
		this.lightBank = new LightBank();
		this.show = new Show();
		this.virtualRoom = new VirtualRoom();

		// TEST CODE PLS REMOVE LATER
		for (int i = 0; i < 3; i++) {
			VirtualBulb bulb = new VirtualBulb();
			bulb.setPosition(i*(1.0/3), 1.0/2);

			Light light = new VirtualLight(bulb);
			LightState state = new LightState(Color.WHITE, (short) 255, (short) 255);
			light.setState(state);
			lightBank.getLights().add(light);

			virtualRoom.addBulb(bulb);
			
			LightTrack track = new LightTrack();
			track.addListener(light);
			show.addLightTrack(track);
		}
	}

	public static HueStew getInstance() {
		if (instance == null) {
			instance = new HueStew();
		}
		return instance;
	}

	public VirtualRoom getVirtualRoom() {
		return virtualRoom;
	}

	public void setVirtualRoom(VirtualRoom virtualRoom) {
		this.virtualRoom = virtualRoom;
	}

	public LightBank getLightBank() {
		return lightBank;
	}

	public Show getShow() {
		return show;
	}
	
	public int getCursor() {
		return cursor;
	}
	
	public void setCursor(int cursor) {
		if (cursor < 0) {
			throw new IllegalArgumentException("Cursor must be positive.");
		}
		
		this.cursor = cursor;
		
		// Update cursor in show
		show.updateCursor(cursor);
	}
}
