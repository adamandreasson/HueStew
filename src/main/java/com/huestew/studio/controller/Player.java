package com.huestew.studio.controller;

import com.huestew.studio.HueStew;
import com.huestew.studio.model.Show;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;

/**
 * A class for playing a lightshow.
 * 
 * @author Marcus
 * @author Adam Andreasson
 *
 */
public class Player {

	/** the show which the player will be playing **/
	private Show show;

	private MediaPlayer mediaPlayer;

	private Thread playingThread;
	
	private int pauseTime;

	public Player(Show show) {

		this.show = show;
		
		try {

			Media media = show.getAudio().getFxMedia();
			mediaPlayer = new MediaPlayer(media);
			mediaPlayer.setAutoPlay(false);

			 mediaPlayer.setOnReady(new Runnable() {

			        @Override
			        public void run() {
						show.setDuration((int)media.getDuration().toMillis());
						
						HueStew.getInstance().loadWave();
			        }
			    });
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Starts the player unless it's already playing.
	 * Creates a new playingThread which handles updating logic and visuals in sync with the media file.
	 */
	public void play() {
		if (mediaPlayer.getStatus() == Status.PLAYING)
			return;

		mediaPlayer.play();

		// TODO Put runnable in new class?
		playingThread = new Thread(new Runnable() {

			@Override
			public void run() {
				boolean keepRunning = true;

				//Keep updating unless the thread is interrupted
				while (keepRunning) {

					HueStew.getInstance().tick();

					try {
						// Sleep for 33 ms (Run at 30 fps)
						Thread.sleep(HueStew.getInstance().getTickDuration());
					} catch (InterruptedException e) {
						// If the thread is interrupted, stop the loop and kill it safely
						keepRunning = false;
					}
				}

				pauseTime = 0;
			}

		});

		playingThread.start();
	}

	/** pauses the show at the current timestamp **/
	public void pause() {
		if (mediaPlayer.getStatus() == Status.PLAYING) {
			pauseTime = getCurrentTime();
			mediaPlayer.pause();
			playingThread.interrupt();
		}
	}

	/** stops playing the show and resets the timestamp to 0 **/
	public void stop() {
		mediaPlayer.stop();
	}

	public int getCurrentTime() {
		if(pauseTime > 0)
			return pauseTime;
		return (int) mediaPlayer.getCurrentTime().toMillis();
	}

	/** Whether or not the player is currently playing **/
	public boolean isPlaying() {
		return mediaPlayer.getStatus() == Status.PLAYING;
	}

	public void seek(int time) {
		mediaPlayer.seek(Duration.millis(time));	

		if(!isPlaying()){
			HueStew.getInstance().tick();
		}
	}

	public void toggle() {
		if(!isPlaying())
			play();
		else
			pause();
	}
}
