package com.huestew.studio;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.huestew.studio.model.Color;

public class ColorTest {
	private double r, g, b;
	private int hue;
	private Color color;

	@Before
	public void before() {
		r = Math.random();
		g = Math.random();
		b = Math.random();
		hue = (int) (Math.random() * 65535);

		color = new Color(r, g, b, hue);
	}

	@Test
	public void constructors() {
		assertTrue(Math.abs(color.getRed() - r) < 0.0001);
		assertTrue(Math.abs(color.getGreen() - g) < 0.0001);
		assertTrue(Math.abs(color.getBlue() - b) < 0.0001);
		assertTrue(color.getHue() == hue);

		Color copy = new Color(color);
		assertNotSame(color, copy);
		assertEquals(color, copy);

		javafx.scene.paint.Color fx = new javafx.scene.paint.Color(r, g, b, 1);
		Color fromFx = new Color(fx);
		assertEquals(color, fromFx);
		assertEquals(color.toFxColor(), fx);
		
	}

	@Test
	public void equals() {
		assertEquals(color, color);
		assertNotEquals(color, new Object());
		assertNotEquals(color, null);

		Color notEqual = new Color(r, g, Math.abs(b - 1));
		assertNotEquals(color, notEqual);
	}

	@Test
	public void hashCodeTest() {
		Color copy = new Color(color);
		assertTrue(color.hashCode() == copy.hashCode());

		Color notEqual = new Color(r, g, Math.abs(b - 1));
		assertFalse(color.hashCode() == notEqual.hashCode());
	}

	@Test
	public void setters() {
		try {
			color.setRed(0D);
			color.setRed(1D);
			color.setGreen(0D);
			color.setGreen(1D);
			color.setBlue(0D);
			color.setBlue(1D);
			color.setHue(0);
			color.setHue(65535);
		} catch (IllegalArgumentException e) {
			fail();
		}

		try {
			color.setRed(-1);
			fail();
		} catch (IllegalArgumentException e) {}

		try {
			color.setGreen(3);
			fail();
		} catch (IllegalArgumentException e) {}

		try {
			color.setBlue(1.01);
			fail();
		} catch (IllegalArgumentException e) {}
	}

	@Test
	public void toStringTest() {
		try {
			color.toString();
		} catch (RuntimeException e) {
			fail();
		}
	}
}
