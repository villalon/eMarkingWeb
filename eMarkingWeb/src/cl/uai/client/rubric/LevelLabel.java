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
package cl.uai.client.rubric;

import cl.uai.client.MarkingInterface;
import cl.uai.client.data.Level;
import cl.uai.client.marks.RubricMark;
import cl.uai.client.resources.Resources;

import com.google.gwt.user.client.ui.HTML;

/**
 * Represents a rubric level in the rubric interface
 * 
 * @author Jorge Villalón <villalon@gmail.com>
 *
 */
public class LevelLabel extends HTML {


	
	private Level lvl;
	private boolean regradeRequested = false;
	private String regradeComment = null;
	
	/**
	 * Creates a rubric level label
	 */
	public LevelLabel(int lvlid) {
		lvl = MarkingInterface.submissionData.getLevelById(lvlid);
		this.updateHtml();
	}
	
	public boolean isRegradeRequested() {
		return regradeRequested;
	}

	public void setRegradeRequested(boolean regradeRequested) {
		this.regradeRequested = regradeRequested;
	}

	public String getRegradeComment() {
		return regradeComment;
	}

	public void setRegradeComment(String regradeComment) {
		this.regradeComment = regradeComment;
	}

	public void updateHtml() {
		if(lvl != null) {
			String bonusHtml = "";
			if(lvl.getCriterion().getBonus() != 0
					&& lvl.getCriterion().getSelectedLevel() != null
					&& lvl.getCriterion().getSelectedLevel() == lvl) {
				bonusHtml = " " + RubricMark.scoreFormat(lvl.getCriterion().getBonus(), true);
			}
			String regradeCommentHtml = "";
			if(this.regradeRequested) {
				regradeCommentHtml += "<hr>" +
						"<span style=\"font-weight: bold;\">" + MarkingInterface.messages.RegradeComment() + "</span><hr>" +
						this.regradeComment;
			}

			this.setHTML("<div class=\"" + Resources.INSTANCE.css().leveldesc() + "\">" 
					+ lvl.getDescription() + "</div><div class=\""
					+ Resources.INSTANCE.css().levelpts() + "\">" 
					+ RubricMark.scoreFormat(lvl.getScore(), false)
					+ bonusHtml
					+ " pts</div><div class=\""+ Resources.INSTANCE.css().regradecomment()+"\">"
					+ regradeCommentHtml
					+ "</div>");
		}
	}
	/**
	 * @return the rubric level
	 */
	public int getLevelId() {
		return lvl.getId();
	}
}
