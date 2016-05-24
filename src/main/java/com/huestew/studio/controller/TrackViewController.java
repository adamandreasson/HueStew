package com.huestew.studio.controller;

import java.util.List;

import com.huestew.studio.controller.tools.SelectTool;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightTrack;
import com.huestew.studio.view.TrackSection;
import com.huestew.studio.view.TrackView;

import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 * Class handles all interaction and drawing of TrackCanvas
 * 
 * @author Adam
 *
 */
public class TrackViewController {

	private Canvas canvas;
	private MainViewController controller;
	private TrackView view;

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

	public void keyboardEvent(KeyEvent event) {
		switch (event.getCode()) {
		case SPACE:
			if (event.getEventType() == KeyEvent.KEY_RELEASED)
				controller.getPlayer().toggle();
			break;
		case CONTROL:
			ctrlDown = event.getEventType() == KeyEvent.KEY_PRESSED;
			break;
		default:
			return;
		}
		event.consume();
	}

	private void updateHoveringKeyFrame(LightTrack track, MouseEvent event) {
		this.hoveringKeyFrame = view.getKeyFrame(track, event.getX(), view.getRelativeTrackY(track, event.getY()));
	}

	private void handleMousePressedEvent(MouseEvent event) {
		canvas.requestFocus();
		clickedSection = view.getSection(event);
		clickedTrack = view.getTrackFromY(event.getY());

		if (clickedSection == TrackSection.VERTICAL_SCROLLBAR) {
			view.getVerticalScrollbar().setBarOrigin(event.getY());
		} else if (clickedSection == TrackSection.HORIZONTAL_SCROLLBAR) {
			view.getHorizontalScrollbar().setBarOrigin(event.getX());
		} else if (event.getButton() == MouseButton.MIDDLE) {
			view.getVerticalScrollbar().setOrigin(event.getY());
			view.getHorizontalScrollbar().setOrigin(event.getX());
		} else if (clickedSection == TrackSection.TIMELINE) {
			view.getHorizontalScrollbar().setOrigin(event.getX());
		} else if (clickedSection == TrackSection.CURSOR) {
			int time = view.getTimeFromX(event.getX());
			controller.getPlayer().seek(time);
		} else {
			sendMouseEventToTool(event);
		}
	}

	private void handleMouseReleasedEvent(MouseEvent event) {
		canvas.requestFocus();

		if (clickedTrack != null) {
			sendMouseEventToTool(event);
		}

		controller.openColorPicker(view.getSelectedKeyFrames());

		clickedSection = TrackSection.NONE;
	}

	private void handleMouseDraggedEvent(MouseEvent event) {
		if (clickedSection == TrackSection.VERTICAL_SCROLLBAR) {
			view.getVerticalScrollbar().setBarPosition(event.getY());
			redraw();
		} else if (clickedSection == TrackSection.HORIZONTAL_SCROLLBAR) {
			view.getHorizontalScrollbar().setBarPosition(event.getX());
			redraw();
		} else if (event.getButton() == MouseButton.MIDDLE) {
			view.getVerticalScrollbar().setPosition(event.getY());
			view.getHorizontalScrollbar().setPosition(event.getX());
			redraw();
		} else if (clickedSection == TrackSection.TIMELINE) {
			view.getHorizontalScrollbar().setPosition(event.getX());
			redraw();
		} else {
			sendMouseEventToTool(event);
		}
	}

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

	private void sendMouseEventToTool(MouseEvent event) {
		if (clickedSection != TrackSection.TRACKS) {
			parseTrackEvent(event);
			return;
		}

		// TODO what's that smell?
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

		// Get normalized y coordinate
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

	private void selectKeyFrames() {

		int tracksInSelection = view.selectKeyFrames(ctrlDown);

		controller.notifySelectionChange(view.getSelectedKeyFrames(), tracksInSelection);
	}

	private void handleScrollEvent(ScrollEvent event) {
		if (event.getTouchCount() == 0) {
			// Scroll wheel -> zoom or scroll horizontally
			if (ctrlDown) {
				// Zoom
				view.adjustZoom(event.getDeltaY() > 0 ? TrackView.ZOOM_IN : TrackView.ZOOM_OUT);
				controller.updateZoomButtons();
			} else {
				// Scroll
				view.getHorizontalScrollbar().addOffset(-event.getDeltaY());
			}
			redraw();
		} else {
			// TODO Touchpad scroll

		}
	}

	private void parseTrackEvent(MouseEvent event) {
		// TODO refactor different sections into this method?
		// Seeking event
		if (clickedSection == TrackSection.CURSOR) {
			int time = view.getTimeFromX(event.getX());
			controller.getPlayer().seek(time);
			redraw();
		}
	}

	public void redraw() {
		if (controller.getShow() == null || controller.getShow().getDuration() == 0)
			return;

		view.redraw(controller.getPlayer().isPlaying());
	}
	/*
	 * public TrackView getView(){ return view; }
	 */

	public void loadWaves(List<String> imagePaths) {
		view.loadWaves(imagePaths);
		redraw();
	}

	public double getTrackPositionY(LightTrack track) {
		return view.getTrackPositionY(track);
	}

	public double getZoom() {
		return view.getZoom();
	}

	public void adjustZoom(double zoom) {
		view.adjustZoom(zoom);
	}

	public List<KeyFrame> getSelection() {
		return view.getSelectedKeyFrames();
	}
}
