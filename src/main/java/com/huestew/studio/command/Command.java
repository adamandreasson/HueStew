package com.huestew.studio.command;

/**
 * 
 * @author Daaale
 *
 */

public interface Command {
	
	public void execute();
	
	public void undo();
	
	public void redo();

	public String getDescription(); 
}
