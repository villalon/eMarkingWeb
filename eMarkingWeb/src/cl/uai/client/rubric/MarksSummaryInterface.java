/**
 * 
 */
package cl.uai.client.rubric;

import cl.uai.client.MarkingInterface;
import cl.uai.client.data.Criterion;
import cl.uai.client.marks.RubricMark;
import cl.uai.client.resources.Resources;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Jorge
 *
 */
public class MarksSummaryInterface extends Composite {

	private VerticalPanel mainPanel = null;
	
	public MarksSummaryInterface() {
		mainPanel = new VerticalPanel();
		mainPanel.addStyleName(Resources.INSTANCE.css().rubricpanel());
		
		this.initWidget(mainPanel);
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		
	}
	
	public void loadSubmissionData() {
		mainPanel.clear();

		float totalscore = 0;
		float totalmaxscore = 0;
		int rows = 0;
		for(int criterionid : MarkingInterface.submissionData.getRubricfillings().keySet()) {
			rows++;
			Criterion criterion = MarkingInterface.submissionData.getRubricfillings().get(criterionid);
			HorizontalPanel hpanel = new HorizontalPanel();
			hpanel.addStyleName(Resources.INSTANCE.css().rubricrow());
			if(rows % 2 == 0) {
				hpanel.addStyleName(Resources.INSTANCE.css().oddrow());
			}
			hpanel.setWidth("100%");
			Label lbl = new Label(criterion.getDescription());
			lbl.addStyleName(Resources.INSTANCE.css().criterionheader());
			hpanel.add(lbl);
			hpanel.setCellWidth(lbl, "50%");
			float score = criterion.getBonus();
			if(criterion.getSelectedLevel() != null) {
				score += criterion.getSelectedLevel().getScore();				
			}
			float maxscore = criterion.getMaxscore();
			totalscore += score;
			totalmaxscore += maxscore;
			lbl = new Label(RubricMark.scoreFormat(score, false) + " / " + RubricMark.scoreFormat(maxscore, false));
			hpanel.add(lbl);
			hpanel.setCellWidth(lbl, "50%");
			hpanel.setCellHorizontalAlignment(lbl, HasAlignment.ALIGN_CENTER);
			mainPanel.add(hpanel);
		}
		mainPanel.add(new HTML("<hr>"));
		HorizontalPanel hpanel = new HorizontalPanel();
		hpanel.addStyleName(Resources.INSTANCE.css().rubricrow());
		hpanel.setWidth("100%");
		Label lbl = new Label("Total");
		lbl.addStyleName(Resources.INSTANCE.css().criterionheader());
		hpanel.add(lbl);
		hpanel.setCellWidth(lbl, "50%");
		lbl = new Label(RubricMark.scoreFormat(totalscore, false) + " / " + RubricMark.scoreFormat(totalmaxscore, false));
		lbl.addStyleName(Resources.INSTANCE.css().criterionheader());
		hpanel.add(lbl);
		hpanel.setCellWidth(lbl, "50%");
		hpanel.setCellHorizontalAlignment(lbl, HasAlignment.ALIGN_CENTER);
		mainPanel.add(hpanel);		
	}
}
