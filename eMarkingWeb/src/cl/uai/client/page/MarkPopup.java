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

import cl.uai.client.MarkingInterface;
import cl.uai.client.marks.Mark;
import cl.uai.client.marks.RubricMark;
import cl.uai.client.resources.Resources;

import com.google.gwt.user.client.ui.HTML;

/**
 * A MarkPopup is  a popup which is shown when the user moves the mouse
 * over a Mark. It indicates details such as the Mark's inner comment, author
 * and score.
 * 
 * @author Jorge Villalón <villalon@gmail.com>
 *
 */
public class MarkPopup extends HTML {

	private static Logger logger = Logger.getLogger(MarkPopup.class.getName());
	
	/** The mark related to the popup **/
	private Mark mark;
	
	/**
	 * Constructor setting css style and empty HTML
	 */
	public MarkPopup() {
		super("&nbsp;");
		this.addStyleName(Resources.INSTANCE.css().markpopup());
	}

	/**
	 * Sets the mark to be shown
	 * 
	 * @param mark current mark
	 */
	public void setMark(Mark mark) {
		if(mark == null) {
			logger.warning("Invalid null mark passed to setMark");
			return;
		}
		
		this.mark = mark;

		// Starts with an empty HTML
		String html = "";
		
		// If it's a RubricMark add score header and both rubric and criterion descriptions
		if(mark instanceof RubricMark) {
			RubricMark rmark = (RubricMark) mark;
			html += "<table style=\"background-color:hsl("+rmark.getLevel().getCriterion().getHue()+",100%,75%);\" width=\"100%\">"
					+"<tr><td style=\"text-align: left;\"><div class=\""+Resources.INSTANCE.css().markcrit()+"\">" 
					+ rmark.getLevel().getCriterion().getDescription() + "</div></td>";
			html += "<td style=\"text-align: right;\" nowrap><div class=\""+Resources.INSTANCE.css().markpts()+"\">"
					+ RubricMark.scoreFormat(rmark.getLevel().getScore() + rmark.getLevel().getBonus(), false) 
					+ " / " + RubricMark.scoreFormat(rmark.getLevel().getCriterion().getMaxscore(), false)
					+"</div></td></tr></table>";
			html += "<div class=\""+Resources.INSTANCE.css().marklvl()+"\">" + rmark.getLevel().getDescription() 
					+ "</div>";
		}
		
		// If the inner comment contains something
		if(this.mark.getRawtext().trim().length() > 0) {
			html += "<div class=\""+Resources.INSTANCE.css().markrawtext()+"\">"+ this.mark.getRawtext() + "</div>";
		}
		
		// Show the marker's name if the marking process is not anonymous
		if(!MarkingInterface.isAnonymous()&&(!mark.isReadOnly())) {
			html += "<div class=\""+Resources.INSTANCE.css().markmarkername()+"\">"+ MarkingInterface.messages.MarkerDetails(this.mark.getMarkername()) + "</div>";
		}
		
		if(mark instanceof RubricMark && ((RubricMark)mark).getRegradeid() > 0) {
			html += "<div style=\"background-color:yellow\">"+"Recorrección"
					+ (((RubricMark)mark).getRegradeaccepted() == 0 ? " solicitada" : " lista")
					+"</div>";
			html += "<div class=\""+Resources.INSTANCE.css().marklvl()+"\">" 
					+ "Motivo: " + ((RubricMark)mark).getRegradeMotiveText() + "<hr>" 
					+ "Comentario: " + ((RubricMark)mark).getRegradecomment()
					+ (((RubricMark)mark).getRegradeaccepted() == 0 ? "" : "<hr>Respuesta: " + (((RubricMark)mark).getRegrademarkercomment()))
					+ "</div>";
		}

		this.setHTML(html);
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
	}
}
