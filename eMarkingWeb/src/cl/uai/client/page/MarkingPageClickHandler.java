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

import java.util.logging.Logger;

import cl.uai.client.EMarkingWeb;
import cl.uai.client.MarkingInterface;
import cl.uai.client.marks.CheckMark;
import cl.uai.client.marks.CommentMark;
import cl.uai.client.marks.CrossMark;
import cl.uai.client.marks.CustomMark;
import cl.uai.client.marks.QuestionMark;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author Jorge Villalón <villalon@gmail.com>
 *
 */
public class MarkingPageClickHandler implements ClickHandler {

	/** For logging purposes **/
	Logger logger = Logger.getLogger(MarkingPageClickHandler.class.getName());

	MarkingPage parentPage = null;
	
	public MarkingPageClickHandler(MarkingPage _parent) {
		this.parentPage = _parent;
	}
	
	@Override
	public void onClick(ClickEvent event) {

		// If interface is in readonly mode no popups should be allowed.
		if(MarkingInterface.readonly) {
			return;
		}
		
		// Calculate basic position and page number to add a Mark
		final int pageno = this.parentPage.getPageNumber();
		final int newposx = event.getClientX() - this.parentPage.getAbsolutePanel().getAbsoluteLeft();
		final int newposy = event.getClientY() - this.parentPage.getAbsolutePanel().getAbsoluteTop();
		int dialogposy = newposy
				+ this.parentPage.getAbsolutePanel().getAbsoluteTop();

		if(dialogposy > (int) ((float) Window.getClientHeight() * 0.8)) {
			dialogposy -= (int) ((float) Window.getClientHeight() * 0.2);
		}

		final long unixtime = System.currentTimeMillis() / 1000L;
		
		final int selectedCriterion = EMarkingWeb.markingInterface.getToolbar().getMarkingButtons().getIndexSelectedCriterion();

		// Switches over the selected button in the rubric interface, to know what mark to add
		switch(EMarkingWeb.markingInterface.getToolbar().getMarkingButtons().getSelectedButton()) {

		// A comment
		case BUTTON_COMMENT:
			final EditMarkDialog dialog = new EditMarkDialog(
					newposx, 
					dialogposy,
					0, // No level id for a text comment
					0); // No regradeid either
			
			dialog.addCloseHandler(new CloseHandler<PopupPanel>() {				
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					if(dialog.isCancelled() || dialog.getTxtComment().trim().length() <= 0) {
						EMarkingWeb.markingInterface.getElement().focus();
						return;
					}

					CommentMark mark = new CommentMark(
							newposx, 
							newposy, 
							pageno,
							MarkingInterface.markerid,
							unixtime,
							"criterion"+ selectedCriterion
							);
					mark.setRawtext(dialog.getTxtComment());
					EMarkingWeb.markingInterface.addMark(mark, parentPage);
				}
			});
			dialog.show();
			break;
			// A cross
		case BUTTON_CROSS:
			CrossMark crmark = new CrossMark(
					newposx, 
					newposy, 
					pageno,
					MarkingInterface.markerid, 
					unixtime,
					"criterion"+ selectedCriterion);
			EMarkingWeb.markingInterface.addMark(crmark, parentPage);
			break;
			// A pen
		case BUTTON_PEN:
			break;
			// When the rubric button is selected nothing happens, it should use DnD
		case BUTTON_RUBRIC:
			AddMarkDialog addmarkdialog = new AddMarkDialog(parentPage);
			addmarkdialog.setRubricLeft(newposx);
			addmarkdialog.setRubricTop(newposy);
			addmarkdialog.center();
			break;
			// A check mark
		case BUTTON_TICK:
			CheckMark cmark = new CheckMark(
					newposx, 
					newposy, 
					pageno,
					MarkingInterface.markerid,
					unixtime,
					"criterion"+ selectedCriterion);
			EMarkingWeb.markingInterface.addMark(cmark, parentPage);
			break;
			// A check mark
		case BUTTON_QUESTION:
			QuestionMark qmark = new QuestionMark(
					newposx, 
					newposy, 
					pageno,
					MarkingInterface.markerid,
					unixtime,
					"criterion"+ selectedCriterion);
			EMarkingWeb.markingInterface.addMark(qmark, parentPage);
			break;
		case BUTTON_CUSTOM:
			CustomMark custommark = new CustomMark(
					EMarkingWeb.markingInterface.getToolbar().getMarkingButtons()
					.getSelectedButtonLabel(),
					newposx, 
					newposy, 
					pageno,
					MarkingInterface.markerid,
					unixtime,
					"criterion"+ selectedCriterion);
			custommark.setRawtext(
					EMarkingWeb.markingInterface.getToolbar().getMarkingButtons()
						.getSelectedButtonLabel() + ": "
						+ EMarkingWeb.markingInterface.getToolbar().getMarkingButtons()
						.getSelectedButtonTitle());
			EMarkingWeb.markingInterface.addMark(custommark, parentPage);
			break;
		default:
		}
		
		EMarkingWeb.markingInterface.getElement().focus();
	}

}
