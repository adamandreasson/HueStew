package com.huestew.studio.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

import com.huestew.studio.controller.LightBank;
import com.huestew.studio.model.Audio;
import com.huestew.studio.model.Color;
import com.huestew.studio.model.HueStewConfig;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightState;
import com.huestew.studio.model.LightTrack;
import com.huestew.studio.model.MissingSongException;
import com.huestew.studio.model.Show;
import com.huestew.studio.view.Light;

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

	public void saveShow(Show show, Map<Light, LightTrack> lights) {

		JSONObject obj = new JSONObject();

		// Save tracks
		List<LightTrack> tracks = show.getLightTracks();
		JSONArray tracksJson = new JSONArray();

		for (LightTrack track : tracks) {

			JSONArray trackArr = new JSONArray(track.getKeyFrames());

			tracksJson.put(trackArr);
		}

		obj.put("tracks", tracksJson);

		// Save audio
		if (show.getAudio() != null)
			obj.put("audio", show.getAudio().getFile().getAbsolutePath());

		// Save lights
		JSONArray lightsJson = new JSONArray();
		for (Entry<Light, LightTrack> entry : lights.entrySet()) {
			if (entry.getValue() != null) {
				lightsJson.put(new JSONObject()
						.put("name", entry.getKey().getName())
						.put("track", tracks.indexOf(entry.getValue()))
						.put("type", entry.getKey().getClass().getSimpleName()));
			}
		}
		obj.put("lights", lightsJson);

		// Write to file
		System.out.println("saving to " + getSaveFile());
		try (PrintWriter out = new PrintWriter(getSaveFile(), "utf-8")) {
			out.println(obj.toString(2));
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void loadTrackData(Show show, Map<String, LightTrack> virtualLightQueue) throws MissingSongException {

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

		} catch (FileNotFoundException e) {
			// There is no saved data
			System.out.println("autosave file not found");
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		JSONObject obj = new JSONObject(everything);

		// Load tracks
		JSONArray tracksJson = obj.getJSONArray("tracks");
		for (int i = 0; i < tracksJson.length(); i++) {

			JSONArray frames = (JSONArray) tracksJson.get(i);
			LightTrack track = new LightTrack();

			for (int j = 0; j < frames.length(); j++) {
				JSONObject frameObj = (JSONObject) frames.get(j);
				JSONObject stateObj = frameObj.getJSONObject("state");
				JSONObject colorObj = stateObj.getJSONObject("color");
				Color color = new Color(colorObj.getDouble("red"), colorObj.getDouble("green"), colorObj.getDouble("blue"), colorObj.getInt("hue"));
				LightState state = new LightState(color, stateObj.getInt("brightness"), stateObj.getInt("saturation"));
				KeyFrame frame = new KeyFrame(frameObj.getInt("timestamp"), state, track);
				track.addKeyFrame(frame);
			}

			show.addLightTrack(track);

		}

		// Load audio
		if (obj.has("audio")) {
			String songPath = obj.getString("audio");

			try {
				show.setAudio(new Audio(songPath));
			} catch (IllegalArgumentException e) {
				throw new MissingSongException(songPath);
			}
		}

		// Load lights
		if (obj.has("lights")) {
			List<LightTrack> tracks = show.getLightTracks();
			JSONArray lightArr = obj.getJSONArray("lights");

			for (int i = 0; i < lightArr.length(); i++) {
				JSONObject lightObj = lightArr.getJSONObject(i);

				String name = lightObj.getString("name");
				String type = lightObj.getString("type");
				int trackIndex = lightObj.getInt("track");

				if (trackIndex < 0) {
					// TODO figure out why this happens
					continue;
				}

				if (type.equals("VirtualLight")) {
					// Let ShowController create and assign virtual light
					virtualLightQueue.put(name, show.getLightTracks().get(trackIndex));
				} else {
					// Light has hopefully already been added by its plugin
					// Assign light to track
					Light light = LightBank.getInstance().getLight(name);
					if (light != null) {
						LightBank.getInstance().updateLight(light, tracks.get(trackIndex));
					}
				}
			}
		}
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
