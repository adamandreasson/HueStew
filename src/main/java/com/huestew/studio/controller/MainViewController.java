package com.huestew.studio.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.huestew.studio.HueStew;
import com.huestew.studio.model.Color;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightTrack;
import com.huestew.studio.util.Util;
import com.huestew.studio.view.TrackActionButton;
import com.huestew.studio.view.TrackView;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
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
	private AnchorPane trackActionPane;
	
	@FXML
	public AnchorPane colorPickerPane;

	@FXML
	private ToggleButton populateToolButton;

	@FXML
	private ToggleButton selectToolButton;

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

	@FXML
	private Button saveButton;

	@FXML
	private Button saveAsButton;

	private Stage stage;
	
	private TrackView trackView;
	
	private List<TrackActionButton> trackActionButtons;

	@Override
	public void init() {

		trackView = new TrackView(trackCanvas);
		HueStew.getInstance().getView().setTrackView(trackView);
		trackView.redraw();

		HueStew.getInstance().getView().getVirtualRoom().setCanvas(previewCanvas);
		HueStew.getInstance().getView().getVirtualRoom().redraw();
		
		trackActionButtons = new ArrayList<>();

		previewCanvasPane.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
			previewCanvas.setWidth((double) newSceneWidth);
			HueStew.getInstance().getView().getVirtualRoom().redraw();
		});

		previewCanvasPane.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> {
			previewCanvas.setHeight((double) newSceneHeight);
			HueStew.getInstance().getView().getVirtualRoom().redraw();
		});

		trackCanvasPane.widthProperty().addListener((observableValue, oldWidth, newWidth) -> {
			trackCanvas.setWidth(newWidth.doubleValue() - trackActionPane.getWidth());
			trackView.redraw();
		});
		
		trackCanvasPane.heightProperty().addListener((observableValue, oldHeight, newHeight) -> {
			trackCanvas.setHeight(newHeight.doubleValue());
			trackView.redraw();
		});
		
		trackActionPane.heightProperty().addListener((observableValue, oldHeight, newHeight) -> {
			updateTrackActionPanePosition();
		});
		
		colorPickerPane.widthProperty().addListener((observableValue, oldHeight, newHeight) -> {
			System.out.println("color picker pane resizxed to " + newHeight);
		});

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

		ToggleGroup toolGroup = new ToggleGroup();
		populateToolButton.setToggleGroup(toolGroup);
		selectToolButton.setToggleGroup(toolGroup);
		selectToolButton.setSelected(true);
	}

	@FXML
	private void populateToolPressed() {
		HueStew.getInstance().getToolbox().getPopulateTool().select();
	}

	@FXML
	private void selectToolPressed() {
		HueStew.getInstance().getToolbox().getSelectTool().select();
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
			HueStew.getInstance().getConfig().setMusicDirectory(file.getParent());
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

	public void enableControls(){
		saveButton.setDisable(false);
		saveAsButton.setDisable(false);
		playStartButton.setDisable(false);
		playButton.setDisable(false);
		pauseButton.setDisable(false);
		volumeSlider.setDisable(false);
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

	public void openColorPickerPane(Set<KeyFrame> selectedKeyFrames) {

		colorPickerPane.getChildren().clear();
		
		double maxDimension = colorPickerPane.getWidth();
		if(colorPickerPane.getHeight() > colorPickerPane.getWidth())
			maxDimension = colorPickerPane.getHeight();
		
		System.out.println("max dimension is " + maxDimension);

		maxDimension -= 20;

		Image img = new Image("color_circle.png");
		ImageView imgView = new ImageView(img);

		double imgScale = img.getWidth()/maxDimension;
		imgView.setFitWidth(maxDimension);
		imgView.setFitHeight(maxDimension);
		imgView.setCursor(Cursor.CROSSHAIR);

		imgView.setOnMouseClicked(event -> {
			int imgX = (int)(event.getX()*imgScale);
			int imgY = (int)(event.getY()*imgScale);
			System.out.println("MOUSE DONW");
			Color c = new Color(img.getPixelReader().getColor(imgX, imgY));
			System.out.println(c);
			
			for(KeyFrame frame : selectedKeyFrames){
				frame.getState().setColor(c);
			}
			
		});

		colorPickerPane.getChildren().add(imgView);
	}
	
	public void updateTrackActionPane(){
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				
				trackActionPane.getChildren().clear();
				trackActionButtons.clear();
					
				Image lightImg = new Image("icon_light.png");
				
				for(LightTrack track : HueStew.getInstance().getShow().getLightTracks()){
					TrackActionButton trackBtn = new TrackActionButton(track);
					trackBtn.setLayoutY(Math.round(trackView.getTrackPositionY(track)));
					trackBtn.setGraphic(new ImageView(lightImg));
					trackBtn.setTooltip(new Tooltip("Configure lights"));
					trackActionPane.getChildren().add(trackBtn);
					trackActionButtons.add(trackBtn);
				}
			}
			
		});
	}

	private void updateTrackActionPanePosition() {
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
								
				for(TrackActionButton btn : trackActionButtons){
					btn.setLayoutY(Math.round(trackView.getTrackPositionY(btn.getTrack())));
					
				}
			}
			
		});
	}

}
