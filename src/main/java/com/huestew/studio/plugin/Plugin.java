/**
 * 
 */
package com.huestew.studio.plugin;

/**
 * @author Adam
 *
 */
public interface Plugin {

	void onEnable();

	void sendLightState();

	void onDisable();
}
