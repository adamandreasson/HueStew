package com.huestew.studio;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Controller class for the Main JavaFX view
 * 
 * @author Adam Andreasson
 */
public class MainViewController extends ViewController{

	@FXML
	private Canvas previewCanvas;

	@Override
	protected void init(){
		
		GraphicsContext gc = previewCanvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, previewCanvas.getWidth(), previewCanvas.getHeight());
        gc.setFill(Color.RED);
        gc.fillOval(10, 60, 30, 30);
	}

}
