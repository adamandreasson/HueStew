package com.huestew.studio.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import com.huestew.studio.command.Command;
import com.huestew.studio.command.CommandManager;

public class CommandHistoryConfigController extends ViewController {

	@FXML
	private AnchorPane wrapPane;

	@FXML
	private Label lblUserAction;

	private CommandHistoryController controller;

	@Override
	public void init() {

	}

	// TODO
	public void updateLabelText(String text) {

		lblUserAction.setText(text);
	}

	public void updateSize(double width) {
		wrapPane.setMaxWidth(width);
	}

}
