/**
 * 
 */
package com.huestew.studio;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * @author Adam
 *
 */
public class TrackView {

	private Canvas canvas;

	/**
	 * Create a new track view with an associated canvas
	 * @param canvas
	 * 				the canvas in which the track view will be drawn.
	 */
	public TrackView(Canvas canvas) {
		this.canvas = canvas;
	}
	
	public void redraw(){
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

		gc.setFill(Color.YELLOW);
        gc.fillText("Track canvas", 30, 30);
	}
	
}
