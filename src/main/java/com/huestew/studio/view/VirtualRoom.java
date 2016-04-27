package com.huestew.studio.view;

import java.util.ArrayList;
import java.util.List;

import com.huestew.studio.model.VirtualBulb;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

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
	 */
	public VirtualRoom() {
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

		if(canvas == null)
			return;
		
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

		for (VirtualBulb bulb : bulbs) {
		    gc.setFill(new RadialGradient(0, 0, 0.5, 0.5, 0.5, true,
		               CycleMethod.REFLECT,
		               new Stop(0.1, new Color(bulb.getColor().getRed()/255D, bulb.getColor().getGreen()/255D, bulb.getColor().getBlue()/255D, 1)),
		               new Stop(1.0, Color.BLACK)));
			gc.fillOval(bulb.getX()*canvas.getWidth()+60, bulb.getY()*canvas.getHeight()-30, 60, 60);
		}

	}

}
