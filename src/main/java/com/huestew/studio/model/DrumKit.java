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

	/**
	 * Add a new {@link Drum} to the drumkit with a given sequence
	 * 
	 * @param sequence
	 *            The sequence to base the drum on
	 * @return The new {@link Drum} instance created
	 * 
	 */
	public Drum addDrum(Sequence sequence) {
		Drum drum = new Drum(sequence, "Drum " + (drums.size() + 1));
		drums.add(drum);
		return drum;
	}

	/**
	 * Remove a drum from the kit
	 * 
	 * @param drum
	 *            The drum to be removed
	 */
	public void removeDrum(Drum drum) {
		drums.remove(drum);
	}

	/**
	 * Get a list of all drums in the kit
	 * 
	 * @return list of drums
	 */
	public List<Drum> getDrums() {
		return drums;
	}

	/**
	 * Is the key clicked linked to a drum in the kit?
	 * 
	 * @param key
	 *            The key on the keyboard that was clicked
	 * @return Whether the key is assigned to a drum in the kit
	 */
	public boolean isValidKey(KeyCode key) {

		for (Drum drum : drums) {
			if (drum.getKey() == key) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Beat any drum in the drum kit with the given key
	 * 
	 * @param key
	 *            The key pressed
	 * @param show
	 *            Show to assigned any drum sequence to
	 * @return Whether the key press resulted in a new sequence
	 */
	public boolean beat(KeyCode key, Show show) {

		for (Drum drum : drums) {
			if (drum.getKey() == key) {
				return drum.beat(show);
			}
		}

		return false;

	}

}
