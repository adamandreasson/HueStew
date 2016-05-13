package com.huestew.studio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class FileHandler {

	Path tmpDir;
	String appDir;

	public FileHandler() throws AccessDeniedException {

		try {
			tmpDir = Files.createTempDirectory("HueStew_");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		appDir = System.getProperty("user.home") + System.getProperty("file.separator") + "HueStew";
		File appDirFile = new File(appDir);
		if (!appDirFile.exists() && !appDirFile.mkdir()) {
			throw new AccessDeniedException("Could not initialize app directory in " + appDir);
		}
	}

	public String getTempFilePath(String file) {
		return tmpDir.toString() + System.getProperty("file.separator") + file;
	}

	public String getAppFilePath(String file) {
		return appDir + System.getProperty("file.separator") + file;
	}

	public void saveConfig(HueStewConfig config) {

		Properties prop = new Properties();
		OutputStream output = null;

		try {

			output = new FileOutputStream(getAppFilePath("config.properties"));

			prop.setProperty("saveDir", config.getSaveDirectory());
			prop.setProperty("musicDir", config.getMusicDirectory());
			prop.setProperty("musicFilePath", config.getMusicFilePath());
			prop.setProperty("volume", String.valueOf(config.getVolume()));
			prop.setProperty("window", config.getWindowDimensions());

			prop.store(output, null);

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

	}

	public HueStewConfig loadConfig() {

		if (new File(getAppFilePath("config.properties")).exists()) {

			Properties prop = new Properties();
			InputStream input = null;

			try {

				input = new FileInputStream(getAppFilePath("config.properties"));
				prop.load(input);

				return new HueStewConfig(prop.getProperty("saveDir", appDir), prop.getProperty("musicDir", System.getProperty("user.home")),
						prop.getProperty("musicFilePath", ""), Double.parseDouble(prop.getProperty("volume", "1.0")), prop.getProperty("window", ""));

			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return new HueStewConfig(appDir, System.getProperty("user.home"), "", 1.0, "");

	}

}
