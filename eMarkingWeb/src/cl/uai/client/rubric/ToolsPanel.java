/**
 * 
 */
package cl.uai.client.rubric;

import cl.uai.client.EMarkingConfiguration;
import cl.uai.client.MarkingInterface;
import cl.uai.client.resources.Resources;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;

/**
 * @author Jorge
 *
 */
public class ToolsPanel extends Composite {
	
	/** Panel holding tools **/
	private TabPanel toolsPanel = null;
	/** Previous comments interface **/
	private PreviousCommentsInterface previousComments = null;
	/** General feedback **/
	private GeneralFeedbackInterface generalFeedback = null;
	/** Sorting pages interfaces **/
	private SortPagesInterface sortPages = null;
	
	private MarksSummaryInterface marksSummary = null;
	
	/** Scroll panel for managing a large number of comments **/
	private ScrollPanel scroll;

	/**
	 * @return the generalFeedback
	 */
	public GeneralFeedbackInterface getGeneralFeedback() {
		return generalFeedback;
	}

	/**
	 * @return the previousComments
	 */
	public PreviousCommentsInterface getPreviousComments() {
		return previousComments;
	}

	public ToolsPanel() {
		toolsPanel = new TabPanel();
		toolsPanel.addStyleName(Resources.INSTANCE.css().previouscomments());
		
		// Marking tools
		generalFeedback = new GeneralFeedbackInterface();

		previousComments = new PreviousCommentsInterface();
		previousComments.setVisible(!EMarkingConfiguration.isReadonly());

		if(!EMarkingConfiguration.isReadonly()) {
			toolsPanel.add(previousComments, MarkingInterface.messages.PreviousComments());
			toolsPanel.add(generalFeedback, MarkingInterface.messages.GeneralFeedback());
		}

		// Sorting pages
		if(EMarkingConfiguration.isSupervisor()) {
			sortPages = new SortPagesInterface();
			toolsPanel.add(sortPages, MarkingInterface.messages.SortPages());
		}

		marksSummary = new MarksSummaryInterface();
		if(EMarkingConfiguration.getMarkingType() != 5) {
			toolsPanel.add(marksSummary, MarkingInterface.messages.Score());
		}
		
		if(toolsPanel.getWidgetCount() > 0) {
			toolsPanel.selectTab(0);
		}
		
		scroll = new ScrollPanel();
		scroll.add(toolsPanel);

		this.initWidget(scroll);
	}
	
	public void loadSumissionData() {
		marksSummary.loadSubmissionData();
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		
		marksSummary.loadSubmissionData();
	}
	
	@Override
	public void setHeight(String height) {
		super.setHeight(height);
		 scroll.setHeight(height);
	}
}
