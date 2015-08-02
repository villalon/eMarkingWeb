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

import java.util.Date;
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
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
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
	
	/** Toolbar main panels, a vertical containing student selector and info **/
	private VerticalPanel mainPanel = null;

	private HorizontalPanel infoLabelPanel = null;
	private HorizontalPanel buttonsPanel = null;
	private Label studentName = null;
	

	/** Icon(arrow) to show and hide the middle of the toolbar, circle with statistical information **/
	private HorizontalPanel containerInformation = null;
	private boolean visibilityToolbar = false;
	private HTML containerIcon = null;
	private Icon iconArrowDown = null;
	private Icon iconArrowUp = null;
	private Image circleProgressCorrection = null;
	private Image circleProgressPublished = null;
	private Image circleAgreeStatus = null;
	
	/** Divs used in design **/
	private HTML divLeft = null;
	private HTML divMiddle = null;
	private HTML divRight = null;
	
	/** Pop up to display statistical information  **/
	private MyPopup popUpProgressCorrection = null;
	private MyPopup popUpProgressPublished = null;
	private MyPopup popUpAgreeStatus = null;
	
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

	/**
	 * @return the studentSelector
	 */
	public Label getStudentSelector() {
		return studentName;
	}

	private SubmissionGrade submissionGrade = null;
	private SubmissionGradeMini notes = null;

	/** Labels for submission info **/
	private Label courseName = null;
	private Label activityName = null;
	private Label lastSave = null;
	
	/**
	 * Popup with statistical data
	 */
	private static class MyPopup extends PopupPanel {
	    public MyPopup() {
	      super(true);
	    }
	  }	
	
	/**
	 * Creates the interface
	 */
	public MarkingToolBar() {
		mainPanel = new VerticalPanel();
		mainPanel.addStyleName(Resources.INSTANCE.css().loadingtoolbar());

		studentName = new Label();

		submissionGrade = new SubmissionGrade();
		notes = new SubmissionGradeMini();

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
		HorizontalPanel activityStudent = new HorizontalPanel();
		activityStudent.add(activityName);
		activityStudent.setCellVerticalAlignment(activityName, HasVerticalAlignment.ALIGN_MIDDLE);
		activityStudent.setCellHorizontalAlignment(activityName, HasHorizontalAlignment.ALIGN_RIGHT);
		divRight = new HTML("&ensp;");
		activityStudent.add(divRight);
		activityStudent.add(studentName);
		activityStudent.setCellVerticalAlignment(studentName, HasVerticalAlignment.ALIGN_MIDDLE);
		activityStudent.setCellHorizontalAlignment(studentName, HasHorizontalAlignment.ALIGN_LEFT);
		submissionPanel.add(activityStudent);
		submissionPanel.add(lastSave);
		
		infoLabelPanel = new HorizontalPanel();
		infoLabelPanel.addStyleName(Resources.INSTANCE.css().toolbarinfo());
		infoLabelPanel.setVisible(false);
		infoLabelPanel.add(coursenamePanel);
		infoLabelPanel.setCellHorizontalAlignment(coursenamePanel, HasHorizontalAlignment.ALIGN_CENTER);
		infoLabelPanel.setCellWidth(coursenamePanel,"20%");
		infoLabelPanel.add(submissionPanel);
		infoLabelPanel.add(submissionGrade);
		infoLabelPanel.setCellHorizontalAlignment(submissionGrade, HasHorizontalAlignment.ALIGN_RIGHT);
		mainPanel.add(infoLabelPanel);
		// Implement show and hide the middle of the rubric, capture event clic in the arrow
		containerIcon = new HTML("");
		containerIcon.addStyleName(Resources.INSTANCE.css().iconArrow());
		iconArrowDown = new Icon(IconType.CHEVRON_DOWN);
		iconArrowUp = new Icon(IconType.CHEVRON_UP);
		containerIcon.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				if(infoLabelPanel.isVisible()){
					infoLabelPanel.setVisible(false);
					visibilityToolbar = false;
					containerIcon.setHTML("<div>"+iconArrowDown.toString()+"</div>");
			    }else{
			    	visibilityToolbar = true;
			    	infoLabelPanel.setVisible(true);
			    	containerIcon.setHTML("<div>"+iconArrowUp.toString()+"</div>");
				}
			}
		});
		
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
		buttonsPanel.setCellWidth(markingButtons, "10%");
		
		buttonsPanel.add(containerIcon);
		buttonsPanel.setCellHorizontalAlignment(containerIcon, HasHorizontalAlignment.ALIGN_CENTER);
		buttonsPanel.setCellVerticalAlignment(containerIcon, HasVerticalAlignment.ALIGN_MIDDLE);
		buttonsPanel.setCellWidth(containerIcon, "38%");
		
		containerInformation = new HorizontalPanel();
		containerInformation.setHeight("24px");
		circleProgressCorrection = new Image();
		containerInformation.add(circleProgressCorrection);
		containerInformation.setCellHorizontalAlignment(circleProgressCorrection, HasHorizontalAlignment.ALIGN_RIGHT);
		containerInformation.setCellVerticalAlignment(circleProgressCorrection, HasVerticalAlignment.ALIGN_MIDDLE);
		containerInformation.setCellWidth(circleProgressCorrection,"20%");
		
		divMiddle = new HTML("");
		containerInformation.add(divMiddle);
		containerInformation.setCellWidth(divMiddle,"20%");
		
		circleProgressPublished = new Image();
		containerInformation.add(circleProgressPublished);
		containerInformation.setCellHorizontalAlignment(circleProgressPublished, HasHorizontalAlignment.ALIGN_CENTER);
		containerInformation.setCellVerticalAlignment(circleProgressPublished, HasVerticalAlignment.ALIGN_MIDDLE);
		containerInformation.setCellWidth(circleProgressPublished,"20%");
		
		divLeft =  new HTML("");
		divLeft.setVisible(false);
		containerInformation.add(divLeft);
		containerInformation.setCellWidth(divLeft,"20%");
		
		circleAgreeStatus = new Image();
		circleAgreeStatus.setVisible(false);
		containerInformation.add(circleAgreeStatus);
		containerInformation.setCellHorizontalAlignment(circleAgreeStatus, HasHorizontalAlignment.ALIGN_LEFT);
		containerInformation.setCellVerticalAlignment(circleAgreeStatus, HasVerticalAlignment.ALIGN_MIDDLE);
		containerInformation.setCellWidth(circleAgreeStatus,"20%");
		
