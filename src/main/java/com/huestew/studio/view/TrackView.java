/**
 * 
 */
package com.huestew.studio.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightTrack;
import com.huestew.studio.model.Show;
import com.huestew.studio.util.GraphicsUtil;
import com.huestew.studio.util.Util;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Class for handling all drawing to the track canvas, as well as related
 * calculations
 * 
 * @author Adam
 *
 */
public class TrackView {

	public static final int PIXELS_PER_SECOND = 100;
	public static final double MINIMUM_ZOOM = 0.25;
	public static final double MAXIMUM_ZOOM = 4;
	public static final double ZOOM_IN = 0.25;
	public static final double ZOOM_OUT = -0.25;

	private static final int KEY_FRAME_SIZE = 5;
	private static final int TRACK_SPACER = 10;
	private static final int MINIMUM_TRACK_HEIGHT = 90;
	private static final int SCROLLBAR_SIZE = 10;

	private static final Color TIMELINE_COLOR = Color.web("#303030");
	private static final Color TIMELINE_TICK_COLOR = Color.web("#616161");
	private static final Color TIMELINE_TEXT_COLOR = Color.web("#a5a5a5");
	private static final Color BACKGROUND_COLOR = Color.web("#535353");
	private static final Color TRACK_COLOR = new Color(0.06, 0.06, 0.06, 0.2);
	private static final Color TRACK_BORDER_COLOR = Color.web("#383838");
	private static final Color SCROLLBAR_COLOR = Color.web("#303030");

	private double zoom = 1;
	private double desiredCursorPosition = 20;
	private int cursorAtLastDraw = 0;
	private boolean useDesiredCursor = true;

	private List<Image> backgroundWaveImages;
	private Scrollbar verticalScrollbar;
	private Scrollbar horizontalScrollbar;

	private Rectangle selectRectangle;
	private List<KeyFrame> selectedKeyFrames;

	private Canvas canvas;
	private Show show;

	/**
	 * Create a new TrackView using given canvas and show
	 * 
	 * @param canvas
	 * @param show
	 */
	public TrackView(Canvas canvas, Show show) {
		this.canvas = canvas;
		this.show = show;
		Platform.runLater(() -> setDesiredCursorPosition(canvas.getWidth() / 2));

		this.selectedKeyFrames = new ArrayList<>();

		this.verticalScrollbar = new Scrollbar(() -> getTotalVisibleTrackHeight(), () -> getTotalTrackHeight());
		this.horizontalScrollbar = new Scrollbar(() -> getVisibleTrackWidth(), () -> getTrackWidth());
	}

	/**
	 * Load wave images to the track view
	 * 
	 * @param filePaths
	 *            The list of images to add to the view
	 */
	public void loadWaves(List<String> filePaths) {
		this.backgroundWaveImages = new ArrayList<Image>();

		for (String path : filePaths)
			this.backgroundWaveImages.add(new Image(path));

	}

	/**
	 * Redraw the track view
	 * 
	 * @param isPlaying
	 *            If the show is playing, calculate if we need to move the view
	 *            on the screen before redrawing
	 */
	public void redraw(boolean isPlaying) {

		// Scroll automatically while playing the show
		if (shouldAutoScroll() && show.getCursor() > cursorAtLastDraw) {
			double x = getXFromTime(show.getCursor());
			double rightEdge = desiredCursorPosition;

			if (x > rightEdge) {
				horizontalScrollbar.addOffset(x - rightEdge);
			}
		}

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

		cursorAtLastDraw = show.getCursor();
	}

	/**
	 * Draw the time line with numbers and ticks
	 * 
	 * @param gc
	 *            GraphicsContext to draw with
	 */
	private void drawTimeline(GraphicsContext gc) {
		LinearGradient lg = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
				new Stop(1.0, new Color(0.2, 0.2, 0.2, 1)), new Stop(0.0, new Color(0.3, 0.3, 0.3, 1)));
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
		int lastTick = getTimeFromX(getVisibleTrackWidth());

