package com.huestew.studio.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.huestew.studio.command.CommandManager;
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

	private static final int DEFAULT_TRACK_COUNT = 3;
	private MainViewController controller;
	private Player player;
	private Show show;
	private Map<String, LightTrack> virtualLightQueue;
	private CommandManager commandManager = new CommandManager();

	public ShowController(MainViewController controller) {
		this.controller = controller;
		this.virtualLightQueue = new TreeMap<>();
	}

	/**
	 * Tick the show (update model logics and then redraw track view)
	 */
	public void tick() {
		// Update model logics
		setCursor(player.getCurrentTime());

		// Update track view canvas
		controller.updateTrackView();
	}

	/**
	 * Save the show and user preferences to file (runs on program exit)
	 * 
	 * @throws IOException
	 *             if the config couldn't be saved to file
	 */
	public void autoSave() throws IOException {
		save();

		HueStewConfig.getInstance().setWindowDimensions(controller.getWindowDimensions());
		Properties prop = new ConfigConverter().toProperties();
		controller.getFileHandler().saveConfig(prop);
	}

	/**
	 * Save the show to JSON
	 * 
	 * @throws IOException
	 *             if saving to file was unsuccessful
	 */
	public void save() throws IOException {

		// Convert show data to json
		JSONObject json = new ShowConverter().toJson(show, LightBank.getInstance().getLights());

		// Get path to save file
		String path = HueStewConfig.getInstance().getSaveFile();

		// If no save path specified, use autosave file
		if (path.isEmpty())
			path = controller.getFileHandler().getAppFilePath(FileHandler.AUTOSAVE_FILE);

		// Save json to file
		controller.getFileHandler().saveJson(path, json);

		// Update window title
		updateTitle();

	}

	/**
	 * Create a new show with the given file
	 * 
	 * @param musicFile
	 *            The music file to base the show around, mp3 format
	 */
	public void createShow(File musicFile) {

		this.show = new Show();
		
		commandManager.clearStacks();

		// Init the SnapshotManager with the new show
		//SnapshotManager.getInstance().setShow(show);
		

		// Assign the music file to the show
		show.setAudio(new Audio(musicFile));

		// Update user preferences
		HueStewConfig.getInstance().setMusicDirectory(musicFile.getParent());
		HueStewConfig.getInstance().setSaveFile("");

		// Initialize the show
		initShow();

	}

	/**
	 * Load a show from saved file
	 * 
	 * @throws IOException
	 *             If the saved file couldn't be loaded
	 */
	public void loadShow() throws IOException, JSONException {

		this.show = new Show();
		
		commandManager.clearStacks();
		
		// Decide which path to load from
		String path = HueStewConfig.getInstance().getSaveFile();
		if (path.isEmpty()) {
			// Save path is empty, load from autosave file
			path = controller.getFileHandler().getAppFilePath(FileHandler.AUTOSAVE_FILE);
		} else if (!new File(path).exists()) {
			// File does not exist, remove save path and load from autosave file instead
			HueStewConfig.getInstance().setSaveFile("");
			path = controller.getFileHandler().getAppFilePath(FileHandler.AUTOSAVE_FILE);
		}

		try {
			// Load json from file
			JSONObject json = controller.getFileHandler().loadJson(path);

			// Convert json to show data
			new ShowConverter().fromJson(json, show, virtualLightQueue);
		} catch (FileNotFoundException e) {
			// Probably first run
			// Stop for now and wait for user to click new/open
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

		// If song is still null, wait for user to create new or load existing
		// project
		if (show.getAudio() == null)
			return;

		controller.updateFooterStatus("Loading project...");

		initShow();

	}

	/**
	 * Initialize the show, create tracks and virtual lights
	 */
	public void initShow() {

		controller.getVirtualRoom().clean();

		if (show.getLightTracks().size() < 1) {
			createEmptyTracks();
		}

		// Remove old virtual lights
		removeVirtualLights();
		
		// Reset the light bank
		LightBank.getInstance().resetLights();

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

		// Calculate all light positions in the virtual room
		controller.getVirtualRoom().calculateBulbPositions();

		// If a player already existed, stop it first
		if (player != null)
			player.stop();

		// Create a new player
		player = new Player(this);
		player.setVolume(HueStewConfig.getInstance().getVolume());

		updateTitle();
		controller.initShow();

	}

	public File browseForSong() {
		// Use a file chooser to find music file
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open music file");

		// Open last known music directory, or home directory if unavailable
		File initialDir = new File(HueStewConfig.getInstance().getMusicDirectory());
		if (!initialDir.exists())
			initialDir = new File(System.getProperty("user.home"));
		fileChooser.setInitialDirectory(initialDir);

		// Limit to MP3 files
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("MP3", "*.mp3"));

		// Return the file that was selected by the user
		return fileChooser.showOpenDialog(controller.getStage());
	}

	/**
	 * Create empty light tracks and add them to the show
	 */
	private void createEmptyTracks() {
		for (int i = 0; i < DEFAULT_TRACK_COUNT; i++) {
			LightTrack track = new LightTrack();
			show.addLightTrack(track);
		}
	}

	/**
	 * Create a new {@link LightTrack} and add it to the {@link Show}. Creates a
	 * {@link VirtualLight} assigned to the track as well
	 */
	public void addTrack() {
		LightTrack track = new LightTrack();
		show.addLightTrack(track);
		addVirtualLight(track);
		controller.updateTracks();
	}

	/**
	 * Add a new {@link VirtualLight} to the {@link Show}
	 */
	public void addVirtualLight() {
		VirtualRoom room = controller.getVirtualRoom();

		VirtualBulb bulb = new VirtualBulb();
		Light light = new VirtualLight(bulb, room.getNextBulbName(), show);
		LightBank.getInstance().addLight(light, null);
		room.addBulb(bulb);
		room.calculateBulbPositions();
		room.redraw();
	}

	/**
	 * Create and add a {@link VirtualLight} to the given {@link LightTrack}
	 * 
	 * @param track
	 *            The track to add the light to
	 */
	public void addVirtualLight(LightTrack track) {
		VirtualBulb bulb = new VirtualBulb();

		Light light = new VirtualLight(bulb, controller.getVirtualRoom().getNextBulbName(), show);
		LightBank.getInstance().addLight(light, track);

		controller.getVirtualRoom().addBulb(bulb);

		track.addListener(light);
	}

	/**
	 * Called when the song has been loaded and the player is ready. Enable the
	 * track canvas and start generating wave form image
	 */
	public void playerReady() {

		int width = (int) ((show.getDuration() / 1000.0) * TrackView.PIXELS_PER_SECOND);
		controller.updateTrackView();

		controller.updateFooterStatus("Generating waveform...");
		createWave(width);

	}

	/**
	 * Generate a waveform image of the show {@link Audio} using the
	 * {@link WaveBuilder}
	 * 
	 * @param width
	 *            Total width of the waveform in pixels
	 */
	private void createWave(int width) {

		String tmpSongFile = controller.getFileHandler().getTempFilePath("song.wav");

		// Generate the waveform on a separate thread to avoid interrupting user
		Thread thread = new Thread(() -> {
			FileUtil.convertAudioFile(show.getAudio().getFile().getPath(), tmpSongFile);

			String tmpWaveFile = controller.getFileHandler().getTempFilePath("wave");
			WaveBuilder builder = new WaveBuilder(tmpSongFile, tmpWaveFile, width, 400);

			controller.updateWaveImage(builder.getImagePaths());
			controller.updateFooterStatus("Ready");
		});
		thread.start();

	}

	/**
	 * Update the title of the show. Create an appropriate title and pass it to
	 * {@link MainViewController} for changing window title
	 */
	private void updateTitle() {
		String title = HueStewConfig.getInstance().getSaveFile();
		if (title.isEmpty()) {
			title = "Untitled";
			if (show.getAudio() != null) {
				title += " (" + FileUtil.removeExtension(show.getAudio().getFile().getName()) + ")";
			}
		} else {
			title = FileUtil.removeExtension(new File(title).getName());
		}
		controller.updateTitle(title + " - HueStew Studio");
	}

	/**
	 * Remove all {@link VirtualLight} related to the show
	 */
	private void removeVirtualLights() {
		for (Light light : LightBank.getInstance().getLights().keySet()) {
			if (light instanceof VirtualLight) {
				LightBank.getInstance().removeLight(light);
			}
		}
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

		// Redraw the light bulbs in the VirtualRoom
		controller.getVirtualRoom().redraw();
	}

	public Player getPlayer() {
		return player;
	}

	public Show getShow() {
		return show;
	}
}
