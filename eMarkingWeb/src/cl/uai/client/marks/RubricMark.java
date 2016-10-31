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

import java.util.Date;
import java.util.List;
import java.util.Map;

import cl.uai.client.EMarkingConfiguration;
import cl.uai.client.EMarkingWeb;
import cl.uai.client.MarkingInterface;
import cl.uai.client.data.AjaxData;
import cl.uai.client.data.AjaxRequest;
import cl.uai.client.data.Criterion;
import cl.uai.client.data.Level;
import cl.uai.client.data.SubmissionGradeData;
import cl.uai.client.marks.collaborative.*;
import cl.uai.client.resources.Resources;
import cl.uai.client.utils.Color;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;

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
	
	/** Collaboratives icons **/
	private LikeMark like = null;
	private DisLikeMark dislike = null;
	private QuoteMark quote = null;
	private DiscussionMark discussion = null;
	private HorizontalPanel collaborativeMarks = null;

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
		
		this.addStyleName(Resources.INSTANCE.css().rubricmark());

		this.setLevelId(lvlid);
		
		// Collaborative buttons
		if(EMarkingConfiguration.getMarkingType() == EMarkingConfiguration.EMARKING_TYPE_MARKER_TRAINING
				&& EMarkingConfiguration.isChatEnabled()){
			collaborativeMarks = new HorizontalPanel();
			
			like = new LikeMark();
			like.setMark(this);
			like.setValue(0);
			collaborativeMarks.add(like);
			
			dislike = new DisLikeMark();
			dislike.setMark(this);
			dislike.setValue(0);
			collaborativeMarks.add(dislike);
			
			quote = new QuoteMark();
			quote.setMark(this);
			quote.setValue(0);
			collaborativeMarks.add(quote);
			
			discussion = new DiscussionMark();
			discussion.setMark(this);
			discussion.setValue(0);
			collaborativeMarks.add(discussion);
			//TODO
			Date time = new Date (timecreated*1000L);	
			discussion.addMessage(time.toString(), markername, rawtext,markerid);
			
			collaborativeMarks.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			collaborativeMarks.addStyleName(Resources.INSTANCE.css().tablecollaborativebuttons());
			
			// The mark dont belongs me
			if(markerid != EMarkingConfiguration.getMarkerId()){
				like.setCanClick(true);				
				dislike.setCanClick(true);				
				quote.setCanClick(true);
				//discussion.setCanClick(true);
			}else{
				like.removeStyleName(Resources.INSTANCE.css().likeIcon());
				like.addStyleName(Resources.INSTANCE.css().mycolloborativeicon());
				
				dislike.removeStyleName(Resources.INSTANCE.css().likeIcon());
				dislike.addStyleName(Resources.INSTANCE.css().mycolloborativeicon());
				
				quote.removeStyleName(Resources.INSTANCE.css().likeIcon());
				quote.addStyleName(Resources.INSTANCE.css().mycolloborativeicon());
			}
			
			// get counter for collaborative buttons
			String url = "ids="+MarkingInterface.getDraftId()+"&commentid="+this.getId();
			AjaxRequest.ajaxRequest("action=getvaluescollaborativebuttons&"+url, new AsyncCallback<AjaxData>() {
				
				@Override
				public void onFailure(Throwable caught) {
					logger.warning("Error values collaboratives buttons");
					hideCollaborativeButtons();
				}
				
				@Override
				public void onSuccess(AjaxData result) {
					List<Map<String, String>> valuesCollaborativesButtons = AjaxRequest.getValuesFromResult(result);

					for(Map<String, String> value : valuesCollaborativesButtons) {
						
						int markerid = Integer.parseInt(value.get("markerid"));
						int type = Integer.parseInt(value.get("type"));
						String markername = value.get("markername");
						String date = value.get("date");
						String text = value.get("text");
						
						switch (type){
							case EMarkingConfiguration.EMARKING_COLLABORATIVE_BUTTON_LIKE:
								like.addValue(1);
								like.setFormat(markerid,markername);
								like.setMarkHTML();
								break;
								
							case EMarkingConfiguration.EMARKING_COLLABORATIVE_BUTTON_DISLIKE:
								dislike.addValue(1);
								dislike.setFormat(markerid,markername);
								dislike.setMarkHTML();
								break;
								
							case EMarkingConfiguration.EMARKING_COLLABORATIVE_BUTTON_QUOTE:
								quote.addValue(1);
								quote.setMarkHTML();
								quote.setFormat(markerid,markername);
								quote.setMarkHTML();
								break;
								
							case EMarkingConfiguration.EMARKING_COLLABORATIVE_BUTTON_DISCUSSION:
								discussion.addValue(1);
								discussion.setMarkHTML();
								discussion.setFormat(markerid,markername);
								discussion.setMarkHTML();
								String[] parts = date.split(" ");
								String[] hourMinute = parts[1].split(":");
								String realDate = hourMinute[0]+":"+hourMinute[1]+" &nbsp &nbsp"+parts[0];
								discussion.addMessage(realDate, markername, text, markerid);
								break;
						}				
					}
				}
			});
			like.setMarkHTML();
			dislike.setMarkHTML();
			quote.setMarkHTML();
			if(getMarkername() != null){
				discussion.instanceDialog();
			}
			discussion.setMarkHTML();
		}

	}

	@Override
	public void setMarkHTML() {
		// If the mark has a color, we use the background to color it
		if(this.criterionid > 0 && EMarkingConfiguration.isColoredRubric()) {
			Color.setWidgetFontHueColor(this.criterionid, this);
		}

		this.setHTML((new Icon(IconType.MAP_MARKER)).toString());		
	}
	
	public String getMarkPopupHTML() {
		// Starts with an empty HTML
		String html = "";
		boolean headerOnly = false;

		RubricMark rmark = (RubricMark) this;
		headerOnly = rmark.isHeaderOnly();

		String criterionindex = "" + EMarkingWeb.markingInterface.getRubricInterface().getRubricPanel().getCriterionIndex(rmark.getCriterionId());
		String leveldesc = headerOnly ? criterionindex : " " + rmark.getLevel().getCriterion().getDescription();
		
		Icon icon = new Icon(this.iconType);

		html += "<table class=\"markrubricheader\" style=\"background-color:hsl("+rmark.getLevel().getCriterion().getHue()+",100%,75%);\" width=\"100%\">"
				+"<tr><td style=\"text-align: left;\"><div class=\""+Resources.INSTANCE.css().markicon()+"\">" 
				+ icon.toString() + "</div></td><td><div class=\""+Resources.INSTANCE.css().markcrit()+"\">" + leveldesc + "</div></td>";
		html += "<td style=\"text-align: right;\" nowrap><div class=\""+Resources.INSTANCE.css().markpts()+"\">"
				+ RubricMark.scoreFormat(rmark.getLevel().getScore() + rmark.getLevel().getBonus(), false) 
				+ " / " + RubricMark.scoreFormat(rmark.getLevel().getCriterion().getMaxscore(), false)
				+"</div></td></tr></table>";
		if(!headerOnly) {
			// Show the level description
			html += "<div class=\""+Resources.INSTANCE.css().marklvl()+"\">" + rmark.getLevel().getDescription() 
					+ "</div>";
			html += "<div class=\""+Resources.INSTANCE.css().markrawtext()+"\">"+ SafeHtmlUtils.htmlEscape(this.getRawtext()) + "</div>";
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
					+ MarkingInterface.messages.Comment() + ": " + SafeHtmlUtils.htmlEscape(this.getRegradecomment())
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
		
		return html;
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
				URL.decode(mark.get("rawtext"))
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

	public float getBonus() {
		return this.bonus;
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
	
	public void addCollaborativesButtons() {
		
		if(collaborativeMarks == null)
			return;
		
		AbsolutePanel abspanel = (AbsolutePanel) this.getParent();
		int top = this.getAbsoluteTop();
		int left = this.getAbsoluteLeft();

		abspanel.add(collaborativeMarks,top,left);
		
		updatePositionCollaborativeButtons();
	}
	
	public void removeCollaborativeButtons(){
		if(collaborativeMarks == null)
			return;
		
		AbsolutePanel abspanel = (AbsolutePanel) this.getParent();
		abspanel.remove(collaborativeMarks);
	}
	
	public void hideCollaborativeButtons(){
		if(collaborativeMarks == null)
			return;
		
		collaborativeMarks.setVisible(false);
	}
	
	public void showCollaborativeButtons(){
		if(collaborativeMarks == null)
			return;
		
		collaborativeMarks.setVisible(true);
	}
	
	public void updatePositionCollaborativeButtons(){
		if(collaborativeMarks == null)
			return;
		
		AbsolutePanel abspanel = (AbsolutePanel) this.getParent();		
		abspanel.setWidgetPosition(collaborativeMarks,abspanel.getWidgetLeft(this),abspanel.getWidgetTop(this)+ this.height-10);
	}
	
	public void updateValueCollaborativeButtons(){
		
	}
	
}
