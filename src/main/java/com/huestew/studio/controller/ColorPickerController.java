/**
 * 
 */
package com.huestew.studio.controller;

import java.util.List;
import java.util.Random;

import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightState;

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

	public ColorPickerController(AnchorPane colorPickerPane) {

		this.colorPickerPane = colorPickerPane;

		colorPickerPane.getChildren().clear();

		colorWheelCanvas = new Canvas();

		redraw();
		colorWheelCanvas.setCursor(Cursor.CROSSHAIR);

		colorWheelCanvas.setOnMouseClicked(event -> {
			Color c = getColorFromX(event.getX());

			pickColor(c);

		});

		colorPickerPane.getChildren().add(colorWheelCanvas);
	}

	private Color getColorFromX(double x) {
		double hue = (x / colorWheelCanvas.getWidth()) * 360.0;
		return Color.hsb(hue, 1, 1);
	}

	private void pickColor(Color c) {
		if (selectedKeyFrames == null || selectedKeyFrames.isEmpty())
			return;

		for (KeyFrame frame : selectedKeyFrames) {
			frame.setState(new LightState(new com.huestew.studio.model.Color(c), frame.getState().getBrightness(),
					frame.getState().getSaturation()));
		}
		redraw();
	}

	public void redraw() {
		Platform.runLater(() -> {
			double maxDimension = colorPickerPane.getWidth();
			if (colorPickerPane.getHeight() < colorPickerPane.getWidth())
				maxDimension = colorPickerPane.getHeight();

			colorWheelCanvas.setWidth(maxDimension);
			colorWheelCanvas.setHeight(maxDimension);
			GraphicsContext gc = colorWheelCanvas.getGraphicsContext2D();
			gc.clearRect(0, 0, colorWheelCanvas.getWidth(), colorWheelCanvas.getHeight());
			// gc.drawImage(img, 0, 0, colorWheelCanvas.getWidth(),
			// colorWheelCanvas.getHeight());
			gc.setFill(new LinearGradient(0, 0, 1.0, 0, true, CycleMethod.REFLECT, new Stop(0.0, Color.RED),
					new Stop(1.0 / 6.0, Color.YELLOW), new Stop(2.0 / 6.0, Color.LIME), new Stop(3.0 / 6.0, Color.AQUA),
					new Stop(4.0 / 6.0, Color.BLUE), new Stop(5.0 / 6.0, Color.MAGENTA), new Stop(1.0, Color.RED)));
			gc.fillRect(0, 0, colorWheelCanvas.getWidth(), colorWheelCanvas.getHeight() / 2);

			if(selectedKeyFrames == null)
				return;
			
			for (KeyFrame frame : selectedKeyFrames) {
				drawColorPoint(gc, frame);
				gc.setFill(frame.getState().getColor().toFxColor());
				gc.fillRect(0, colorWheelCanvas.getHeight() / 2, colorWheelCanvas.getWidth(),
						colorWheelCanvas.getHeight() / 2);
			}
		});
	}

	private void drawColorPoint(GraphicsContext gc, KeyFrame frame) {
		Color c = frame.getState().getColor().toFxColor();
		double degree = c.getHue();
		double x = (degree / 360.0) * colorWheelCanvas.getWidth();

		Random rand = new Random(frame.getTimestamp());
		double y = rand.nextInt((int) colorWheelCanvas.getHeight() / 2);

		gc.setFill(Color.WHITE);
		gc.fillPolygon(new double[] { x - KEY_FRAME_SIZE, x, x + KEY_FRAME_SIZE, x },
				new double[] { y, y + KEY_FRAME_SIZE, y, y - KEY_FRAME_SIZE }, 4);
	}

	public void setFrames(List<KeyFrame> selectedKeyFrames) {
		this.selectedKeyFrames = selectedKeyFrames;
		redraw();
	}

}
