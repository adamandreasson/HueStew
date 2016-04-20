package com.huestew.studio;

import com.huestew.studio.tools.*;

/**
 * A toolbox containing a set of tools.
 * 
 * @author Daniel Illipe
 * @author Patrik Olson
 * @author Marcus Randevik
 * @author Adam Andreasson
 */

public enum Toolbox {
	POPULATE(new PopulateTool());
	
	private Tool tool;
	
	Toolbox(Tool tool) {
		this.tool = tool;
	}
	
	/**
	 * Select this tool.
	 */
	public void select() {
		selected = tool;
	}

	private static Tool selected = POPULATE.tool;

	public static Tool getTool() {
		return selected;
	}
}