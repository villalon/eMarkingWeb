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

import cl.uai.client.EMarkingComposite;
import cl.uai.client.EMarkingWeb;
import cl.uai.client.MarkingInterface;
import cl.uai.client.data.Criterion;
import cl.uai.client.marks.RubricMark;
import cl.uai.client.resources.Resources;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author Jorge Villalón <villalon@gmail.com>
 *
 */
public class CriterionHeader extends EMarkingComposite {

	/** Header main panel **/
	private VerticalPanel mainPanel = null;
	private int criterionId;
	private int index;
	private String criterionDescription;
	private HTML criterionMarker = null;
	private HTML bonusHtml = null;
	private HTML regradeHtml = null;
	private HTML loadingIcon = null;
	private int commentId = 0;
	private int commentPage = 0;
	private int backGroundPercent = 0;
	private int regradeid = 0;
	public int getRegradeid() {
		return regradeid;
	}

	public void setRegradeData(int regradeid, int regradeaccepted) {
		this.regradeid = regradeid;
		this.regradeaccepted = regradeaccepted;
		String html = "<div class=\""+Resources.INSTANCE.css().criterionheaderbonus()+"\">";
		if(this.regradeid > 0) {
			if(this.regradeaccepted == 0)
				html += "Por recorregir";
			else
				html += "Recorregida";
		}
		html += "</div>";
		this.regradeHtml.setHTML(html);
	}

	public int getRegradeaccepted() {
		return regradeaccepted;
	}

	private int regradeaccepted = 0;
	
	public CriterionHeader(int idx, int cid, String cdesc, float b, int regrid, int regraccepted) {
		this.mainPanel = new VerticalPanel();
		this.mainPanel.addStyleName(Resources.INSTANCE.css().criterionrow());
		this.criterionId = cid;
		this.criterionDescription = cdesc;
		this.index = idx;
		
		Label lbl = new Label(cdesc);
		lbl.addStyleName(Resources.INSTANCE.css().criterionheader());
		
		mainPanel.add(lbl);
		
		
		criterionMarker = new HTML();
		criterionMarker.addStyleName(Resources.INSTANCE.css().rubricmark());
		criterionMarker.setVisible(false);
		criterionMarker.setHTML("<div class=\""+Resources.INSTANCE.css().innercomment()+"\">"+index+"</div>");
		criterionMarker.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				if(commentId > 0 && commentPage > 0)
					EMarkingWeb.markingInterface.getMarkingPagesInterface().highlightRubricMark(commentId, commentPage);
			}
		});
		mainPanel.add(criterionMarker);
		
		bonusHtml = new HTML();
		setBonus(b);
		
		mainPanel.add(bonusHtml);
		
		regradeHtml = new HTML();
		regradeHtml.setVisible(false);
		
		mainPanel.add(regradeHtml);
		
		this.setRegradeData(regrid, regraccepted);

		loadingIcon = new HTML();
		loadingIcon.setVisible(false);
		Icon iconloading = new Icon(IconType.COG);
		loadingIcon.setHTML(iconloading.toString());
		loadingIcon.addStyleName(Resources.INSTANCE.css().loadingicon());
		loadingIcon.addStyleName("icon-spin");

		mainPanel.add(loadingIcon);
		mainPanel.setCellHorizontalAlignment(loadingIcon, HasHorizontalAlignment.ALIGN_CENTER);
		
		initWidget(mainPanel);
	}

	public int getCriterionId() {
		return criterionId;
	}

	public int getIndex() {
		return index;
	}
	
	public void setBonus(float b) {
		float score = 0;
		Criterion criterion = MarkingInterface.submissionData.getRubricfillings().get(criterionId);
		if(criterion.getSelectedLevel() != null) {
			score += criterion.getSelectedLevel().getScore() + criterion.getSelectedLevel().getBonus();
		}
		String html = "<div class=\""+Resources.INSTANCE.css().criterionheaderbonus()+"\">"+
				RubricMark.scoreFormat(score, false) + " / " + RubricMark.scoreFormat(criterion.getMaxscore(), false);
		html += "</div>";
		this.bonusHtml.setHTML(html);
	}

	public String getCriterionDescription() {
		return criterionDescription;
	}

	public void setMarkerVisible(boolean visible) {
		criterionMarker.setVisible(visible);
		bonusHtml.setVisible(visible);
		regradeHtml.setVisible(visible);
		if(this.regradeid == 0)
			regradeHtml.setVisible(false);
	}

	public void setCommentId(int commentid) {
		this.commentId = commentid;
	}
	
	public int getCommentId() {
		return this.commentId;
	}

	public void setCommentPage(int commentpage) {
		this.commentPage = commentpage;
	}
	
	public int getCommentPage() {
		return this.commentPage;
	}

	public int getBackGroundPercent() {
		return backGroundPercent;
	}

	public void setBackGroundPercent(int backGroundPercent) {
		this.backGroundPercent = backGroundPercent;
		this.criterionMarker.removeStyleName(Resources.INSTANCE.css().marker0());
		this.criterionMarker.removeStyleName(Resources.INSTANCE.css().marker25());
		this.criterionMarker.removeStyleName(Resources.INSTANCE.css().marker50());
		this.criterionMarker.removeStyleName(Resources.INSTANCE.css().marker75());
		this.criterionMarker.removeStyleName(Resources.INSTANCE.css().marker100());
		this.criterionMarker.addStyleName(RubricMark.getBackgroundImage(backGroundPercent));
	}

	public void setLoadingVisible(boolean visible) {
		this.loadingIcon.setVisible(visible);
	}
}
