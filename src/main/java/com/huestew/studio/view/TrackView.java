package com.huestew.studio.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.huestew.studio.HueStew;
import com.huestew.studio.Scrollbar;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightTrack;
import com.huestew.studio.tools.SelectTool;
import com.huestew.studio.util.GraphicsUtil;
import com.huestew.studio.util.Util;

import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Class handles all interaction and drawing of TrackCanvas
 * 
 * @author Adam
 *
 */
public class TrackView {
	private enum Section {
		CURSOR(Cursor.E_RESIZE), TIMELINE(Cursor.OPEN_HAND), TRACKS(null), 
		VERTICAL_SCROLLBAR(Cursor.DEFAULT), HORIZONTAL_SCROLLBAR(Cursor.DEFAULT), NONE(null);
		
		private Cursor cursor;

		private Section(Cursor cursor) {
			this.cursor = cursor;
		}

		boolean hasCursor() {
			return cursor != null;
		}

		Cursor getCursor() {
			return cursor;
		}
	}

	private static final int KEY_FRAME_SIZE = 5;
	private static final int TRACK_SPACER = 10;
	private static final int MINIMUM_TRACK_HEIGHT = 80;
	private static final int SCROLLBAR_SIZE = 8;

	public static final int PIXELS_PER_SECOND = 100;

	private static final Color TIMELINE_COLOR = Color.web("#303030");
	private static final Color TIMELINE_TICK_COLOR = Color.web("#616161");
	private static final Color TIMELINE_TICK_SHADOW_COLOR = new Color(0.26, 0.26, 0.26, 0);
	private static final Color TIMELINE_TEXT_COLOR = Color.web("#a5a5a5");
	private static final Color BACKGROUND_COLOR = Color.web("#535353");
	private static final Color TRACK_COLOR = new Color(0.06, 0.06, 0.06, 0.2);
	private static final Color TRACK_BORDER_COLOR = Color.web("#383838");
	private static final Color SCROLLBAR_COLOR = Color.web("#303030");

	private Canvas canvas;
	private List<Image> backgroundWaveImages;
	private long lastRedraw;

	private Section clickedSection = Section.NONE;
	private LightTrack clickedTrack;

	private double offsetX = 0;
	private double scrollOriginX = -1;
	private Rectangle selectRectangle;
	private Set<KeyFrame> selectedKeyFrames;

	private Scrollbar verticalScrollbar;

	private KeyFrame hoveringKeyFrame = null;

