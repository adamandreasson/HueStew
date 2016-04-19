package com.huestew.studio;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class VirtualRoom {

	private Canvas canvas;

	public VirtualRoom(Canvas canvas){
		this.canvas = canvas;
	}
	
	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}
	
	public void redraw(){

		GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.RED);
        gc.fillOval(canvas.getWidth()/2-15, canvas.getHeight()/2-15, 30, 30);
        
	}
	
}
