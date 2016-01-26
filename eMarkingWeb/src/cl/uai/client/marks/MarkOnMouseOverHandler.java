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

import cl.uai.client.EMarkingConfiguration;
import cl.uai.client.EMarkingWeb;
import cl.uai.client.MarkingInterface;

import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;

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
		// Gets the absolute panel which contains the mark to calculate its coordinates
		Mark mark = (Mark) event.getSource();
		AbsolutePanel abspanel = (AbsolutePanel) mark.getParent();

		int topdiff = - 20;
		int widthdiff = - 12;
		
		if(mark instanceof RubricMark) {
			topdiff = - 20;
			widthdiff = - 20;
		}
		// Calculates basic left, top position for icons
		int top = mark.getAbsoluteTop() - abspanel.getAbsoluteTop() + (topdiff);
		int left = mark.getAbsoluteLeft() + mark.getOffsetWidth() + (widthdiff);

		// Check if icons and popup are already added in the panel, if not adds them
		if(abspanel.getWidgetIndex(Mark.editIcon) < 0)
			abspanel.add(Mark.editIcon, left, top);

		if(abspanel.getWidgetIndex(Mark.deleteIcon) < 0)
			abspanel.add(Mark.deleteIcon, left, top);

		if(abspanel.getWidgetIndex(Mark.regradeIcon) < 0)
			abspanel.add(Mark.regradeIcon, left, top);

		if(abspanel.getWidgetIndex(Mark.minimizeIcon) < 0)
			abspanel.add(Mark.minimizeIcon, left, top);

		// Make sure no other icons are left
		Mark.hideIcons();

		// If we are in grading mode, show delete and edit icons
		if(!EMarkingConfiguration.isReadonly()) {
			
			if(mark instanceof RubricMark) {
				abspanel.setWidgetPosition(Mark.minimizeIcon, left, top);
				Mark.minimizeIcon.setVisible(true);
				Mark.minimizeIcon.setMark(mark);
				left -= 15;
				
			}
			
			// Edit icon is only for comments and rubrics
			if(mark instanceof CommentMark || mark instanceof RubricMark) {
				abspanel.setWidgetPosition(Mark.editIcon, left, top);
				Mark.editIcon.setVisible(true);
				Mark.editIcon.setMark(mark);
				left -= 15;
				top -= 1;
			}

			// Delete icon
			abspanel.setWidgetPosition(Mark.deleteIcon, left, top);
			Mark.deleteIcon.setVisible(true);
			Mark.deleteIcon.setMark(mark);
		}
		
		// If the user owns the submission and the dates are ok we show the regrade icon
		if(EMarkingConfiguration.isOwnDraft() && MarkingInterface.submissionData.isRegradingAllowed()) {
			// Edit icon is only for comments and rubrics
			if(mark instanceof RubricMark) {
				abspanel.setWidgetPosition(Mark.regradeIcon, left, top);
				Mark.regradeIcon.setVisible(true);
				Mark.regradeIcon.setMark(mark);
			}			
		}

		// Highlight the rubric interface if the mark is a RubricMark
		if(mark instanceof RubricMark) {
			int criterionid = ((RubricMark) mark).getCriterionId();
			EMarkingWeb.markingInterface.getRubricInterface().getRubricPanel().highlightRubricCriterion(criterionid);
		}
						
	}
}
