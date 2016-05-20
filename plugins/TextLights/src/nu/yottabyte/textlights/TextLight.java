/**
 * 
 */
package nu.yottabyte.textlights;

import java.beans.PropertyChangeEvent;

import com.huestew.studio.view.Light;

/**
 * @author Adam
 *
 */
public class TextLight implements Light{

	private String name;
	
	public TextLight(String name){
		this.name = name;
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		System.out.println("Text light change!");
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

}
