/**
 * 
 */
package nu.yottabyte.textlights;

import java.beans.PropertyChangeEvent;
import com.huestew.studio.model.KeyFrame;
import com.huestew.studio.model.KeyFrameTransition;
import com.huestew.studio.model.LightState;
import com.huestew.studio.view.Light;

/**
 * @author Adam
 *
 */
public class TextLight implements Light {

	private String name;
	private KeyFrame lastFrame;

	public TextLight(String name) {
		this.name = name;
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {

		if (event.getPropertyName().equals("keyFrameTransition")) {
			KeyFrameTransition transition = (KeyFrameTransition) event.getNewValue();
			if (transition.getTo() == null)
				return;
			if (lastFrame != null && lastFrame.equals(transition.getTo()))
				return;
			if (lastFrame != null && lastFrame.getState().equals(transition.getTo().getState()))
				return;

			LightState state = transition.getTo().getState();

			System.out.println(System.currentTimeMillis() + " | " + name + " update! : " + state.getBrightness());

			lastFrame = transition.getTo();

		}
	}

	@Override
	public String getName() {
		return name;
	}

}
