package com.huestew.studio;

import com.huestew.studio.controller.LightController;
import com.huestew.studio.model.LightState;

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
		bulb.setColor(state.getColor());
	}
	
	public VirtualBulb getBulb(){
		return bulb;
	}

}
