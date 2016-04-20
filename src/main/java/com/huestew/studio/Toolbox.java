package com.huestew.studio;

import com.huestew.studio.tools.*;

import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;

/**
 * A toolbox containing a set of tools.
 * 
 * @author Daniel Illipe
 * @author Patrik Olson
 * @author Marcus Randevik
 * @author Adam Andreasson
 */

public enum Toolbox {
	POPULATE(new PopulateTool(), new ImageCursor(new Image("cursor_pencil_add.png"), 4, 0)),
	MOVE(new MoveTool(), Cursor.MOVE),
	REMOVE(new RemoveTool(), Cursor.CLOSED_HAND);

	private Tool tool;
	private Cursor cursor;

	Toolbox(Tool tool, Cursor cursor) {
		this.tool = tool;
		this.cursor = cursor;
	}

	/**
	 * Select this tool.
	 */
	public void select() {
		selected = this;
	}

	private static Toolbox selected = POPULATE;

	public static Tool getTool() {
		return selected.tool;
	}

	public static Cursor getCursor() {
		return selected.cursor;
	}
}