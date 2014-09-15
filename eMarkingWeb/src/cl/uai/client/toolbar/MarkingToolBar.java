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
package cl.uai.client.toolbar;

import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import cl.uai.client.EMarkingComposite;
import cl.uai.client.EMarkingWeb;
import cl.uai.client.MarkingInterface;
import cl.uai.client.data.AjaxData;
import cl.uai.client.data.AjaxRequest;
import cl.uai.client.data.SubmissionGradeData;
import cl.uai.client.resources.Resources;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * @author Jorge Villalon <villalon@gmail.com>
 *
 */
public class MarkingToolBar extends EMarkingComposite {

	Logger logger = Logger.getLogger(MarkingToolBar.class.getName());
	
	/** Toolbar main panels, a vertical containing student selector and info **/
	private VerticalPanel mainPanel = null;

	private HorizontalPanel infoLabelPanel = null;
	private HorizontalPanel buttonsPanel = null;
	private Label studentSelector = null;
	
	/** Buttons to select commands **/
	private MarkingButtons markingButtons = null;

	/**
	 * @return the rubricButtons
	 */
	public MarkingButtons getMarkingButtons() {
		return markingButtons;
	}
	
	private PushButton finishMarkingButton = null;

	private PushButton saveChangesButton = null;
	
	private CheckBox chkContinue = null;

	/**
	 * @return the studentSelector
	 */
	public Label getStudentSelector() {
		return studentSelector;
	}

	private SubmissionGrade submissionGrade = null;

