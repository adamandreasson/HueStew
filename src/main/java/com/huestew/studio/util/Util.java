package com.huestew.studio.util;

/**
 * Various utilities.
 */
public class Util {

	/**
	 * Add starting zero to time
	 * 
	 * @param i
	 *            The number to be formatted
	 * @return a String with a zero in front if i < 10
	 */
	private static String formatTime(int i) {
		if (i >= 10)
			return String.valueOf(i);
		else
			return "0" + i;
	}

	/**
	 * Create a nice timestamp string from given number of milliseconds
	 * 
	 * @param i
	 *            Timestamp in milliseconds
	 * @return A nice string
	 */
	public static String formatTimestamp(int i) {
		int minutes = Math.floorDiv(i, 60 * 1000);
		i -= minutes * 60 * 1000;
		int seconds = Math.floorDiv(i, 1000);
		i -= seconds * 1000;
		return formatTime(minutes) + ":" + formatTime(seconds);
	}
}
