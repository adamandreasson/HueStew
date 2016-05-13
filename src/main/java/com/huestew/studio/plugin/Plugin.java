/**
 * 
 */
package com.huestew.studio.plugin;

/**
 * @author Adam
 *
 */
public abstract class Plugin {

	public abstract void onEnable();

	public abstract void sendLightState();

	public abstract void onDisable();
}
