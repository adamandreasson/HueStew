package com.huestew.studio.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

import org.json.JSONObject;

import com.huestew.studio.HueStew;
import com.huestew.studio.io.FileHandler;
import com.huestew.studio.model.Audio;
import com.huestew.studio.model.HueStewConfig;
import com.huestew.studio.model.LightTrack;
import com.huestew.studio.model.MissingSongException;
import com.huestew.studio.model.Show;
import com.huestew.studio.model.VirtualBulb;
import com.huestew.studio.util.FileUtil;
import com.huestew.studio.util.WaveBuilder;
import com.huestew.studio.view.Light;
import com.huestew.studio.view.MissingSongAlert;
import com.huestew.studio.view.TrackView;
import com.huestew.studio.view.VirtualLight;
import com.huestew.studio.view.VirtualRoom;

import javafx.stage.FileChooser;

public class ShowController {

	private MainViewController controller;
	private Player player;
	private FileHandler fileHandler;
	private Show show;
	private HueStewConfig config;
	private Map<String, LightTrack> virtualLightQueue;
	
	public ShowController(MainViewController controller){
		this.controller = controller;
		this.fileHandler = HueStew.getInstance().getFileHandler();
		this.config = HueStew.getInstance().getConfig();
		this.virtualLightQueue = new TreeMap<>();
	}

	public void tick() {
		// Update model logics
		setCursor(player.getCurrentTime());

		// Update track view canvas
		controller.updateTrackView();
	}


	public void autoSave() {
		save();
		System.out.println("AUTO SAVING");
		config.setWindowDimensions(controller.getWindowDimensions());
		Properties prop = new ConfigConverter().toProperties(config);
		try {
			fileHandler.saveConfig(prop);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void save() {

		JSONObject json = new ShowConverter().toJson(show, LightBank.getInstance().getLights());

		try {
			fileHandler.saveJson(getSaveFile(), json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		updateTitle();

	}

	public void createShow(File musicFile) {

		this.show = new Show();

		show.setAudio(new Audio(musicFile));

		config.setMusicDirectory(musicFile.getParent());
		config.setSaveFile("");

		initShow();

	}

	public void loadShow() {

		this.show = new Show();

		try {
			JSONObject json = fileHandler.loadJson(getLoadFile());
			new ShowConverter().fromJson(json, show, virtualLightQueue);
		} catch (FileNotFoundException e) {
			// Probably first run
			// Stop for now and wait for user to click new/open
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (MissingSongException e) {
			// Replace song
			MissingSongAlert alert = new MissingSongAlert(e.getPath());
			alert.showAndWait();

			File file = browseForSong();
			if (file != null && file.exists()) {
				show.setAudio(new Audio(file));
			}
		}

		// If song is still null, wait for user to create new or load existing project
		if (show.getAudio() == null)
			return;

		controller.updateFooterStatus("Loading project...");
		initShow();

	}

	public void initShow() {

		// TODO? TEST CODE PLS REMOVE LATER

		controller.getVirtualRoom().clean();

		if(show.getLightTracks().size() < 1){
			createEmptyTracks();
		}

		// Remove old virtual lights
		removeVirtualLights();

		// Re-assign virtual lights from file
		for (Entry<String, LightTrack> entry : virtualLightQueue.entrySet()) {
			addVirtualLight(entry.getValue());
		}
		virtualLightQueue.clear();

		// Make sure each track has at least one virtual light
		for (LightTrack track : show.getLightTracks()) {
			if (LightBank.getInstance().getLights(track).isEmpty()) {
				addVirtualLight(track);
			}
		}

		controller.getVirtualRoom().calculateBulbPositions();

		if (player != null)
			player.stop();

		player = new Player(this);
		player.setVolume(config.getVolume());

		updateTitle();
		controller.initShow();

	}

	public File browseForSong() {
		FileChooser fileChooser = new FileChooser();

		File initialDir = new File(config.getMusicDirectory());
		if (!initialDir.exists())
			initialDir = new File(System.getProperty("user.home"));

		fileChooser.setInitialDirectory(initialDir);
		fileChooser.setTitle("Open music file");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("MP3", "*.mp3"));

		return fileChooser.showOpenDialog(controller.getStage());
	}

	private void createEmptyTracks() {
		for (int i = 0; i < 3; i++) {
			LightTrack track = new LightTrack();
			show.addLightTrack(track);
		}
	}

	public void addTrack() {
		LightTrack track = new LightTrack();
		show.addLightTrack(track);
		addVirtualLight(track);
		controller.updateTracks();
	}

	public void addVirtualLight() {
		VirtualRoom room = controller.getVirtualRoom();
		
		VirtualBulb bulb = new VirtualBulb();
		Light light = new VirtualLight(bulb, room.getNextBulbName(), show);
		LightBank.getInstance().addLight(light, null);
		room.addBulb(bulb);
		room.calculateBulbPositions();
		room.redraw();
	}

	public void addVirtualLight(LightTrack track) {
		VirtualBulb bulb = new VirtualBulb();

		Light light = new VirtualLight(bulb, controller.getVirtualRoom().getNextBulbName(), show);
		LightBank.getInstance().addLight(light, track);

		controller.getVirtualRoom().addBulb(bulb);

		track.addListener(light);
	}

	public void playerReady() {

		int width = (int) ((show.getDuration() / 1000.0) * TrackView.PIXELS_PER_SECOND);
		controller.updateTrackView();

		controller.updateFooterStatus("Generating waveform...");
		createWave(width);

	}
	
	private void createWave(int width) {

		String tmpSongFile = fileHandler.getTempFilePath("song.wav");
		System.out.println(tmpSongFile);

		// TODO? THIS SHOULD ALL BE ELSEWHERE
		Thread thread = new Thread(() -> {
			FileUtil.convertAudioFile(show.getAudio().getFile().getPath(), tmpSongFile);

			String tmpWaveFile = fileHandler.getTempFilePath("wave");
			WaveBuilder builder = new WaveBuilder(tmpSongFile, tmpWaveFile, width, 400);

			controller.updateWaveImage(builder.getImagePaths());
			controller.updateFooterStatus("Ready");
		});
		thread.start();

	}

	public int getCursor() {
		return show.getCursor();
	}

	public void setCursor(int cursor) {
		if (cursor < 0) {
			throw new IllegalArgumentException("Cursor must be positive.");
		}

		// Update cursor in show
		show.updateCursor(cursor);

		// TODO this should probably not be here
		controller.getVirtualRoom().redraw();
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	public Show getShow() {
		return show;
	}
	
	private void updateTitle() {
		String title = config.getSaveFile();
		if (title.isEmpty()) {
			title = "Untitled";
			if (show.getAudio() != null) {
				title += " (" + removeExtension(show.getAudio().getFile().getName()) + ")";
			}
		} else {
			title = removeExtension(new File(title).getName());
		}
		controller.updateTitle(title + " - HueStew Studio");
	}
	
	private String removeExtension(String path) {
		return path.substring(0, path.lastIndexOf('.'));
	}

	private void removeVirtualLights() {
		for (Light light : LightBank.getInstance().getLights().keySet()) {
			if (light instanceof VirtualLight) {
				LightBank.getInstance().removeLight(light);
			}
		}
	}

	private String getSaveFile() {
		return config.getSaveFile().isEmpty() ? 
				fileHandler.getAppFilePath(FileHandler.AUTOSAVE_FILE) : config.getSaveFile();
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

		return fileHandler.getAppFilePath(FileHandler.AUTOSAVE_FILE);
	}
}
