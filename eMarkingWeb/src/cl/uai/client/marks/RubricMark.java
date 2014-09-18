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

import cl.uai.client.EMarkingWeb;
import cl.uai.client.MarkingInterface;
import cl.uai.client.data.Criterion;
import cl.uai.client.data.Level;
import cl.uai.client.data.SubmissionGradeData;
import cl.uai.client.resources.Resources;

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
			int posx,
			int posy,
			int pageno,
			int markerid,
			int lvlid,
			long timecreated, 
			String colour) {
		super(posx, posy, pageno, markerid, timecreated, colour);
		
		// Rubric marks have format 2
		this.format = 2;

		this.setLevelId(lvlid);

		this.addStyleName(Resources.INSTANCE.css().rubricmark());
		
		// Sets the background image accordingly
		this.setBackground();
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
	 * Sets the inner HTML of the Mark
	 */
	@Override
	protected void setMarkHTML() {
		super.setMarkHTML();
		
		int index = EMarkingWeb.markingInterface.getRubricInterface().getRubricPanel().getCriterionIndex(this.getCriterionId());

		this.setHTML("<div class=\""+Resources.INSTANCE.css().innercomment()+"\">"+index+"</div>");
			
		
		this.setBackground();
	}
	
	/**
	 * Sets the background image according to the rubric level
	 */
	private void setBackground(){
		
		this.removeStyleName(Resources.INSTANCE.css().othermarker0());
		this.removeStyleName(Resources.INSTANCE.css().othermarker25());
		this.removeStyleName(Resources.INSTANCE.css().othermarker50());
		this.removeStyleName(Resources.INSTANCE.css().othermarker75());
		this.removeStyleName(Resources.INSTANCE.css().othermarker100());
		this.removeStyleName(Resources.INSTANCE.css().marker0());
		this.removeStyleName(Resources.INSTANCE.css().marker25());
		this.removeStyleName(Resources.INSTANCE.css().marker50());
		this.removeStyleName(Resources.INSTANCE.css().marker75());
		this.removeStyleName(Resources.INSTANCE.css().marker100());
		Level lvl = MarkingInterface.submissionData.getLevelById(levelid);
		if(this.isReadOnly()&&!MarkingInterface.readonly){
			this.addStyleName(this.getOtherBackgroundImage(
					lvl.getCriterion().getPercentForLevel(lvl)));
		}else{
			this.addStyleName(getBackgroundImage(
					lvl.getCriterion().getPercentForLevel(lvl)));
		}
	}
	
	/**
	 * Gets the image corresponding to a certain number from 0 to 120
	 * 
	 * @param percent the number from 0 to 120
	 * @return a CSS url image
	 */
	public static String getBackgroundImage(int percent) {
		
		if(percent <= 30) {			
			return Resources.INSTANCE.css().marker0();
		} else if(percent <= 60) {			
			return Resources.INSTANCE.css().marker25();
		} else if(percent <= 90) {			
			return Resources.INSTANCE.css().marker50();
		} else if(percent < 120) {			
			return Resources.INSTANCE.css().marker75();
		} else {			
			return Resources.INSTANCE.css().marker100();
		}		
	}
	
	/**
	 * Gets the image corresponding to a certain number from 0 to 120
	 * 
	 * @param percent the number from 0 to 120
	 * @return a CSS url image
	 */
	private String getOtherBackgroundImage(int percent) {
		
		if(percent <= 30) {			
			return Resources.INSTANCE.css().othermarker0();
		} else if(percent <= 60) {			
			return Resources.INSTANCE.css().othermarker25();
		} else if(percent <= 90) {			
			return Resources.INSTANCE.css().othermarker50();
		} else if(percent < 120) {			
			return Resources.INSTANCE.css().othermarker75();
		} else {			
			return Resources.INSTANCE.css().othermarker100();
		}		
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
				Integer.parseInt(mark.get("posx")), 
				Integer.parseInt(mark.get("posy")), 
				Integer.parseInt(mark.get("pageno")),
				Integer.parseInt(mark.get("markerid")),
				Integer.parseInt(mark.get("levelid")),
				Long.parseLong(mark.get("timecreated")),
				String.valueOf(mark.get("colour")));
		
		markobj.setId(Integer.parseInt(mark.get("id"))); 
		markobj.setRawtext(mark.get("rawtext"));
		markobj.setMarkername(mark.get("markername"));
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
			int levelid, float bonus, String regradecomment, int regradeaccepted) {
		super.update(newcomment, newposx, newposy, levelid, bonus, regradecomment, regradeaccepted);
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
