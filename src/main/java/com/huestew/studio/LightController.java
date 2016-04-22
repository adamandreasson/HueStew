/**
 * 
 */
package com.huestew.studio;

import java.beans.PropertyChangeListener;

import com.huestew.studio.model.LightState;

/**
 * A interface used for sending commands to both physical and virtiual lights
 * @author Adam
 *
 */
public interface LightController extends PropertyChangeListener {
	
	/**
	 * Send a command to a specified light to change it's current state
	 * @param state
	 * 				the new state to be sent to the light
	 */
	void setState(LightState state);
}
