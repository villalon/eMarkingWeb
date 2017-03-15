/**
 * 
 */
package cl.uai.client.toolbar.buttons.icons;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;

import cl.uai.client.EMarkingConfiguration;

/**
 * @author Jorge Villalon
 *
 */
public class Highlighter extends Icon {

	/**
	 * 
	 */
	public Highlighter() {
	}

	/**
	 * @param type
	 */
	public Highlighter(IconType type) {
		super(type);
	}
	
	@Override
	public String toString() {
		String moodleurlbase = EMarkingConfiguration.getMoodleUrl().replace("/ajax/a.php", "");
		String s = "<img style=\"height:20px;\" src=\"" + moodleurlbase + "/img/marker.png\" height=\"20\">";
		return s;
	}
}
