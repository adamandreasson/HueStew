package com.huestew.studio;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightTrack;

public class LightTrackTest {
	LightTrack track;
	KeyFrame keyFrame;

	@Before
	public void before() {
		track = new LightTrack();
		keyFrame = new KeyFrame(0, null, null);
	}

	@Test
	public void addKeyFrame() {
		track.addKeyFrame(keyFrame);
		assertTrue(track.getKeyFrames().size() == 1);
		assertTrue(track.getKeyFrames().contains(keyFrame));
		
		try {
			track.addKeyFrame(keyFrame);
			fail();
		} catch (IllegalArgumentException e) {}
	}

	@Test
	public void removeKeyFrame() {
		track.addKeyFrame(keyFrame);
		track.removeKeyFrame(keyFrame);

		assertTrue(track.getKeyFrames().size() == 0);
	}
}
