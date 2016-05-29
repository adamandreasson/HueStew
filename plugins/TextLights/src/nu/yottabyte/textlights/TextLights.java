/**
 * 
 */
package nu.yottabyte.textlights;

import com.huestew.studio.controller.LightBank;
import com.huestew.studio.plugin.Plugin;

/**
 * A very basic plugin for demonstrating the functionality
 * @author Adam
 *
 */
public class TextLights implements Plugin{

	@Override
	public void onDisable() {
		System.out.println("Text light plugin disabled");
	}

	@Override
	public void onEnable() {
		System.out.println("Text light plugin enabled");
		
		for(int i = 0; i < 5; i++){
			TextLight light = new TextLight("Text light no. " + i);
			LightBank.getInstance().addLight(light);
		}
	}

}
