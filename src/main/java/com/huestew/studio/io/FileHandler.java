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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class for dealing with loading and saving files of different types
 * 
 * @author Adam
 *
 */
public class FileHandler {

	public static final String CONFIG_FILE = "config.properties";
	public static final String AUTOSAVE_FILE = "autosave.json";

	private String tmpDir;
	private String appDir;

	public FileHandler() throws AccessDeniedException {

		// Get root directory of program files
		appDir = System.getProperty("user.home") + File.separator + "HueStew";
		File appDirFile = new File(appDir);
		if (!appDirFile.exists() && !appDirFile.mkdir()) {
			throw new AccessDeniedException("Could not initialize app directory in " + appDir);
		}

		// Get directory containing temporary files that are deleted when the application closes
		tmpDir = appDir + File.separator + "temp";
		File tmpDirFile = new File(tmpDir);
		if (!tmpDirFile.exists() && !tmpDirFile.mkdir()) {
			throw new AccessDeniedException("Could not initialize temp directory in " + tmpDirFile.getAbsolutePath());
		}

		// Get directory containing plugins
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

	/**
	 * Save properties to file
	 * 
	 * @param prop
	 *            The Properties we wish to save to file
	 * @throws IOException
	 *             if we weren't able to save to file
	 */
	public void saveConfig(Properties prop) throws IOException {

		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(getAppFilePath(CONFIG_FILE)),
				"utf-8")) {
			prop.store(writer, null);
		}

	}

	/**
	 * Load config from file
	 * 
	 * @return Properties object with settings created from file
	 * @throws IOException
	 *             if file read was unsuccessful
	 */
	public Properties loadConfig() throws IOException {

		Properties prop = new Properties();

		try (InputStreamReader reader = new InputStreamReader(new FileInputStream(getAppFilePath(CONFIG_FILE)),
				"utf-8")) {
			prop.load(reader);
		}

		return prop;

	}

	public void saveJson(String path, JSONObject json) throws IOException {

		// Write to file
		try (PrintWriter out = new PrintWriter(path, "utf-8")) {
			out.println(json.toString(2));
		}

	}

	/**
	 * Load a JSONObject from a file in path
	 * 
	 * @param path
	 *            The path we wish to load from
	 * @return A new JSONObject from path
	 * @throws IOException
	 *             if file was unable to load or corrupt
	 */
	public JSONObject loadJson(String path) throws IOException, JSONException {

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

	/**
	 * Delete files in tmp dir
	 */
	public void clean() {
		for (File filePath : new File(tmpDir).listFiles()) {
			filePath.delete();
		}
	}

}
