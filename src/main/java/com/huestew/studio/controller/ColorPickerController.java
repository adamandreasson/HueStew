/**
 * 
 */
package com.huestew.studio.controller;

import java.util.List;

import com.huestew.studio.model.Color;
import com.huestew.studio.model.KeyFrame;

import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;

/**
 * @author Adam
 *
 */
public class ColorPickerController {

	private double imgScale;

	private Image img;
	private AnchorPane colorPickerPane;
	private List<KeyFrame> selectedKeyFrames;
	private Canvas colorWheelCanvas;

	public ColorPickerController(AnchorPane colorPickerPane) {

		this.colorPickerPane = colorPickerPane;

		colorPickerPane.getChildren().clear();

		img = new Image("color_circle.png");
		colorWheelCanvas = new Canvas();

		redraw();
		colorWheelCanvas.setCursor(Cursor.CROSSHAIR);

		colorWheelCanvas.setOnMouseClicked(event -> {
			int imgX = (int) (event.getX() * imgScale);
			int imgY = (int) (event.getY() * imgScale);
			Color c = new Color(img.getPixelReader().getColor(imgX, imgY));
			System.out.println(c);

			pickColor(c);

		});

		colorPickerPane.getChildren().add(colorWheelCanvas);
	}

	private void pickColor(Color c) {
		if (selectedKeyFrames == null || selectedKeyFrames.isEmpty())
			return;

		for (KeyFrame frame : selectedKeyFrames) {
			frame.getState().setColor(c);
		}
	}

	public void redraw() {
		Platform.runLater(() -> {
			double maxDimension = colorPickerPane.getWidth();
			if (colorPickerPane.getHeight() < colorPickerPane.getWidth())
				maxDimension = colorPickerPane.getHeight();

			maxDimension -= 20;
			imgScale = img.getWidth() / maxDimension;
			colorWheelCanvas.setWidth(maxDimension);
			colorWheelCanvas.setHeight(maxDimension);
			GraphicsContext gc = colorWheelCanvas.getGraphicsContext2D();
			gc.clearRect(0, 0, colorWheelCanvas.getWidth(), colorWheelCanvas.getHeight());
			gc.drawImage(img, 0, 0, colorWheelCanvas.getWidth(), colorWheelCanvas.getHeight());
		});
	}

	public void setFrames(List<KeyFrame> selectedKeyFrames) {
		this.selectedKeyFrames = selectedKeyFrames;
		redraw();
	}

}
