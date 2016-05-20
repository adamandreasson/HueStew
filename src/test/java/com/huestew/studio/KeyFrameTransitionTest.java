package com.huestew.studio;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.huestew.studio.model.Color;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.KeyFrameTransition;
import com.huestew.studio.model.LightState;
import com.huestew.studio.model.LightTrack;

public class KeyFrameTransitionTest {
	private KeyFrameTransition transition;
	private LightState state;
	private LightTrack track;
	private KeyFrame from;
	private KeyFrame to;

	@Before
	public void before() {
		state = new LightState(new Color(0, 0, 0), 0, 0);
		track = new LightTrack();
		from = new KeyFrame((int) (Math.random() * 100), state, track);
		to = new KeyFrame((int) (Math.random() * 100), state, track);
		transition = new KeyFrameTransition(from, to);
	}

	@Test
	public void test() {
		assertEquals(from, transition.getFrom());
		assertEquals(to, transition.getTo());

		transition.setFrom(new KeyFrame(101 + (int) (Math.random() * 100), state, track));
		assertNotEquals(from, transition.getFrom());

		transition.setTo(new KeyFrame(101 + (int) (Math.random() * 100), state, track));
		assertNotEquals(to, transition.getTo());
	}

}
