package com.huestew.studio;

import com.huestew.studio.tools.*;

/**
 * A toolbox containing a list of tools.
 * 
 * @author Daniel Illipe
 * @author Patrik Olson
 * @author Marcus Randevik
 * @author Adam Andreasson
 */

public class Toolbox {
	public enum Tools {
		POPULATE(new PopulateTool());
		
		private Tool tool;
		
		Tools(Tool tool) {
			this.tool = tool;
		}
		
		public void select() {
			getInstance().setTool(tool);
		}
	}
	
	private static Toolbox instance;
	
	private Tool tool;
	
	public Tool getTool(){
		return tool;
	}
	
	private void setTool(Tool tool) {
		this.tool = tool;
	}
	
	public static Toolbox getInstance() {
		if (instance == null) {
			instance = new Toolbox();
		}
		return instance;
	}
}