	/**
	 * Create a new track view with an associated canvas
	 * 
	 * @param canvas
	 *            the canvas in which the track view will be drawn.
	 */
	public TrackView(Canvas canvas) {
		this.canvas = canvas;
		canvas.setVisible(false);
		this.selectedKeyFrames = new HashSet<KeyFrame>();
		this.verticalScrollbar = new Scrollbar(() -> getTotalVisibleTrackHeight(), () -> getTotalTrackHeight());
		
		canvas.widthProperty().addListener((a, b, c) -> {
			System.out.println("Canvas width: " + b.doubleValue() + ", Window width: " + canvas.getScene().getWindow().getWidth());
		});

		// Register mouse event handlers
		canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
			canvas.requestFocus();

			if (scrollOriginX != -1) {
				scrollOriginX = -1;
			} else if (clickedTrack != null) {
				sendMouseEventToTool(event);
			}

			if (event.getButton() == MouseButton.SECONDARY && hoveringKeyFrame != null) {
				// HueStew.getInstance().getView().openColorPickerPane(hoveringKeyFrame);
				HueStew.getInstance().getView().openColorPickerPane(selectedKeyFrames);
			}

			clickedSection = Section.NONE;
		});
		canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
			canvas.requestFocus();
			clickedSection = getSection(event);
			clickedTrack = getTrackFromY(event.getY());
			System.out.println(clickedSection.name());

			if (clickedSection == Section.VERTICAL_SCROLLBAR) {
				verticalScrollbar.setBarOrigin(event.getY());
			} else if (event.getButton() == MouseButton.MIDDLE) {
				verticalScrollbar.setOrigin(event.getY());
			} else if (event.getButton() == MouseButton.PRIMARY && clickedSection == Section.TIMELINE
					|| event.getButton() == MouseButton.MIDDLE) {
				// Scroll
				scrollOriginX = event.getX();
			} else if (clickedTrack != null) {
				sendMouseEventToTool(event);
			}
		});
		canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
			if (clickedSection == Section.VERTICAL_SCROLLBAR) {
				verticalScrollbar.setBarPosition(event.getY());
				redraw();
			} else if (event.getButton() == MouseButton.MIDDLE) {
				verticalScrollbar.setPosition(event.getY());
				redraw();
			} else if (scrollOriginX != -1) {
				// TODO replace with horizontal scrollbar?
				setOffset(offsetX + scrollOriginX - event.getX());
				scrollOriginX = event.getX();
				redraw();
			} else {
				sendMouseEventToTool(event);
			}
		});
		canvas.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
			// Get light track from mouse coordinates
			LightTrack track = getTrackFromY(event.getY());

			// Set cursor
			if (clickedSection.hasCursor()) {
				canvas.setCursor(clickedSection.getCursor());
			} else {
				Section section = getSection(event);

				if (section.hasCursor()) {
					canvas.setCursor(section.getCursor());
				} else if (track != null) {
					updateHoveringKeyFrame(track, event);
					canvas.setCursor(HueStew.getInstance().getToolbox().getSelectedTool()
							.getCursor(hoveringKeyFrame != null, clickedSection != Section.NONE));
				}
			}
		});
	}

	public void loadWaves(List<String> filePaths) {
		this.backgroundWaveImages = new ArrayList<Image>();

		try {
			for (String path : filePaths)
				this.backgroundWaveImages.add(new Image(path));

			redraw();

		} catch (IllegalArgumentException e) {
			System.out.println("wave not generated yet probably?");
		}
	}

	public void keyboardEvent(KeyEvent event) {
		switch (event.getCode()) {
		case SPACE:
			HueStew.getInstance().getPlayer().toggle();
			break;
		default:
			return;
		}
		redraw();
		event.consume();

	}

	private void updateHoveringKeyFrame(LightTrack track, MouseEvent event) {
		this.hoveringKeyFrame = getKeyFrame(track, event.getX(), getRelativeTrackY(track, event.getY()));
	}

	private void sendMouseEventToTool(MouseEvent event) {
		if (clickedSection != Section.TRACKS) {
			parseTrackEvent(event);
			return;
		}

		// TODO what's that smell?
		if (HueStew.getInstance().getToolbox().getActiveTool() instanceof SelectTool) {
			updateSelectRectangle(event);
		}

		if (hoveringKeyFrame != null)
			HueStew.getInstance().getToolbox().getSelectTool().setActive();
		if (event.getEventType() == MouseEvent.MOUSE_RELEASED)
			HueStew.getInstance().getToolbox().reset();

		// Get normalized y coordinate
		double inverseTrackY = getTrackHeight() - getRelativeTrackY(clickedTrack, event.getY());
		double normalizedY = inverseTrackY / getTrackHeight();
		if (normalizedY > 1)
			normalizedY = 1;
		if (normalizedY < 0)
			normalizedY = 0;

		// Pass event to current tool
		HueStew.getInstance().getToolbox().getActiveTool().doAction(event, clickedTrack, hoveringKeyFrame, selectedKeyFrames, getTimeFromX(event.getX()), normalizedY);

		// Redraw canvas
		redraw();
	}

	private void updateSelectRectangle(MouseEvent event) {

		if (event.getEventType() == MouseEvent.MOUSE_PRESSED && hoveringKeyFrame == null) {

			selectRectangle = new Rectangle(0, 0);
			selectRectangle.setX(event.getX());
			selectRectangle.setY(event.getY());

		} else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {

			if (selectRectangle == null)
				return;

			selectRectangle.setWidth(event.getX() - selectRectangle.getX());
			selectRectangle.setHeight(event.getY() - selectRectangle.getY());

		} else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
			if (selectRectangle == null)
				return;

			selectKeyFrames();
			selectRectangle = null;
		}

	}

	private void selectKeyFrames() {

		Set<KeyFrame> selection = new HashSet<KeyFrame>();

		double x1 = selectRectangle.getX();
		double x2 = selectRectangle.getX() + selectRectangle.getWidth();
		double minX = Math.min(x1, x2);
		double maxX = Math.max(x1, x2);

		double y1 = selectRectangle.getY();
		double y2 = selectRectangle.getY() + selectRectangle.getHeight();
		double minY = Math.min(y1, y2);
		double maxY = Math.max(y1, y2);

		int i = -1;
		for (LightTrack track : HueStew.getInstance().getShow().getLightTracks()) {
			i++;

			if (getTrackPositionY(i) < minY - getTrackHeight())
				continue;

			if (getTrackPositionY(i) > maxY)
				break;

			double trackMinY = minY - getTrackPositionY(i);
			double trackMaxY = maxY - getTrackPositionY(i);

			/*
			 * System.out.println("Track " + i + " position Y: " +
			 * getTrackPositionY(i)); System.out.println("Track " + i + " min: "
			 * + trackMinY); System.out.println("Track " + i + " max: " +
			 * trackMaxY);
			 */

			for (KeyFrame keyFrame : track.getKeyFrames()) {
				double localFrameY = getTrackHeight() - getRelativeYFromBrightness(keyFrame.getState().getBrightness());
				double frameX = getXFromTime(keyFrame.getTimestamp());

				if (frameX > minX && frameX < maxX) {
					if (localFrameY > trackMinY && localFrameY < trackMaxY) {
						selection.add(keyFrame);
					}
				}
			}
		}

		selectedKeyFrames = selection;
	}

	private void parseTrackEvent(MouseEvent event) {
		// Seeking event
		if (clickedSection == Section.CURSOR) {
			int time = getTimeFromX(event.getX());
			HueStew.getInstance().getPlayer().seek(time);
			redraw();
		}
	}

	private void setOffset(double offset) {
		double maxOffset = getXFromTime(HueStew.getInstance().getShow().getDuration()) + offsetX - canvas.getWidth();
		offsetX = Math.max(0, Math.min(maxOffset, offset));
	}

	public void redraw() {
		if (HueStew.getInstance().getShow() == null || HueStew.getInstance().getShow().getDuration() == 0)
			return;

		// Don't draw too often, it's unnecessary
		long now = System.currentTimeMillis();
		if (now - lastRedraw < 16)
			return;
		lastRedraw = now;

		if (!canvas.isVisible())
			canvas.setVisible(true);

		// Scroll automatically while playing the show
		if (HueStew.getInstance().getPlayer().isPlaying()) {
			double x = getXFromTime(HueStew.getInstance().getCursor());
			double leftEdge = 10;
			double rightEdge = canvas.getWidth() - 300;

			if (x > rightEdge) {
				setOffset(offsetX + x - rightEdge);
			} else if (x < leftEdge) {
				setOffset(offsetX + x - leftEdge);
			}
		}

		// Perform drawing on javafx main thread
		Platform.runLater(new Runnable() {
			public void run() {
				GraphicsContext gc = canvas.getGraphicsContext2D();
				gc.setFont(new Font(11.0));
				gc.setFill(BACKGROUND_COLOR);
				gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

				drawWave(gc);
				drawLightTracks(gc);
				drawTimeline(gc);
				drawSelection(gc);
				drawCursor(gc);
				drawScrollbars(gc);
			}
		});
	}

	private void drawTimeline(GraphicsContext gc) {
		LinearGradient lg = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(1.0, new Color(0.2, 0.2, 0.2, 1)), new Stop(0.0, new Color(0.3, 0.3, 0.3, 1)));
		gc.setFill(lg);
		gc.fillRect(0, 0, canvas.getWidth(), 40);

		gc.setFill(TIMELINE_COLOR);
		gc.fillRect(0, 20, canvas.getWidth(), 20);

		gc.setLineWidth(1);
		gc.setStroke(Color.web("#222222"));

		gc.strokeLine(0, 20.5, canvas.getWidth(), 20.5);

		gc.setStroke(TIMELINE_TICK_COLOR);

		gc.setFill(TIMELINE_TEXT_COLOR);

		int firstTick = getTimeFromX(0);
		int lastTick = getTimeFromX(getTrackWidth());

		// Draw out the ticks on the timeline
		for (int time = firstTick; time <= lastTick; time++) {
			if (time % 100 != 0)
				continue;
			double x = getXFromTime(time);
			// If the timestamp is divisible by 10, we will draw a longer tick
			// and display the time.
			if (time % 1000 == 0) {
				gc.setTextAlign(TextAlignment.CENTER);
				gc.fillText(Util.formatTimestamp(time), x, 31);
			}
			gc.setStroke(TIMELINE_TICK_COLOR);
			GraphicsUtil.sharpLine(gc, x, time % 500 == 0 ? 32 : 36, x, 40);
		}
	}

	private void drawWave(GraphicsContext gc) {
		if (backgroundWaveImages == null)
			return;

		for (int i = 0; i < backgroundWaveImages.size(); i++) {
			int startX = 1024 * i;
			Image image = backgroundWaveImages.get(i);

			// Skip if image is to the left of the canvas
			if (startX + image.getWidth() < offsetX)
				continue;

			// Skip the rest if image is to the right of the canvas
			if (startX > canvas.getWidth() + offsetX)
				return;

			gc.drawImage(image, startX - offsetX, 40, image.getWidth(), canvas.getHeight() - 40);
		}
	}

	private void drawLightTracks(GraphicsContext gc) {
		int i = 0;
		for (LightTrack track : HueStew.getInstance().getShow().getLightTracks()) {
			gc.setFill(TRACK_COLOR);
			gc.fillRect(0, getTrackPositionY(i), getTrackWidth(), getTrackHeight());

			gc.setStroke(TRACK_BORDER_COLOR);
			gc.strokeLine(0, getTrackPositionY(i), getTrackWidth(), getTrackPositionY(i));

			drawTrackPolygon(gc, track, getTrackPositionY(i));
			drawKeyFrames(gc, track, getTrackPositionY(i));
			i++;
		}
	}

	private void drawTrackPolygon(GraphicsContext gc, LightTrack track, double startY) {
		gc.setFill(new Color(0.0, 0.2, 0.7, 0.5));
		gc.beginPath();
		gc.moveTo(getXFromTime(0), startY + getTrackHeight());

		Iterator<KeyFrame> iterator = track.getKeyFrames().iterator();
		double x = 0.0;

		while (iterator.hasNext()) {
			KeyFrame frame = iterator.next();
			x = getXFromTime(frame.getTimestamp());
			double y = startY + getTrackHeight() - getRelativeYFromBrightness(frame.getState().getBrightness());
			gc.lineTo(x, y);
		}
		gc.lineTo(x, startY + getTrackHeight());
		gc.lineTo(0, startY + getTrackHeight());

		gc.fill();
		gc.closePath();
	}

	private void drawKeyFrames(GraphicsContext gc, LightTrack track, double startY) {
		Iterator<KeyFrame> iterator = track.getKeyFrames().iterator();
		while (iterator.hasNext()) {
			KeyFrame frame = iterator.next();

			double x = getXFromTime(frame.getTimestamp());
			double y = startY + getTrackHeight() - getRelativeYFromBrightness(frame.getState().getBrightness());
			boolean selected = false;

			if (selectedKeyFrames != null && selectedKeyFrames.contains(frame))
				selected = true;

			drawKeyFrame(gc, x, y, selected);
		}

	}

	private void drawCursor(GraphicsContext gc) {
		double x = getXFromTime(HueStew.getInstance().getCursor());
		double y = 20 + KEY_FRAME_SIZE;

		gc.setStroke(Color.BLACK);
		gc.setLineWidth(1);
		GraphicsUtil.sharpLine(gc, x, 20, x, canvas.getHeight());

		gc.setFill(Color.BROWN);
		gc.fillPolygon(new double[] { x, x - KEY_FRAME_SIZE, x + KEY_FRAME_SIZE, x }, new double[] { y, y - KEY_FRAME_SIZE, y - KEY_FRAME_SIZE, y }, 3);
	}

	private void drawKeyFrame(GraphicsContext gc, double x, double y, boolean selected) {
		gc.setFill(Color.YELLOW);
		if (selected)
			gc.setFill(Color.RED);
		gc.fillPolygon(new double[] { x - KEY_FRAME_SIZE, x, x + KEY_FRAME_SIZE, x }, new double[] { y, y + KEY_FRAME_SIZE, y, y - KEY_FRAME_SIZE }, 4);
	}

	private void drawSelection(GraphicsContext gc) {
		// TODO limit to track area?
		if (selectRectangle != null) {
			gc.rect(selectRectangle.getX(), selectRectangle.getY(), selectRectangle.getWidth(), selectRectangle.getHeight());
			gc.setFill(new Color(0.2, 0.0, 1.0, 0.1));
			gc.setStroke(new Color(0.2, 0.0, 1.0, 0.9));
			gc.setLineWidth(0.5);
			gc.fill();
			gc.stroke();
		}
	}

	private void drawScrollbars(GraphicsContext gc) {
		// TODO
		gc.setFill(SCROLLBAR_COLOR);
		gc.fillRoundRect(getTrackWidth(), getTotalTrackPositionY() + verticalScrollbar.getBarPosition(), SCROLLBAR_SIZE, verticalScrollbar.getBarSize(), SCROLLBAR_SIZE, SCROLLBAR_SIZE);
	}

	private LightTrack getTrackFromY(double y) {
		double adjustedY = y - getTotalTrackPositionY() + verticalScrollbar.getOffset();
		int trackNumber = (int) Math.floor(adjustedY / (getTrackHeight() + TRACK_SPACER));
		List<LightTrack> tracks = HueStew.getInstance().getShow().getLightTracks();
		if (trackNumber >= 0 && trackNumber < tracks.size()) {
			return tracks.get(trackNumber);
		}
		return null;
	}

	private KeyFrame getKeyFrame(LightTrack track, double x, double y) {
		TreeSet<KeyFrame> keyFrames = track.getKeyFrames();
		KeyFrame target = new KeyFrame(getTimeFromX(x));

		KeyFrame left = keyFrames.floor(target);
		KeyFrame right = keyFrames.ceiling(target);

		if (right != null && isInside(right, x, y)) {
			return right;
		} else if (left != null && isInside(left, x, y)) {
			return left;
		} else {
			return null;
		}
	}

	private boolean isInside(KeyFrame keyFrame, double x, double y) {
		double kX = getXFromTime(keyFrame.getTimestamp());

		if (kX < x - KEY_FRAME_SIZE || kX > x + KEY_FRAME_SIZE) {
			return false;
		}

		double kY = getTrackHeight() - getRelativeYFromBrightness(keyFrame.getState().getBrightness());

		return kY >= y - KEY_FRAME_SIZE && kY <= y + KEY_FRAME_SIZE;
	}

	private double getRelativeTrackY(LightTrack track, double y) {
		int trackNumber = HueStew.getInstance().getShow().getLightTracks().indexOf(track);
		double trackStartY = getTrackPositionY(trackNumber);
		return (y - trackStartY);
	}

	private double getXFromTime(int time) {
		return (time * PIXELS_PER_SECOND / 1000.0) - offsetX;
	}

	private int getTimeFromX(double x) {
		return (int) ((x + offsetX) / (PIXELS_PER_SECOND / 1000.0));
	}

	private double getRelativeYFromBrightness(int brightness) {
		return (brightness / 255.0) * getTrackHeight();
	}

	private double getTrackWidth() {
		return canvas.getWidth() - SCROLLBAR_SIZE;
	}

	private double getTrackHeight() {
		return Math.max(MINIMUM_TRACK_HEIGHT, getTotalVisibleTrackHeight() / HueStew.getInstance().getShow().getLightTracks().size() - TRACK_SPACER);
	}

	private double getTotalVisibleTrackHeight() {
		return canvas.getHeight() - getTotalTrackPositionY();
	}

	private double getTotalTrackHeight() {
		return (getTrackHeight() + TRACK_SPACER) * HueStew.getInstance().getShow().getLightTracks().size();
	}

	private double getTrackPositionY(int i) {
		return getTotalTrackPositionY() + (getTrackHeight() + TRACK_SPACER) * i - verticalScrollbar.getOffset();
	}

	private double getTotalTrackPositionY() {
		return 40;
	}

	private Section getSection(MouseEvent event) {
		return getSection(event.getX(), event.getY());
	}

	private Section getSection(double x, double y) {
		if (x > getTrackWidth() && y >= getTotalTrackPositionY()) {
			return Section.VERTICAL_SCROLLBAR;
		} else if (y <= 20) {
			return Section.CURSOR;
		} else if (y <= 40) {
			return Section.TIMELINE;
		} else {
			return Section.TRACKS;
		}
	}
}
