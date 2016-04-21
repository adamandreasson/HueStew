/**
 * 
 */
package com.huestew.studio.util;

import javafx.scene.canvas.GraphicsContext;

/**
 * @author Adam
 *
 */
public class GraphicsUtil {

	/**
	 * Fak that's nice.	
	 * @param gc
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public static void sharpLine(GraphicsContext gc, double x1, double y1, double x2, double y2){
		gc.strokeLine(Math.round(x1)+0.5, Math.round(y1)+0.5, Math.round(x2)+0.5, Math.round(y2)+0.5);
	}
	
}
