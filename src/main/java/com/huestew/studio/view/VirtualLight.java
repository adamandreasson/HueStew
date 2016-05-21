package com.huestew.studio.view;

import java.beans.PropertyChangeEvent;

import com.huestew.studio.model.Color;
import com.huestew.studio.model.KeyFrameTransition;
import com.huestew.studio.model.LightState;
import com.huestew.studio.model.Show;
import com.huestew.studio.model.VirtualBulb;

/**
 * Controller for a virtual light
 * 
 * @author Adam
 *
 */
public class VirtualLight implements Light {

	private VirtualBulb bulb;
	private String name;
	private Show show;

	/**
	 * Creates a new virtualLight with a bulb assigned to it.
	 * 
	 * @param bulb
	 *            the bulb which is assigned to the new virtualight.
	 */
	public VirtualLight(VirtualBulb bulb, String name, Show show) {
		this.bulb = bulb;
		bulb.setState(new LightState(new Color(0, 0, 0), 0, 0));
		this.name = name;
		this.show = show;
	}

	/**
	 * Get the bulb assigned to this virtuallight
	 * 
	 * @return the bulb assigned to this virutallight
	 */
	public VirtualBulb getBulb() {
		return bulb;
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
		double diff = Math.abs(to - from);
		if (from < to) {
			return from + (diff * progress);
		} else {
			return from - (diff * progress);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("keyFrameTransition")) {
			KeyFrameTransition transition = (KeyFrameTransition) evt.getNewValue();

			if (transition.getFrom() == null || transition.getTo() == null) {

				if (transition.getFrom() == null && transition.getTo() != null) {
					bulb.setState(transition.getTo().getState());
					return;
				}
				return;

			} else {

				int cursor = show.getCursor() - transition.getFrom().getTimestamp();
				int transitionLength = transition.getTo().getTimestamp() - transition.getFrom().getTimestamp();
				double transitionProgress = cursor / ((double) transitionLength);
				LightState interState = blendLightStates(transition.getFrom().getState(), transition.getTo().getState(),
						transitionProgress);
				bulb.setState(interState);

			}
		}
	}

	@Override
	public String getName() {
		return name;
	}

}
