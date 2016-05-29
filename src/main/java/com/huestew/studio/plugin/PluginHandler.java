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
	
	/**
	 * Add a new plugin
	 * @param plugin
	 */
	public void addPlugin(Plugin plugin){
		plugins.add(plugin);
	}

	/**
	 * Rund the onDisable() method in all loaded plugins
	 */
	public void sendDisable() {
		for(Plugin plugin : plugins){
			plugin.onDisable();
		}
	}
	
}
