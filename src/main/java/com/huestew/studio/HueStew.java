package com.huestew.studio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;

import com.huestew.studio.controller.ConfigConverter;
import com.huestew.studio.io.FileHandler;
import com.huestew.studio.model.HueStewConfig;
import com.huestew.studio.plugin.PluginHandler;
import com.huestew.studio.plugin.PluginLoader;

/**
 * Main class for the HueStew Studio model
 * 
 * @author Adam Andreasson
 */
public enum HueStew {
	INSTANCE;

	private int tickDuration;
	private FileHandler fileHandler;
	private HueStewConfig config;
	private PluginHandler pluginHandler;

	private HueStew() {
		
		try {
			this.fileHandler = new FileHandler();
		} catch (AccessDeniedException e) {
			handleError(e);
		}
		
		pluginHandler = new PluginHandler();
		File pluginFolder = new File(fileHandler.getAppFilePath("plugins/"));
		System.out.println(pluginFolder);
		new PluginLoader(pluginFolder, pluginHandler);
		
		this.tickDuration = 33;

		try {
			this.config = new ConfigConverter().fromProperties(fileHandler.loadConfig());
		} catch (FileNotFoundException e) {
			// First run, use default config
			this.config = HueStewConfig.getDefaultConfig();
		} catch (IOException e) {
			// Display error message and use default config
			handleError(e);
			this.config = HueStewConfig.getDefaultConfig();
		}
	}

	public static HueStew getInstance() {
		return INSTANCE;
	}

	private void handleError(Exception e) {
		// TODO
		System.out.println("ERROR: " + e.getMessage());
		e.printStackTrace();
	}

	public int getTickDuration() {
		return tickDuration;
	}

	public void setTickDuration(int tickDuration) {
		if (tickDuration <= 0) {
			throw new IllegalArgumentException("Tick duration must be greater than zero.");
		}

		this.tickDuration = tickDuration;
	}

	/**
	 * @return the fileHandler
	 */
	public FileHandler getFileHandler() {
		return fileHandler;
	}
	
	/**
	 * @return the pluginHandler
	 */
	public PluginHandler getPluginHandler() {
		return pluginHandler;
	}

	/**
	 * @return the config
	 */
	public HueStewConfig getConfig() {
		return config;
	}

	public void shutdown() {
		pluginHandler.sendDisable();
	}

}
