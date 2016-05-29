package com.huestew.studio.controller.tools;

/**
 * A toolbox containing a set of tools.
 * 
 * @author Daniel Illipe
 * @author Patrik Olson
 * @author Marcus Randevik
 * @author Adam Andreasson
 */
public class Toolbox {
	private final PopulateTool populateTool = new PopulateTool(this);
	private final SelectTool selectTool = new SelectTool(this);

	private Tool selectedTool;
	private Tool activeTool;

	public Toolbox() {
		selectedTool = activeTool = selectTool;
	}

	public PopulateTool getPopulateTool() {
		return populateTool;
	}

	public SelectTool getSelectTool() {
		return selectTool;
	}

	public void reset() {
		activeTool = selectedTool;
	}

	public Tool getSelectedTool() {
		return selectedTool;
	}
	
	public void setSelectedTool(Tool tool) {
		if (tool == null) {
			throw new IllegalArgumentException("Tool may not be null");
		}
		selectedTool = activeTool = tool;
	}

	public Tool getActiveTool() {
		return activeTool;
	}
	
	public void setActiveTool(Tool tool) {
		if (tool == null) {
			throw new IllegalArgumentException("Tool may not be null");
		}
		activeTool = tool;
	}
}
