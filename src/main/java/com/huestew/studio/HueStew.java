/**
 * 
 */
package com.huestew.studio;

import java.io.File;
import java.nio.file.AccessDeniedException;

import com.huestew.studio.controller.Player;
import com.huestew.studio.model.Audio;
import com.huestew.studio.model.Color;
import com.huestew.studio.model.LightBank;
import com.huestew.studio.model.LightState;
import com.huestew.studio.model.LightTrack;
import com.huestew.studio.model.Show;
import com.huestew.studio.model.VirtualBulb;
import com.huestew.studio.util.FileUtil;
import com.huestew.studio.util.WaveBuilder;
import com.huestew.studio.view.HueStewView;
import com.huestew.studio.view.Light;
import com.huestew.studio.view.TrackView;
import com.huestew.studio.view.VirtualLight;

/**
 * Main class for the HueStew Studio model
 * 
 * @author Adam Andreasson
 */
public class HueStew {

	private static HueStew instance = null;

	private HueStewView view;
	private LightBank lightBank;
	private Show show;
	private Player player;
	private int cursor;
	private int tickDuration;
	private FileHandler fileHandler;
	private HueStewConfig config;

	private HueStew() {
		try {
			this.fileHandler = new FileHandler();
		} catch (AccessDeniedException e) {
			handleError(e);
		}
		this.view = new HueStewView();
		this.lightBank = new LightBank();
		this.tickDuration = 33;

		this.config = fileHandler.loadConfig();

		loadAutoSave();
	}

	private void loadAutoSave() {

		if (config.getMusicFilePath().isEmpty())
			return;

		File musicFile = new File(config.getMusicFilePath());
		if (!musicFile.exists())
			return;

		view.updateFooterStatus("Loading last session...");
		initShow(musicFile);

	}

	private void handleError(Exception e) {
		// TODO
		System.out.println("ERROR: " + e.getMessage());
		e.printStackTrace();
	}

	public static HueStew getInstance() {
		if (instance == null) {
			instance = new HueStew();
		}
		return instance;
	}

	public void tick() {
		// Update model logics
		setCursor(player.getCurrentTime());

		// Update track view canvas
		getView().updateTrackView();
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

			Light light = new VirtualLight(bulb);
			LightState state = new LightState(new Color(1,1,1), 255, 255);
			light.setState(state);
			lightBank.getLights().add(light);

			view.getVirtualRoom().addBulb(bulb);

			track.addListener(light);
			
			i++;
		}

		show.setAudio(new Audio(audioFile));
		player = new Player(show);
		player.setVolume(config.getVolume());

		view.updateTitle(audioFile.getName() + " - HueStew Studio");
		view.enableControls();

	}

	private void createEmptyTracks() {
		for (int i = 0; i < 3; i++) {
			LightTrack track = new LightTrack();
			show.addLightTrack(track);
		}
	}

	public void playerReady() {

		int width = (int) ((show.getDuration() / 1000.0) * TrackView.PIXELS_PER_SECOND);
		getView().updateTrackView();

		view.updateFooterStatus("Generating waveform...");
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

				HueStew.getInstance().getView().updateWaveImage(builder.getImagePaths());
				view.updateFooterStatus("Ready");
			}

		});
		thread.start();

	}

	public void autoSave() {
		save();
		System.out.println("AUTO SAVING");
		config.setWindowDimensions(view.getWindowDimensions());
		fileHandler.saveConfig(config);
	}

	public void save() {

		fileHandler.saveTrackData();

	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	public HueStewView getView() {
		return view;
	}

	public void setView(HueStewView view) {
		this.view = view;
	}

	public LightBank getLightBank() {
		return lightBank;
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
		getView().getVirtualRoom().redraw();
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
	 * @return the config
	 */
	public HueStewConfig getConfig() {
		return config;
	}

}
