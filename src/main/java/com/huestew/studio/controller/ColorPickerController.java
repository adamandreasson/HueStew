/**
 * 
 */
package com.huestew.studio.controller;

import java.util.List;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightState;
import com.huestew.studio.model.SnapshotManager;

import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

/**
 * @author Adam
 *
 */
public class ColorPickerController {

	private static final int KEY_FRAME_SIZE = 5;

	private AnchorPane colorPickerPane;
	private List<KeyFrame> selectedKeyFrames;
	private Canvas colorWheelCanvas;
	private TrackViewController trackViewController;

	public ColorPickerController(AnchorPane colorPickerPane) {

		this.colorPickerPane = colorPickerPane;

		colorPickerPane.getChildren().clear();

		colorWheelCanvas = new Canvas();
		colorWheelCanvas.setCursor(Cursor.CROSSHAIR);

		// Register mouse event handlers
		colorWheelCanvas.setOnMouseDragged(event -> pickColor(event.getX(), event.getY()));
		colorWheelCanvas.setOnMousePressed(event -> {
			// Take snapshot before changing color
			SnapshotManager.getInstance().commandIssued();
			pickColor(event.getX(), event.getY());
		});

		colorPickerPane.getChildren().add(colorWheelCanvas);
		updateSize();
	}

	private Color getColorFromX(double x) {
		double hue = ((x - 10) / getColorBoxWidth()) * 360.0;
		return Color.hsb(hue, 1, 1);
	}

	private double getSaturationFromY(double y) {
		return ((getColorBoxHeight() - y) + 10) / getColorBoxHeight();
	}

	private void pickColor(double x, double y) {
		if (selectedKeyFrames == null || selectedKeyFrames.isEmpty())
			return;

		// Calculate color from x coordinate
		Color c = getColorFromX(x);

		// Calculate saturation from y coordinate
		int saturation = (int) (getSaturationFromY(y) * 255.0);
		if (saturation < 0)
			saturation = 0;
		if (saturation > 255)
			saturation = 255;

		// Update light state of all selected key frames
		for (KeyFrame frame : selectedKeyFrames) {
			frame.setState(new LightState(new com.huestew.studio.model.Color(c), frame.getState().getBrightness(),
					saturation));
		}

		// Redraw color picker
		redraw();

		// Redraw track view
		if (trackViewController != null)
			trackViewController.redraw();
	}

	public void updateSize() {
		// Fit color picker to pane and redraw
		colorWheelCanvas.setWidth(colorPickerPane.getWidth());
		colorWheelCanvas.setHeight(colorPickerPane.getHeight());
		redraw();
	}

	public void redraw() {
		Platform.runLater(() -> {
			GraphicsContext gc = colorWheelCanvas.getGraphicsContext2D();

			// Clear canvas
			gc.clearRect(0, 0, colorWheelCanvas.getWidth(), colorWheelCanvas.getHeight());

			// Draw color picker gradient
			gc.setFill(new LinearGradient(0, 0, 1.0, 0, true, CycleMethod.REFLECT, new Stop(0.0, Color.RED),
					new Stop(1.0 / 6.0, Color.YELLOW), new Stop(2.0 / 6.0, Color.LIME), new Stop(3.0 / 6.0, Color.AQUA),
					new Stop(4.0 / 6.0, Color.BLUE), new Stop(5.0 / 6.0, Color.MAGENTA), new Stop(1.0, Color.RED)));
			gc.fillRect(10, 10, getColorBoxWidth(), getColorBoxHeight());

			gc.setFill(new LinearGradient(0, 0, 0, 1.0, true, CycleMethod.REFLECT, new Stop(0.0, Color.TRANSPARENT),
					new Stop(1.0, Color.WHITE)));
			gc.fillRect(10, 10, getColorBoxWidth(), getColorBoxHeight());

			if (selectedKeyFrames == null)
				return;

			// Draw selected key frames onto color picker
			for (KeyFrame frame : selectedKeyFrames) {
				drawColorPoint(gc, frame);
				gc.setFill(frame.getState().getColor().toFxColor());
				gc.fillRect(10, colorWheelCanvas.getHeight() - 40, colorWheelCanvas.getWidth() - 20, 30);
			}
		});
	}

	private void drawColorPoint(GraphicsContext gc, KeyFrame frame) {
		// Calculate x and y coordinate from light state
		Color c = frame.getState().getColor().toFxColor();
		double saturation = 1.0 - (frame.getState().getSaturation() / 255.0);
		double degree = c.getHue();
		double x = 10 + ((degree / 360.0) * getColorBoxWidth());
		double y = 10 + (saturation * (getColorBoxHeight()));

		// Draw key frame
		gc.setStroke(Color.GRAY);
		gc.setFill(Color.WHITE);
		gc.strokePolygon(new double[] { x - KEY_FRAME_SIZE, x, x + KEY_FRAME_SIZE, x },
				new double[] { y, y + KEY_FRAME_SIZE, y, y - KEY_FRAME_SIZE }, 4);
		gc.fillPolygon(new double[] { x - KEY_FRAME_SIZE, x, x + KEY_FRAME_SIZE, x },
				new double[] { y, y + KEY_FRAME_SIZE, y, y - KEY_FRAME_SIZE }, 4);
	}

	private double getColorBoxWidth() {
		return colorWheelCanvas.getWidth() - 20;
	}

	private double getColorBoxHeight() {
		return colorWheelCanvas.getHeight() - 60;
	}

	public void setFrames(List<KeyFrame> selectedKeyFrames) {
		// Update selected key frames and redraw color picker
		this.selectedKeyFrames = selectedKeyFrames;
		redraw();
	}

	public void setTrackViewController(TrackViewController controller) {
		this.trackViewController = controller;
	}

}
