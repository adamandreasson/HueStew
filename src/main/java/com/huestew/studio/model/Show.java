package com.huestew.studio.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A light show currently being worked on
 * 
 * @author Adam Andreasson
 */
public class Show {

	private List<LightTrack> lightTracks = new ArrayList<>();

	public void addLightTrack(LightTrack track) {
		lightTracks.add(track);
	}

	public void removeLightTrack(LightTrack track) {
		lightTracks.remove(track);
	}

	public List<LightTrack> getLightTracks(){
		//TODO
		return lightTracks;
	}
	
}
