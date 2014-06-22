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
package cl.uai.client.marks;

import cl.uai.client.EMarkingWeb;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Manages when the mouse goes out from a Mark
 * 
 * @author Jorge Villalón <villalon@gmail.com>
 *
 */
public class MarkOnMouseOutHandler implements MouseOutHandler {
	@Override
	public void onMouseOut(MouseOutEvent event) {
		Mark mark = (Mark) event.getSource();
		
		// Hide details popup
		if(Mark.markPopup != null) {
			if(Mark.markPopup.getParent() instanceof PopupPanel) {
				Mark.markPopup.getParent().setVisible(false);
			}
			Mark.markPopup.removeFromParent();
			Mark.markPopup = null;
		}
		
		// Dehighlight the corresponding row in the rubric
		if(mark instanceof RubricMark) {
			int criterionid = ((RubricMark) mark).getCriterionId();
			EMarkingWeb.markingInterface.getRubricInterface().getRubricPanel().dehighlightRubricCriterion(criterionid);
		}
	}
}

