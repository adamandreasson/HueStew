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
import java.util.Properties;

import org.json.JSONObject;

public class FileHandler {

	public static final String CONFIG_FILE = "config.properties";
	public static final String AUTOSAVE_FILE = "autosave.json";

	private String tmpDir;
	private String appDir;

	public FileHandler() throws AccessDeniedException {

		appDir = System.getProperty("user.home") + File.separator + "HueStew";
		File appDirFile = new File(appDir);
		if (!appDirFile.exists() && !appDirFile.mkdir()) {
			throw new AccessDeniedException("Could not initialize app directory in " + appDir);
		}

		tmpDir = appDir + File.separator + "temp";
		File tmpDirFile = new File(tmpDir);
		if (!tmpDirFile.exists() && !tmpDirFile.mkdir()) {
			throw new AccessDeniedException("Could not initialize temp directory in " + tmpDirFile.getAbsolutePath());
		}

		File pluginFile = new File(appDirFile, "plugins");
		if (!pluginFile.exists() && !pluginFile.mkdir()) {
			throw new AccessDeniedException("Could not initialize plugin directory in " + pluginFile.getAbsolutePath());
		}

	}

	public String getTempFilePath(String file) {
		return tmpDir + File.separator + file;
	}

	public String getAppFilePath(String file) {
		return appDir + File.separator + file;
	}

	public void saveConfig(Properties prop) throws IOException {

		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(getAppFilePath(CONFIG_FILE)), "utf-8")) {
			prop.store(writer, null);
		}

	}

	public Properties loadConfig() throws IOException {

		Properties prop = new Properties();

		try (InputStreamReader reader = new InputStreamReader(new FileInputStream(getAppFilePath(CONFIG_FILE)), "utf-8")) {
			prop.load(reader);
		}

		return prop;

	}

	public void saveJson(String path, JSONObject json) throws IOException {

		// Write to file
		System.out.println("saving to " + path);
		try (PrintWriter out = new PrintWriter(path, "utf-8")) {
			out.println(json.toString(2));
		}

	}

	public JSONObject loadJson(String path) throws IOException {

		// Read from file
		String everything;

		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "utf-8"))) {

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

	public void clean() {
	    for (File filePath : new File(tmpDir).listFiles()) {
			filePath.delete();
	    }
	}

}
