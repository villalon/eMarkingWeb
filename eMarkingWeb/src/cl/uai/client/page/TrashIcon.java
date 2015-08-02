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

import cl.uai.client.EMarkingWeb;
import cl.uai.client.MarkingInterface;
import cl.uai.client.marks.Mark;
import cl.uai.client.resources.Resources;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;

/**
 * @author Jorge Villalón <villalon@gmail.com>
 *
 */
public class TrashIcon extends HTML {

	/**The mark related to the icon **/
	protected Mark mark = null;
	
	/**
	 * Creates a trash icon
	 */
	public TrashIcon() {
		Icon icon = new Icon(IconType.TRASH);
		this.setHTML(icon.toString());
		this.addStyleName(Resources.INSTANCE.css().trashicon());
		
		this.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {				
				event.stopPropagation();
				// TODO: Add confirmation dialog when deleting
				processCommand(event);
			}
		});
	}
	
	/**
	 * Sets the corresponding Mark for the icon
	 * 
	 * @param sourcemark current mark
	 */
	public void setMark(Mark sourcemark) {
		this.mark = sourcemark;
	}
	
	/**
	 * Processes its command when icon was clicked
	 * 
	 * @param event
	 */
	protected void processCommand(ClickEvent event) {
		if(Window.confirm(MarkingInterface.messages.DeleteMarkConfirm())) {
			EMarkingWeb.markingInterface.deleteMark((Mark) mark);
			Mark.hideIcons();
		}
	}
}
