package com.huestew.studio.controller;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import com.huestew.studio.model.Audio;
import com.huestew.studio.model.Color;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightState;
import com.huestew.studio.model.LightTrack;
import com.huestew.studio.model.MissingSongException;
import com.huestew.studio.model.Show;
import com.huestew.studio.view.Light;

public class ShowConverter {
	/**
	 * Import a show from JSON.
	 * 
	 * @param json
	 *            the json object to import show data from
	 * @param show
	 *            the show to save the imported data to
	 * @param virtualLights
	 *            a map to save imported (virtual-)light mappings to
	 * 
	 * @throws MissingSongException
	 *             if the audio file cannot be found
	 */
	public void fromJson(JSONObject obj, Show show, Map<String, LightTrack> virtualLights) throws MissingSongException {
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
					virtualLights.put(name, show.getLightTracks().get(trackIndex));
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

	/**
	 * Export a show to JSON.
	 * 
	 * @param show
	 *            the show to export data from
	 * @param lights
	 *            light mappings to export data from
	 * 
	 * @return the resulting JSON object.
	 */
	public JSONObject toJson(Show show, Map<Light, LightTrack> lights) {
		JSONObject json = new JSONObject();

		// Save tracks
		List<LightTrack> tracks = show.getLightTracks();
		JSONArray tracksJson = new JSONArray();

		for (LightTrack track : tracks) {

			JSONArray trackArr = new JSONArray(track.getKeyFrames());

			tracksJson.put(trackArr);
		}

		json.put("tracks", tracksJson);

		// Save audio
		if (show.getAudio() != null)
			json.put("audio", show.getAudio().getFile().getAbsolutePath());

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
		json.put("lights", lightsJson);

		return json;
	}
}
