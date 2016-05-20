package com.huestew.studio;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.huestew.studio.model.Color;
import com.huestew.studio.model.LightState;
import com.huestew.studio.model.VirtualBulb;

public class VirtualBulbTest {
	private VirtualBulb virtualBulb;

	@Before
	public void before() {
		virtualBulb = new VirtualBulb();
	}

	@Test
	public void setPosition() {
		double x = Math.random();
		double y = Math.random();

		virtualBulb.setPosition(x, y);
		assertTrue(Math.abs(x - virtualBulb.getX()) < 0.0001);
		assertTrue(Math.abs(y - virtualBulb.getY()) < 0.0001);
	}

	@Test
	public void setState() {
		LightState state = new LightState(new Color(0, 0, 0), 0, 0);
		virtualBulb.setState(state);
		assertEquals(state, virtualBulb.getState());
	}
}
