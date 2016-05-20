package com.huestew.studio.model;

import java.io.File;

import javafx.scene.media.Media;

public class Audio {
	private File file;
	
	public Audio(File file) {
		if (!file.exists()) {
			throw new IllegalArgumentException("File does not exist");
		}
		this.file = file;
	}
	
	public Media getFxMedia() {
		return new Media(file.toURI().toString());
	}
	
	public File getFile(){
		return file;
	}
}
