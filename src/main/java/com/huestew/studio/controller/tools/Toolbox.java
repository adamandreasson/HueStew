package com.huestew.studio.controller.tools;

import com.huestew.studio.controller.MainViewController;

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
	private MainViewController controller;

	public Toolbox(MainViewController controller) {
		this.controller = controller;
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
		selectedTool = tool;
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

	/**
	 * @return the controller
	 */
	public MainViewController getController() {
		return controller;
	}
	
}
