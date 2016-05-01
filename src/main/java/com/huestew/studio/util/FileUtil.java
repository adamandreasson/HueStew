package com.huestew.studio.util;

import java.io.File;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;

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

		while (mediaReader.readPacket() == null);
	}
}
