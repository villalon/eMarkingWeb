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

import cl.uai.client.EMarkingConfiguration;
import cl.uai.client.marks.Mark;
import cl.uai.client.marks.RubricMark;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.user.client.ui.AbsolutePanel;

/**
 * Drag and Drop handler for a Marking page
 * 
 * @author Jorge Villalón <villalon@gmail.com>
 *
 */
public class MarkingPageDragHandler implements DragHandler {

	Logger logger = Logger.getLogger(MarkingPageDragHandler.class.getName());
	
	/** Marking pages are surrounded by a scroll and an absolute panel **/
	private AbsolutePanel absolutePanel = null;
	private MarkingPage page = null;
	/**
	 * Constructor require the panels
	 * @param apanel
	 * @param spanel
	 */
	public MarkingPageDragHandler(AbsolutePanel apanel, MarkingPage _page) {
		this.absolutePanel = apanel;
		this.page = _page;
	}
	
	@Override
	public void onPreviewDragStart(DragStartEvent event)
			throws VetoDragException {
	}

	@Override
	public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
	}

	/**
	 * When starting to drag, hide Mark icons
	 */
	@Override
	public void onDragStart(DragStartEvent event) {
		Mark.hideIcons();
	}

	/**
	 * When a Mark is dropped, update its position
	 */
	@Override
	public void onDragEnd(DragEndEvent event) {
		
		// If we are in readonly mode we don't update the data but let the mark move for usability
		if(EMarkingConfiguration.isReadonly())
			return;

		// Read the data from the interface to update the mark
		Mark mark = (Mark) event.getSource();
		int level = 0;
		float bonus = 0;
		String regradecomment = "";
		int motive = 0;
		
		if(mark instanceof RubricMark) {
			level = ((RubricMark) mark).getLevelId();
			bonus = ((RubricMark) mark).getLevel().getBonus();
			regradecomment = ((RubricMark) mark).getRegrademarkercomment();
			motive = ((RubricMark) mark).getRegrademotive();
		}
		
		int widthPage = this.page.getWidth();
		int heightPage = this.page.getHeight();
		
		mark.update(
				mark.getRawtext(),
				event.getContext().desiredDraggableX - absolutePanel.getAbsoluteLeft(), 
				event.getContext().desiredDraggableY - absolutePanel.getAbsoluteTop(),
				level,
				bonus, 
				regradecomment, 
				motive,
				widthPage,
				heightPage);				
	}
}
