package com.huestew.studio.controller;

import java.io.File;

import com.huestew.studio.HueStew;
import com.huestew.studio.model.Audio;
import com.huestew.studio.model.LightTrack;
import com.huestew.studio.model.Show;
import com.huestew.studio.model.VirtualBulb;
import com.huestew.studio.util.FileHandler;
import com.huestew.studio.util.FileUtil;
import com.huestew.studio.util.WaveBuilder;
import com.huestew.studio.view.Light;
import com.huestew.studio.view.TrackView;
import com.huestew.studio.view.VirtualLight;

public class ShowController {

	private MainViewController controller;
	private Player player;
	private FileHandler fileHandler;
	private int cursor;
	private Show show;
	
	public ShowController(MainViewController controller){
		this.controller = controller;
		this.fileHandler = HueStew.getInstance().getFileHandler();
		
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
		HueStew.getInstance().getConfig().setWindowDimensions(controller.getWindowDimensions());
		fileHandler.saveConfig(HueStew.getInstance().getConfig());
	}

	public void save() {

		fileHandler.saveTrackData(show.getLightTracks());

	}


	public void loadAutoSave() {

		if (HueStew.getInstance().getConfig().getMusicFilePath().isEmpty())
			return;

		File musicFile = new File(HueStew.getInstance().getConfig().getMusicFilePath());
		if (!musicFile.exists())
			return;

		controller.updateFooterStatus("Loading last session...");
		initShow(musicFile);

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

			controller.getVirtualRoom().addBulb(bulb);

			track.addListener(light);		
			i++;
		}

		show.setAudio(new Audio(audioFile));
		if (player != null)
			player.stop();
		player = new Player(this);
		player.setVolume(HueStew.getInstance().getConfig().getVolume());

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
		controller.updateTrackView();

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

				controller.updateWaveImage(builder.getImagePaths());
				controller.updateFooterStatus("Ready");
			}

		});
		thread.start();

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
	
}
