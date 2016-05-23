package com.huestew.studio;

import static org.junit.Assert.*;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Before;
import org.junit.Test;

import com.huestew.studio.model.Color;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightState;
import com.huestew.studio.model.LightTrack;

public class KeyFrameTest {
	private LightState state;
	private LightTrack track;
	private KeyFrame keyFrame;

	@Before
	public void before() {
		state = new LightState(new Color(0, 0, 0), 0, 0);
		track = new LightTrack();
		keyFrame = new KeyFrame(0, state, track);
	}

	@Test
	public void remove() {
		LightTrack track = new LightTrack();
		keyFrame = new KeyFrame(0, null, track);

		track.addKeyFrame(keyFrame);
		assertFalse(track.getKeyFrames().isEmpty());

		keyFrame.remove();
		assertTrue(track.getKeyFrames().isEmpty());
	}

	@Test
	public void setTimestamp() {
		int timestamp = (int) (Math.random() * 100);
		keyFrame.setTimestamp(timestamp);
		assertSame(timestamp, keyFrame.getTimestamp());

		try {
			keyFrame.setTimestamp(-1);
			fail();
		} catch (IllegalArgumentException e) {}
	}

	@Test
	public void setState() {
		LightState state = new LightState(new Color(0, 0, 0), 0, 0);
		keyFrame.setState(state);
		assertSame(state, keyFrame.getState());
	}

	@Test
	public void compareTo() {
		KeyFrame low = new KeyFrame(100);
		KeyFrame high = new KeyFrame(ThreadLocalRandom.current().nextInt(101, 200));
		assertTrue(low.compareTo(high) < 0);
	}

	@Test
	public void equals() {
		assertEquals(keyFrame, keyFrame);
		assertEquals(keyFrame, new KeyFrame(keyFrame));
		assertNotEquals(keyFrame, null);
		assertNotEquals(keyFrame, new Object());

		KeyFrame notSame = new KeyFrame(5);
		assertNotEquals(keyFrame, notSame);

		notSame = new KeyFrame(0, null, new LightTrack());
		assertNotEquals(keyFrame, notSame);

		KeyFrame same = new KeyFrame(0, state, track);
		assertEquals(keyFrame, same);
	}
}
