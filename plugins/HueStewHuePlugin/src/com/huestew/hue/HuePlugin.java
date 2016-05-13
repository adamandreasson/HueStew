/**
 * 
 */
package com.huestew.hue;

import com.huestew.studio.plugin.Plugin;

/**
 * @author Adam
 *
 */
public class HuePlugin extends Plugin {


	@Override
	public void sendLightState() {
		System.out.println("Hue plugin sending light state bro");
	}

	@Override
	public void init() {
		System.out.println("Hue plugin initiaited");
	}

}
