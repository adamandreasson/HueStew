package com.huestew.studio;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * A class for graphical representation of virtual bulbs. 
 * @author Adam
 *
 */
public class VirtualRoom {

	/** the canvas used to draw the virtual room in.**/
	private Canvas canvas;
	
	/** list of all virtual bulbs in the virtual room **/
	private List<VirtualBulb> bulbs;

	/**
	 * Create a new virtual room with an associated canvas
	 * @param canvas
	 * 				the canvas in which the virtual room will be drawn.
	 */
	public VirtualRoom(Canvas canvas) {
		this.canvas = canvas;
		bulbs = new ArrayList<VirtualBulb>();
	}
	
	/**
	 * Adds a bulb to the list
	 * @param bulb
	 * 				the buld which is to be added to the list
	 */
	public void addBulb(VirtualBulb bulb) {
		bulbs.add(bulb);
	}

	/**
	 * Sets a new canvas for the virtual room to be drawn in.
	 * @param canvas
	 * 				the new canvas.
	 */				
	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}

	/**
	 * Redraws the entire virtual room including the bulbs.
	 */
	public void redraw() {

		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		gc.setFill(Color.RED);
		gc.fillOval(canvas.getWidth() / 2 - 15, canvas.getHeight() / 2 - 15, 30, 30);

		for (VirtualBulb bulb : bulbs) {
			gc.setFill(new Color(bulb.getColor().getRed(), bulb.getColor().getGreen(), bulb.getColor().getBlue(), 1));
			gc.fillOval(canvas.getWidth() / 2 - 15, canvas.getHeight() / 2 - 15, 30, 30);
		}

	}

}
