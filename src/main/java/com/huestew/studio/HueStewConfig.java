/**
 * 
 */
package com.huestew.studio;

/**
 * The config class used to store information regarding how to set up the studio 
 * as well as the show.
 * @author Adam
 *
 */
public class HueStewConfig {

	private String saveDirectory;
	private String musicDirectory;
	private String musicFilePath;
	private double volume;
	
	/**
	 * Creates a new HueStewConfig object with the specified values.
	 * @param saveDirectory
	 * 						the directory used to store the show.
	 * @param musicDirectory
	 * 						the default directory to look for music files
	 * @param musicFilePath
	 * 						the path for the music file most recently used in the show
	 * @param volume
	 * 						the volume at which the music will be played at. (double between 0 and 1)
	 */
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
