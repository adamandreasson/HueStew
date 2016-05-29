package com.huestew.studio.controller;

import javafx.application.Platform;
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

	private static final int TICK_RATE = 33;

	private MediaPlayer mediaPlayer;

	private Thread playingThread;

	private int pauseTime;

	private ShowController controller;

	/**
	 * Creates a new player object with a specified show to play.
	 * 
	 * @param show
	 *            the show to be played.
	 */
	public Player(ShowController controller) {

		this.controller = controller;

		try {

			Media media = controller.getShow().getAudio().getFxMedia();
			mediaPlayer = new MediaPlayer(media);
			mediaPlayer.setAutoPlay(false);

			mediaPlayer.setOnReady(() -> {
				controller.getShow().setDuration((int) media.getDuration().toMillis());
				controller.playerReady();
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Starts the player unless it's already playing. Creates a new
	 * playingThread which handles updating logic and visuals in sync with the
	 * media file.
	 */
	public void play() {
		if (isPlaying())
			return;

		pauseTime = 0;
		mediaPlayer.play();

		// TODO Put runnable in new class?
		playingThread = new Thread(() -> {
			boolean keepRunning = true;

			// Keep updating unless the thread is interrupted
			while (keepRunning) {

				Platform.runLater(() -> controller.tick());

				try {
					// Sleep for 33 ms (Run at 30 fps)
					Thread.sleep(TICK_RATE);
				} catch (InterruptedException e) {
					// If the thread is interrupted, stop the loop and kill
					// it safely
					keepRunning = false;
				}
			}

			pauseTime = 0;
		});

		playingThread.setDaemon(true);
		playingThread.start();
	}

	/**
	 * Pauses the show at the current timestamp
	 */
	public void pause() {
		if (isPlaying()) {
			pauseTime = getCurrentTime();
			mediaPlayer.pause();
			playingThread.interrupt();
		}
	}

	/**
	 * Stops playing the show and resets the timestamp to 0
	 */
	public void stop() {
		mediaPlayer.stop();
	}

	/**
	 * @return the time at which the player is at, if it's paused the pausetime
	 *         is returned.
	 */
	public int getCurrentTime() {
		if (pauseTime > 0)
			return pauseTime;
		return (int) mediaPlayer.getCurrentTime().toMillis();
	}

	/**
	 * Whether or not the player is currently playing
	 * 
	 * @return Whether or not the player is currently playing
	 */
	public boolean isPlaying() {
		return mediaPlayer.getStatus() == Status.PLAYING;
	}

	/**
	 * Move the player to a given time
	 * 
	 * @param time
	 *            The desired new time, in milliseconds
	 */
	public void seek(int time) {
		mediaPlayer.seek(Duration.millis(time));

		if (!isPlaying()) {
			pauseTime = time;
			controller.tick();
		}
	}

	/**
	 * Toggle player status (play/pause)
	 */
	public void toggle() {
		if (!isPlaying())
			play();
		else
			pause();
	}

	public void setVolume(double volume) {
		mediaPlayer.setVolume(volume);
	}
}
