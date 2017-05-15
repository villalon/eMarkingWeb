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
 * 				   Hans C. Jeria <hansj@live.cl>
 * @license   http://www.gnu.org/copyleft/gpl.html GNU GPL v3 or later
 */
package cl.uai.client.toolbar;

import java.util.logging.Logger;

import cl.uai.client.EMarkingComposite;
import cl.uai.client.EMarkingConfiguration;
import cl.uai.client.MarkingInterface;
import cl.uai.client.data.SubmissionGradeData;
import cl.uai.client.resources.Resources;
import cl.uai.client.toolbar.buttons.ChatButtons;
import cl.uai.client.toolbar.buttons.ExamButtons;
import cl.uai.client.toolbar.buttons.HelpButtons;
import cl.uai.client.toolbar.buttons.MarkingButtons;
import cl.uai.client.toolbar.buttons.ViewButtons;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TabPanel;


/**
 * @author Jorge Villalon <villalon@gmail.com>
 * 
 *
 */
public class MarkingToolBar extends EMarkingComposite {

	Logger logger = Logger.getLogger(MarkingToolBar.class.getName());
	
	private HorizontalPanel buttonsPanel = null;
	
	/** Buttons to select commands **/
	private MarkingButtons markingButtons = null;

	private ExamButtons examButtons = null;
	
	private ExamButtons extraButtons = null;
	
	private ViewButtons viewButtons = null;
	
	private ChatButtons chatButtons = null;
	
	private HelpButtons helpButtons = null;
	
	/** Tab panel with the set of buttons **/
	private TabPanel tabButtonsPanel = null;
	/**
	 * @return the rubricButtons
	 */
	public MarkingButtons getMarkingButtons() {
		return markingButtons;
	}
	
	private DraftGrade grade = null;

	/**
	 * Creates the interface
	 */
	public MarkingToolBar() {

		logger.fine("Entering MarkingToolbar");

		grade = new DraftGrade();

		markingButtons = new MarkingButtons();
		markingButtons.setVisible(false);

		examButtons = new ExamButtons(true);
		examButtons.setVisible(true);
		
		extraButtons = new ExamButtons(false);
		extraButtons.setVisible(true);
		
		viewButtons = new ViewButtons();
		viewButtons.setVisible(false);
		
		chatButtons = new ChatButtons();
		chatButtons.setVisible(false);
		
		helpButtons = new HelpButtons();
		helpButtons.setVisible(false);

		tabButtonsPanel = new TabPanel();
		tabButtonsPanel.addStyleName(Resources.INSTANCE.css().tabbuttons());
		
		tabButtonsPanel.add(markingButtons, MarkingInterface.messages.Mark().toUpperCase());
		tabButtonsPanel.add(examButtons, MarkingInterface.messages.Exam().toUpperCase());
		tabButtonsPanel.add(viewButtons, MarkingInterface.messages.View().toUpperCase());
		tabButtonsPanel.add(chatButtons, MarkingInterface.messages.Collaboration().toUpperCase());
		tabButtonsPanel.add(helpButtons, MarkingInterface.messages.Help().toUpperCase());

		buttonsPanel = new HorizontalPanel();
		buttonsPanel.setVisible(false);
		
		buttonsPanel.addStyleName(Resources.INSTANCE.css().buttonspanel());		
		buttonsPanel.add(tabButtonsPanel);
		buttonsPanel.setCellHorizontalAlignment(markingButtons, HasHorizontalAlignment.ALIGN_LEFT);
		buttonsPanel.setCellWidth(markingButtons, "50%");
		
		SubmissionGradeData sdata = MarkingInterface.submissionData;
		
		if(sdata != null 
				&& EMarkingConfiguration.getMarkingType() == EMarkingConfiguration.EMARKING_TYPE_MARKER_TRAINING
				&& EMarkingConfiguration.isChatEnabled()) {
			buttonsPanel.add(new HTML("Hola, acá habran estadisticas de corrección"));
			buttonsPanel.setCellWidth(markingButtons, "30%");
		}
		
		buttonsPanel.add(grade);
		buttonsPanel.setCellVerticalAlignment(grade, HasVerticalAlignment.ALIGN_MIDDLE);
		buttonsPanel.setCellHorizontalAlignment(grade, HasHorizontalAlignment.ALIGN_RIGHT);
		buttonsPanel.setCellWidth(grade, "10%");
		
		extraButtons = new ExamButtons(false);
		extraButtons.getFinishMarkingButton().setVisible(true);
		extraButtons.getSaveAndJumpToNextButton().setVisible(true);
		extraButtons.getSaveChangesButton().setVisible(true);
		buttonsPanel.add(extraButtons);
		buttonsPanel.setCellVerticalAlignment(extraButtons, HasVerticalAlignment.ALIGN_MIDDLE);
		buttonsPanel.setCellHorizontalAlignment(extraButtons, HasHorizontalAlignment.ALIGN_RIGHT);
		buttonsPanel.setCellWidth(extraButtons, "40%");
		
		this.initWidget(buttonsPanel);
	}

	/**
	 * Loads submission data when changed
	 */
	public void loadSubmissionData() {
		
		this.markingButtons.loadSubmissionData();
		this.examButtons.loadSubmissionData();
		this.extraButtons.loadSubmissionData();
		this.chatButtons.loadSubmissionData();
		this.viewButtons.loadSubmissionData();
		this.helpButtons.loadSubmissionData();
		this.grade.loadSubmissionData();
		
		buttonsPanel.setVisible(true);
		
		if(EMarkingConfiguration.isReadonly()) {
			tabButtonsPanel.remove(markingButtons);
			tabButtonsPanel.remove(examButtons);
			tabButtonsPanel.remove(chatButtons);
		}
		
		if(!EMarkingConfiguration.isChatEnabled()) {
			tabButtonsPanel.remove(chatButtons);
		}

		if(EMarkingConfiguration.isFormativeFeedbackOnly()) {
			buttonsPanel.remove(grade);
			tabButtonsPanel.remove(examButtons);
		}
		tabButtonsPanel.selectTab(0);
	}

	public void setButtonPressed(int index) {
		markingButtons.setButtonPressed(index, false);
	}

	public ViewButtons getViewButtons() {
		return this.viewButtons;
	}

	public ChatButtons getChatButtons() {
		return this.chatButtons;
	}

	public DraftGrade getGrade() {
		return this.grade;
	}
	
	public void setSelectedTab(int idx) {
		this.tabButtonsPanel.selectTab(idx);
	}
}
