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

import java.util.Map;

import cl.uai.client.EMarkingConfiguration;
import cl.uai.client.EMarkingWeb;
import cl.uai.client.MarkingInterface;
import cl.uai.client.data.Criterion;
import cl.uai.client.data.Level;
import cl.uai.client.data.SubmissionGradeData;
import cl.uai.client.resources.Resources;
import cl.uai.client.utils.Color;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.i18n.client.NumberFormat;

/**
 * A RubricMark represents a mark on a student page which contains
 * a rubric level (a judgement) which may have a bonus modifier
 * 
 * @author Jorge Villalón <villalon@gmail.com>
 *
 */
public class RubricMark extends Mark { 

	/** The rubric level indicating its criterion and level in the rubric **/
	private int levelid = 0;
	private float bonus;
	private int regradeid = 0;
	private String regradecomment = null;
	private int regrademotive = 0;
	private int regradeaccepted = 0;
	private String regrademarkercomment = null;
	private boolean headerOnly = false;

	public boolean isHeaderOnly() {
		return headerOnly;
	}

	public void setHeaderOnly(boolean headerOnly) {
		this.headerOnly = headerOnly;
	}

	public String getRegrademarkercomment() {
		return regrademarkercomment;
	}

	public void setRegrademarkercomment(String regrademarkercomment) {
		this.regrademarkercomment = regrademarkercomment;
	}

	/**
	 * Creates a new RubricMark at a specific position in a page
	 * 
	 * @param posx X coordinate in the page
	 * @param posy Y coordinate in the page
	 * @param pageno page number (from 1 to N)
	 * @param rubricLevel the rubric level indicating criterion and level in the rubric
	 */
	public RubricMark(
			int id,
			int posx,
			int posy,
			int pageno,
			int markerid,
			int lvlid,
			long timecreated,
			int criterionid,
			String markername,
			String rawtext) {
		super(id, posx, posy, pageno, markerid, timecreated, criterionid, markername, rawtext);
		
		// Rubric marks have format 2
		this.format = 2;
		this.iconType = IconType.TH;

		this.addStyleName(Resources.INSTANCE.css().markpopup());
		
		this.setLevelId(lvlid);
	}

	@Override
	public void setMarkHTML() {
		
			// Starts with an empty HTML
			String html = "";
			boolean headerOnly = false;
			
				RubricMark rmark = (RubricMark) this;
				headerOnly = rmark.isHeaderOnly();
				
				Icon icon = new Icon(this.iconType);
				
				html += "<table class=\"markrubricheader\" style=\"background-color:hsl("+rmark.getLevel().getCriterion().getHue()+",100%,75%);\" width=\"100%\">"
						+"<tr><td style=\"text-align: left;\"><div class=\""+Resources.INSTANCE.css().markicon()+"\">" 
						+ icon.toString() + "</div></td><td><div class=\""+Resources.INSTANCE.css().markcrit()+"\">" + rmark.getLevel().getCriterion().getDescription() + "</div></td>";
				html += "<td style=\"text-align: right;\" nowrap><div class=\""+Resources.INSTANCE.css().markpts()+"\">"
						+ RubricMark.scoreFormat(rmark.getLevel().getScore() + rmark.getLevel().getBonus(), false) 
						+ " / " + RubricMark.scoreFormat(rmark.getLevel().getCriterion().getMaxscore(), false)
						+"</div></td></tr></table>";
				if(!headerOnly)
				html += "<div class=\""+Resources.INSTANCE.css().marklvl()+"\">" + rmark.getLevel().getDescription() 
						+ "</div>";
			
			if(!headerOnly) {
				html += "<div class=\""+Resources.INSTANCE.css().markrawtext()+"\">"+ this.getRawtext() + "</div>";
				// Show the marker's name if the marking process is not anonymous
				if(!EMarkingConfiguration.isMarkerAnonymous()) {
					html += "<div class=\""+Resources.INSTANCE.css().markmarkername()+"\">"+ MarkingInterface.messages.MarkerDetails(this.getMarkername()) + "</div>";
				}
			}
			
			
			if(this.getRegradeid() > 0 && !headerOnly) {
				html += "<div style=\"background-color:#FFFF99; width:99%; font-size: 10pt;\" class=\""+Resources.INSTANCE.css().markcrit()+"\">"+ MarkingInterface.messages.Regrade()
						+ " " + MarkingInterface.messages.Requested()
						+"</div>";
				html += "<div class=\""+Resources.INSTANCE.css().marklvl()+"\">" 
						+ MarkingInterface.messages.Motive() + ": " + this.getRegradeMotiveText() + "<hr>" 
						+ MarkingInterface.messages.Comment() + ": " + this.getRegradecomment()
						+ "</div>";
				if(this.getRegradeaccepted() > 0) {
				html += "<div style=\"background-color:#FFFF99; width:99%; font-size: 10pt;\" class=\""+Resources.INSTANCE.css().markcrit()+"\">"+ MarkingInterface.messages.Regrade()
						+ " " +  MarkingInterface.messages.Replied()
						+"</div>";
				html += "<div class=\""+Resources.INSTANCE.css().marklvl()+"\">"
						+ "<hr>"+MarkingInterface.messages.RegradeReply()+": " + this.getRegrademarkercomment()
						+ "</div>";
				}
			}
			
			// If the mark has a color, we use the background to color it
			if(this.criterionid > 0 && EMarkingConfiguration.isColoredRubric()) {
				Color.setWidgetBackgroundHueColor(this.criterionid, this);
			}
			
			this.setHTML(html);		
		}
	
