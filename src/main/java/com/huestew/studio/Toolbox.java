package com.huestew.studio;

import com.huestew.studio.tools.*;

import javafx.scene.Cursor;

/**
 * A toolbox containing a set of tools.
 * 
 * @author Daniel Illipe
 * @author Patrik Olson
 * @author Marcus Randevik
 * @author Adam Andreasson
 */

public enum Toolbox {
	POPULATE(new PopulateTool()),
	MOVE(new MoveTool()),
	REMOVE(new RemoveTool());

	private Tool tool;

	Toolbox(Tool tool) {
		this.tool = tool;
	}

	/**
	 * Select this tool.
	 */
	public void select() {
		selected = this;
	}

	private static Toolbox selected = POPULATE;

	/**
	 * Returns the currently selected tool
	 * @return 
	 * 			the currently selected tool
	 */
	public static Tool getTool() {
		return selected.tool;
	}

	/**
	 * Returns the current cursor based on the tool being used 
	 * @param isMouseDown 
	 * @param hoveringCursor 
	 * @return
	 * 			the cursor corresponding to the currently selected tool
	 */
	public static Cursor getCursor(boolean hoveringCursor, boolean isMouseDown) {
		return selected.tool.getCursor(hoveringCursor, isMouseDown);
	}
}