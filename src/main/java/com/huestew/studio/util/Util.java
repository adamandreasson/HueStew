package com.huestew.studio.util;

import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Various utilities.
 */
public class Util {

	public static Stage createStage() {
		Stage stage = new Stage();
		stage.getIcons().add(new Image("/icon_256x256.png"));
		return stage;
	}
	
	private static String formatTime(int i){
		if(i >= 10)
			return String.valueOf(i);
		else
			return "0"+i;
	}

	public static String formatTimestamp(int i) {
		int minutes = Math.floorDiv(i, 60 * 1000);
		i -= minutes * 60 * 1000;
		int seconds = Math.floorDiv(i, 1000);
		i -= seconds * 1000;
		return formatTime(minutes) + ":" + formatTime(seconds);
	}
}
