/**
 * 
 */
package com.huestew.studio.view;

import java.beans.PropertyChangeListener;

/**
 * A interface used for sending commands to both physical and virtual lights
 * 
 * @author Adam
 *
 */
public interface Light extends PropertyChangeListener {

	String getName();

}
