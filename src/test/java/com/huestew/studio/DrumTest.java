package com.huestew.studio;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.huestew.studio.model.Drum;
import com.huestew.studio.model.LightTrack;
import com.huestew.studio.model.Sequence;
import com.huestew.studio.model.Show;

import javafx.scene.input.KeyCode;

public class DrumTest {
	
	private KeyCode key;
	private Sequence sequence;
	private LightTrack track;
	private String name;
	private Drum drum;
	private Show show;
	
	@Before
	public void before() {
		track = new LightTrack();
		name = UUID.randomUUID().toString();
		drum = new Drum(sequence, name);
		show = new Show();
		
	}
	
	@Test
	public void setKey(){
		drum.setKey(key);
		assertEquals(drum.getKey(), key);
	}
	
	@Test
	public void setSequence(){
		drum.setSequence(sequence);
		assertEquals(drum.getSequence(), sequence);
	}
	
	
	@Test
	public void setTrack(){
		drum.setTrack(track);
		assertEquals(drum.getTrack(), track);
	}
		
	@Test
	public void setName(){
		drum.setName(name);
		assertEquals(drum.getName(), name);
		
	}
	
	@Test
	public void beat(){
		
		drum.beat(show);
		
		assertFalse(track == null);
		
		// something more ...
		
	}

}	
	


	

	
	
