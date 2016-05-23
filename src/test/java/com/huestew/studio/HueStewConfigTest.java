package com.huestew.studio;

import static org.junit.Assert.*;

import java.io.File;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.huestew.studio.model.HueStewConfig;

public class HueStewConfigTest {
	private HueStewConfig config;
	private String random;

	@Before
	public void before() {
		config = new HueStewConfig(System.getProperty("user.home") + File.separator 
				+ "HueStew", "", System.getProperty("user.home"), "", 1.0, "");
		random = UUID.randomUUID().toString();
	}

	@Test
	public void setSaveDirectory() {
		config.setSaveDirectory(random);
		assertEquals(config.getSaveDirectory(), random);
	}

	@Test
	public void setMusicDirectory() {
		config.setMusicDirectory(random);
		assertEquals(config.getMusicDirectory(), random);
	}

	@Test
	public void setMusicFilePath() {
		config.setMusicFilePath(random);
		assertEquals(config.getMusicFilePath(), random);
	}

	@Test
	public void setWindowDimensions() {
		config.setWindowDimensions(random);
		assertEquals(config.getWindowDimensions(), random);
	}

	@Test
	public void setVolume() {
		double volume = Math.random();
		config.setVolume(volume);
		assertTrue(Math.abs(volume - config.getVolume()) < 0.0001);
	}
}
