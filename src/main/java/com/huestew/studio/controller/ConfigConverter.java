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
	public HueStewConfig fromProperties(Properties prop) {
		HueStewConfig config = new HueStewConfig(
				prop.getProperty("saveDir", System.getProperty("user.home")),
				prop.getProperty("saveFile", ""),
				prop.getProperty("musicDir", System.getProperty("user.home")),
				Double.parseDouble(prop.getProperty("volume", "1.0")),
				prop.getProperty("window", ""));
		return config;
	}

	/**
	 * Export HueStewConfig to a Properties object.
	 * 
	 * @param config
	 *            the HueStewConfig to export config from
	 * 
	 * @return the resulting Properties object
	 */
	public Properties toProperties(HueStewConfig config) {
		Properties prop = new Properties();
		prop.setProperty("saveDir", config.getSaveDirectory());
		prop.setProperty("saveFile", config.getSaveFile());
		prop.setProperty("musicDir", config.getMusicDirectory());
		prop.setProperty("volume", String.valueOf(config.getVolume()));
		prop.setProperty("window", config.getWindowDimensions());
		return prop;
	}
}