		// Draw out the ticks on the timeline
		for (int time = firstTick; time <= lastTick; time++) {
			if (time % 100 != 0)
				continue;

			if(this.zoom < 0.5 && time % 200 != 0)
				continue;

			double x = getXFromTime(time);
			// If the timestamp is divisible by 10, we will draw a longer tick
			// and display the time.
			if (time % 1000 == 0) {

				if(this.zoom > 0.5 || time % 2000 == 0){
					gc.setTextAlign(TextAlignment.CENTER);
					gc.fillText(Util.formatTimestamp(time), x, 31);
				}
			}
			gc.setStroke(TIMELINE_TICK_COLOR);
			GraphicsUtil.sharpLine(gc, x, time % 500 == 0 ? 32 : 36, x, 40);
		}
	}

	/**
	 * Draw the audio wave form in the background
	 * 
	 * @param gc
	 *            GraphicsContext to draw with
	 */
	private void drawWave(GraphicsContext gc) {
		if (backgroundWaveImages == null)
			return;

		for (int i = 0; i < backgroundWaveImages.size(); i++) {
			double startX = 1024 * zoom * i;
			Image image = backgroundWaveImages.get(i);

			// Skip if image is to the left of the canvas
			if (startX + image.getWidth() * zoom < horizontalScrollbar.getOffset())
				continue;

			// Skip the rest if image is to the right of the canvas
			if (startX > canvas.getWidth() + horizontalScrollbar.getOffset())
				return;

			gc.drawImage(image, startX - horizontalScrollbar.getOffset(), getTotalTrackPositionY(),
					image.getWidth() * zoom, canvas.getHeight() - 40);
		}
	}

	/**
	 * Draw all light tracks
	 * 
	 * @param gc
	 */
	private void drawLightTracks(GraphicsContext gc) {
		int i = 0;
		for (LightTrack track : show.getLightTracks()) {
			gc.setFill(TRACK_COLOR);
			gc.fillRect(0, getTrackPositionY(i), getVisibleTrackWidth(), getTrackHeight());

			gc.setStroke(TRACK_BORDER_COLOR);
			gc.strokeLine(0, getTrackPositionY(i), getVisibleTrackWidth(), getTrackPositionY(i));

			drawTrackPolygon(gc, track, getTrackPositionY(i));
			drawKeyFrames(gc, track, getTrackPositionY(i));
			i++;
		}
	}

	/**
	 * Draw a track polygon at given Y coordinate. This is essentially a filled
	 * area chart under the key frames
	 * 
	 * @param gc
	 *            GraphicsContext to draw with
	 * @param track
	 *            The track
	 * @param startY
	 *            the initial Y coordinate at which to draw the polygon
	 */
	private void drawTrackPolygon(GraphicsContext gc, LightTrack track, double startY) {
		gc.setFill(new Color(0.0, 0.2, 0.7, 0.5));
		gc.beginPath();
		gc.moveTo(getXFromTime(0), startY + getTrackHeight());

		double x = 0.0;

		for (KeyFrame frame : track.getKeyFrames()) {
			x = getXFromTime(frame.getTimestamp());
			double y = startY + getTrackHeight() - getRelativeYFromBrightness(frame.getState().getBrightness());
			gc.lineTo(x, y);
		}

		gc.lineTo(x, startY + getTrackHeight());
		gc.lineTo(0, startY + getTrackHeight());

		gc.fill();
		gc.closePath();
	}

	/**
	 * Draw all key frames to a track
	 * 
	 * @param gc
	 *            GraphicsContext to draw with
	 * @param track
	 *            The track
	 * @param startY
	 *            the initial Y coordinate at which to draw the polygon
	 * 
	 */
	private void drawKeyFrames(GraphicsContext gc, LightTrack track, double startY) {
		Iterator<KeyFrame> iterator = track.getKeyFrames().iterator();
		while (iterator.hasNext()) {
			KeyFrame frame = iterator.next();

			double x = getXFromTime(frame.getTimestamp());
			double y = startY + getTrackHeight() - getRelativeYFromBrightness(frame.getState().getBrightness());
			Color color = frame.getState().getColor().toFxColor();
			boolean selected = false;

			if (selectedKeyFrames.contains(frame))
				selected = true;

			drawKeyFrame(gc, x, y, color, selected);
		}

	}

	/**
	 * Draw a key frame at given coordinate
	 * 
	 * @param gc
	 *            The GraphicsContext to draw with
	 * @param x
	 *            X coordinate of the frame
	 * @param y
	 *            Y coordinate of the frame
	 * @param color
	 *            Color of the frame
	 * @param selected
	 *            Whether the frame is selected
	 */
	private void drawKeyFrame(GraphicsContext gc, double x, double y, Color color, boolean selected) {
		double[] xPoints = new double[] { x - KEY_FRAME_SIZE, x, x + KEY_FRAME_SIZE, x };
		double[] yPoints = new double[] { y, y + KEY_FRAME_SIZE, y, y - KEY_FRAME_SIZE };

		if (selected) {
			gc.setStroke(Color.WHITESMOKE);
			gc.setLineWidth(2.0);
			gc.strokePolygon(xPoints, yPoints, 4);
		}

		gc.setFill(color);
		gc.fillPolygon(xPoints, yPoints, 4);
	}

	/**
	 * Draw the cursor (vertical black line)
	 * 
	 * @param gc
	 *            GraphicsContext to draw with
	 */
	private void drawCursor(GraphicsContext gc) {
		double x = getXFromTime(show.getCursor());
		double y = 20 + KEY_FRAME_SIZE;

		gc.setStroke(Color.BLACK);
		gc.setLineWidth(1);
		GraphicsUtil.sharpLine(gc, x, 20, x, canvas.getHeight());

		gc.setFill(Color.BROWN);
		gc.fillPolygon(new double[] { x, x - KEY_FRAME_SIZE, x + KEY_FRAME_SIZE, x },
				new double[] { y, y - KEY_FRAME_SIZE, y - KEY_FRAME_SIZE, y }, 3);
	}

	/**
	 * Draw the selection rectangle
	 * 
	 * @param gc
	 *            The GraphicsContext to draw with
	 */
	private void drawSelection(GraphicsContext gc) {
		if (selectRectangle != null) {
			gc.beginPath();
			gc.rect(selectRectangle.getX(), selectRectangle.getY(), selectRectangle.getWidth(),
					selectRectangle.getHeight());
			gc.setFill(new Color(0.2, 0.0, 1.0, 0.1));
			gc.setStroke(new Color(0.2, 0.0, 1.0, 0.9));
			gc.setLineWidth(0.5);
			gc.fill();
			gc.stroke();
		}
	}

	/**
	 * Draw scroll bars
	 * 
	 * @param gc
	 *            The GraphicsContext to draw with
	 */
	private void drawScrollbars(GraphicsContext gc) {
		gc.setFill(SCROLLBAR_COLOR);
		gc.fillRoundRect(getVisibleTrackWidth(), getTotalTrackPositionY() + verticalScrollbar.getBarPosition(),
				SCROLLBAR_SIZE, verticalScrollbar.getBarSize(), SCROLLBAR_SIZE, SCROLLBAR_SIZE);
		gc.fillRoundRect(horizontalScrollbar.getBarPosition(), getTotalVisibleTrackHeight() + getTotalTrackPositionY(),
				horizontalScrollbar.getBarSize(), SCROLLBAR_SIZE, SCROLLBAR_SIZE, SCROLLBAR_SIZE);
	}

	/**
	 * Get the {@link LightTrack} at a given y coordinate
	 * 
	 * @param y
	 *            The coordinate to look at
	 * @return The light track at this coordinate. Null if no track is found
	 */
	public LightTrack getTrackFromY(double y) {
		double adjustedY = y - getTotalTrackPositionY() + verticalScrollbar.getOffset();
		int trackNumber = (int) Math.floor(adjustedY / (getTrackHeight() + TRACK_SPACER));
		List<LightTrack> tracks = show.getLightTracks();
		if (trackNumber >= 0 && trackNumber < tracks.size()) {
			return tracks.get(trackNumber);
		}
		return null;
	}

	/**
	 * Get the key frame given track and x, y coordinates
	 * 
	 * @param track
	 * @param x
	 * @param y
	 * @return KeyFrame at given coordinates, null if not found
	 */
	public KeyFrame getKeyFrame(LightTrack track, double x, double y) {
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

	/**
	 * Check whether a given position is inside a given key frame marker
	 * 
	 * @param keyFrame
	 * @param x
	 * @param y
	 * @return true if position (x,y) is inside the specified keyFrame, false if not
	 */
	private boolean isInside(KeyFrame keyFrame, double x, double y) {
		double kX = getXFromTime(keyFrame.getTimestamp());

		if (kX < x - KEY_FRAME_SIZE || kX > x + KEY_FRAME_SIZE) {
			return false;
		}

		double kY = getTrackHeight() - getRelativeYFromBrightness(keyFrame.getState().getBrightness());

		return kY >= y - KEY_FRAME_SIZE && kY <= y + KEY_FRAME_SIZE;
	}

	public double getRelativeTrackY(LightTrack track, double y) {
		int trackNumber = show.getLightTracks().indexOf(track);
		double trackStartY = getTrackPositionY(trackNumber);
		return (y - trackStartY);
	}

	private double getXFromTime(int time) {
		return (time * PIXELS_PER_SECOND * zoom / 1000.0) - horizontalScrollbar.getOffset();
	}

	public int getTimeFromX(double x) {
		return (int) ((x + horizontalScrollbar.getOffset()) / (PIXELS_PER_SECOND * zoom / 1000.0));
	}

	private double getRelativeYFromBrightness(int brightness) {
		return (brightness / 255.0) * getTrackHeight();
	}

	private double getVisibleTrackWidth() {
		return canvas.getWidth() - SCROLLBAR_SIZE;
	}

	private double getTrackWidth() {
		return show.getDuration() / 1000D * PIXELS_PER_SECOND * zoom;
	}

	public double getTrackHeight() {
		return Math.max(MINIMUM_TRACK_HEIGHT,
				getTotalVisibleTrackHeight() / show.getLightTracks().size() - TRACK_SPACER);
	}

	private double getTotalVisibleTrackHeight() {
		return canvas.getHeight() - getTotalTrackPositionY() - SCROLLBAR_SIZE;
	}

	private double getTotalTrackHeight() {
		return (getTrackHeight() + TRACK_SPACER) * show.getLightTracks().size();
	}

	private double getTrackPositionY(int i) {
		return getTotalTrackPositionY() + (getTrackHeight() + TRACK_SPACER) * i - verticalScrollbar.getOffset();
	}

	public double getTrackPositionY(LightTrack track) {
		return getTrackPositionY(show.getLightTracks().indexOf(track));
	}

	private double getTotalTrackPositionY() {
		return 40;
	}

	public double getZoom() {
		return zoom;
	}

	/**
	 * Adjusts zoom level
	 * 
	 * 
	 * @param factor used to adjust zoom level
	 */
	public void adjustZoom(double factor) {
		double offsetWithoutZoom = horizontalScrollbar.getOffset() / zoom;
		zoom = Math.max(MINIMUM_ZOOM, Math.min(MAXIMUM_ZOOM, zoom + factor));

		// TODO Scrollbar+setOffset(double)
		horizontalScrollbar.addOffset(-horizontalScrollbar.getOffset());
		horizontalScrollbar.addOffset(offsetWithoutZoom * zoom);
	}

	public Scrollbar getVerticalScrollbar() {
		return verticalScrollbar;
	}

	public Scrollbar getHorizontalScrollbar() {
		return horizontalScrollbar;
	}

	public List<KeyFrame> getSelectedKeyFrames() {
		return selectedKeyFrames;
	}

	public void newSelectRectangle(double x, double y) {
		selectRectangle = new Rectangle(0, 0);
		selectRectangle.setX(x);
		selectRectangle.setY(y);
	}

	public Rectangle getSelectRectangle() {
		return selectRectangle;
	}

	public void updateSelectRectangle(double x, double y) {
		selectRectangle.setWidth(x - selectRectangle.getX());
		selectRectangle.setHeight(y - selectRectangle.getY());

	}

	public boolean selectRectangleExists() {
		return selectRectangle != null;
	}

	public void resetSelectRectangle() {
		selectRectangle = null;
	}

	/**
	 * Make a selection of key frames using the select rectangle
	 * 
	 * @param ctrlDown
	 *            Whether the user is holding ctrl
	 * @return The amount of tracks within the new selection
	 */
	public int selectKeyFrames(boolean ctrlDown) {

		List<KeyFrame> selection = new ArrayList<KeyFrame>();
		int tracksInSelection = 0;

		if (ctrlDown) {
			selection = selectedKeyFrames;
			tracksInSelection = 1; // TODO maybe rewrite this part
		}
		double x1 = selectRectangle.getX();
		double x2 = selectRectangle.getX() + selectRectangle.getWidth();
		double minX = Math.min(x1, x2);
		double maxX = Math.max(x1, x2);

		double y1 = selectRectangle.getY();
		double y2 = selectRectangle.getY() + selectRectangle.getHeight();
		double minY = Math.min(y1, y2);
		double maxY = Math.max(y1, y2);

		int i = -1;

		for (LightTrack track : show.getLightTracks()) {
			i++;
			boolean trackInSelection = false;

			if (getTrackPositionY(i) < minY - getTrackHeight())
				continue;

			if (getTrackPositionY(i) > maxY)
				break;

			double trackMinY = minY - getTrackPositionY(i);
			double trackMaxY = maxY - getTrackPositionY(i);

			for (KeyFrame keyFrame : track.getKeyFrames()) {
				double localFrameY = getTrackHeight() - getRelativeYFromBrightness(keyFrame.getState().getBrightness());
				double frameX = getXFromTime(keyFrame.getTimestamp());

				if (frameX > minX && frameX < maxX) {
					if (localFrameY > trackMinY && localFrameY < trackMaxY) {
						selection.add(keyFrame);
						trackInSelection = true;
					}
				}
			}

			if (trackInSelection)
				tracksInSelection++;

		}

		selectedKeyFrames = selection;

		return tracksInSelection;
	}

	public TrackSection getSection(MouseEvent event) {
		return getSection(event.getX(), event.getY());
	}

	private TrackSection getSection(double x, double y) {
		if (x > getVisibleTrackWidth() && y >= getTotalTrackPositionY()) {
			return TrackSection.VERTICAL_SCROLLBAR;
		} else if (y >= getTotalVisibleTrackHeight() + getTotalTrackPositionY()) {
			return TrackSection.HORIZONTAL_SCROLLBAR;
		} else if (y <= 20) {
			return TrackSection.CURSOR;
		} else if (y <= 40) {
			return TrackSection.TIMELINE;
		} else {
			return TrackSection.TRACKS;
		}
	}

	/**
	 * Checks if auto scroll should be performed
	 * 
	 * 
	 * @return
	 */
	
	private boolean shouldAutoScroll() {
		double x = getXFromTime(show.getCursor());
		return useDesiredCursor && x > 0 && x < getVisibleTrackWidth();
	}

	public void setUseDesiredCursor(boolean enabled) {
		this.useDesiredCursor = enabled;
	}

	public void setDesiredCursorPosition(double x) {
		if (x >= 0) {
			this.desiredCursorPosition = x;
		}
	}

	public void updateDesiredCursorPosition() {
		setDesiredCursorPosition(getXFromTime(show.getCursor()));
	}

}
