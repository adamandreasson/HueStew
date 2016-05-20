package com.huestew.studio;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.huestew.studio.model.Audio;

import javafx.scene.media.Media;

public class AudioTest {
	@ClassRule
	public static TemporaryFolder folder = new TemporaryFolder();
	private static File file;
	private Audio audio;

	@BeforeClass
	public static void beforeClass() {
		try {
			file = folder.newFile("dummy.mp3");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Could not create temporary test file");
		}
	}

	@Before
	public void before() {
		audio = new Audio(file);
	}

	@Test
	public void constructor() {
		File nonexistent = new File(folder.getRoot(), "hi.mp3");

		try {
			audio = new Audio(nonexistent);
			fail();
		} catch (IllegalArgumentException e) {}
	}

	@Test
	public void getFxMedia() {
		Media media = audio.getFxMedia();
		assertFalse(media == null);
	}

	@Test
	public void getFile() {
		assertTrue(audio.getFile() == file);
	}
}
