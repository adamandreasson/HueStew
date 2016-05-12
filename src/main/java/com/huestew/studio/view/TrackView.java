package com.huestew.studio.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import com.huestew.studio.HueStew;
import com.huestew.studio.Toolbox;
import com.huestew.studio.controller.ColorPickerController;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightTrack;
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

/**
 * @author Adam
 *
 */
public class TrackView {
	private static final int KEY_FRAME_SIZE = 5;
	public static final int PIXELS_PER_SECOND = 100;

	private Canvas canvas;
	private List<Image> backgroundWaveImages;
	
	private double offsetX = 0;
	private double scrollOriginX = -1;

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
			isMouseDown = false;
			
			if (event.getButton() == MouseButton.SECONDARY && hoveringKeyFrame != null) {

				ColorPickerController cpc = (ColorPickerController) Util.loadFxml("/com/huestew/studio/colorpicker.fxml");

				cpc.setKeyFrame(hoveringKeyFrame);

				HueStew.getInstance().getMainViewController().setColorPickerPane(cpc.getView());
			}

			if (scrollOriginX != -1) {
				scrollOriginX = -1;
			} else {
				sendMouseEventToTool(event);
			}
		});
		canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
			isMouseDown = true;
			
			if (event.getButton() == MouseButton.PRIMARY && event.getY() >= 20 
					&& getTrackFromY(event.getY()) == null) {
				// Scroll
				scrollOriginX = event.getX();
			} else {
				sendMouseEventToTool(event);
			}
		});
		canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
			if (scrollOriginX != -1) {
				double offset = offsetX + scrollOriginX - event.getX();
				double maxOffset = getXFromTime(HueStew.getInstance().getShow().getDuration()) + offsetX - canvas.getWidth();
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

		// Register key event handlers (after the scene has been created)
		Platform.runLater(new Runnable() {
			public void run() {
				canvas.getScene().addEventHandler(KeyEvent.KEY_PRESSED, event -> Toolbox.getTool().doAction(event));
				canvas.getScene().addEventHandler(KeyEvent.KEY_RELEASED, event -> {
					keyDownEvent(event);
					Toolbox.getTool().doAction(event);
				});
			}
		});
	}

	public void loadWaves(List<String> filePaths, int totalWidth) {

		this.backgroundWaveImages = new ArrayList<Image>();
		
		try {
			for(String path : filePaths){
				System.out.println(path);
				this.backgroundWaveImages.add(new Image(path));
			}
			System.out.println("setting canvas wirdth to " + totalWidth);
			//canvas.setWidth(totalWidth);
			redraw();

		} catch (IllegalArgumentException e) {
			System.out.println("wave not generated yet probably?");
		}
	}

	private void keyDownEvent(KeyEvent event) {

		switch (event.getCode()) {
		case SPACE:
			HueStew.getInstance().getPlayer().toggle();
			break;
		default:
			break;
		}

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
		// Get normalized y coordinate
		double inverseTrackY = getTrackHeight() - getRelativeTrackY(track, event.getY());
		double normalizedY = inverseTrackY / getTrackHeight();

		// Pass event to current tool
		Toolbox.getTool().doAction(event, track, hoveringKeyFrame, getTimeFromX(event.getX()), normalizedY);

		// Redraw canvas
		redraw();
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
		if (HueStew.getInstance().getShow().getAudio() == null || backgroundWaveImages == null) {
			System.out.println("Missing audio or waveform image");
			return;
		}

		if (!canvas.isVisible()) {
			canvas.setVisible(true);
		}

		// Perform drawing on javafx main thread
		Platform.runLater(new Runnable() {
			public void run() {

				GraphicsContext gc = canvas.getGraphicsContext2D();
				gc.setFill(Color.LIGHTGRAY);
				gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

				drawTimeline(gc);
				drawLightTracks(gc);
				drawCursor(gc);
			}
		});
	}

	private void drawTimeline(GraphicsContext gc) {
		gc.setFill(Color.WHITESMOKE);
		gc.fillRect(0, 0, canvas.getWidth(), 40);

		drawWave(gc);

		gc.setStroke(Color.GRAY);
		gc.setLineWidth(1);
		gc.strokeLine(0, 20.5, canvas.getWidth(), 20.5);

		gc.setFill(Color.BLACK);

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
			drawTrackPolygon(gc, track, getTrackPositionY(i));
			drawKeyFrames(gc, track, getTrackPositionY(i));

			gc.setStroke(Color.GRAY);
			gc.setLineWidth(1);
			gc.strokeLine(0, getTrackPositionY(i), canvas.getWidth(), getTrackPositionY(i));
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
			drawKeyFrame(gc, getXFromTime(frame.getTimestamp()),
					startY + getTrackHeight() - getRelativeYFromBrightness(frame.getState().getBrightness()));
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
