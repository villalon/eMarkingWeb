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

import java.util.logging.Logger;

import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;

import cl.uai.client.EMarkingWeb;

/**
 * Handler for moving the mouse over a mark. Implements showing
 * the edit and delete buttons, and the Mark popup with details
 * 
 * @author Jorge Villalón <villalon@gmail.com>
 *
 */
public class MarkOnMouseOverHandler implements MouseOverHandler {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(MarkOnMouseOverHandler.class.getName());

	@Override
	public void onMouseOver(MouseOverEvent event) {
		Mark mark = (Mark) event.getSource();
		
		Mark.showIcons(mark);
		
		// Highlight the rubric interface if the mark is a RubricMark
		if(mark instanceof RubricMark) {
			int criterionid = ((RubricMark) mark).getCriterionId();
			EMarkingWeb.markingInterface.getRubricInterface().getRubricPanel().highlightRubricCriterion(criterionid);
		}
		
	}
}
