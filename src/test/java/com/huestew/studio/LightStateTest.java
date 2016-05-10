package com.huestew.studio;

import static org.junit.Assert.*;

import javafx.scene.paint.Color;

import org.junit.Before;
import org.junit.Test;

import com.huestew.studio.model.LightState;

public class LightStateTest {
	LightState lightState;
	
	@Before
	public void before() {
		lightState = new LightState(Color.BLACK, 0, 0);
	}
	
	@Test
	public void setColor() {
		lightState.setColor(Color.GREEN);
		
		assertTrue(lightState.getColor().equals(Color.GREEN));
	}
	
	@Test
	public void setBrightness() {
		lightState.setBrightness(50);
		
		assertTrue(lightState.getBrightness() == 50);
	}
	
	@Test
	public void setSaturation() {
		lightState.setSaturation(100);
		
		assertTrue(lightState.getSaturation() == 100);
	}
}
