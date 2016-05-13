package com.huestew.studio.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.huestew.studio.HueStew;
import com.huestew.studio.Toolbox;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightTrack;
import com.huestew.studio.tools.SelectTool;
import com.huestew.studio.util.GraphicsUtil;

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

/**
 * Class handles all interaction and drawing of TrackCanvas
 * 
 * @author Adam
 *
 */
public class TrackView {
	private static final int KEY_FRAME_SIZE = 5;
	private static final int TRACK_SPACER = 10;

	public static final int PIXELS_PER_SECOND = 100;

	public static final Color TIMELINE_COLOR = new Color(0.3, 0.3, 0.3, 1);
	public static final Color TIMELINE_TICK_COLOR = new Color(0.5, 0.5, 0.5, 1);
	public static final Color TIMELINE_TICK_SHADOW_COLOR = new Color(0.26, 0.26, 0.26, 1);
	public static final Color BACKGROUND_COLOR = new Color(0.4, 0.4, 0.4, 1);
	public static final Color TRACK_COLOR = new Color(0.0, 0.0, 0.0, 0.2);

	private Canvas canvas;
	private List<Image> backgroundWaveImages;
	private long lastRedraw;

	private double offsetX = 0;
	private double scrollOriginX = -1;
	private Rectangle selectRectangle;
	private Set<KeyFrame> selectedKeyFrames;

	private KeyFrame hoveringKeyFrame = null;
	private boolean isMouseDown = false;

