package com.huestew.studio.controller;

import com.huestew.studio.HueStew;
import com.huestew.studio.view.TrackView;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

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

	@Override
	public void init() {
		TrackView trackView = new TrackView(trackCanvas);
		trackView.redraw();

		HueStew.getInstance().getVirtualRoom().setCanvas(previewCanvas);
		HueStew.getInstance().getVirtualRoom().redraw();

		previewCanvasPane.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
			previewCanvas.setWidth((double) newSceneWidth);
			HueStew.getInstance().getVirtualRoom().redraw();
		});

		previewCanvasPane.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> {
			previewCanvas.setHeight((double) newSceneHeight);
			HueStew.getInstance().getVirtualRoom().redraw();
		});

		trackScrollPane.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
			trackCanvas.setWidth((double) newSceneWidth - 16);
			trackView.redraw();
		});

		trackScrollPane.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> {
			trackCanvas.setHeight((double) newSceneHeight - 16);
			trackView.redraw();
		});
	}

}