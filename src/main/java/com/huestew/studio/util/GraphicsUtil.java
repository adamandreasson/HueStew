/**
 * 
 */
package com.huestew.studio.util;

import javafx.scene.canvas.GraphicsContext;

/**
 * Utility for graphics
 * 
 * @author Adam
 *
 */
public class GraphicsUtil {

	/**
	 * Draw a sharp line, that is, draw it between two pixels. Thank you, based
	 * JavaFX
	 * 
	 * @param gc
	 *            GraphicsContext to draw with
	 * @param x1
	 *            Start x
	 * @param y1
	 *            Start y
	 * @param x2
	 *            End x
	 * @param y2
	 *            End y
	 */
	public static void sharpLine(GraphicsContext gc, double x1, double y1, double x2, double y2) {
		gc.strokeLine(Math.round(x1) + 0.5, Math.round(y1) + 0.5, Math.round(x2) + 0.5, Math.round(y2) + 0.5);
	}

}
