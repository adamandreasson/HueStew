package com.huestew.studio.model;

@SuppressWarnings("serial")
public class MissingSongException extends Exception {
	private String path;

	public MissingSongException(String path) {
		super("Could not find song: " + path);
		this.path = path;
	}

	public String getPath() {
		return path;
	}
}
