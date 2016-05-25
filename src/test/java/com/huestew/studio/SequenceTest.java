package com.huestew.studio;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;


import org.junit.Test;

import com.huestew.studio.model.Color;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightState;
import com.huestew.studio.model.LightTrack;
import com.huestew.studio.model.Sequence;

public class SequenceTest {
	
	private List<KeyFrame> origFrames;
	private List<KeyFrame> frames;
	private Sequence sequence;
	private LightTrack track;
	private LightState state;
	private Color color;
	private KeyFrame frame;
	
	@Test
	public void test(){
		
		origFrames = new ArrayList<KeyFrame>(); 
		track = new LightTrack();
		color = new Color(0.5, 0.5, 0.5);
		state = new LightState(color, 137, 137);
		frame = new KeyFrame(5000, state, track);
		origFrames.add(frame);
		
		sequence = new Sequence(origFrames); 
		frames = sequence.getFrames();
		
		assertEquals(frames.size(), origFrames.size());
		
	}
	
	
	
}
