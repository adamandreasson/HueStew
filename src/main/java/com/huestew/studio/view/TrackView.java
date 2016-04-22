package com.huestew.studio.view;

import java.util.Iterator;
import java.util.TreeSet;

import com.huestew.studio.HueStew;
import com.huestew.studio.Toolbox;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightTrack;
import com.huestew.studio.util.GraphicsUtil;

import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * @author Adam
 *
 */
public class TrackView {
	private static final int KEY_FRAME_SIZE = 5;

	private Canvas canvas;

	/**
	 * Create a new track view with an associated canvas
	 * 
	 * @param canvas
	 *            the canvas in which the track view will be drawn.
	 */
	public TrackView(Canvas canvas) {
		this.canvas = canvas;

		canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			System.out.println("TrackView: MouseClicked, x=" + event.getX() + ", y=" + event.getY());
			// Get light track and timestamp from mouse coordinates
			LightTrack track = getTrackFromY(event.getY());
			if (track == null) return;
			
			// Get normalized y coordinate
			double inverseTrackY = getTrackHeight() - getRelativeTrackY(track, event.getY());
			double normalizedY = inverseTrackY / getTrackHeight();
			System.out.println(normalizedY);
			
			// Get clicked key frame
			KeyFrame keyFrame = getKeyFrame(track, event.getX(), getRelativeTrackY(track, event.getY()));
			
			// Pass event to current tool
			Toolbox.getTool().doAction(event, track, keyFrame, getTimeFromX(event.getX()) , normalizedY);

			redraw();
		});

		canvas.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
			// System.out.println("TrackView: MouseMoved, x=" + event.getX() +
			// ", y=" + event.getY());
			// Get light track and timestamp from mouse coordinates
			LightTrack track = getTrackFromY(event.getY());

			if (track == null) {
				canvas.setCursor(Cursor.OPEN_HAND);
			} else {
				canvas.setCursor(Toolbox.getCursor());
			}
		});
	}

	public void redraw() {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.LIGHTGRAY);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

		drawTimeline(gc);
		drawLightTracks(gc);
	}

	private void drawTimeline(GraphicsContext gc) {

		gc.setFill(Color.WHITESMOKE);
		gc.fillRect(0, 0, canvas.getWidth(), 20);

		gc.setFill(Color.BLACK);

		// Draw out 60 ticks on the timeline
		for (int i = 0; i < 60; i++) {

			int time = i * 1000;
			// If the timestamp is divisible by 10, we will draw a longer tick
			// and display the time.
			if (i % 10 == 0) {
				gc.fillText("" + i, getXFromTime(time), 14);
			}
			GraphicsUtil.sharpLine(gc, getXFromTime(time), i % 5 == 0 ? 12 : 16, getXFromTime(time), 20);
		}
	}

	private LightTrack getTrackFromY(double y) {
		double adjustedY = y - getTotalTrackPositionY();
		int trackNumber = (int) Math.floor(adjustedY / getTrackHeight());
		if (trackNumber >= 0 && trackNumber < HueStew.getInstance().getShow().getLightTracks().size()) {
			return HueStew.getInstance().getShow().getLightTracks().get(trackNumber);
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
		
		System.out.println("kY: " + kY + ", y: " + y);
		
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
		return (int) (relativeX * 60000.0);
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

	private double getRelativeYFromBrightness(int brightness) {
		return (brightness / 255.0) * getTrackHeight();
	}

	private void drawKeyFrame(GraphicsContext gc, double x, double y) {
		gc.setFill(Color.YELLOW);
		gc.fillPolygon(new double[] { x - KEY_FRAME_SIZE, x, x + KEY_FRAME_SIZE, x }, new double[] { y, y + KEY_FRAME_SIZE, y, y - KEY_FRAME_SIZE }, 4);
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
		// TODO Auto-generated method stub
		return 20;
	}

}
