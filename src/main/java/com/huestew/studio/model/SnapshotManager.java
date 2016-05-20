package com.huestew.studio.model;

import java.util.ArrayList;
import java.util.List;

/** 
 * A manager for snapshots of the model, used to support redo/undo.
 * @author Marcus
 *
 */
public enum SnapshotManager {
	INSTANCE;

	private List<Show> undoStack;
	
	private List<Show> redoStack;
	
	private SnapshotManager() {
		undoStack = new ArrayList<Show>();
		redoStack = new ArrayList<Show>();
			
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
	public void commandIssued(Show show) {		
	}
	
	/**
	 * Undo the previous action, will return the latest version on the undo stack.
	 * Will add the pre undo version on the redo stack.
	 * @param show
	 * 				the pre undo version of the show.
	 * @return
	 * 			the latest version of show on the undo stack.
	 */
	public Show undo(Show show) {
		return null;
	}
	
	/**
	 * Redo the previous action, will return the version present previous to undo.
	 * Will add the undo version on the undo stack.
	 * @param show
	 * 				the version to be put on the undo stack.
	 * @return
	 * 			the pre undo version of show.
	 */
	public Show redo(Show show) {
		return null;
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
}
