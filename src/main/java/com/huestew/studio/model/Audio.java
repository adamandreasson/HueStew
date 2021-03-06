package com.huestew.studio.model;

import java.io.File;

import javafx.scene.media.Media;

/**
 * Holds an audio file for the show
 * @author Adam
 *
 */
public class Audio {
	private File file;

	public Audio(File file) {
		if (!file.exists()) {
			throw new IllegalArgumentException("File does not exist");
		}
		this.file = file;
	}

	public Audio(String path) {
		this(new File(path));
	}

	public Media getFxMedia() {
		return new Media(file.toURI().toString());
	}

	public File getFile() {
		return file;
	}
}
