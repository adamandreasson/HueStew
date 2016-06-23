package com.huestew.studio.command;

import java.util.Stack;

/**
 * 
 * @author Daniel
 *
 */

public class CommandManager {

	private Stack<Command> undoStack = new Stack<Command>();

	private Stack<Command> redoStack = new Stack<Command>();

	public CommandManager() {

	}

	public void executeCmd(Command command) {
		command.execute();
		undoStack.push(command);
		redoStack.clear();
		
		System.out.println(undoStack.size());
	}

	public void undo() {

		if (isUndoAvailable()) {

			Command toUndo = undoStack.pop();
			toUndo.undo();
			redoStack.push(toUndo);
			System.out.println("undidded");

		} else {
			throw new IllegalStateException("Cannot undo when undostack is zero");
		}

	}

	public void redo() {
		if (isRedoAvailable()) {
			
			Command toRedo = redoStack.pop();
			toRedo.redo();
			undoStack.push(toRedo);
		} else {
			throw new IllegalStateException("Cannot redo when redostack is zero");
		}
	}

	/**
	 * Determines whether undo is currently possible
	 * 
	 * @return true if undo is possible.
	 */
	public boolean isUndoAvailable() {
		return !undoStack.empty();
	}

	/**
	 * Determines whether redo is currently possible
	 * 
	 * @return true if redo is possible.
	 */
	public boolean isRedoAvailable() {
		return !redoStack.empty();
	}
	
	public void clearStacks(){
		undoStack.removeAllElements();
		redoStack.removeAllElements();
	}

}
