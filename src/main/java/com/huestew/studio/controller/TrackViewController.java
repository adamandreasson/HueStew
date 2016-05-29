package com.huestew.studio.controller;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.huestew.studio.controller.tools.SelectTool;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightTrack;
import com.huestew.studio.view.Scrollbar;
import com.huestew.studio.view.TrackSection;
import com.huestew.studio.view.TrackView;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 * Controller that handles all interaction with the TrackCanvas
 * 
 * @author Adam
 *
 */
public class TrackViewController {

	private static final int REDRAW_INTERVAL = 16;

	private Canvas canvas;
	private MainViewController controller;
	private TrackView view;

	private Thread redrawThread;
	private AtomicBoolean redraw;

	private TrackSection clickedSection = TrackSection.NONE;
	private LightTrack clickedTrack;

	private KeyFrame hoveringKeyFrame = null;

	private boolean ctrlDown;

	/**
	 * Create a new track view with an associated canvas
	 * 
	 * @param canvas
	 *            the canvas in which the track view will be drawn.
	 */
	public TrackViewController(Canvas canvas, MainViewController controller) {
		this.canvas = canvas;
		this.controller = controller;
		this.view = new TrackView(canvas, controller.getShow());
		this.redraw = new AtomicBoolean();

		// Register mouse event handlers
		canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> handleMouseReleasedEvent(event));
		canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> handleMousePressedEvent(event));
		canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> handleMouseDraggedEvent(event));
		canvas.addEventHandler(MouseEvent.MOUSE_MOVED, event -> handleMouseMovedEvent(event));

		// Register scroll event handler
		canvas.addEventHandler(ScrollEvent.SCROLL, event -> handleScrollEvent(event));

		// Update scrollbars when canvas size is changed
		canvas.widthProperty().addListener((a, b, c) -> {
			if (controller.getShow() != null) {
				view.getHorizontalScrollbar().update();
			}
		});
		canvas.heightProperty().addListener((a, b, c) -> {
			if (controller.getShow() != null) {
				view.getVerticalScrollbar().update();
			}
		});
	}

	/**
	 * Handle a keyboard event input (global events handled here)
	 * 
	 * @param event
	 *            The event passed from JavaFX
	 */
	public void keyboardEvent(KeyEvent event) {
		switch (event.getCode()) {

		// Space is used to pause/play
		case SPACE:
			if (event.getEventType() == KeyEvent.KEY_RELEASED)
				controller.getPlayer().toggle();
			break;

		// Remember if ctrl is down
		case CONTROL:
			ctrlDown = event.getEventType() == KeyEvent.KEY_PRESSED;
			break;

		// Pass the event to other listeners if any other key was pressed
		default:
			return;
		}

		event.consume();
	}

	/**
	 * Remember which {@link KeyFrame} the mouse cursor is hovering
	 * 
	 * @param track
	 *            The track which the cursor is located within
	 * @param event
	 *            The event that triggered the update
	 */
	private void updateHoveringKeyFrame(LightTrack track, MouseEvent event) {
		this.hoveringKeyFrame = view.getKeyFrame(track, event.getX(), view.getRelativeTrackY(track, event.getY()));
	}

	/**
	 * Handle a mouse press event (on key down)
	 * 
	 * @param event
	 */
	private void handleMousePressedEvent(MouseEvent event) {

		canvas.requestFocus();
		clickedSection = view.getSection(event);
		clickedTrack = view.getTrackFromY(event.getY());

		if (clickedSection == TrackSection.VERTICAL_SCROLLBAR) {

			Scrollbar scrollbar = view.getVerticalScrollbar();

			transferScrollbar(scrollbar, event.getY());
			scrollbar.setBarOrigin(event.getY());

		} else if (clickedSection == TrackSection.HORIZONTAL_SCROLLBAR) {

			Scrollbar scrollbar = view.getHorizontalScrollbar();

			transferScrollbar(scrollbar, event.getX());
			scrollbar.setBarOrigin(event.getX());
			view.setUseDesiredCursor(false);

		} else if (event.getButton() == MouseButton.MIDDLE) {

			view.getVerticalScrollbar().setOrigin(event.getY());
			view.getHorizontalScrollbar().setOrigin(event.getX());
			view.setUseDesiredCursor(false);

		} else if (clickedSection == TrackSection.CURSOR) {

			seekingEvent(event);

		} else if (clickedSection == TrackSection.TIMELINE) {

			view.getHorizontalScrollbar().setOrigin(event.getX());
			view.setUseDesiredCursor(false);

		} else if (clickedSection == TrackSection.TRACKS) {

			sendMouseEventToTool(event);

		}
	}

	/**
	 * Handle mouse release event (key up)
	 * 
	 * @param event
	 */
	private void handleMouseReleasedEvent(MouseEvent event) {
		canvas.requestFocus();

		if (clickedSection == TrackSection.TIMELINE || clickedSection == TrackSection.HORIZONTAL_SCROLLBAR
				|| event.getButton() == MouseButton.MIDDLE) {
			view.setUseDesiredCursor(true);
			view.updateDesiredCursorPosition();
		}

		// Pass event to currently active tool if the mouse was clicked on a
		// track
		if (clickedSection == TrackSection.TRACKS) {
			sendMouseEventToTool(event);
		}

		// Update the color picker canvas
		controller.updateColorPicker(view.getSelectedKeyFrames());

		clickedSection = TrackSection.NONE;
	}

	/**
	 * Handle mouse drag event
	 * 
	 * @param event
	 */
	private void handleMouseDraggedEvent(MouseEvent event) {

		if (clickedSection == TrackSection.VERTICAL_SCROLLBAR) {

			view.getVerticalScrollbar().setBarPosition(event.getY());
			controller.updateTrackActionPanePosition();
			redraw();

		} else if (clickedSection == TrackSection.HORIZONTAL_SCROLLBAR) {

			view.getHorizontalScrollbar().setBarPosition(event.getX());
			redraw();

		} else if (event.getButton() == MouseButton.MIDDLE) {

			view.getVerticalScrollbar().setPosition(event.getY());
			view.getHorizontalScrollbar().setPosition(event.getX());
			controller.updateTrackActionPanePosition();
			redraw();

		} else if (clickedSection == TrackSection.CURSOR) {

			seekingEvent(event);

		} else if (clickedSection == TrackSection.TIMELINE) {

			view.getHorizontalScrollbar().setPosition(event.getX());
			redraw();

		} else if (clickedSection == TrackSection.TRACKS) {

			sendMouseEventToTool(event);
		}

	}

	/**
	 * Handle mouse move event. Only used for updating hovered {@link KeyFrame}
	 * and changing the cursor. Don't add method calls here until necessary to
	 * keep up performance.
	 * 
	 * @param event
	 */
	private void handleMouseMovedEvent(MouseEvent event) {
		// Get light track from mouse coordinates
		LightTrack track = view.getTrackFromY(event.getY());

		// Set cursor
		if (clickedSection.hasCursor()) {
			canvas.setCursor(clickedSection.getCursor());
		} else {
			TrackSection section = view.getSection(event);

			if (section.hasCursor()) {
				canvas.setCursor(section.getCursor());
			} else if (track != null) {
				updateHoveringKeyFrame(track, event);
				canvas.setCursor(controller.getToolbox().getSelectedTool().getCursor(hoveringKeyFrame != null,
						clickedSection != TrackSection.NONE));
			}
		}
	}

	/**
	 * Handle a scroll event
	 * 
	 * @param event
	 */
	private void handleScrollEvent(ScrollEvent event) {
		if (ctrlDown) {
			// Zoom
			view.adjustZoom(event.getDeltaY() > 0 ? TrackView.ZOOM_IN : TrackView.ZOOM_OUT);
			controller.updateZoomButtons();
		} else {
			// Scroll horizontally
			view.getHorizontalScrollbar().addOffset(-event.getDeltaY());
			view.updateDesiredCursorPosition();
		}
		redraw();
	}

	/**
	 * Handle a seeking event
	 * 
	 * @param event
	 */
	private void seekingEvent(MouseEvent event) {
		view.setDesiredCursorPosition(event.getX());
		int time = view.getTimeFromX(event.getX());
		controller.getPlayer().seek(time);
		redraw();
	}

	/**
	 * Pass the mouse event to active tool
	 * 
	 * @param event
	 */
	private void sendMouseEventToTool(MouseEvent event) {

		// Update selection rectangle if the select tool is active
		if (controller.getToolbox().getActiveTool() instanceof SelectTool) {
			updateSelectRectangle(event);
		}

		if (clickedTrack == null) {
			return;
		}

		if (hoveringKeyFrame != null)
			controller.getToolbox().getSelectTool().setActive();

		if (event.getEventType() == MouseEvent.MOUSE_RELEASED)
			controller.getToolbox().reset();

		// Calculate normalized y coordinate
		double inverseTrackY = view.getTrackHeight() - view.getRelativeTrackY(clickedTrack, event.getY());
		double normalizedY = inverseTrackY / view.getTrackHeight();
		if (normalizedY > 1)
			normalizedY = 1;
		if (normalizedY < 0)
			normalizedY = 0;

		// Pass event to current tool
		controller.getToolbox().getActiveTool().doAction(event, clickedTrack, hoveringKeyFrame,
				view.getSelectedKeyFrames(), view.getTimeFromX(event.getX()), normalizedY);

		// Redraw canvas
		redraw();
	}

	/**
	 * Update the selection rectangle based on mouse position
	 * 
	 * @param event
	 */
	private void updateSelectRectangle(MouseEvent event) {

		if (event.getEventType() == MouseEvent.MOUSE_PRESSED && hoveringKeyFrame == null) {

			view.newSelectRectangle(event.getX(), event.getY());

		} else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {

			if (!view.selectRectangleExists())
				return;

			view.updateSelectRectangle(event.getX(), event.getY());

		} else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
			if (!view.selectRectangleExists())
				return;

			selectKeyFrames();
			view.resetSelectRectangle();
		}

	}

	/**
	 * Notify the parent controller of a change in selected key frames
	 */
	private void selectKeyFrames() {

		int tracksInSelection = view.selectKeyFrames(ctrlDown);

		controller.notifySelectionChange(view.getSelectedKeyFrames(), tracksInSelection);
	}

	/**
	 * Request a new redraw in the track view thread
	 */
	public void redraw() {
		redraw.set(true);
	}

	/**
	 * Do a redraw of the {@link TrackView} if possible
	 */
	private void performRedraw() {
		if (controller.getShow() == null || controller.getShow().getDuration() == 0)
			return;

		view.redraw(controller.getPlayer().isPlaying());
	}

	public void startRedrawThread() {
		stopRedrawThread();

		redrawThread = new Thread(() -> {
			while (true) {
				if (redraw.getAndSet(false))
					Platform.runLater(() -> performRedraw());

				// Sleep until next frame should be drawn
				try {
					Thread.sleep(REDRAW_INTERVAL);
				} catch (InterruptedException e) {
					break;
				}
			}
		});
		redrawThread.start();
	}

	public void stopRedrawThread() {
		if (redrawThread != null)
			redrawThread.interrupt();
	}

	/**
	 * Pass the list of images to the {@link TrackView} and redraw
	 * 
	 * @param imagePaths
	 *            List of image paths for the wave form
	 */
	public void loadWaves(List<String> imagePaths) {
		view.loadWaves(imagePaths);
		redraw();
	}

	/**
	 * Get the Y position of a {@link LightTrack} in the canvas
	 * 
	 * @param track
	 *            The track to get the position of
	 * @return Y position in pixels of the track
	 */
	public double getTrackPositionY(LightTrack track) {
		return view.getTrackPositionY(track);
	}

	public double getZoom() {
		return view.getZoom();
	}

	/**
	 * Adjust the zoom. Pass to {@link TrackView}
	 * 
	 * @param zoom
	 *            Zoom level (factor where 1.0 is 100% zoom)
	 */
	public void adjustZoom(double zoom) {
		view.adjustZoom(zoom);
	}

	/**
	 * Move scroll bar paddle to X position
	 * 
	 * @param scrollbar
	 *            Scroll bar to move
	 * @param newPosition
	 *            The new X position we wish to place the paddle at
	 */
	private void transferScrollbar(Scrollbar scrollbar, double newPosition) {
		// TODO this has gone too far...
		if (!scrollbar.isOnBar(newPosition)) {
			scrollbar.addOffset(-scrollbar.getOffset());
			scrollbar.setBarOrigin(0);
			scrollbar.setBarPosition(newPosition - scrollbar.getBarSize() / 2);
			redraw();
		}
	}

	/**
	 * Get active selection from {@link TrackView}
	 * 
	 * @return list of selected key frames
	 */
	public List<KeyFrame> getSelection() {
		return view.getSelectedKeyFrames();
	}

	/**
	 * Get horizontal scroll bar from {@link TrackView}
	 * 
	 * @return horizontal scroll bar
	 */
	public Scrollbar getHorizontalScrollbar() {
		return view.getHorizontalScrollbar();
	}
}
