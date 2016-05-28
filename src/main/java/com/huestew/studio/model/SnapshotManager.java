package com.huestew.studio.model;

import java.util.List;
import java.util.Stack;

/** 
 * A manager for snapshots of the model, used to support redo/undo.
 * @author Marcus
 *
 */
public enum SnapshotManager {
	INSTANCE;

	private Stack<Show> undoStack;
	
	private Stack<Show> redoStack;

	private Show show;
	
	private SnapshotManager() {
		undoStack = new Stack<Show>();
		redoStack = new Stack<Show>();
	}
	
	public static SnapshotManager getInstance() {
		return INSTANCE;
	}
	
	/**
	 * To be called when a command that changes the show is issued.
	 * Will add the previous version of the show in the undo stack as well ass
	 * clear the redo stack.
	 * @param show
	 * 				the version of the show previous to the command
	 */
	public void commandIssued() {
		System.out.println("SNAPSHOT");
		undoStack.push(new Show(show));
		redoStack.clear();
	}
	
	/**
	 * Undo the previous action, will return the latest version on the undo stack.
	 * Will add the pre undo version on the redo stack.
	 * @param show
	 * 				the pre undo version of the show.
	 * @return
	 * 			the latest version of show on the undo stack.
	 */
	public void undo() {
		if(canUndo()) {
			Show s = undoStack.pop();
			redoStack.push(new Show(show));
			
			transferData(s, show);
		} else {
			throw new IllegalStateException("Cannot undo when undostack is zero");
		}
		
	}
	
	/**
	 * Redo the previous action, will return the version present previous to undo.
	 * Will add the undo version on the undo stack.
	 * @param show
	 * 				the version to be put on the undo stack.
	 * @return
	 * 			the pre undo version of show.
	 */
	public void redo() {
		if (canRedo()) {
			Show s = redoStack.pop();
			undoStack.push(new Show(show));
			
			transferData(s, show);
		} else {
			throw new IllegalStateException("Cannot redo when redostack is zero");
		}
	}
	
	/**
	 * Determines whether undo is currently possible
	 * @return
	 * 			true if undo is possible.
	 */
	public boolean canUndo() {
		return !undoStack.isEmpty();
	}
	
	/**
	 * Determines whether redo is currently possible
	 * @return
	 * 			true if redo is possible.
	 */
	public boolean canRedo() {
		return !redoStack.isEmpty();
	}

	public void clear() {
		undoStack.clear();
		redoStack.clear();
	}

	public void setShow(Show show) {
		this.show = show;
		clear();
	}
	
	private void transferData(Show from, Show to) {
		// Transfer key frames
		List<LightTrack> fromTracks = from.getLightTracks();
		List<LightTrack> toTracks = to.getLightTracks();

		for (int i = 0; i < toTracks.size(); i++) {
			toTracks.get(i).setKeyFrames(fromTracks.get(i).getKeyFrames());
		}
	}
}
