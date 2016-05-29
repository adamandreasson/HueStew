package com.huestew.studio.util;

import java.io.File;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;

/**
 * Utility class for files
 * @author Adam
 *
 */
public class FileUtil {
	/**
	 * Convert an audio file from one format to another.
	 * 
	 * @param input
	 *            The path to the original audio file
	 * @param output
	 *            The path to the new audio file that should be created
	 */
	public static void convertAudioFile(String input, String output) {
		if (!new File(input).exists()) {
			throw new IllegalArgumentException("Input file does not exist");
		}

		IMediaReader mediaReader = ToolFactory.makeReader(input);
		IMediaWriter mediaWriter = ToolFactory.makeWriter(output, mediaReader);
		mediaReader.addListener(mediaWriter);

		while (mediaReader.readPacket() == null)
			;
	}

	/**
	 * Check whether a file is a valid music file (mp3)
	 * 
	 * @param file
	 *            The file of interest
	 * @return Does the file has the mp3 extension
	 */
	public static boolean isMusicFile(File file) {
		String extension = "";
		String fileName = file.getAbsolutePath();

		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			extension = fileName.substring(i + 1);

			if (extension.equalsIgnoreCase("mp3"))
				return true;
		}

		return false;
	}

	/**
	 * Remove the file extension from a file path
	 * 
	 * @param path
	 *            The file path
	 * @return The file path without file extension
	 */
	public static String removeExtension(String path) {
		return path.substring(0, path.lastIndexOf('.'));
	}
}
