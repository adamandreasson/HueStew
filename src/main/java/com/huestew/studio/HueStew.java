package com.huestew.studio;

import java.io.File;
import java.nio.file.AccessDeniedException;

import com.huestew.studio.controller.MainViewController;
import com.huestew.studio.controller.Player;
import com.huestew.studio.model.Audio;
import com.huestew.studio.model.HueStewConfig;
import com.huestew.studio.model.LightTrack;
import com.huestew.studio.model.Show;
import com.huestew.studio.model.VirtualBulb;
import com.huestew.studio.plugin.PluginHandler;
import com.huestew.studio.plugin.PluginLoader;
import com.huestew.studio.util.FileHandler;
import com.huestew.studio.util.FileUtil;
import com.huestew.studio.util.WaveBuilder;
import com.huestew.studio.view.Light;
import com.huestew.studio.view.LightBank;
import com.huestew.studio.view.TrackView;
import com.huestew.studio.view.VirtualLight;

/**
 * Main class for the HueStew Studio model
 * 
 * @author Adam Andreasson
 */
public enum HueStew {
	INSTANCE;

	private MainViewController controller;
	private Show show;
	private Player player;
	private int cursor;
	private int tickDuration;
	private FileHandler fileHandler;
	private HueStewConfig config;
	private PluginHandler pluginHandler;

	private HueStew() {
		
		try {
			this.fileHandler = new FileHandler();
		} catch (AccessDeniedException e) {
			handleError(e);
		}
		
		pluginHandler = new PluginHandler();
		File pluginFolder = new File(fileHandler.getAppFilePath("plugins/"));
		System.out.println(pluginFolder);
		new PluginLoader(pluginFolder, pluginHandler);
		
		this.tickDuration = 33;

		this.config = fileHandler.loadConfig();
	}

	public static HueStew getInstance() {
		return INSTANCE;
	}

	public void setController(MainViewController controller) {
		this.controller = controller;
	}

	public void loadAutoSave() {

		if (config.getMusicFilePath().isEmpty())
			return;

		File musicFile = new File(config.getMusicFilePath());
		if (!musicFile.exists())
			return;

		controller.updateFooterStatus("Loading last session...");
		initShow(musicFile);

	}

	private void handleError(Exception e) {
		// TODO
		System.out.println("ERROR: " + e.getMessage());
		e.printStackTrace();
	}

	public void tick() {
		// Update model logics
		setCursor(player.getCurrentTime());

		// Update track view canvas
		controller.getView().updateTrackView();
	}

	public void initShow(File audioFile) {

		this.show = new Show();
		
		fileHandler.loadTrackData(show);

		// TEST CODE PLS REMOVE LATER
		
		if(show.getLightTracks().size() < 1){
			createEmptyTracks();
		}
		
		int i = 0;
		for (LightTrack track : show.getLightTracks()) {
			VirtualBulb bulb = new VirtualBulb();
			double x = (i + 1) * (1.0 / (show.getLightTracks().size() + 1));
			bulb.setPosition(x, 1.0 / 2);

			Light light = new VirtualLight(bulb, "Virtual Light " + i);
			LightBank.getInstance().addLight(light, track);

			controller.getView().getVirtualRoom().addBulb(bulb);

			track.addListener(light);		
			i++;
		}

		show.setAudio(new Audio(audioFile));
		if (player != null)
			player.stop();
		player = new Player(show);
		player.setVolume(config.getVolume());

		controller.updateTitle(audioFile.getName() + " - HueStew Studio");
		controller.enableControls();
		
		controller.updateTracks();
	}

	private void createEmptyTracks() {
		for (int i = 0; i < 3; i++) {
			LightTrack track = new LightTrack();
			show.addLightTrack(track);
		}
	}

	public void playerReady() {

		int width = (int) ((show.getDuration() / 1000.0) * TrackView.PIXELS_PER_SECOND);
		controller.getView().updateTrackView();

		controller.updateFooterStatus("Generating waveform...");
		createWave(width);

	}

	private void createWave(int width) {

		String tmpSongFile = fileHandler.getTempFilePath("song.wav");
		System.out.println(tmpSongFile);

		// THIS SHOULD ALL BE ELSEWHERE
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {

				FileUtil.convertAudioFile(show.getAudio().getFile().getPath(), tmpSongFile);

				String tmpWaveFile = fileHandler.getTempFilePath("wave");
				WaveBuilder builder = new WaveBuilder(tmpSongFile, tmpWaveFile, width, 400);

				controller.getView().updateWaveImage(builder.getImagePaths());
				controller.updateFooterStatus("Ready");
			}

		});
		thread.start();

	}

	public void autoSave() {
		save();
		System.out.println("AUTO SAVING");
		config.setWindowDimensions(controller.getWindowDimensions());
		fileHandler.saveConfig(config);
	}

	public void save() {

		pluginHandler.sendDisable();
		fileHandler.saveTrackData(show.getLightTracks());

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

	public int getCursor() {
		return cursor;
	}

	public void setCursor(int cursor) {
		if (cursor < 0) {
			throw new IllegalArgumentException("Cursor must be positive.");
		}

		this.cursor = cursor;

		// Update cursor in show
		show.updateCursor(cursor);

		// TODO this should probably not be here
		controller.getView().getVirtualRoom().redraw();
	}

	public int getTickDuration() {
		return tickDuration;
	}

	public void setTickDuration(int tickDuration) {
		if (tickDuration <= 0) {
			throw new IllegalArgumentException("Tick duration must be greater than zero.");
		}

		this.tickDuration = tickDuration;
	}

	/**
	 * @return the fileHandler
	 */
	public FileHandler getFileHandler() {
		return fileHandler;
	}
	
	/**
	 * @return the pluginHandler
	 */
	public PluginHandler getPluginHandler() {
		return pluginHandler;
	}

	/**
	 * @return the config
	 */
	public HueStewConfig getConfig() {
		return config;
	}

}
