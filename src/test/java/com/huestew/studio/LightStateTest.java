package com.huestew.studio;

import static org.junit.Assert.*;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Before;
import org.junit.Test;

import com.huestew.studio.model.Color;
import com.huestew.studio.model.LightState;

public class LightStateTest {
	private Color color;
	private short brightness;
	private short saturation;
	private LightState lightState;

	@Before
	public void before() {
		color = new Color(Math.random(), Math.random(), Math.random());
		brightness = (short) ThreadLocalRandom.current().nextInt(256);
		saturation = (short) ThreadLocalRandom.current().nextInt(256);
		lightState = new LightState(color, brightness, saturation);
	}

	@Test
	public void copy() {
		LightState copy = new LightState(lightState);
		assertEquals(lightState, copy);
		assertNotSame(lightState, copy);
	}

	@Test
	public void setColor() {
		color = new Color(0, 1, 0);
		lightState.setColor(color);
		assertEquals(color, lightState.getColor());
	}

	@Test
	public void setBrightness() {
		brightness = (short) ThreadLocalRandom.current().nextInt(256);
		lightState.setBrightness(brightness);
		assertTrue(brightness == lightState.getBrightness());

		try {
			lightState.setBrightness(-1);
			fail();
		} catch (IllegalArgumentException e) {}

		try {
			lightState.setBrightness(256);
			fail();
		} catch (IllegalArgumentException e) {}
	}

	@Test
	public void setSaturation() {
		saturation = (short) ThreadLocalRandom.current().nextInt(256);
		lightState.setSaturation(saturation);
		assertTrue(saturation == lightState.getSaturation());

		try {
			lightState.setSaturation(-1);
			fail();
		} catch (IllegalArgumentException e) {}

		try {
			lightState.setSaturation(256);
			fail();
		} catch (IllegalArgumentException e) {}
	}

	@Test
	public void hashCodeTest() {
		LightState copy = new LightState(lightState);
		assertTrue(lightState.hashCode() == copy.hashCode());

		copy.setBrightness(Math.abs(brightness - 255));
		assertNotSame(lightState.hashCode(), copy.hashCode());
	}

	@Test
	public void equals() {
		assertEquals(lightState, lightState);
		assertNotEquals(lightState, null);
		assertNotEquals(lightState, new Object());

		LightState other = new LightState(lightState);
		other.setBrightness(Math.abs(lightState.getBrightness() - 1));
		assertNotEquals(lightState, other);

		other = new LightState(lightState);
		other.setSaturation(Math.abs(lightState.getSaturation() - 1));
		assertNotEquals(lightState, other);

		other = new LightState(lightState);
		Color otherColor = new Color(color);
		otherColor.setRed(Math.abs(color.getRed() - 1));
		other.setColor(otherColor);
		assertNotEquals(lightState, other);

		lightState.setBrightness(0);
		other = new LightState(lightState);
		other.setSaturation(Math.abs(lightState.getSaturation() - 1));
		assertEquals(lightState, other);
	}
}
