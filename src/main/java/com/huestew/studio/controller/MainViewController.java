package com.huestew.studio.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.huestew.studio.controller.tools.Toolbox;
import com.huestew.studio.io.FileHandler;
import com.huestew.studio.model.HueStewConfig;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightTrack;
import com.huestew.studio.model.Sequence;
import com.huestew.studio.model.Show;
import com.huestew.studio.model.SnapshotManager;
import com.huestew.studio.plugin.PluginHandler;
import com.huestew.studio.plugin.PluginLoader;
import com.huestew.studio.util.FileUtil;
import com.huestew.studio.view.ErrorAlert;
import com.huestew.studio.view.Light;
import com.huestew.studio.view.Scrollbar;
import com.huestew.studio.view.TrackActionPane;
import com.huestew.studio.view.TrackView;
import com.huestew.studio.view.VirtualLight;
import com.huestew.studio.view.VirtualRoom;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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
	private AnchorPane colorPickerPane;

	@FXML
	private AnchorPane drumKitPaneWrap;

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
	private Menu editMenu;

	@FXML
	private Menu viewMenu;

	@FXML
	private Menu insertMenu;

	@FXML
	private MenuItem saveMenuItem;

	@FXML
	private MenuItem saveAsMenuItem;

	@FXML
	private MenuItem pasteMenuItem;

	@FXML
	private MenuItem zoomInMenuItem;

	@FXML
	private MenuItem zoomOutMenuItem;

	@FXML
	private AnchorPane rootPane;

	private Stage stage;
	private List<TrackActionPane> trackActionPanes;

	private FileHandler fileHandler;
	private PluginHandler pluginHandler;

	private ShowController showController;
	private TrackViewController trackViewController;
	private TrackMenuController trackMenuController;
	private DrumKitController drumKitController;
	private ColorPickerController colorPickerController;

	private VirtualRoom virtualRoom;
	private Toolbox toolbox;
	private Sequence clipBoard;

	@Override
	public void init() {

		try {
			this.fileHandler = new FileHandler();
		} catch (AccessDeniedException e) {
			handleError(e, "Unable to create necessary workspace directories");
		}
		loadConfig();

		this.showController = new ShowController(this);
		this.colorPickerController = new ColorPickerController(colorPickerPane);
		this.drumKitController = new DrumKitController(drumKitPaneWrap, this);
		this.trackMenuController = (TrackMenuController) ViewController.loadFxml("/trackmenu.fxml");

		this.virtualRoom = new VirtualRoom();
		this.toolbox = new Toolbox();
		this.trackActionPanes = new ArrayList<>();

		try {
			showController.loadShow();
		} catch (IOException | JSONException e) {
			handleError(e, "Corrupt save file");
		}

		virtualRoom.setCanvas(previewCanvas);
		virtualRoom.redraw();

		initPlugins();

		initPropertyListeners();

		initVisuals();

		initDragDropFiles();

	}

	/**
	 * Load the user configuration from file (through ConfigConverter) and put
	 * it in HueStew
	 */
	private void loadConfig() {

		try {
			new ConfigConverter().fromProperties(fileHandler.loadConfig());
		} catch (FileNotFoundException e) {
			// First run, use default config
			HueStewConfig.setDefaults();
		} catch (IOException e) {
			// Display error message and use default config
			handleError(e, "Could not load configuration file");
			HueStewConfig.setDefaults();
		}

	}

	/**
	 * Initialize plugin handler and load plugins
	 */
	private void initPlugins() {

		this.pluginHandler = new PluginHandler();
		File pluginFolder = new File(fileHandler.getAppFilePath("plugins/"));
		new PluginLoader(pluginFolder, pluginHandler);
	}

	/**
	 * Initialize all javafx property listeners in the main view
	 */
	private void initPropertyListeners() {

		previewCanvasPane.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
			previewCanvas.setWidth((double) newSceneWidth);
			virtualRoom.redraw();
		});

		previewCanvasPane.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> {
			previewCanvas.setHeight((double) newSceneHeight);
			virtualRoom.redraw();
		});

		trackActionParentPane.heightProperty().addListener((a, b, c) -> updateTrackActionPanePosition());

		colorPickerPane.heightProperty().addListener((a, b, c) -> colorPickerController.updateSize());
		colorPickerPane.widthProperty().addListener((a, b, c) -> colorPickerController.updateSize());

		drumKitPaneWrap.widthProperty().addListener((a, b, c) -> drumKitController.updateSize());

		volumeSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
			double normalizedVolume = newValue.doubleValue() / 100;
			if (showController.getPlayer() != null) {
				showController.getPlayer().setVolume(normalizedVolume);
			}
			HueStewConfig.getInstance().setVolume(normalizedVolume);
		});

	}

	/**
	 * Prepare all the visuals with appropriate text and images
	 */
	private void initVisuals() {

		volumeSlider.setValue(HueStewConfig.getInstance().getVolume() * 100);

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

		// Make sure the buttons cannot be deselected
		populateToolButton.addEventHandler(ActionEvent.ACTION, e -> populateToolButton.setSelected(true));
		selectToolButton.addEventHandler(ActionEvent.ACTION, e -> selectToolButton.setSelected(true));

		rootPane.getChildren().add(0, trackMenuController.getParent());

	}

	/**
	 * Register the rootPane as being susceptible to file drag and drops
	 */
	private void initDragDropFiles() {

		rootPane.setOnDragOver(event -> {

			Dragboard db = event.getDragboard();
			if (db.hasFiles()) {
				event.acceptTransferModes(TransferMode.COPY);
			} else {
				event.consume();
			}

		});

		rootPane.setOnDragDropped(event -> {

			Dragboard db = event.getDragboard();
			boolean success = false;
			if (db.hasFiles()) {
				if (db.getFiles().size() < 2) {
					File file = db.getFiles().get(0);
					System.out.println(file.getAbsolutePath());
					if (FileUtil.isMusicFile(file)) {
						showController.createShow(file);
					}
				}
			}

			event.setDropCompleted(success);
			event.consume();

		});
	}

	private void handleError(Exception e, String message) {
		// Show error dialog
		ErrorAlert alert = new ErrorAlert(message);
		alert.show();

		// Print to console
		e.printStackTrace();
	}

	/**
	 * Initialize the track view canvas. This method is called once a show is
	 * ready
	 */
	private void initTrackCanvas() {

		if (trackViewController != null)
			trackViewController.stopRedrawThread();

		trackViewController = new TrackViewController(trackCanvas, this);
		trackViewController.startRedrawThread();

		colorPickerController.setTrackViewController(trackViewController);

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

	public void initShow() {

		initTrackCanvas();
		enableControls();
		updateTracks();
	}

	/**
	 * Paste what is in the clipboard to the show.
	 */
	private void paste() {
		if (getShow() == null)
			return;

		if (clipBoard == null)
			return;

		SnapshotManager.getInstance().commandIssued();

		int cursor = getShow().getCursor();

		for (KeyFrame frame : clipBoard.getFrames()) {
			LightTrack track = frame.track();
			KeyFrame pastedFrame = new KeyFrame(cursor + frame.getTimestamp(), frame.getState(), track);
			track.addKeyFrame(pastedFrame);
		}

		trackViewController.redraw();
	}

	/**
	 * Update the footer status text. Uses Platform.runLater to avoid
	 * multithread issues
	 * 
	 * @param label
	 *            New status
	 */
	public void updateFooterStatus(String label) {
		Platform.runLater(() -> footerStatus.setText(label));
	}

	/**
	 * Update the window title. Uses Platform.runLater to avoid multithread
	 * issues
	 * 
	 * @param title
	 *            New title
	 */
	public void updateTitle(String title) {
		Platform.runLater(() -> stage.setTitle(title));
	}

	public void enableControls() {
		saveButton.setDisable(false);
		saveAsButton.setDisable(false);
		playStartButton.setDisable(false);
		playButton.setDisable(false);
		pauseButton.setDisable(false);
		volumeSlider.setDisable(false);
		saveMenuItem.setDisable(false);
		saveAsMenuItem.setDisable(false);
		editMenu.setDisable(false);
		viewMenu.setDisable(false);
		insertMenu.setDisable(false);
		insertLightTrackButton.setDisable(false);
		insertVirtualLightButton.setDisable(false);
	}

	public void setVolume(double volume) {
		volumeSlider.setValue(volume * 100);
	}

	public void setStage(Stage stage) {
		this.stage = stage;

		setWindowDimensions(HueStewConfig.getInstance().getWindowDimensions());
	}

	public String getWindowDimensions() {
		return stage.isMaximized() + "," + stage.getX() + "," + stage.getY() + "," + stage.getWidth() + ","
				+ stage.getHeight();
	}

	/**
	 * Set the dimensions of the main view stage.
	 * 
	 * @param dimensions
	 *            A string of dimensions separated by commas. X, Y, Width,
	 *            Height.
	 */
	private void setWindowDimensions(String dimensions) {
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

	/**
	 * Redirects to {@link ColorPickerController}
	 * 
	 * @param selectedKeyFrames
	 */
	public void updateColorPicker(List<KeyFrame> selectedKeyFrames) {
		colorPickerController.setFrames(selectedKeyFrames);
	}

	/**
	 * Method is called when a there is a change in number of tracks. Clear all
	 * {@link TrackActionPane} and create new ones.
	 */
	public void updateTracks() {
		Platform.runLater(() -> {
			trackActionParentPane.getChildren().clear();
			trackActionPanes.clear();
			ToggleGroup actionGroup = new ToggleGroup();
			List<LightTrack> tracks = showController.getShow().getLightTracks();

			for (LightTrack track : tracks) {
				TrackActionPane trackPane = new TrackActionPane(track, actionGroup);
				if (tracks.size() > 1) {
					trackPane.setOnRemove((e) -> {
						showController.getShow().removeLightTrack(track);
						removeVirtualLights(track);
						LightBank.getInstance().updateAvailableLights(showController.getShow().getLightTracks());
						updateTracks();

						SnapshotManager.getInstance().clear();
					});
				}
				trackPane.getTrackBtn().setOnAction((e) -> {
					trackMenuController.openFor(trackPane.getTrackBtn(), track);
				});

				AnchorPane.setTopAnchor((Node) trackPane,
						Math.round(trackViewController.getTrackPositionY(track)) + 0.0);
				trackActionParentPane.getChildren().add(trackPane);
				trackActionPanes.add(trackPane);
			}

			trackViewController.redraw();
		});
	}

	/**
	 * Remove all {@link VirtualLight} from a {@link LightTrack}
	 * 
	 * @param track
	 */
	private void removeVirtualLights(LightTrack track) {
		for (Light light : LightBank.getInstance().getLights(track)) {
			if (light instanceof VirtualLight) {
				LightBank.getInstance().removeLight(light);
			}
		}
	}

	/**
	 * Similar to updateTracks(), this method is called when the track canvas
	 * pane is resized, and recalculates the position of all
	 * {@link TrackActionPane}
	 */
	public void updateTrackActionPanePosition() {
		Platform.runLater(() -> {
			for (TrackActionPane pane : trackActionPanes) {
				AnchorPane.setTopAnchor((Node) pane,
						Math.round(trackViewController.getTrackPositionY(pane.getTrack())) + 0.0);
			}
		});
	}

	/**
	 * Can we create a sequence from the selected key frames?
	 * 
	 * @param selectedKeyFrames
	 *            List of key frames in selection
	 * @param tracksInSelection
	 *            Number of tracks included in the selection
	 * @return Whether a sequence can be created
	 */
	private boolean sequencePossible(List<KeyFrame> selectedKeyFrames, int tracksInSelection) {
		return !(selectedKeyFrames == null || selectedKeyFrames.isEmpty() || tracksInSelection > 1
				|| selectedKeyFrames.size() < 2);
	}

	/**
	 * Method is called when selected frames are changed. Decides whether
	 * creating a new sequence from the selection is possible
	 * 
	 * @param selectedKeyFrames
	 *            List of key frames in selection
	 * @param tracksInSelection
	 *            Number of tracks included in the selection
	 */
	public void notifySelectionChange(List<KeyFrame> selectedKeyFrames, int tracksInSelection) {
		setSequencePossible(sequencePossible(selectedKeyFrames, tracksInSelection));
	}

	/**
	 * Update visuals based on whether a sequence can be created currently
	 * 
	 * @param b
	 *            Is it possible to create a sequence
	 */
	private void setSequencePossible(boolean b) {
		addSequenceButton.setDisable(!b);
	}

	/**
	 * Update the track view canvas. Redirects to {@link TrackViewController}
	 */
	public void updateTrackView() {
		trackViewController.redraw();
	}

	/**
	 * Update the wave image background. Redirected to
	 * {@link TrackViewController}
	 * 
	 * @param imagePaths
	 */
	public void updateWaveImage(List<String> imagePaths) {
		trackViewController.loadWaves(imagePaths);
	}

	/**
	 * Shut down the program
	 * 
	 * @throws IOException
	 *             If the program failed to autosave configuration
	 */
	public void shutdown() throws IOException {
		pluginHandler.sendDisable();
		try {
			showController.autoSave();
		} catch (IOException e) {
			// Futile to show error popup since program is shutting down
			// Print in console instead
			e.printStackTrace();
		}
		
	}

	/**
	 * Tick. Redirect to {@link ShowController}
	 */
	public void tick() {
		showController.tick();
	}

	/*
	 * Getters
	 */

	public Player getPlayer() {
		return showController.getPlayer();
	}

	public Show getShow() {
		return showController.getShow();
	}

	public Stage getStage() {
		return stage;
	}

	public Toolbox getToolbox() {
		return toolbox;
	}

	public VirtualRoom getVirtualRoom() {
		return virtualRoom;
	}

	public FileHandler getFileHandler() {
		return fileHandler;
	}

	/*
	 * JavaFX input handlers
	 */

	public void handleKeyboardEvent(KeyEvent event) {
		if (event.getEventType() == KeyEvent.KEY_PRESSED) {
			drumKitController.keyboardEvent(event);
		}
		if (trackViewController != null)
			trackViewController.keyboardEvent(event);

		// Pass on event to current tool
		toolbox.getSelectedTool().doAction(event);
		trackViewController.redraw();
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
		Scrollbar bar = trackViewController.getHorizontalScrollbar();
		bar.addOffset(-bar.getOffset());
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
	private void exitButtonPressed() {
		stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
	}

	@FXML
	protected void newButtonPressed() {
		File file = showController.browseForSong();

		if (file != null) {
			showController.createShow(file);
		}

	}

	@FXML
	private void openButtonPressed() {
		FileChooser fileChooser = new FileChooser();

		File initialDir = new File(HueStewConfig.getInstance().getSaveDirectory());
		fileChooser.setInitialDirectory(initialDir);
		fileChooser.setTitle("Open project");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JSON", "*.json"));

		File file = fileChooser.showOpenDialog(stage);

		if (file != null) {

			HueStewConfig.getInstance().setSaveFile(file.getAbsolutePath());
			HueStewConfig.getInstance().setSaveDirectory(file.getParentFile().getAbsolutePath());

			try {
				showController.loadShow();
			} catch (IOException e) {
				handleError(e, "Corrupt save file");
			}
		}
	}

	@FXML
	private void saveButtonPressed() {
		if (HueStewConfig.getInstance().getSaveFile().isEmpty()) {
			saveAsButtonPressed();
			return;
		}

		try {
			showController.save();
		} catch (IOException e) {
			handleError(e, "Unable to save show");
		}
	}

	@FXML
	private void saveAsButtonPressed() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(HueStewConfig.getInstance().getSaveDirectory()));
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));
		fileChooser.setTitle("Choose save location");

		File file = fileChooser.showSaveDialog(stage);

		if (file != null) {

			HueStewConfig.getInstance().setSaveFile(file.getAbsolutePath());
			HueStewConfig.getInstance().setSaveDirectory(file.getParentFile().getAbsolutePath());

			try {
				showController.save();
			} catch (IOException e) {
				handleError(e, "Unable to save show");
			}
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
		drumKitController.addButtonPressed(trackViewController.getSelection());
	}

	@FXML
	private void insertLightTrackPressed() {
		showController.addTrack();
		SnapshotManager.getInstance().clear();
	}

	@FXML
	private void insertVirtualLightPressed() {
		showController.addVirtualLight();
	}

	@FXML
	private void copyPressed() {
		setClipboard(trackViewController.getSelection());
		pasteMenuItem.setDisable(false);
	}

	private void setClipboard(List<KeyFrame> selection) {
		if (selection == null || selection.isEmpty())
			return;

		clipBoard = new Sequence(selection);
	}

	@FXML
	private void pastePressed() {
		paste();
	}

	@FXML
	private void undoPressed() {
		if (SnapshotManager.getInstance().canUndo()) {
			SnapshotManager.getInstance().undo();
			trackViewController.redraw();
		}
	}

	@FXML
	private void redoPressed() {
		if (SnapshotManager.getInstance().canRedo()) {
			SnapshotManager.getInstance().redo();
			trackViewController.redraw();
		}
	}

}
