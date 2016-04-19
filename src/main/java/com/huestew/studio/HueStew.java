/**
 * 
 */
package com.huestew.studio;

/**
 * Main class for the HueStew Studio model
 * 
 * @author Adam Andreasson
 */
public class HueStew {

	private static HueStew instance = null;

	private VirtualRoom virtualRoom;
	
	protected HueStew() {
		// Exists only to defeat instantiation.
	}
	
	public static HueStew getInstance() {
		if(instance == null) {
			instance = new HueStew();
		}
		return instance;
	}

	public VirtualRoom getVirtualRoom() {
		return virtualRoom;
	}

	public void setVirtualRoom(VirtualRoom virtualRoom) {
		this.virtualRoom = virtualRoom;
	}
	
}
