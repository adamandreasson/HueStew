package com.huestew.studio;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class VirtualRoom {

	private Canvas canvas;
	private List<VirtualBulb> bulbs;

	public VirtualRoom(Canvas canvas) {
		this.canvas = canvas;
		bulbs = new ArrayList<VirtualBulb>();
	}

	public void addBulb(VirtualBulb bulb) {
		bulbs.add(bulb);
	}

	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}

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
