package com.huestew.studio;

/**
 * Controller for a virtual light
 * 
 * @author Adam
 *
 */
public class VirtualLightController extends LightController {

	@Override
	public void setState(LightState state) {
		System.out.println("light controller update kek");
	}
	
}
