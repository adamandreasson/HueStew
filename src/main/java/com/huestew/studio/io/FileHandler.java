package com.huestew.studio.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.json.JSONObject;

import com.huestew.studio.model.HueStewConfig;

public class FileHandler {
	
	private static final String CONFIG_FILE = "config.properties";
	private static final String AUTOSAVE_FILE = "autosave.json";

	private Path tmpDir;
	private String appDir;
	private HueStewConfig config;

	public FileHandler() throws AccessDeniedException {

		try {
			tmpDir = Files.createTempDirectory("HueStew_");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		appDir = System.getProperty("user.home") + File.separator + "HueStew";
		File appDirFile = new File(appDir);
		if (!appDirFile.exists() && !appDirFile.mkdir()) {
			throw new AccessDeniedException("Could not initialize app directory in " + appDir);
		}
		File pluginFile = new File(appDirFile.toString() + File.separator + "plugins");
		if (!pluginFile.exists() && !pluginFile.mkdir()) {
			throw new AccessDeniedException("Could not initialize plugin directory in " + pluginFile.getAbsolutePath());
		}

	}

	public String getTempFilePath(String file) {
		return tmpDir.toString() + File.separator + file;
	}

	public String getAppFilePath(String file) {
		return appDir + File.separator + file;
	}

	public void saveConfig(HueStewConfig config) {

		Properties prop = new Properties();

		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(getAppFilePath(CONFIG_FILE)), "utf-8")) {

			prop.setProperty("saveDir", config.getSaveDirectory());
			prop.setProperty("saveFile", config.getSaveFile());
			prop.setProperty("musicDir", config.getMusicDirectory());
			prop.setProperty("volume", String.valueOf(config.getVolume()));
			prop.setProperty("window", config.getWindowDimensions());

			prop.store(writer, null);

		} catch (IOException io) {
			io.printStackTrace();
		}

	}

	public HueStewConfig loadConfig() {

		if (new File(getAppFilePath(CONFIG_FILE)).exists()) {

			Properties prop = new Properties();

			try (InputStreamReader reader = new InputStreamReader(new FileInputStream(getAppFilePath(CONFIG_FILE)), "utf-8")) {

				prop.load(reader);

				config = new HueStewConfig(
						prop.getProperty("saveDir", System.getProperty("user.home")),
						prop.getProperty("saveFile", ""),
						prop.getProperty("musicDir", System.getProperty("user.home")),
						Double.parseDouble(prop.getProperty("volume", "1.0")),
						prop.getProperty("window", ""));
				return config;

			} catch (IOException ex) {
				System.out.println("Failed to load config, using defaults");
				ex.printStackTrace();
			}
		}

		config = new HueStewConfig(System.getProperty("user.home"), "", System.getProperty("user.home"), 1.0, "");
		return config;

	}

	public void saveShow(JSONObject json) throws IOException {

		// Write to file
		System.out.println("saving to " + getSaveFile());
		try (PrintWriter out = new PrintWriter(getSaveFile(), "utf-8")) {
			out.println(json.toString(2));
		}

	}

	public JSONObject loadShow() throws IOException {

		// Read from file
		String everything = "";

		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(getLoadFile()), "utf-8"))) {

			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			everything = sb.toString();

		}

		return new JSONObject(everything);

	}

	public void clean(){
	    for(File filePath : tmpDir.toFile().listFiles()) {
			filePath.delete();
	    }
		tmpDir.toFile().delete();
	}

	private String getSaveFile() {
		return config.getSaveFile().isEmpty() ? getAppFilePath(AUTOSAVE_FILE) : config.getSaveFile();
	}

	private String getLoadFile() {
		String path = config.getSaveFile();
		if (!path.isEmpty()) {
			if (new File(path).exists()) {
				return path;
			} else {
				config.setSaveFile("");
			}
		}

		return getAppFilePath(AUTOSAVE_FILE);
	}

}