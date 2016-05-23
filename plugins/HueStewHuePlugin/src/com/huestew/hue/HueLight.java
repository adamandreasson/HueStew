/**
 * 
 */
package com.huestew.hue;

import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.Map;

import com.huestew.studio.model.Color;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.KeyFrameTransition;
import com.huestew.studio.model.LightState;
import com.huestew.studio.view.Light;
import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

/**
 * @author Adam
 *
 */
public class HueLight implements Light {

	private PHLight phLight;
	private KeyFrame lastFrame;
	private String name;

	public HueLight(PHLight phLight, String name) {
		this.phLight = phLight;
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.
	 * PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("keyFrameTransition")) {
			KeyFrameTransition transition = (KeyFrameTransition) evt.getNewValue();
			if (transition.getTo() == null)
				return;
			if (lastFrame != null && lastFrame.equals(transition.getTo()))
				return;
			if (lastFrame != null && lastFrame.getState().equals(transition.getTo().getState()))
				return;

			LightState state = transition.getTo().getState();
			Color color = state.getColor();
			PHLightState lightState = new PHLightState();
			System.out.println(color.getHue());
			if (color.getHue() < 0) {
				float xy[] = PHUtilities.calculateXYFromRGB((int) color.getRed() * 255, (int) color.getGreen() * 255,
						(int) color.getBlue() * 255, "LCT001");

				lightState.setX(xy[0]);
				lightState.setY(xy[1]);
			} else {
				lightState.setHue(color.getHue());
			}

			int brightness = state.getBrightness();
			if (brightness > 254)
				brightness = 254;

			if (brightness > 1) {
				lightState.setOn(true);
				lightState.setBrightness(brightness);
			} else {
				lightState.setOn(false);
			}

			int saturation = state.getSaturation();
			if (saturation > 254)
				saturation = 254;
			lightState.setSaturation(saturation);

			if (transition.getFrom() != null) {
				int timeDiff = transition.getTo().getTimestamp() - transition.getFrom().getTimestamp();
				int transitionTime = (int) Math.ceil(timeDiff / 100.0);
				lightState.setTransitionTime(transitionTime);
			}

			System.out.println(System.currentTimeMillis() + " Seinding update!");

			PHHueSDK.getInstance().getSelectedBridge().updateLightState(phLight, lightState, new PHLightListener() {

				@Override
				public void onSuccess() {
				}

				@Override
				public void onStateUpdate(Map<String, String> arg0, List<PHHueError> arg1) {
					System.out.println(arg0);
				}

				@Override
				public void onError(int code, String message) {
					System.err.println("oh no");
					System.err.println(message);
				}

				@Override
				public void onSearchComplete() {
					// TODO Auto-generated method stub

				}

				@Override
				public void onReceivingLights(List<PHBridgeResource> arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onReceivingLightDetails(PHLight arg0) {
					// TODO Auto-generated method stub

				}
			});

			lastFrame = transition.getTo();

		}
	}

	@Override
	public String getName() {
		return name;
	}

}
