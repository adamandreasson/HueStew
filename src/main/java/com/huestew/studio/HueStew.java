/**
 * 
 */
package com.huestew.studio;

import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.huestew.studio.controller.Player;
import com.huestew.studio.model.Audio;
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

	private HueStew() {
		this.view = new HueStewView();
		this.lightBank = new LightBank();
		this.show = new Show();
		this.tickDuration = 33;

		// TEST CODE PLS REMOVE LATER
		for (int i = 0; i < 3; i++) {
			VirtualBulb bulb = new VirtualBulb();
			bulb.setPosition(i*(1.0/3), 1.0/2);

			Light light = new VirtualLight(bulb);
			LightState state = new LightState(Color.WHITE, 255, 255);
			light.setState(state);
			lightBank.getLights().add(light);

			view.getVirtualRoom().addBulb(bulb);

			LightTrack track = new LightTrack();
			track.addListener(light);
			show.addLightTrack(track);
		}

		// TEST CODE PLS IGNORE
		show.setAudio(new Audio(new File("song.mp3")));
		player = new Player(show);


		// THIS SHOULD ALL BE ELSEWHERE
		Thread thread = new Thread(new Runnable(){

			@Override
			public void run() {
				try {
					Path tmp = Files.createTempDirectory("HueStew_");
					String tmpSongFile = tmp.toString()+"/song.wav";
					FileUtil.convertAudioFile("song.mp3", tmpSongFile);

					System.out.println("SHOW DURACTION " + show.getDuration());
					int width = (int) ((show.getDuration()/1000.0) * TrackView.PIXELS_PER_SECOND);
					System.out.println("wave width " + width);
					new WaveBuilder(tmpSongFile, "wave.png", width, 400);

					System.out.println("WAVE GENERATED DONE");
					HueStew.getInstance().getView().updateWaveImage();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		});
		thread.start();

	}

	public static HueStew getInstance() {
		if (instance == null) {
			instance = new HueStew();
		}
		return instance;
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

	public void tick(){
		// Update model logics
		setCursor(player.getCurrentTime());

		// Update track view canvas
		getView().updateTrackView();
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

}
