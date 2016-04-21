package com.huestew.studio.view;

import java.util.Iterator;

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
			double inverseTrackY = getTrackHeight() - getRelativeTrackY(track, event.getY());
			double normalizedY = inverseTrackY / getTrackHeight();
			System.out.println(normalizedY);

			if (track != null) {
				// Pass event to current tool
				Toolbox.getTool().doAction(event, track, getTimeFromX(event.getX()), normalizedY);

				redraw();
			}
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
		Iterator<KeyFrame> iterator = track.getKeyFrames();
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
		gc.fillPolygon(new double[] { x - 5, x, x + 5, x }, new double[] { y, y + 5, y, y - 5 }, 4);
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
