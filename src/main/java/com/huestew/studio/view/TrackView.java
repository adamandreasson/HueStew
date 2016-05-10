package com.huestew.studio.view;

import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import com.huestew.studio.HueStew;
import com.huestew.studio.Toolbox;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightTrack;
import com.huestew.studio.util.GraphicsUtil;

import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * @author Adam
 *
 */
public class TrackView {
	private static final int KEY_FRAME_SIZE = 5;

	private Canvas canvas;
	
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

		// Register mouse event handlers
		canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> sendMouseEventToTool(event));
		canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> sendMouseEventToTool(event));
		canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> sendMouseEventToTool(event));

		canvas.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
			// Get light track from mouse coordinates
			LightTrack track = getTrackFromY(event.getY());

			// Set cursor
			if (track == null) {
				if(event.getY() > 20)
				canvas.setCursor(Cursor.OPEN_HAND);
				else
					canvas.setCursor(Cursor.E_RESIZE);
			} else {
				updateHoveringKeyFrame(track, event);
				canvas.setCursor(Toolbox.getCursor(hoveringKeyFrame != null, isMouseDown));
			}
			
		});
		
		// Register key event handlers (after the scene has been created)
		Platform.runLater(new Runnable() {
			public void run() {
				canvas.getScene().addEventHandler(KeyEvent.KEY_PRESSED, event -> Toolbox.getTool().doAction(event));
				canvas.getScene().addEventHandler(KeyEvent.KEY_RELEASED, event -> Toolbox.getTool().doAction(event));
			}
		});
	}

	private void updateHoveringKeyFrame(LightTrack track, MouseEvent event) {
		this.hoveringKeyFrame = getKeyFrame(track, event.getX(), getRelativeTrackY(track, event.getY()));
	}

	private void sendMouseEventToTool(MouseEvent event) {
		// Get light track and timestamp from mouse coordinates
		LightTrack track = getTrackFromY(event.getY());
		if (track == null){
			parseTrackEvent(event);
			return;
		}
		// Get normalized y coordinate
		double inverseTrackY = getTrackHeight() - getRelativeTrackY(track, event.getY());
		double normalizedY = inverseTrackY / getTrackHeight();

		// Get clicked key frame
		KeyFrame keyFrame = getKeyFrame(track, event.getX(), getRelativeTrackY(track, event.getY()));

		// Pass event to current tool
		Toolbox.getTool().doAction(event, track, keyFrame, getTimeFromX(event.getX()), normalizedY);

		// Redraw canvas
		redraw();
	}

	private void parseTrackEvent(MouseEvent event) {
		
		// Seeking event
		if(event.getY() < 20){
			int time = getTimeFromX(event.getX());
			HueStew.getInstance().getPlayer().seek(time);
			redraw();
		}
	}

	public void redraw() {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.LIGHTGRAY);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

		drawTimeline(gc);
		drawLightTracks(gc);
		drawCursor(gc);
	}

	private void drawTimeline(GraphicsContext gc) {

		gc.setFill(Color.WHITESMOKE);
		gc.fillRect(0, 0, canvas.getWidth(), 40);
		
		gc.setStroke(Color.GRAY);
		gc.setLineWidth(1);
		gc.strokeLine(0, 20.5, canvas.getWidth(), 20.5);

		gc.setFill(Color.BLACK);

		// Draw out 60 ticks on the timeline
		for (int i = 0; i < 60; i++) {

			int time = i * 1000;
			// If the timestamp is divisible by 10, we will draw a longer tick
			// and display the time.
			if (i % 10 == 0) {
				gc.fillText("" + i, getXFromTime(time), 34);
			}
			GraphicsUtil.sharpLine(gc, getXFromTime(time), i % 5 == 0 ? 32 : 36, getXFromTime(time), 40);
		}
	}

	private void drawLightTracks(GraphicsContext gc) {
		int i = 0;
		for (LightTrack track : HueStew.getInstance().getShow().getLightTracks()) {
			drawKeyFrames(gc, track, getTrackPositionY(i));

			gc.setStroke(Color.GRAY);
			gc.setLineWidth(1);
			gc.strokeLine(0, getTrackPositionY(i), canvas.getWidth(), getTrackPositionY(i));
			i++;
		}
	}

	private void drawKeyFrames(GraphicsContext gc, LightTrack track, double startY) {
		Iterator<KeyFrame> iterator = track.getKeyFrames().iterator();
		while (iterator.hasNext()) {
			KeyFrame frame = iterator.next();
			drawKeyFrame(gc, getXFromTime(frame.getTimestamp()),
					startY + getTrackHeight() - getRelativeYFromBrightness(frame.getState().getBrightness()));
		}
	}

	private void drawCursor(GraphicsContext gc) {
		double x = getXFromTime(HueStew.getInstance().getCursor());
		double y = 20+KEY_FRAME_SIZE;

		gc.setStroke(Color.BLACK);
		gc.setLineWidth(1);
		GraphicsUtil.sharpLine(gc, x, 20, x, canvas.getHeight());
		
		gc.setFill(Color.BROWN);
		gc.fillPolygon(new double[] { x, x - KEY_FRAME_SIZE, x + KEY_FRAME_SIZE, x },
				new double[] { y, y - KEY_FRAME_SIZE, y - KEY_FRAME_SIZE, y }, 3);
	}

	private void drawKeyFrame(GraphicsContext gc, double x, double y) {
		gc.setFill(Color.YELLOW);
		gc.fillPolygon(new double[] { x - KEY_FRAME_SIZE, x, x + KEY_FRAME_SIZE, x },
				new double[] { y, y + KEY_FRAME_SIZE, y, y - KEY_FRAME_SIZE }, 4);
	}

	private LightTrack getTrackFromY(double y) {
		double adjustedY = y - getTotalTrackPositionY();
		int trackNumber = (int) Math.floor(adjustedY / getTrackHeight());
		List<LightTrack> tracks = HueStew.getInstance().getShow().getLightTracks();
		if (trackNumber >= 0 && trackNumber < tracks.size()) {
			return tracks.get(trackNumber);
		}
		return null;
	}

	private KeyFrame getKeyFrame(LightTrack track, double x, double y) {
		TreeSet<KeyFrame> keyFrames = track.getKeyFrames();
		KeyFrame target = new KeyFrame(getTimeFromX(x), null);

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

	private double getXFromTime(int i) {
		return (i * canvas.getWidth()) / 60000;
	}

	private int getTimeFromX(double x) {
		double relativeX = x / canvas.getWidth();
		return Math.max(0, (int) (relativeX * 60000.0));
	}

	private double getRelativeYFromBrightness(int brightness) {
		return (brightness / 255.0) * getTrackHeight();
	}

	private double getTrackHeight() {
		return getTotalTrackHeight() / HueStew.getInstance().getShow().getLightTracks().size();
	}

	private double getTotalTrackHeight() {
		return canvas.getHeight() - getTotalTrackPositionY();
	}

	private double getTrackPositionY(int i) {
		return getTotalTrackPositionY() + getTrackHeight() * i;
	}

	private double getTotalTrackPositionY() {
		return 40;
	}

}
