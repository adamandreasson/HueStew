package com.huestew.studio.controller;

import java.io.File;

import com.huestew.studio.HueStew;
import com.huestew.studio.Toolbox;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.util.Util;
import com.huestew.studio.view.TrackView;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
	public AnchorPane colorPickerPane;

	@FXML
	private Button populateToolButton;

	@FXML
	private Button selectToolButton;

	@FXML
	private Slider volumeSlider;

	@FXML
	private Label footerStatus;

    @FXML
    private Button playStartButton;

    @FXML
    private Button playButton;

    @FXML
    private Button pauseButton;
    
	private Stage stage;

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

		trackCanvasPane.widthProperty().addListener((observableValue, oldWidth, newWidth) -> {
			trackCanvas.setWidth(newWidth.doubleValue());
			trackView.redraw();
		});
		trackCanvasPane.heightProperty().addListener((observableValue, oldHeight, newHeight) -> {
			trackCanvas.setHeight(newHeight.doubleValue());
			trackView.redraw();
		});

		HueStew.getInstance().getView().setTrackView(trackView);

		volumeSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
			double normalizedVolume = newValue.doubleValue() / 100;
			if (HueStew.getInstance().getPlayer() != null) {
				HueStew.getInstance().getPlayer().setVolume(normalizedVolume);
			}
			HueStew.getInstance().getConfig().setVolume(normalizedVolume);
		});

		footerStatus.setText("");

		playStartButton.setGraphic(new ImageView(new Image("icon_play_start.png")));
		playStartButton.setTooltip(new Tooltip("Play from the beginning"));
		
		playButton.setGraphic(new ImageView(new Image("icon_play.png")));
		playButton.setTooltip(new Tooltip("Play"));
		
		pauseButton.setGraphic(new ImageView(new Image("icon_pause.png")));
		pauseButton.setTooltip(new Tooltip("Pause playback"));
		
		populateToolButton.setGraphic(new ImageView(new Image("icon_pencil.png")));
		populateToolButton.setTooltip(new Tooltip("Populate tool"));
		
		selectToolButton.setGraphic(new ImageView(new Image("icon_cursor.png")));
		selectToolButton.setTooltip(new Tooltip("Select tool"));
		
	}

	@FXML
	private void populateToolPressed() {
		Toolbox.POPULATE.select();
	}

	@FXML
	private void selectToolPressed() {
		Toolbox.SELECT.select();
	}

	@FXML
	private void playStartButtonPressed() {
		HueStew.getInstance().getPlayer().seek(0);
		HueStew.getInstance().getPlayer().play();
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
	private void newButtonPressed() {
		FileChooser fileChooser = new FileChooser();

		File initialDir = new File(HueStew.getInstance().getConfig().getMusicDirectory());
		if (!initialDir.exists())
			initialDir = new File(System.getProperty("user.home"));

		fileChooser.setInitialDirectory(initialDir);
		fileChooser.setTitle("Open music file");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("mp3", "*.mp3"));

		File file = fileChooser.showOpenDialog(Util.createStage());

		if (file != null) {
			HueStew.getInstance().getConfig().setMusicDirectory(file.getParent().toString());
			HueStew.getInstance().getConfig().setMusicFilePath(file.toString());
			HueStew.getInstance().initShow(file);
		}

	}

	@FXML
	private void saveButtonPressed() {
		System.out.println("saving");

		HueStew.getInstance().save();
		/*
		 * FileChooser fileChooser = new FileChooser();
		 * fileChooser.setInitialDirectory(new
		 * File(System.getProperty("user.home")));
		 * fileChooser.getExtensionFilters().addAll(new
		 * FileChooser.ExtensionFilter("JSON", "*.json")); fileChooser.setTitle(
		 * "Choose save location"); File file =
		 * fileChooser.showSaveDialog(Util.createStage()); if (file != null) {
		 * System.out.println("SAVING TO " + file.getAbsolutePath()); }
		 */
	}

	public void openColorPickerPane(KeyFrame hoveringKeyFrame) {

		ColorPickerController cpc = (ColorPickerController) Util.loadFxml("/com/huestew/studio/colorpicker.fxml");

		cpc.setKeyFrame(hoveringKeyFrame);

		colorPickerPane.getChildren().clear();
		colorPickerPane.getChildren().add(cpc.getView());

	}

	public void setVolume(double volume) {
		volumeSlider.setValue(volume * 100);
	}

	/**
	 * Update the footer status text. Uses Platform.runLater to avoid
	 * multithread issues
	 * 
	 * @param label
	 *            New status
	 */
	public void updateFooterStatus(String label) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				footerStatus.setText(label);
			}
		});
	}

	/**
	 * Update the window title. Uses Platform.runLater to avoid multithread
	 * issues
	 * 
	 * @param title
	 *            New title
	 */
	public void updateTitle(String title) {
		stage.setTitle(title);
	}

	/**
	 * @param stage
	 *            the stage to set
	 */
	public void setStage(Stage stage) {
		this.stage = stage;

		setStageSize(HueStew.getInstance().getConfig().getWindowDimensions());
	}

	public String getStageSize() {
		return stage.isMaximized() + "," + stage.getX() + "," + stage.getY() + "," + stage.getWidth() + ","
				+ stage.getHeight();
	}

	public void setStageSize(String dimensions) {
		if (dimensions.isEmpty())
			return;

		String[] split = dimensions.split(",");
		boolean maximized = Boolean.parseBoolean(split[0]);
		if (maximized) {
			stage.setMaximized(true);
			return;
		}

		stage.setX(Double.parseDouble(split[1]));
		stage.setY(Double.parseDouble(split[2]));
		stage.setWidth(Double.parseDouble(split[3]));
		stage.setHeight(Double.parseDouble(split[4]));
	}

}