	/**
	 * Gets the rubric level of the RubricMark
	 * @return a rubric level
	 */
	public int getLevelId() {
		return levelid;
	}

	/**
	 * Formats a score, removing zeroes to the right if necessary.
	 * It might include its sign (+ or -). 
	 * 
	 * @param score the score to format
	 * @param sign include the sign
	 * @return the formatted score as string
	 */
	public static String scoreFormat(double score, boolean sign) {
		return getNumberFormat(sign).format(score);
	}
	
	/**
	 * Parses a formatted float value
	 * 
	 * @param sign
	 * @return
	 */
	public static NumberFormat getNumberFormat(boolean sign) {
		String format = sign ? "+#.##;-#" : "#.##";
		return NumberFormat.getFormat(format);
	}
			
	/**
	 * Creates a RubricMark from a Hash with Strings as key value pairs,
	 * parsing the values in the map and casting them to the proper
	 * types
	 * 
	 * @param mark the Hash
	 * @return a RubricMark object
	 */
	public static RubricMark createFromMap(Map<String, String> mark) {
		RubricMark markobj = new RubricMark(
				Integer.parseInt(mark.get("id")),
				Integer.parseInt(mark.get("posx")), 
				Integer.parseInt(mark.get("posy")), 
				Integer.parseInt(mark.get("pageno")),
				Integer.parseInt(mark.get("markerid")),
				Integer.parseInt(mark.get("levelid")),
				Long.parseLong(mark.get("timecreated")),
				Integer.parseInt(mark.get("criterionid")),
				mark.get("markername"),
				mark.get("rawtext")
				);
		
		markobj.setRegradeid(Integer.parseInt(mark.get("regradeid")));
		markobj.setRegradecomment(mark.get("regradecomment"));
		markobj.setRegrademotive(Integer.parseInt(mark.get("motive")));
		markobj.setRegradeaccepted(Integer.parseInt(mark.get("regradeaccepted")));
		markobj.setRegrademarkercomment(mark.get("regrademarkercomment"));
				
		return markobj;
	}
	
	public int getRegradeid() {
		return regradeid;
	}

	public void setRegradeid(int regradeid) {
		this.regradeid = regradeid;
	}

	public String getRegradecomment() {
		return regradecomment;
	}

	public void setRegradecomment(String regradecomment) {
		this.regradecomment = regradecomment;
	}

	@Override
	public void setLoading() {
		super.setLoading();
		
		EMarkingWeb.markingInterface.getRubricInterface().getRubricPanel().loadingRubricCriterion(levelid);
	}
	
	/**
	 * Updates the mark's basic data: comment and position 
	 */
	@Override
	public void update(String newcomment, int newposx, int newposy,
			int levelid, float bonus, String regradecomment, int regradeaccepted, int widthPage, int heightPage) {
		
		super.update(newcomment, newposx, newposy, levelid, bonus, regradecomment, regradeaccepted, widthPage, heightPage);
		Criterion criterion = MarkingInterface.submissionData.getLevelById(levelid).getCriterion();
		EMarkingWeb.markingInterface.getRubricInterface().getRubricPanel().updateRubricCriterion(
				criterion.getId(), 
				bonus, 
				criterion.getHue(), 
				levelid, 
				regradeaccepted, 
				regradeaccepted);
	}

	public Level getLevel() {
		return MarkingInterface.submissionData.getLevelById(levelid);
	}
	
	public int getCriterionId() {
		if(getLevel() != null) {
			return getLevel().getCriterion().getId();
		}
		return 0;
	}
	/**
	 * Changes the rubric level for this rubric
	 * @param newlevel
	 */
	public void setLevelId(int levelid) {
		this.levelid = levelid;
	}

	public void setBonus(float newbonus) {
		this.bonus = newbonus;
		this.getLevel().setBonus(this.bonus);
	}
	
	public int getRegrademotive() {
		return regrademotive;
	}
	
	public String getRegradeMotiveText() {
		return SubmissionGradeData.getRegradeMotiveText(regrademotive);
	}

	public void setRegrademotive(int regrademotive) {
		this.regrademotive = regrademotive;
	}

	public int getRegradeaccepted() {
		return regradeaccepted;
	}

	public void setRegradeaccepted(int regradeaccepted) {
		this.regradeaccepted = regradeaccepted;
	}
}
