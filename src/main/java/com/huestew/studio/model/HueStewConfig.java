package com.huestew.studio.model;

/**
 * The config class used to store information regarding how to set up the studio 
 * as well as the show.
 * @author Adam
 *
 */
public enum HueStewConfig {
	INSTANCE;

	private String saveDirectory;
	private String saveFile;
	private String musicDirectory;
	private double volume;
	private String windowDimensions;
	private int minFrameDistance;

	public static HueStewConfig getInstance() {
		return INSTANCE;
	}

	/**
	 * @return the saveDirectory
	 */
	public String getSaveDirectory() {
		return saveDirectory;
	}

	/**
	 * @param saveDirectory the saveDirectory to set
	 */
	public void setSaveDirectory(String saveDirectory) {
		this.saveDirectory = saveDirectory;
	}

	/**
	 * @return the saveFile
	 */
	public String getSaveFile() {
		return saveFile;
	}

	/**
	 * @param saveFile the saveFile to set
	 */
	public void setSaveFile(String saveFile) {
		this.saveFile = saveFile;
	}

	/**
	 * @return the musicDirectory
	 */
	public String getMusicDirectory() {
		return musicDirectory;
	}

	/**
	 * @param musicDirectory the musicDirectory to set
	 */
	public void setMusicDirectory(String musicDirectory) {
		this.musicDirectory = musicDirectory;
	}

	/**
	 * @return the volume
	 */
	public double getVolume() {
		return volume;
	}

	/**
	 * @param volume the volume to set
	 */
	public void setVolume(double volume) {
		this.volume = volume;
	}

	/**
	 * @return the windowDimensions
	 */
	public String getWindowDimensions() {
		return windowDimensions;
	}

	/**
	 * @param windowDimensions the windowDimensions to set
	 */
	public void setWindowDimensions(String windowDimensions) {
		this.windowDimensions = windowDimensions;
	}

	/**
	 * @return the minFrameDistance
	 */
	public int getMinFrameDistance() {
		return minFrameDistance;
	}

	/**
	 * @param minFrameDistance the minFrameDistance to set
	 */
	public void setMinFrameDistance(int minFrameDistance) {
		this.minFrameDistance = minFrameDistance;
	}

	public static void setDefaults() {
		HueStewConfig.getInstance().setSaveDirectory(System.getProperty("user.home"));
		HueStewConfig.getInstance().setSaveFile("");
		HueStewConfig.getInstance().setMusicDirectory(System.getProperty("user.home"));
		HueStewConfig.getInstance().setVolume(1.0);
		HueStewConfig.getInstance().setWindowDimensions("");
	}

}
