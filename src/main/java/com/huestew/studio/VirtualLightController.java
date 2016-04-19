package com.huestew.studio;

/**
 * Controller for a virtual light
 * 
 * @author Adam
 *
 */
public class VirtualLightController extends LightController {

	private VirtualBulb bulb;

	public VirtualLightController(VirtualBulb bulb) {
		this.bulb = bulb;
	}

	@Override
	public void setState(LightState state) {
		System.out.println("light controller update kek");
		bulb.setColor(state.getColor());
	}

}
