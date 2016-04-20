/**
 * 
 */
package com.huestew.studio;

import java.awt.Color;

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

	protected HueStew() {
		this.lightBank = new LightBank();
		this.show = new Show();
		this.virtualRoom = new VirtualRoom();

		// TEST CODE PLS REMOVE LATER
		for (int i = 0; i < 3; i++) {
			VirtualBulb bulb = new VirtualBulb();
			bulb.setPosition(i*(1.0/3), 1.0/2);

			Light light = new Light();
			LightState state = new LightState(Color.WHITE, (short) 255, (short) 255);
			light.setState(state);
			light.setController(new VirtualLightController(bulb));
			lightBank.getLights().add(light);
			light.getController().setState(light.getState());

			virtualRoom.addBulb(bulb);
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
}
