package com.huestew.studio;

import java.nio.file.Paths;

import com.huestew.studio.model.Show;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;

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

	public Player() {

		try {

			String song = Paths.get("song.mp3").toUri().toString();
			Media media = new Media(song);
			mediaPlayer = new MediaPlayer(media);
			mediaPlayer.setAutoPlay(false);

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

					// Update model logics
					HueStew.getInstance().setCursor(getCurrentTime());
					
					// Update track view canvas
					HueStew.getInstance().getView().updateTrackView();
					
					try {
						// Sleep for 33 ms (Run at 30 fps)
						Thread.sleep(1000 / 30);
					} catch (InterruptedException e) {
						// If the thread is interrupted, stop the loop and kill it safely
						keepRunning = false;
					}
				}
			}

		});

		playingThread.start();
	}

	/** pauses the show at the current timestamp **/
	public void pause() {
		mediaPlayer.pause();
		playingThread.interrupt();
	}

	/** stops playing the show and resets the timestamp to 0 **/
	public void stop() {
		mediaPlayer.stop();
	}

	public int getCurrentTime() {
		return (int) mediaPlayer.getCurrentTime().toMillis();
	}

	/** Whether or not the player is currently playing **/
	public boolean isPlaying() {
		return mediaPlayer.getStatus() == Status.PLAYING;
	}
}
