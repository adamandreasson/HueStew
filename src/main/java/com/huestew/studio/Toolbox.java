package com.huestew.studio;

import java.util.List;



/**
 * 
 * A toolbox containing a list of tools.
 * 
 * 
 * @author Daniel Illipe
 * @author Patrik Olson
 * @author Marcus Randevik
 * @author Adam Andreasson
 * 
 */

public class Toolbox {
	
	private static Toolbox instance = null;
	
	private Toolbox(){};
	
	public static Toolbox getInstance(){
		
		if(instance == null){
			instance = new Toolbox();
		}
		
		return instance;
	}
	
	private List<Tool> tools;
	
	private Tool tool;
	
	
	/**
	 * 
	 * @return returns current tool
	 * 
	 */
	public Tool getTool(){
		
		return tool;
		
	}
	
	
	

}