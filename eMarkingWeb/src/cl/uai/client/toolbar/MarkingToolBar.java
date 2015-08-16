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

import java.util.Map;
import java.util.logging.Logger;

import cl.uai.client.EMarkingComposite;
import cl.uai.client.EMarkingWeb;
import cl.uai.client.page.MarkingPage;
import cl.uai.client.MarkingInterface;
import cl.uai.client.data.AjaxData;
import cl.uai.client.data.AjaxRequest;
import cl.uai.client.data.SubmissionGradeData;
import cl.uai.client.marks.Mark;
import cl.uai.client.marks.RubricMark;
import cl.uai.client.resources.Resources;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * @author Jorge Villalon <villalon@gmail.com>
 * 
 *
 */
public class MarkingToolBar extends EMarkingComposite {

	Logger logger = Logger.getLogger(MarkingToolBar.class.getName());
	
	private HorizontalPanel buttonsPanel = null;
	private Label studentName = null;
	
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
	
	private ToggleButton chkContinue = null;

	private DraftGrade grade = null;

	/**
	 * Creates the interface
	 */
	public MarkingToolBar() {
		studentName = new Label();

		grade = new DraftGrade();

		markingButtons = new MarkingButtons();
		markingButtons.setVisible(false);

		Icon finishIcon = new Icon(IconType.CHECK);
		finishMarkingButton = new PushButton();
		finishMarkingButton.setHTML(finishIcon.toString());
		finishMarkingButton.setTitle(MarkingInterface.messages.FinishMarking());
		finishMarkingButton.addStyleName(Resources.INSTANCE.css().finishmarkingbutton());
		finishMarkingButton.setVisible(false);
		finishMarkingButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Count the total marks, rubric mark more toolbar mark
								int allRubricMark = 0;
								// If a rubric mark  is eliminated this becomes invisible
								// Count how many rubric marks have been removed
								int invisibleMark = 0;
								VerticalPanel pages = EMarkingWeb.markingInterface.getMarkingPagesInterface().getPagesPanel();
								for(int i = 0; i < pages.getWidgetCount() ; i++){
									Map<Integer, Mark> marks = ((MarkingPage) pages.getWidget(i)).getMarkWidgets();
									allRubricMark+= ((MarkingPage)pages.getWidget(i)).isHaveRubricMark();
				 					for(Mark m : marks.values()) {
				 						if(m instanceof RubricMark){
				 							if(!m.isVisible()){
												invisibleMark++;
				 							}
				 						}
				 					}
				 				}
			if( (allRubricMark > 0 && invisibleMark == 0) || (allRubricMark > invisibleMark && allRubricMark > 0)){				
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
											loadSubmissionData();
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
			}else{
				//It has not been corrected any questions
				Window.alert("You need to add one or more criteria in the rubric");
			}
			}
		});
		
		Icon icon = new Icon(IconType.SAVE);
		saveChangesButton = new PushButton();
		saveChangesButton.setHTML(icon.toString());
		saveChangesButton.setTitle(MarkingInterface.messages.SaveChangesClose());
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
							loadSubmissionData();
							return;
						} else {
							Window.alert(MarkingInterface.messages.NoMoreSubmissions());
							EMarkingWeb.markingInterface.finishLoading();							
						}
					}
				});
			}
		});
		
		Icon chkContinueIcon = new Icon(IconType.USER);
		chkContinue = new ToggleButton();
		chkContinue.setHTML(chkContinueIcon.toString());
		chkContinue.setTitle(MarkingInterface.messages.JumpToNextStudent());
		chkContinue.addStyleName(Resources.INSTANCE.css().jumptonext());
		chkContinue.setVisible(false);

		buttonsPanel = new HorizontalPanel();
		
		buttonsPanel.addStyleName(Resources.INSTANCE.css().buttonspanel());		
		buttonsPanel.add(markingButtons);
		buttonsPanel.setCellHorizontalAlignment(markingButtons, HasHorizontalAlignment.ALIGN_LEFT);
		buttonsPanel.setCellWidth(markingButtons, "48%");
		
		buttonsPanel.add(grade);
		buttonsPanel.setCellVerticalAlignment(grade, HasVerticalAlignment.ALIGN_MIDDLE);
		buttonsPanel.setCellHorizontalAlignment(grade, HasHorizontalAlignment.ALIGN_CENTER);
		buttonsPanel.setCellWidth(grade, "2%");
		
		HorizontalPanel hpanel = new HorizontalPanel();		
		hpanel.add(chkContinue);		
		hpanel.add(saveChangesButton);
		hpanel.add(finishMarkingButton);
				
		buttonsPanel.add(hpanel);
		buttonsPanel.setCellHorizontalAlignment(hpanel, HasHorizontalAlignment.ALIGN_RIGHT);
		buttonsPanel.setCellWidth(finishMarkingButton, "48%");
		
		this.initWidget(buttonsPanel);
	}

	/**
	 * Loads submission data when changed
	 */
	public void loadSubmissionData() {
		
		markingButtons.setVisible(true);
		saveChangesButton.setVisible(true);
		finishMarkingButton.setVisible(false);
		chkContinue.setVisible(true);
		
		buttonsPanel.setVisible(!MarkingInterface.readonly);
				
		SubmissionGradeData sdata = MarkingInterface.submissionData;
		
		if(!MarkingInterface.isStudentAnonymous()) {
			studentName.setText(sdata.getLastname() + ", " + sdata.getFirstname());
		} else {
			studentName.setText(MarkingInterface.messages.StudentN(MarkingInterface.messages.Anonymous()));
		}
		
		if(MarkingInterface.supervisor && !MarkingInterface.submissionData.isQualitycontrol()) {
			finishMarkingButton.setVisible(true);
		}
		
		this.grade.loadSubmissionData();
	}

	public void setButtonPressed(int index) {
		markingButtons.setButtonPressed(index, false);
	}	
}
