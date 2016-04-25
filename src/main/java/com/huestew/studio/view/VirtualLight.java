package com.huestew.studio.view;

import java.beans.PropertyChangeEvent;

import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightState;
import com.huestew.studio.model.VirtualBulb;

/**
 * Controller for a virtual light
 * 
 * @author Adam
 *
 */
public class VirtualLight implements Light {

	/** the virtual bulb assigned to this controller. **/
	private VirtualBulb bulb;

	/**
	 * Creates a new virtualLight with a bulb assigned to it.
	 * @param bulb
	 * 				the bulb which is assigned to the new virtualight.
	 */
	public VirtualLight(VirtualBulb bulb) {
		this.bulb = bulb;
	}
	
	/**
	 * Get the bulb assigned to this virtuallight
	 * @return
	 * 			the bulb assigned to this virutallight
	 */
	public VirtualBulb getBulb(){
		return bulb;
	}
	
	@Override
	/**
	 * Send a command to the assigned virtualbulb to change its state
	 * @param state
	 * 				the new state for the virtualbulb
	 */
	public void setState(LightState state) {
		bulb.setColor(state.getColor());
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("latestKeyFrame")) {
			KeyFrame keyFrame = (KeyFrame) evt.getNewValue();
			
			setState(keyFrame.getState());
		}
	}

}
