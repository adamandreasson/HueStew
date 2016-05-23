package com.huestew.studio.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.huestew.studio.HueStew;
import com.huestew.studio.controller.tools.Toolbox;
import com.huestew.studio.model.Color;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightTrack;
import com.huestew.studio.model.Show;
import com.huestew.studio.util.Util;
import com.huestew.studio.view.TrackActionPane;
import com.huestew.studio.view.TrackView;
import com.huestew.studio.view.VirtualRoom;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
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
	private AnchorPane trackActionParentPane;

	@FXML
	public AnchorPane colorPickerPane;

	@FXML
	public AnchorPane drumKitPaneWrap;

	@FXML
	private ToggleButton populateToolButton;

	@FXML
	private ToggleButton selectToolButton;

	@FXML
	private Button addSequenceButton;

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
	
	@FXML
	private Button insertLightTrackButton;
	
	@FXML
	private Button insertVirtualLightButton;

	@FXML
	private MenuItem zoomInMenuItem;

	@FXML
	private MenuItem zoomOutMenuItem;

	@FXML
	private AnchorPane rootPane;

	private ShowController showController;
	private VirtualRoom virtualRoom;
	private Stage stage;
	private List<TrackActionPane> trackActionPanes;
	private TrackViewController trackViewController;
	private TrackMenuController trackMenuController;
	private DrumKitController drumKitController;
	private Toolbox toolbox;

	@Override
	public void init() {

		this.toolbox = new Toolbox(this);
		this.virtualRoom = new VirtualRoom();
		this.showController = new ShowController(this);
		showController.loadAutoSave();

		virtualRoom.setCanvas(previewCanvas);
		virtualRoom.redraw();

		trackActionPanes = new ArrayList<>();

		previewCanvasPane.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
			previewCanvas.setWidth((double) newSceneWidth);
			virtualRoom.redraw();
		});

		previewCanvasPane.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> {
			previewCanvas.setHeight((double) newSceneHeight);
			virtualRoom.redraw();
		});

		trackActionParentPane.heightProperty().addListener((observableValue, oldHeight, newHeight) -> {
			updateTrackActionPanePosition();
		});

		colorPickerPane.widthProperty().addListener((observableValue, oldHeight, newHeight) -> {
			System.out.println("color picker pane resizxed to " + newHeight);
		});

		volumeSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
			double normalizedVolume = newValue.doubleValue() / 100;
			if (showController.getPlayer() != null) {
				showController.getPlayer().setVolume(normalizedVolume);
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

		addSequenceButton.setGraphic(new ImageView(new Image("icon_sequence.png")));
		addSequenceButton.setTooltip(new Tooltip("Create sequence"));

		insertVirtualLightButton.setGraphic(new ImageView(new Image("icon_light_add.png")));
		insertVirtualLightButton.setTooltip(new Tooltip("Insert virtual light"));

		insertLightTrackButton.setGraphic(new ImageView(new Image("icon_track_add.png")));
		insertLightTrackButton.setTooltip(new Tooltip("Insert virtual light"));

		addSequenceButton.setDisable(true);

		ToggleGroup toolGroup = new ToggleGroup();
		populateToolButton.setToggleGroup(toolGroup);
		selectToolButton.setToggleGroup(toolGroup);
		selectToolButton.setSelected(true);

		trackMenuController = (TrackMenuController) ViewController.loadFxml("/trackmenu.fxml");
		rootPane.getChildren().add(0, trackMenuController.getParent());

		drumKitController = new DrumKitController(drumKitPaneWrap, this);

	}
	
	private void initTrackCanvas(){

		trackViewController = new TrackViewController(trackCanvas, this);
		trackCanvas.setWidth(trackCanvasPane.getWidth() - trackActionParentPane.getWidth());
		trackCanvas.setHeight(trackCanvasPane.getHeight());
		trackViewController.redraw();

		trackCanvasPane.widthProperty().addListener((observableValue, oldWidth, newWidth) -> {
			trackCanvas.setWidth(newWidth.doubleValue() - trackActionParentPane.getWidth());
			trackViewController.redraw();
		});

		trackCanvasPane.heightProperty().addListener((observableValue, oldHeight, newHeight) -> {
			trackCanvas.setHeight(newHeight.doubleValue());
			trackViewController.redraw();
		});
	}

	@FXML
	private void populateToolPressed() {
		toolbox.getPopulateTool().select();
	}

	@FXML
	private void selectToolPressed() {
		toolbox.getSelectTool().select();
	}

	@FXML
	private void playStartButtonPressed() {
		showController.getPlayer().seek(0);
		showController.getPlayer().play();
	}

	@FXML
	private void playButtonPressed() {
		showController.getPlayer().play();
	}

	@FXML
	private void pauseButtonPressed() {
		showController.getPlayer().pause();
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
			showController.initShow(file);
		}

	}

	@FXML
	private void saveButtonPressed() {
		System.out.println("saving");

		if (HueStew.getInstance().getConfig().getSaveFile().isEmpty()) {
			saveAsButtonPressed();
			return;
		}

		showController.save();
	}
	
	@FXML
	private void saveAsButtonPressed() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(HueStew.getInstance().getConfig().getSaveDirectory()));
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));
		fileChooser.setTitle("Choose save location");

		File file = fileChooser.showSaveDialog(Util.createStage());

		if (file != null) {
			System.out.println("SAVING TO " + file.getAbsolutePath());
			HueStew.getInstance().getConfig().setSaveFile(file.getAbsolutePath());
			HueStew.getInstance().getConfig().setSaveDirectory(file.getParentFile().getAbsolutePath());
			showController.save();
		}
	}

	@FXML
	private void zoomInButtonPressed() {
		trackViewController.adjustZoom(TrackView.ZOOM_IN);
		trackViewController.redraw();
		updateZoomButtons();
	}

	@FXML
	private void zoomOutButtonPressed() {
		trackViewController.adjustZoom(TrackView.ZOOM_OUT);
		trackViewController.redraw();
		updateZoomButtons();
	}

	public void updateZoomButtons() {
		zoomInMenuItem.setDisable(trackViewController.getZoom() == TrackView.MAXIMUM_ZOOM);
		zoomOutMenuItem.setDisable(trackViewController.getZoom() == TrackView.MINIMUM_ZOOM);
	}

	@FXML
	private void addSequencePressed() {
		drumKitController.addButtonPressed();
	}
	
	@FXML
	private void insertLightTrackPressed(){
		showController.addTrack();
	}
	
	@FXML
	private void insertVirtualLightPressed(){
		showController.addVirtualLight();
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
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				stage.setTitle(title);
			}
		});
	}

	public void enableControls() {
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

		setWindowDimensions(HueStew.getInstance().getConfig().getWindowDimensions());
	}

	public String getWindowDimensions() {
		return stage.isMaximized() + "," + stage.getX() + "," + stage.getY() + "," + stage.getWidth() + ","
				+ stage.getHeight();
	}

	public void setWindowDimensions(String dimensions) {
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

	public void openColorPickerPane(List<KeyFrame> selectedKeyFrames) {

		colorPickerPane.getChildren().clear();

		double maxDimension = colorPickerPane.getWidth();
		if (colorPickerPane.getHeight() > colorPickerPane.getWidth())
			maxDimension = colorPickerPane.getHeight();

		System.out.println("max dimension is " + maxDimension);

		maxDimension -= 20;

		Image img = new Image("color_circle.png");
		ImageView imgView = new ImageView(img);

		double imgScale = img.getWidth() / maxDimension;
		imgView.setFitWidth(maxDimension);
		imgView.setFitHeight(maxDimension);
		imgView.setCursor(Cursor.CROSSHAIR);

		imgView.setOnMouseClicked(event -> {
			int imgX = (int) (event.getX() * imgScale);
			int imgY = (int) (event.getY() * imgScale);
			System.out.println("MOUSE DONW");
			Color c = new Color(img.getPixelReader().getColor(imgX, imgY));
			System.out.println(c);

			for (KeyFrame frame : selectedKeyFrames) {
				frame.getState().setColor(c);
			}

		});

		colorPickerPane.getChildren().add(imgView);
	}

	public void updateTracks() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				trackActionParentPane.getChildren().clear();
				trackActionPanes.clear();
				ToggleGroup actionGroup = new ToggleGroup();

				for (LightTrack track : showController.getShow().getLightTracks()) {
					TrackActionPane trackPane = new TrackActionPane(track, actionGroup, trackMenuController);
					
					
					AnchorPane.setTopAnchor((Node)trackPane, Math.round(trackViewController.getTrackPositionY(track))+0.0);
					trackActionParentPane.getChildren().add(trackPane);
					trackActionPanes.add(trackPane);
				}
				
				trackViewController.redraw();
			}

		});
	}

	private void updateTrackActionPanePosition() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				for (TrackActionPane pane : trackActionPanes) {
					AnchorPane.setTopAnchor((Node)pane, Math.round(trackViewController.getTrackPositionY(pane.getTrack()))+0.0);

				}
			}

		});
	}

	private boolean sequencePossible(List<KeyFrame> selectedKeyFrames, int tracksInSelection) {
		return !(selectedKeyFrames == null || selectedKeyFrames.isEmpty() || tracksInSelection > 1
				|| selectedKeyFrames.size() < 2);
	}

	public void notifySelectionChange(List<KeyFrame> selectedKeyFrames, int tracksInSelection) {
		setSequencePossible(sequencePossible(selectedKeyFrames, tracksInSelection));
	}

	public void setSequencePossible(boolean b) {
		addSequenceButton.setDisable(!b);
	}

	/**
	 * @return the toolbox
	 */
	public Toolbox getToolbox() {
		return toolbox;
	}

	public void updateTrackView() {
		trackViewController.redraw();
	}

	public void updateWaveImage(List<String> imagePaths) {
		trackViewController.loadWaves(imagePaths);
	}

	public void handleKeyboardEvent(KeyEvent event) {
		trackViewController.keyboardEvent(event);
	}

	public VirtualRoom getVirtualRoom() {
		return virtualRoom;
	}

	public void shutdown() {
		HueStew.getInstance().shutdown();
		showController.autoSave();
	}

	public void tick() {
		showController.tick();
	}

	public Player getPlayer() {
		return showController.getPlayer();
	}
	
	public Show getShow(){
		return showController.getShow();
	}

	public void initShow(String title) {

		updateTitle(title);
		
		initTrackCanvas();
		enableControls();
		updateTracks();
	}

}
