package com.huestew.studio;

import com.huestew.studio.model.Show;

/**
 * A class for playing a lightshow.
 * @author Marcus
 *
 */
public class Player {

	/** used to determine whether or not the player is currently playing a show **/
	private boolean playing;
	
	/** the show which the player will be playing **/
	private Show show;
	
	/** starts playing the show **/
	public void play() {
		//TODO
	}
	
	/** pauses the show at the current timestamp **/
	public void pause() {
		//TODO
	}
	
	/** stops playing the show and resets the timestamp to 0 **/
	public void stop() {
		//TODO
	}
	
	/** Determines whether or not the player is currently playing **/
	public boolean isPlaying() {
		return playing;
	}
}
