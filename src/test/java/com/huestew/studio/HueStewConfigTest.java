package com.huestew.studio;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.huestew.studio.model.HueStewConfig;

public class HueStewConfigTest {
	private HueStewConfig config;
	private String random;

	@Before
	public void before() {
		config = HueStewConfig.getInstance();
		random = UUID.randomUUID().toString();
	}

	@Test
	public void setSaveDirectory() {
		config.setSaveDirectory(random);
		assertEquals(config.getSaveDirectory(), random);
	}
	
	@Test
	public void setSaveFile() {
		config.setSaveFile(random);
		assertEquals(config.getSaveFile(), random);
	}

	@Test
	public void setMusicDirectory() {
		config.setMusicDirectory(random);
		assertEquals(config.getMusicDirectory(), random);
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

	@Test
	public void setMinFrameDistance() {
		int distance = (int) (Math.random() * 1000);
		config.setMinFrameDistance(distance);
		assertTrue(config.getMinFrameDistance() == distance);
	}
}
