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
import cl.uai.client.marks.CommentMark;
import cl.uai.client.marks.Mark;
import cl.uai.client.rubric.LevelLabel;
import cl.uai.client.rubric.PreviousCommentLabel;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.user.client.ui.Widget;

/**
 * Drop controller which is attached to each page in the pages interface.
 * It allows labels from the rubric to be dropped in it.
 * 
 * @author Jorge Villalón
 *
 */
public class MarkingPageDropController extends SimpleDropController {

	private MarkingPage parentPage = null;
	
	/**
	 * @param dropTarget
	 */
	public MarkingPageDropController(Widget dropTarget, MarkingPage _parent) {
		super(dropTarget);
		
		this.parentPage = _parent;
		
		Mark.hideIcons();
	}

	@Override
	public void onPreviewDrop(DragContext context) throws VetoDragException {
		super.onPreviewDrop(context);

		// Calculate position accordingly. Substracting the absolute panel position to the final drop position.
		int posx = context.desiredDraggableX 
				- this.parentPage.getAbsolutePanel().getAbsoluteLeft();
		int posy = context.desiredDraggableY 
				- this.parentPage.getAbsolutePanel().getAbsoluteTop();
		
		// A dropped object can be a rubric level or a previous comment
		if(context.draggable instanceof LevelLabel) {
			// If it is a rubric level add a rubric mark
			LevelLabel lbl = (LevelLabel) context.draggable;
			EMarkingWeb.markingInterface.addRubricMark(
					lbl.getLevelId(), 
					posx, 
					posy,
					parentPage);
		} else if(context.draggable instanceof PreviousCommentLabel) {
			// If it is a previous comment then add a mark
			int pageno = parentPage.getPageNumber();
			PreviousCommentLabel lbl = (PreviousCommentLabel) context.draggable;
			long unixtime = System.currentTimeMillis() / 1000L;
			CommentMark mark = new CommentMark(
					posx, 
					posy, 
					pageno,
					MarkingInterface.markerid,
					unixtime,
					"");
			mark.setRawtext(lbl.getText());
			EMarkingWeb.markingInterface.addMark(mark, parentPage);
		}
		
		throw new VetoDragException();
	}
}
