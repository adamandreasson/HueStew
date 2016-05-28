/**
 * 
 */
package com.huestew.studio.model;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.input.KeyCode;

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

	public boolean isValidKey(KeyCode key) {

		for (Drum drum : drums) {
			if (drum.getKey() == key) {
				return true;
			}
		}

		return false;
	}

	public boolean beat(KeyCode key, Show show) {

		for (Drum drum : drums) {
			if (drum.getKey() == key) {
				// TODO creates a snapshot even if drum wasn't beat
				SnapshotManager.getInstance().commandIssued();
				return drum.beat(show);
			}
		}

		return false;

	}

}