	/** Labels for submission info **/
	private Label courseName = null;
	private Label activityName = null;
	private Label lastSave = null;
	
	
	/**
	 * Creates the interface
	 */
	public MarkingToolBar() {
		mainPanel = new VerticalPanel();
		mainPanel.addStyleName(Resources.INSTANCE.css().loadingtoolbar());

		studentSelector = new Label();

		submissionGrade = new SubmissionGrade();

		markingButtons = new MarkingButtons();
		markingButtons.setVisible(false);

		courseName = new Label();
		courseName.addStyleName(Resources.INSTANCE.css().coursename());
		
		HTML emarkingLogo = new HTML("eMarking");
		emarkingLogo.addStyleName(Resources.INSTANCE.css().logo());
		HTML buildnumber = new HTML("&nbsp;&nbsp;v" + MarkingInterface.geteMarkingVersion());
		buildnumber.addStyleName(Resources.INSTANCE.css().lastsave());
		HorizontalPanel hpanelLogo = new HorizontalPanel();
		hpanelLogo.add(emarkingLogo);
		hpanelLogo.add(buildnumber);
		hpanelLogo.setCellVerticalAlignment(buildnumber, HasVerticalAlignment.ALIGN_BOTTOM);
		
		VerticalPanel coursenamePanel = new VerticalPanel();
		coursenamePanel.add(hpanelLogo);
		coursenamePanel.add(courseName);

		activityName = new Label();
		activityName.addStyleName(Resources.INSTANCE.css().activityname());

		lastSave = new Label();
		lastSave.addStyleName(Resources.INSTANCE.css().lastsave());

		VerticalPanel submissionPanel = new VerticalPanel();
		submissionPanel.add(activityName);
		submissionPanel.add(studentSelector);
		submissionPanel.add(lastSave);
		
		// Info label
		infoLabelPanel = new HorizontalPanel();
		infoLabelPanel.addStyleName(Resources.INSTANCE.css().toolbarinfo());
		infoLabelPanel.setVisible(false);
		infoLabelPanel.add(coursenamePanel);
		infoLabelPanel.add(submissionPanel);
		infoLabelPanel.add(submissionGrade);
		infoLabelPanel.setCellHorizontalAlignment(submissionGrade, HasHorizontalAlignment.ALIGN_RIGHT);
		
		mainPanel.add(infoLabelPanel);
		
		finishMarkingButton = new PushButton(MarkingInterface.messages.FinishMarking());
		finishMarkingButton.addStyleName(Resources.INSTANCE.css().finishmarkingbutton());
		finishMarkingButton.setVisible(false);
		finishMarkingButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(MarkingInterface.submissionData != null && MarkingInterface.submissionData.getId() > 0) {
					
					FinishMarkingDialog finishDialog = new FinishMarkingDialog();
					finishDialog.setGeneralFeedback(EMarkingWeb.markingInterface.getRubricInterface().getToolsPanel().getGeneralFeedback().getFeedbackText());
					
					finishDialog.addCloseHandler(new CloseHandler<PopupPanel>() {						
						@Override
						public void onClose(CloseEvent<PopupPanel> event) {
							FinishMarkingDialog dialog = (FinishMarkingDialog) event.getSource();
							
							if(dialog.isCancelled())
								return;
							
							EMarkingWeb.markingInterface.addLoading(true);
							
							AjaxRequest.ajaxRequest("action=finishmarking"
									+ "&feedback=" + URL.encode(dialog.getGeneralFeedback()), 
									new AsyncCallback<AjaxData>() {
								
								@Override
								public void onSuccess(AjaxData result) {
									Map<String, String> values = AjaxRequest.getValueFromResult(result);
									
									if(!values.get("error").equals("")) {
										Window.alert(MarkingInterface.messages.ErrorFinishingEmarking());
										EMarkingWeb.markingInterface.finishLoading();
									} else {
										int nextsubmission = Integer.parseInt(values.get("nextsubmission"));
										if(nextsubmission > 0 && chkContinue.getValue()) {
											EMarkingWeb.markingInterface.finishLoading();
											MarkingInterface.setSubmissionId(nextsubmission);
											EMarkingWeb.markingInterface.loadSubmissionData();
											return;
										}
										logger.fine("Closing window");
										if(!EMarkingWeb.closeAndReload()) {
											EMarkingWeb.markingInterface.finishLoading();
											Window.alert(MarkingInterface.messages.FinishingMarkingSuccessfull());
										}
									}
								}
								
								@Override
								public void onFailure(Throwable caught) {
									Window.alert(MarkingInterface.messages.ErrorFinishingEmarking());
									EMarkingWeb.markingInterface.finishLoading();
								}
							});
						}
					});
					finishDialog.center();
				}
			}
		});
		
		saveChangesButton = new PushButton(MarkingInterface.messages.SaveChangesClose());
		saveChangesButton.addStyleName(Resources.INSTANCE.css().finishmarkingbutton());
		saveChangesButton.setVisible(false);
		saveChangesButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(!chkContinue.getValue())
					EMarkingWeb.closeAndReload();

				EMarkingWeb.markingInterface.addLoading(true);

				AjaxRequest.ajaxRequest("action=getnextsubmission", 
						new AsyncCallback<AjaxData>() {

					@Override
					public void onFailure(Throwable caught) {
						EMarkingWeb.markingInterface.finishLoading();
						Window.alert("Epic fail");
					}

					@Override
					public void onSuccess(AjaxData result) {
						Map<String, String> values = AjaxRequest.getValueFromResult(result);
						int nextsubmission = Integer.parseInt(values.get("nextsubmission"));
						if(nextsubmission > 0 && chkContinue.getValue()) {
							EMarkingWeb.markingInterface.finishLoading();
							MarkingInterface.setSubmissionId(nextsubmission);
							EMarkingWeb.markingInterface.loadSubmissionData();
							return;
						} else {
							Window.alert(MarkingInterface.messages.NoMoreSubmissions());
							EMarkingWeb.markingInterface.finishLoading();							
						}
					}

				});
			}
		});
		
		chkContinue = new CheckBox(MarkingInterface.messages.JumpToNextStudent());
		chkContinue.addStyleName(Resources.INSTANCE.css().jumptonext());
		chkContinue.setVisible(false);

		buttonsPanel = new HorizontalPanel();
		buttonsPanel.addStyleName(Resources.INSTANCE.css().buttonspanel());
		
		buttonsPanel.add(markingButtons);
		buttonsPanel.setCellHorizontalAlignment(markingButtons, HasHorizontalAlignment.ALIGN_LEFT);
		
		buttonsPanel.add(chkContinue);
		buttonsPanel.setCellHorizontalAlignment(chkContinue, HasHorizontalAlignment.ALIGN_RIGHT);
		buttonsPanel.setCellVerticalAlignment(chkContinue, HasVerticalAlignment.ALIGN_MIDDLE);
		buttonsPanel.setCellWidth(chkContinue, "150px");
		
		buttonsPanel.add(saveChangesButton);
		buttonsPanel.setCellHorizontalAlignment(saveChangesButton, HasHorizontalAlignment.ALIGN_RIGHT);
		buttonsPanel.setCellWidth(saveChangesButton, "1px");

		buttonsPanel.add(finishMarkingButton);
		buttonsPanel.setCellHorizontalAlignment(finishMarkingButton, HasHorizontalAlignment.ALIGN_RIGHT);
		buttonsPanel.setCellWidth(finishMarkingButton, "1px");
		
		mainPanel.add(buttonsPanel);
		
		this.initWidget(mainPanel);
	}

	/**
	 * Loads submission data when changed
	 */
	public void loadSubmissionData() {
		mainPanel.removeStyleName(Resources.INSTANCE.css().loadingtoolbar());
		mainPanel.addStyleName(Resources.INSTANCE.css().toolbar());
		
		infoLabelPanel.setVisible(true);
		markingButtons.setVisible(true);
		saveChangesButton.setVisible(true);
		finishMarkingButton.setVisible(false);
		chkContinue.setVisible(true);
		
		buttonsPanel.setVisible(!MarkingInterface.readonly);
		
		
		SubmissionGradeData sdata = MarkingInterface.submissionData;
		
		
		if(!MarkingInterface.isAnonymous()) {
			this.courseName.setText(sdata.getCoursename());
			this.courseName.setTitle(sdata.getCourseshort());
			studentSelector.setText(sdata.getLastname() + ", " + sdata.getFirstname());
		} else {
			this.courseName.setText(MarkingInterface.messages.AnonymousCourse());
			studentSelector.setText(MarkingInterface.messages.StudentN(MarkingInterface.messages.Anonymous()));
		}
		this.activityName.setText(sdata.getActivityname());
		
		if(MarkingInterface.supervisor) {
			finishMarkingButton.setVisible(true);
		}
		
		this.submissionGrade.loadSubmissionData();
		
		markingButtons.loadCustomMarksButtons(sdata.getCustommarks());
		
		loadSubmissionTimeModified();
	}

	/**
	 * Loads the last modification time
	 */
	public void loadSubmissionTimeModified() {
		Date lastSaveDate = null;

		if(MarkingInterface.submissionData.getDatemodified() != null) {
			lastSaveDate = MarkingInterface.submissionData.getDatemodified();
		} else if(MarkingInterface.submissionData.getDatecreated() != null) {
			lastSaveDate = MarkingInterface.submissionData.getDatecreated();
		}

		String message = null;
		if(lastSaveDate == null) {
			message = " " + MarkingInterface.messages.Never();
		} else {
			message = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM).format(lastSaveDate);				
		}
		// Timer for pinging system
		Timer timer = new Timer() {
			@Override
			public void run() {
				lastSave.removeStyleName(Resources.INSTANCE.css().lastsaveanim());
			}
		};

		lastSave.addStyleName(Resources.INSTANCE.css().lastsaveanim());
		lastSave.setText(MarkingInterface.messages.LastSaved(message));
		
		timer.schedule(3000);
	}
	
	public void setButtonPressed(int index) {
		markingButtons.setButtonPressed(index, false);
	}
}
