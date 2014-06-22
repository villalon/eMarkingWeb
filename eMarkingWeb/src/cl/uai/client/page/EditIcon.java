// This file is part of Moodle - http://moodle.org/
//
// Moodle is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// Moodle is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Moodle.  If not, see <http://www.gnu.org/licenses/>.

/**
 * @package   eMarking
 * @copyright 2013 Jorge Villalón <villalon@gmail.com>
 * @license   http://www.gnu.org/copyleft/gpl.html GNU GPL v3 or later
 */
package cl.uai.client.page;

import cl.uai.client.marks.Mark;
import cl.uai.client.resources.Resources;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;

/**
 * @author Jorge Villalón <villalon@gmail.com>
 *
 */
public class EditIcon extends TrashIcon {

	/**
	 * Constructs an edit icon
	 */
	public EditIcon() {
		super();
		
		// Sets inner HTML
		Icon icon = new Icon(IconType.EDIT);
		this.setHTML(icon.toString());
		
		// Removes trash CSS style
		this.removeStyleName(Resources.INSTANCE.css().trashicon());
		
		// Adds own CSS style
		this.addStyleName(Resources.INSTANCE.css().editicon());		
	}
	
	@Override
	protected void processCommand(ClickEvent event) {
		EditIcon icon = (EditIcon) event.getSource();
		
		// Hide icons as edit was clicked
		Mark.hideIcons();

		Mark mark = (Mark) icon.mark;
		
		mark.updateMark(event.getClientX(), event.getClientY());
	}
}
