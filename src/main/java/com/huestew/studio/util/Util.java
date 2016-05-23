package com.huestew.studio.util;

/**
 * Various utilities.
 */
public class Util {
	
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