/*		buttonsPanel.add(containerInformation);
		buttonsPanel.setCellHorizontalAlignment(containerInformation, HasHorizontalAlignment.ALIGN_CENTER);
		buttonsPanel.setCellVerticalAlignment(containerInformation, HasVerticalAlignment.ALIGN_MIDDLE);
		buttonsPanel.setCellWidth(containerInformation, "15%");
*/
		buttonsPanel.add(notes);
		buttonsPanel.setCellVerticalAlignment(notes, HasVerticalAlignment.ALIGN_MIDDLE);
		buttonsPanel.setCellHorizontalAlignment(notes, HasHorizontalAlignment.ALIGN_CENTER);
		//buttonsPanel.setCellWidth(notes, "4%");
		
		HorizontalPanel hpanel = new HorizontalPanel();		
		hpanel.add(chkContinue);		
		hpanel.add(saveChangesButton);
		hpanel.add(finishMarkingButton);
				
		buttonsPanel.add(hpanel);
		buttonsPanel.setCellHorizontalAlignment(hpanel, HasHorizontalAlignment.ALIGN_RIGHT);
		buttonsPanel.setCellWidth(finishMarkingButton, "5%");
		
		mainPanel.add(buttonsPanel);		
		this.initWidget(mainPanel);
	}

	/**
	 * Loads submission data when changed
	 */
	public void loadSubmissionData() {
		mainPanel.removeStyleName(Resources.INSTANCE.css().loadingtoolbar());
		mainPanel.addStyleName(Resources.INSTANCE.css().toolbar());
		
		infoLabelPanel.setVisible(visibilityToolbar);
		markingButtons.setVisible(true);
		saveChangesButton.setVisible(true);
		finishMarkingButton.setVisible(false);
		chkContinue.setVisible(true);
		
		buttonsPanel.setVisible(!MarkingInterface.readonly);
				
		SubmissionGradeData sdata = MarkingInterface.submissionData;
		
		//TODO onload set progress bars values
		//progressId: progressBar, pId: progressBarNum
		//agreeId: agreeBar, pId: agreeBarNum

		if(!MarkingInterface.isStudentAnonymous()) {
			this.courseName.setText(sdata.getCoursename());
			this.courseName.setTitle(sdata.getCourseshort());
			studentName.setText(sdata.getLastname() + ", " + sdata.getFirstname());
		} else {
			this.courseName.setText(MarkingInterface.messages.AnonymousCourse());
			studentName.setText(MarkingInterface.messages.StudentN(MarkingInterface.messages.Anonymous()));
		}
		this.activityName.setText(sdata.getActivityname());
		
		if(MarkingInterface.supervisor && !MarkingInterface.submissionData.isQualitycontrol()) {
			finishMarkingButton.setVisible(true);
		}
		
		this.submissionGrade.loadSubmissionData();
		this.notes.loadSubmissionData();
		this.markingButtons.loadSubmissionData();

		loadSubmissionTimeModified();
		containerIcon.addStyleName(Resources.INSTANCE.css().iconArrow());
		if(visibilityToolbar){
			containerIcon.setHTML("<div>"+iconArrowUp.toString()+"</div>");
		}else{
			containerIcon.setHTML("<div>"+iconArrowDown.toString()+"</div>");
		}
		
		popUpProgressCorrection = new MyPopup();
		implementStatisticalCircle(circleProgressCorrection, MarkingInterface.getGeneralProgress(), MarkingInterface.messages.StatusGrading(), popUpProgressCorrection, 0.62, 0.04);
		
		popUpProgressPublished = new MyPopup();
		implementStatisticalCircle(circleProgressPublished, MarkingInterface.getPublishedProgress(), MarkingInterface.messages.Published(), popUpProgressPublished, 0.65, 0.04);

		popUpAgreeStatus = new MyPopup();
		implementStatisticalCircle(circleAgreeStatus, MarkingInterface.getGeneralAgree(), MarkingInterface.messages.AgreeStatus(), popUpAgreeStatus, 0.71, 0.04);
		
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
	public HorizontalPanel getInfoLabel(){
		return infoLabelPanel;
	}
		/**
		 * 	
		 * @param circle: object Image
		 * @param data: double with information statistical
		 * @param text: String for MyPopup message
		 * @param popUp: Object MyPopup for statistical circles
		 * @param posx: int position axis x
		 * @param posy: int position axis y
		 */
		private void implementStatisticalCircle(Image circle, double data, String text, final MyPopup popUp, final double posx, final double posy){
			int percentageCircle = 0;
			// Number tens
			int tens = (int) (data - (int) (data/10)*10);
			if(tens >= 5){
				percentageCircle= (int) data - tens;
			}else{
				percentageCircle= (int) data - tens - 10;
			}
			
			// Add image corresponding to the percentage
			switch (percentageCircle){
					
				case 0:  circle.setResource(Resources.INSTANCE.percentage10());
						break;
				case 10: circle.setResource(Resources.INSTANCE.percentage20());
						break;
				case 20: circle.setResource(Resources.INSTANCE.percentage30());
						break;
				case 30: circle.setResource(Resources.INSTANCE.percentage40());
						break;
				case 40: circle.setResource(Resources.INSTANCE.percentage50());
						break;
				case 50: circle.setResource(Resources.INSTANCE.percentage60());
						break;
				case 60: circle.setResource(Resources.INSTANCE.percentage70());
						break;
				case 70: circle.setResource(Resources.INSTANCE.percentage80());
						break;
				case 80: circle.setResource(Resources.INSTANCE.percentage90());
						break;
				case 90: circle.setResource(Resources.INSTANCE.percentage100());
						break;
				case 100: circle.setResource(Resources.INSTANCE.percentage100());
						break;
				}
				if(data == 0.00){
					circle.setResource(Resources.INSTANCE.percentage0());
				}
				
				circle.addStyleName(Resources.INSTANCE.css().statisticalCircle());
				
				int percentaje = (int) (data*100);
				double dataPercentaje = (percentaje/100);
				popUp.setWidget(new Label(text +": "+ dataPercentaje +"%"));
				circle.addMouseMoveHandler(new MouseMoveHandler(){
					public void onMouseMove(MouseMoveEvent event){
						popUp.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
					          public void setPosition(int offsetWidth, int offsetHeight) {
					            int left = (int) ((Window.getClientWidth() - offsetWidth) * posx);
					            int top = (int) ((Window.getClientHeight() - offsetHeight) * posy);
					            popUp.setPopupPosition(left, top);
					          }
					        });
						popUp.show();
					}
				});
				circle.addMouseOutHandler(new MouseOutHandler(){
					 public void onMouseOut(MouseOutEvent event){
						 popUp.hide();
					 }
				});
		}
	
}
