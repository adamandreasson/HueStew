package com.huestew.studio;

import static org.junit.Assert.*;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;

import com.huestew.studio.model.Audio;
import com.huestew.studio.model.Color;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightState;
import com.huestew.studio.model.LightTrack;
import com.huestew.studio.model.Show;

public class ShowTest {
	private Show show;

	@Before
	public void before() {
		show = new Show();
	}

	@Test
	public void constructor() {
		show.setDuration(500);

		LightTrack track = new LightTrack();
		track.addKeyFrame(new KeyFrame(500, new LightState(new Color(1, 1, 1), 0, 0), track));
		show.addLightTrack(track);

		Show copy = new Show(show);
		assertNotSame(show, copy);
		assertTrue(show.getLightTracks().size() == copy.getLightTracks().size());
		assertEquals(show.getDuration(), copy.getDuration());
	}

	@Test
	public void addLightTrack() {
		LightTrack track = new LightTrack();
		show.addLightTrack(track);
		assertTrue(show.getLightTracks().size() == 1);
		assertTrue(show.getLightTracks().contains(track));
	}

	@Test
	public void removeLightTrack() {
		LightTrack track = new LightTrack();
		show.addLightTrack(track);

		try {
			show.removeLightTrack(track);
			fail();
		} catch (IllegalStateException e) {}
		
		show.addLightTrack(new LightTrack());
		show.removeLightTrack(track);
		assertTrue(show.getLightTracks().size() == 1);
	}

	@Test
	public void getLightTracks() {
		show.getLightTracks().add(new LightTrack());
		assertTrue(show.getLightTracks().size() == 0);
	}

	@Test
	public void setDuration() {
		show.setDuration(500);
		assertTrue(show.getDuration() == 500);

		try {
			show.setDuration(-500);
			fail();
		} catch (IllegalArgumentException e) {}
		assertTrue(show.getDuration() >= 0);
	}

	@Test
	public void setAudio() {
		Audio audio = new Audio(new File(System.getProperty("user.home")).listFiles()[0]);
		show.setAudio(audio);
		assertTrue(show.getAudio().equals(audio));
	}

	@Test
	public void updateCursor() {
		final AtomicBoolean cursorUpdated = new AtomicBoolean();
		LightTrack track = new LightTrack();
		show.addLightTrack(track);

		PropertyChangeListener listener = e -> cursorUpdated.set(true);
		track.addListener(listener);
		
		show.updateCursor(0);
		assertTrue(cursorUpdated.get());
		assertTrue(show.getCursor() == 0);
		
		cursorUpdated.set(false);
		track.removeListener(listener);
		show.updateCursor(100);
		assertFalse(cursorUpdated.get());
		assertTrue(show.getCursor() == 100);
	}
}
