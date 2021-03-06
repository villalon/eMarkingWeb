/**
 * 
 */
package cl.uai.client.rubric;

import cl.uai.client.EMarkingConfiguration;
import cl.uai.client.MarkingInterface;
import cl.uai.client.resources.Resources;

import com.google.gwt.user.client.ui.Composite;
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
	/** Sorting pages interfaces **/
	private SortPagesInterface sortPages = null;
	
	private MarksSummaryInterface marksSummary = null;
	
	private ChangeLogInterface changeLogInterface = null;
	
	/**
	 * @return the previousComments
	 */
	public PreviousCommentsInterface getPreviousComments() {
		return previousComments;
	}

	public ToolsPanel() {
		toolsPanel = new TabPanel();
		toolsPanel.addStyleName(Resources.INSTANCE.css().previouscomments());
		
		previousComments = new PreviousCommentsInterface();
		previousComments.setVisible(!EMarkingConfiguration.isReadonly());

		if(!EMarkingConfiguration.isReadonly()) {
			toolsPanel.add(previousComments, MarkingInterface.messages.PreviousComments());
		}

		marksSummary = new MarksSummaryInterface();
		if(EMarkingConfiguration.getMarkingType() != EMarkingConfiguration.EMARKING_TYPE_PRINT_SCAN
				&& !EMarkingConfiguration.isFormativeFeedbackOnly()) {
			toolsPanel.add(marksSummary, MarkingInterface.messages.Score());
		}
		
		changeLogInterface = new ChangeLogInterface();
		if(EMarkingConfiguration.isChangeLogEnabled()) {
			toolsPanel.add(changeLogInterface, MarkingInterface.messages.ChangeLog());
		}
		
		// Sorting pages
		if(EMarkingConfiguration.isSupervisor()) {
			sortPages = new SortPagesInterface();
			toolsPanel.add(sortPages, MarkingInterface.messages.SortPages());
		}

		if(toolsPanel.getWidgetCount() > 0) {
			toolsPanel.selectTab(0);
		}
		
		this.initWidget(toolsPanel);
	}
	
	public void loadSumissionData() {
		marksSummary.loadSubmissionData();
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		
		marksSummary.loadSubmissionData();
	}
}
