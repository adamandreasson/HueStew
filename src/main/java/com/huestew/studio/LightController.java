/**
 * 
 */
package com.huestew.studio;

import java.beans.PropertyChangeListener;

import com.huestew.studio.model.LightState;

/**
 * @author Adam
 *
 */
public interface LightController extends PropertyChangeListener {
	void setState(LightState state);
}
