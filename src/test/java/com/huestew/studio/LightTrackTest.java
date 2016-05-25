package com.huestew.studio;

import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;

import com.huestew.studio.model.Color;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightState;
import com.huestew.studio.model.LightTrack;

public class LightTrackTest {
	LightTrack track;
	KeyFrame keyFrame;

	@Before
	public void before() {
		track = new LightTrack();
		keyFrame = new KeyFrame(0, new LightState(new Color(0, 0, 0), 0, 0), track);
	}
	
	@Test
	public void constructor() {
		AtomicBoolean cursorUpdated = new AtomicBoolean();
		track.addListener(e -> cursorUpdated.set(true));
		track.addKeyFrame(keyFrame);
		
		LightTrack copy = new LightTrack(track);
		assertTrue(track.getKeyFrames().size() == copy.getKeyFrames().size());

		copy.updateCursor(500);
		assertTrue(cursorUpdated.get());
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