	/**
	 * Create a new track view with an associated canvas
	 * 
	 * @param canvas
	 *            the canvas in which the track view will be drawn.
	 */
	public TrackView(Canvas canvas) {
		this.canvas = canvas;
		canvas.setVisible(false);

		// Register mouse event handlers
		canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			canvas.requestFocus();
			isMouseDown = false;

			if (event.getButton() == MouseButton.SECONDARY && hoveringKeyFrame != null) {
				HueStew.getInstance().getView().openColorPickerPane(hoveringKeyFrame);
			}

			if (scrollOriginX != -1) {
				scrollOriginX = -1;
			} else {
				sendMouseEventToTool(event);
			}
		});
		canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
			canvas.requestFocus();

			isMouseDown = true;

			if (event.getButton() == MouseButton.PRIMARY && event.getY() >= 20 && getTrackFromY(event.getY()) == null
					|| event.getButton() == MouseButton.MIDDLE) {
				// Scroll
				scrollOriginX = event.getX();
			} else {
				sendMouseEventToTool(event);
			}
		});
		canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
			if (scrollOriginX != -1) {
				double offset = offsetX + scrollOriginX - event.getX();
				double maxOffset = getXFromTime(HueStew.getInstance().getShow().getDuration()) + offsetX
						- canvas.getWidth();
				offsetX = Math.max(0, Math.min(maxOffset, offset));
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
			if (track == null) {
				if (event.getY() > 20)
					canvas.setCursor(Cursor.OPEN_HAND);
				else
					canvas.setCursor(Cursor.E_RESIZE);
			} else {
				updateHoveringKeyFrame(track, event);
				canvas.setCursor(Toolbox.getCursor(hoveringKeyFrame != null, isMouseDown));
			}
		});

	}

	public void loadWaves(List<String> filePaths) {

		this.backgroundWaveImages = new ArrayList<Image>();

		try {
			for (String path : filePaths) {
				System.out.println(path);
				this.backgroundWaveImages.add(new Image(path));
			}

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
		// Get light track and timestamp from mouse coordinates
		LightTrack track = getTrackFromY(event.getY());
		if (track == null) {
			parseTrackEvent(event);
			return;
		}

		// TODO what's that smell?
		if (Toolbox.getActiveTool() instanceof SelectTool) {
			updateSelectRectangle(event);
		}

		if (hoveringKeyFrame != null)
			Toolbox.SELECT.setActive();
		if (event.getEventType() == MouseEvent.MOUSE_CLICKED)
			Toolbox.reset();

		// Get normalized y coordinate
		double inverseTrackY = getTrackHeight() - getRelativeTrackY(track, event.getY());
		double normalizedY = inverseTrackY / getTrackHeight();
		if (normalizedY > 1)
			normalizedY = 1;
		if (normalizedY < 0)
			normalizedY = 0;

		// Pass event to current tool
		Toolbox.getActiveTool().doAction(event, track, hoveringKeyFrame, getTimeFromX(event.getX()), normalizedY);

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

		} else if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
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
		if (event.getY() < 20) {
			int time = getTimeFromX(event.getX());
			HueStew.getInstance().getPlayer().seek(time);
			redraw();
		}
	}

	public void redraw() {
		if (HueStew.getInstance().getShow() == null || HueStew.getInstance().getShow().getDuration() == 0) 
			return;

		// Don't draw too often, it's unnecessary
		long now = System.currentTimeMillis();
		if(now - lastRedraw < 16)
			return;
		lastRedraw = now;

		if (!canvas.isVisible()) 
			canvas.setVisible(true);


		// Perform drawing on javafx main thread
		Platform.runLater(new Runnable() {
			public void run() {

				GraphicsContext gc = canvas.getGraphicsContext2D();
				gc.setFill(BACKGROUND_COLOR);
				gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

				drawTimeline(gc);
				drawLightTracks(gc);
				drawSelection(gc);
				drawCursor(gc);
			}
		});
	}

	private void drawTimeline(GraphicsContext gc) {
		LinearGradient lg = new LinearGradient(0, 0, 0, 1, true,
				CycleMethod.NO_CYCLE,
				new Stop(1.0, new Color(0.2,0.2,0.2,1)),
				new Stop(0.0, new Color(0.3,0.3,0.3,1)));
		gc.setFill(lg);
		gc.fillRect(0, 0, canvas.getWidth(), 40);

		drawWave(gc);

		gc.setLineWidth(1);
		gc.setStroke(TIMELINE_TICK_COLOR);

		gc.strokeLine(0, 20.5, canvas.getWidth(), 20.5);
		gc.strokeLine(0, 40.5, canvas.getWidth(), 40.5);

		gc.setStroke(TIMELINE_TICK_SHADOW_COLOR);
		gc.strokeLine(0, 19.5, canvas.getWidth(), 19.5);
		gc.strokeLine(0, 39.5, canvas.getWidth(), 39.5);

		gc.setFill(Color.WHITE);

		int firstTick = getTimeFromX(0) / 1000;
		int lastTick = getTimeFromX(canvas.getWidth()) / 1000;

		// Draw out the ticks on the timeline
		for (int i = firstTick; i <= lastTick; i++) {
			int time = i * 1000;
			double x = getXFromTime(time);
			// If the timestamp is divisible by 10, we will draw a longer tick
			// and display the time.
			if (i % 10 == 0) {
				gc.fillText("" + i, x, 34);
			}
			gc.setStroke(TIMELINE_TICK_SHADOW_COLOR);
			GraphicsUtil.sharpLine(gc, x-1, i % 5 == 0 ? 31 : 35, x-1, 40);
			gc.setStroke(TIMELINE_TICK_COLOR);
			GraphicsUtil.sharpLine(gc, x, i % 5 == 0 ? 32 : 36, x, 40);
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
			gc.fillRect(0, getTrackPositionY(i), canvas.getWidth(), getTrackHeight());

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
		gc.fillPolygon(new double[] { x, x - KEY_FRAME_SIZE, x + KEY_FRAME_SIZE, x },
				new double[] { y, y - KEY_FRAME_SIZE, y - KEY_FRAME_SIZE, y }, 3);
	}

	private void drawKeyFrame(GraphicsContext gc, double x, double y, boolean selected) {
		gc.setFill(Color.YELLOW);
		if (selected)
			gc.setFill(Color.RED);
		gc.fillPolygon(new double[] { x - KEY_FRAME_SIZE, x, x + KEY_FRAME_SIZE, x },
				new double[] { y, y + KEY_FRAME_SIZE, y, y - KEY_FRAME_SIZE }, 4);
	}

	private void drawSelection(GraphicsContext gc) {

		if (selectRectangle != null) {
			gc.rect(selectRectangle.getX(), selectRectangle.getY(), selectRectangle.getWidth(),
					selectRectangle.getHeight());
			gc.setFill(new Color(0.2, 0.0, 1.0, 0.1));
			gc.setStroke(new Color(0.2, 0.0, 1.0, 0.9));
			gc.setLineWidth(0.5);
			gc.fill();
			gc.stroke();
		}
	}

	private LightTrack getTrackFromY(double y) {
		double adjustedY = y - getTotalTrackPositionY();
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

	private double getTrackHeight() {
		return getTotalTrackHeight() / HueStew.getInstance().getShow().getLightTracks().size() - TRACK_SPACER;
	}

	private double getTotalTrackHeight() {
		return canvas.getHeight() - getTotalTrackPositionY();
	}

	private double getTrackPositionY(int i) {
		return getTotalTrackPositionY() + (getTrackHeight() + TRACK_SPACER) * i;
	}

	private double getTotalTrackPositionY() {
		return 40;
	}

}
