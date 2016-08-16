package com.huestew.studio.controller;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class CommandHistoryController {

	private MainViewController controller;
	private AnchorPane commandHistoryPane;
	private VBox grid;
	private List<CommandHistoryConfigController> historyConfigs;
	
	public CommandHistoryController(AnchorPane commandHistoryPane, MainViewController controller) {
		this.controller = controller;
		this.commandHistoryPane = commandHistoryPane;
		this.historyConfigs = new ArrayList<CommandHistoryConfigController>();
		
		this.grid = new VBox();
		AnchorPane.setTopAnchor(grid, 0.0);
		AnchorPane.setLeftAnchor(grid, 0.0);
		AnchorPane.setRightAnchor(grid, 0.0);
		AnchorPane.setBottomAnchor(grid, 0.0);

		commandHistoryPane.getChildren().add(grid);
	}
	
	public void commandExecuted(String context) {
		
	}

	/**
	 * Updates the commandhistory pane when something has been undone.
	 */
	public void undid() {
		
	}

	public void redid(String context) {

	}
}
