/**
 * 
 */
package com.huestew.studio;

/**
 * @author Adam
 *
 */
public class HueStewConfig {

	private String saveDirectory;
	private String musicDirectory;
	private String musicFilePath;
	private double volume;
	
	public HueStewConfig(String saveDirectory, String musicDirectory, String musicFilePath, double volume){
		this.saveDirectory = saveDirectory;
		this.musicDirectory = musicDirectory;
		this.musicFilePath = musicFilePath;
		this.volume = volume;
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
	 * @return the musicFilePath
	 */
	public String getMusicFilePath() {
		return musicFilePath;
	}
	/**
	 * @param musicFilePath the musicFilePath to set
	 */
	public void setMusicFilePath(String musicFilePath) {
		this.musicFilePath = musicFilePath;
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
	
}
