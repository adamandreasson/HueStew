package com.huestew.studio.controller;

import java.util.Properties;

import com.huestew.studio.model.HueStewConfig;

public class ConfigConverter {
	/**
	 * Import HueStewConfig from a Properties object.
	 * 
	 * @param prop
	 *            the properties object to import config from
	 * 
	 * @return the imported HueStewConfig
	 */
	public void fromProperties(Properties prop) {
		HueStewConfig.getInstance().setSaveDirectory(prop.getProperty("saveDir", System.getProperty("user.home")));
		HueStewConfig.getInstance().setSaveFile(prop.getProperty("saveFile", ""));
		HueStewConfig.getInstance().setMusicDirectory(prop.getProperty("musicDir", System.getProperty("user.home")));
		HueStewConfig.getInstance().setVolume(Double.parseDouble(prop.getProperty("volume", "1.0")));
		HueStewConfig.getInstance().setWindowDimensions(prop.getProperty("window", ""));
	}

	/**
	 * Export HueStewConfig to a Properties object.
	 * 
	 * @param config
	 *            the HueStewConfig to export config from
	 * 
	 * @return the resulting Properties object
	 */
	public Properties toProperties() {
		HueStewConfig config = HueStewConfig.getInstance();
		Properties prop = new Properties();
		prop.setProperty("saveDir", config.getSaveDirectory());
		prop.setProperty("saveFile", config.getSaveFile());
		prop.setProperty("musicDir", config.getMusicDirectory());
		prop.setProperty("volume", String.valueOf(config.getVolume()));
		prop.setProperty("window", config.getWindowDimensions());
		return prop;
	}
}
