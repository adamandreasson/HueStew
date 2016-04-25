package com.huestew.studio;

import static org.junit.Assert.*;

import java.awt.Color;

import org.junit.Before;
import org.junit.Test;

import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightState;

public class KeyFrameTest {
	KeyFrame keyFrame;

	@Before
	public void before() {
		keyFrame = new KeyFrame(0, null);
	}
	
	@Test
	public void setTimestamp() {
		keyFrame.setTimestamp(50);
		
		assertTrue(keyFrame.getTimestamp() == 50);
	}

	@Test
	public void setState() {
		LightState state = new LightState(Color.black, 0, 0);
		keyFrame.setState(state);
		
		assertTrue(keyFrame.getState() == state);
	}
}
