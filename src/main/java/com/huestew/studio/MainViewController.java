package com.huestew.studio;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;

/**
 * Controller class for the Main JavaFX view
 * 
 * @author Adam Andreasson
 */
public class MainViewController extends ViewController{


    @FXML
    private Canvas previewCanvas;

    @FXML
    private AnchorPane previewCanvasPane;

	@Override
	protected void init(){
		HueStew.getInstance().setVirtualRoom(new VirtualRoom(previewCanvas));
		HueStew.getInstance().getVirtualRoom().redraw();
		
		previewCanvasPane.widthProperty().addListener((val, newVal, oldVal) -> {
			previewCanvas.setWidth((double) newVal);
			HueStew.getInstance().getVirtualRoom().redraw();
		});
		
		previewCanvasPane.heightProperty().addListener((val, newVal, oldVal) -> {
			previewCanvas.setHeight((double) newVal);
			HueStew.getInstance().getVirtualRoom().redraw();
		});
	
	}
	

}
