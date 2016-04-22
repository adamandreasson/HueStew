package com.huestew.studio;

import java.beans.PropertyChangeEvent;

import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.LightState;
import com.huestew.studio.model.VirtualBulb;
import com.huestew.studio.view.Light;

/**
 * Controller for a virtual light
 * 
 * @author Adam
 *
 */
public class VirtualLightController implements Light {

	private VirtualBulb bulb;

	public VirtualLightController(VirtualBulb bulb) {
		this.bulb = bulb;
	}
	
	public VirtualBulb getBulb(){
		return bulb;
	}
	
	@Override
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
