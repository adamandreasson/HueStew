package com.huestew.studio;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.huestew.studio.model.Audio;
import com.huestew.studio.model.LightTrack;
import com.huestew.studio.model.Show;

public class ShowTest {
	private Show show;

	@Before
	public void before() {
		show = new Show();
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
		show.removeLightTrack(track);
		
		assertTrue(show.getLightTracks().size() == 0);
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
		
		boolean negative = true;
		try {
			show.setDuration(-500);
		} catch (IllegalArgumentException e) {
			negative = false;
		}
		
		assertFalse(negative);
		assertTrue(show.getDuration() >= 0);
	}
	
	@Test
	public void setAudio() {
		Audio audio = new Audio(new File(System.getProperty("user.home")).listFiles()[0]);
		show.setAudio(audio);
		
		assertTrue(show.getAudio().equals(audio));
	}
}
