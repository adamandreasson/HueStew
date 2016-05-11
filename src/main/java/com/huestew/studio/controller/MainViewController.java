package com.huestew.studio.controller;

import java.io.File;
import java.io.IOException;

import com.huestew.studio.HueStew;
import com.huestew.studio.Toolbox;
import com.huestew.studio.view.TrackView;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Controller class for the Main JavaFX view
 * 
 * @author Adam Andreasson
 */
public class MainViewController extends ViewController {

	@FXML
	private Canvas previewCanvas;

	@FXML
	private AnchorPane previewCanvasPane;

	@FXML
	private Canvas trackCanvas;

	@FXML
	private AnchorPane trackCanvasPane;

	@FXML
	private ScrollPane trackScrollPane;

	@FXML
	private Button removeToolButton;

	@FXML
	private Button populateToolButton;

	@Override
	public void init() {
		TrackView trackView = new TrackView(trackCanvas);
		trackView.redraw();

		HueStew.getInstance().getView().getVirtualRoom().setCanvas(previewCanvas);
		HueStew.getInstance().getView().getVirtualRoom().redraw();

		previewCanvasPane.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
			previewCanvas.setWidth((double) newSceneWidth);
			HueStew.getInstance().getView().getVirtualRoom().redraw();
		});

		previewCanvasPane.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> {
			previewCanvas.setHeight((double) newSceneHeight);
			HueStew.getInstance().getView().getVirtualRoom().redraw();
		});

		/*
		 * trackScrollPane.widthProperty().addListener((observableValue,
		 * oldSceneWidth, newSceneWidth) -> { trackCanvas.setWidth((double)
		 * newSceneWidth - 16); trackView.redraw(); });
		 */

		trackScrollPane.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> {
			trackCanvas.setHeight((double) newSceneHeight - 16);
			trackView.redraw();
		});

		HueStew.getInstance().getView().setTrackView(trackView);
	}

	@FXML
	private void removeToolPressed() {
		Toolbox.REMOVE.select();
	}

	@FXML
	private void populateToolPressed() {
		Toolbox.POPULATE.select();
	}

	@FXML
	private void moveToolPressed() {
		Toolbox.MOVE.select();
	}

	@FXML
	private void playButtonPressed() {
		HueStew.getInstance().getPlayer().play();
	}

	@FXML
	private void pauseButtonPressed() {
		HueStew.getInstance().getPlayer().pause();
	}

	@FXML
	private void saveButtonClicked() {
		System.out.println("saving");
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JPG", "*.jpg"),
				new FileChooser.ExtensionFilter("PNG", "*.png"));
		fileChooser.setTitle("Choose save location");
		File file = fileChooser.showSaveDialog(new Stage());
		if (file != null) {
			System.out.println("SAVING");
		}

	}
}
