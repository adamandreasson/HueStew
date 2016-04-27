package com.huestew.studio;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

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
}
