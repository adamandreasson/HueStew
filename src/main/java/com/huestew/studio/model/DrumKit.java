/**
 * 
 */
package com.huestew.studio.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam
 *
 */
public class DrumKit {

	private List<Drum> drums;

	public DrumKit() {
		this.drums = new ArrayList<Drum>();
	}

	public Drum addDrum(Sequence sequence) {
		Drum drum = new Drum(sequence, "Drum " + (drums.size() + 1));
		drums.add(drum);
		return drum;
	}

	public List<Drum> getDrums() {
		return drums;
	}

}
