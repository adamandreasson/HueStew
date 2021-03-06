package com.huestew.studio.view;

import javafx.scene.Cursor;

/**
 * Enum representing a section of the TrackView, with desired cursor
 * @author Patrik
 * @author Adam
 *
 */
public enum TrackSection {
	CURSOR(Cursor.E_RESIZE), TIMELINE(Cursor.OPEN_HAND), TRACKS(null), VERTICAL_SCROLLBAR(
			Cursor.DEFAULT), HORIZONTAL_SCROLLBAR(Cursor.DEFAULT), NONE(null);

	private Cursor cursor;

	private TrackSection(Cursor cursor) {
		this.cursor = cursor;
	}

	public boolean hasCursor() {
		return cursor != null;
	}

	public Cursor getCursor() {
		return cursor;
	}
}