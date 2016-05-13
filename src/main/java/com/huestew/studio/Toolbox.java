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
	SELECT(new SelectTool());
	

	private Tool tool;

	Toolbox(Tool tool) {
		this.tool = tool;
	}

	/**
	 * Select this tool.
	 */
	public void select() {
		selected = this;
		active = this;
	}

	/**
	 * Make this tool active.
	 */
	public void setActive() {
		active = this;
	}
	
	private static Toolbox selected = POPULATE;
	private static Toolbox active = POPULATE;

	/**
	 * Returns the currently selected tool
	 * @return 
	 * 			the currently selected tool
	 */
	public static Tool getSelectedTool() {
		return selected.tool;
	}

	/**
	 * Returns the currently active tool
	 * @return 
	 * 			the currently active tool
	 */
	public static Tool getActiveTool() {
		return active.tool;
	}
	
	/**
	 * Returns the currently active tool
	 * @return 
	 * 			the currently active tool
	 */
	public static void reset() {
		active = selected;
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