/**
 * 
 */
package com.huestew.studio.plugin;

import java.util.HashSet;
import java.util.Set;

/**
 * Keeps control of all loaded plugins
 * @author Adam
 *
 */
public class PluginHandler {

	Set<Plugin> plugins;
	
	public PluginHandler() {
		plugins = new HashSet<Plugin>();
	}
	
	public void addPlugin(Plugin plugin){
		plugins.add(plugin);
	}
	
	public void sendLightState(){
		for(Plugin plugin : plugins){
			plugin.sendLightState();
		}
	}

	public void sendDisable() {
		for(Plugin plugin : plugins){
			plugin.onDisable();
		}
	}
	
}
