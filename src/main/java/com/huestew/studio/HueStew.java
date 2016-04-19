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

	protected HueStew() {
		this.lightBank = new LightBank();

		// TEST CODE PLS REMOVE LATER
		for (int i = 0; i < 3; i++) {
			VirtualBulb bulb = new VirtualBulb();

			Light light = new Light();
			LightState state = new LightState(Color.WHITE, (short) 255, (short) 255);
			light.setState(state);
			light.setController(new VirtualLightController(bulb));
			lightBank.getLights().add(light);
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
}
