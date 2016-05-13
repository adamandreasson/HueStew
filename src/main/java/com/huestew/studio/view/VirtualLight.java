package com.huestew.studio.view;

import java.beans.PropertyChangeEvent;

import com.huestew.studio.HueStew;
import com.huestew.studio.model.Color;
import com.huestew.studio.model.KeyFrameTransition;
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
	 * 
	 * @param bulb
	 *            the bulb which is assigned to the new virtualight.
	 */
	public VirtualLight(VirtualBulb bulb) {
		this.bulb = bulb;
	}

	/**
	 * Get the bulb assigned to this virtuallight
	 * 
	 * @return the bulb assigned to this virutallight
	 */
	public VirtualBulb getBulb() {
		return bulb;
	}

	@Override
	/**
	 * Send a command to the assigned virtualbulb to change its state
	 * 
	 * @param state
	 *            the new state for the virtualbulb
	 */
	public void setState(LightState state) {
		bulb.setState(state);
	}


	private LightState blendLightStates(LightState from, LightState to, double transitionProgress) {
		int brightness = (int) blend(from.getBrightness(), to.getBrightness(), transitionProgress);
		int saturation = (int) blend(from.getSaturation(), to.getSaturation(), transitionProgress);
		Color color = new Color(blend(from.getColor().getRed(), to.getColor().getRed(), transitionProgress), 
				blend(from.getColor().getGreen(), to.getColor().getGreen(), transitionProgress),
				blend(from.getColor().getBlue(), to.getColor().getBlue(), transitionProgress));
		
		return new LightState(color, brightness, saturation);
	}
	
	private double blend(double from, double to, double progress) {
		double diff = Math.abs(to-from);
		if(from < to){
			return from + (diff*progress);
		}else{
			return from - (diff*progress);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("keyFrameTransition")) {
			KeyFrameTransition transition = (KeyFrameTransition) evt.getNewValue();

			if (transition.getFrom() == null || transition.getTo() == null) {

				if (transition.getFrom() == null && transition.getTo() != null) {
					setState(transition.getTo().getState());
					return;
				}
				return;

			} else {

				int cursor = HueStew.getInstance().getCursor() - transition.getFrom().getTimestamp();
				int transitionLength = transition.getTo().getTimestamp() - transition.getFrom().getTimestamp();
				double transitionProgress = cursor / ((double) transitionLength);
				LightState interState = blendLightStates(transition.getFrom().getState(), transition.getTo().getState(), transitionProgress);
				setState(interState);

			}
		}
	}

}
