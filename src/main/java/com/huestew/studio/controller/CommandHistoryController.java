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
	private String labelText;

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
		CommandHistoryConfigController configController = (CommandHistoryConfigController) ViewController.loadFxml("/commandhistorytile.fxml");
		configController.updateLabelText(context);
		configController.updateSize(commandHistoryPane.getWidth());
		
		historyConfigs.add(configController);
		grid.getChildren().add(0, configController.getParent());
	}

	/**
	 * Updates the commandhistory pane when something has been undone.
	 */
	public void undid() {
		historyConfigs.remove(historyConfigs.size()-1);
		grid.getChildren().remove(0);
	}

	public void redid(String context) {
		CommandHistoryConfigController configController = (CommandHistoryConfigController) ViewController.loadFxml("/drumtile.fxml");
		configController.updateLabelText(context);
		configController.updateSize(commandHistoryPane.getWidth());
		
		historyConfigs.add(configController);
		grid.getChildren().add(0, configController.getParent());

	}

	public String getLabelText() {

		return labelText;

	}

	public void updateSize() {
		grid.setMaxWidth(commandHistoryPane.getWidth());
		for (CommandHistoryConfigController config : historyConfigs) {
			config.updateSize(commandHistoryPane.getWidth());
		}
	}
}
