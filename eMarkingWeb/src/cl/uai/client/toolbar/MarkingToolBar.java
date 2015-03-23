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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
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
	private Label studentSelector = null;
	private HTML progressStatusHtml = new HTML("");
	private HTML progressPublishedHtml = new HTML("");
	private HTML agreeStatusHtml = new HTML("");
	
	/** Icon(arrow) to show and hide the middle of the toolbar, circle with statistical information **/
	private HorizontalPanel containerInformation = null;	
	private boolean visibilityToolbar = false;
	private  HTML containerIcon = new HTML("");
	private final Icon iconArrowDown = new Icon(IconType.CHEVRON_DOWN);
	private final Icon iconArrowUp = new Icon(IconType.CHEVRON_UP);
	private Image circleProgressCorrection = new Image();
	private Image circleProgressPublished = new Image();
	private Image circleAgreeStatus = new Image();
	
	/** Divs used in design **/
	private HTML divLeft = new HTML("");
	private HTML divMiddle = new HTML("");
	private HTML divRight = new HTML("&ensp;");
	
	/** Pop up to display statistical information  **/
	private MyPopup popUpProgressCorrection = new MyPopup();
	private MyPopup popUpProgressPublished = new MyPopup();
	private MyPopup popUpAgreeStatus = new MyPopup();
	
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
	private SubmissionGradeMini notes = null;

	/** Labels for submission info **/
	private Label courseName = null;
	private Label activityName = null;
	private Label lastSave = null;
	
	/**
	 * Popup 
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

		studentSelector = new Label();

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
		activityStudent.add(divRight);
		activityStudent.add(studentSelector);
		activityStudent.setCellVerticalAlignment(studentSelector, HasVerticalAlignment.ALIGN_MIDDLE);
		activityStudent.setCellHorizontalAlignment(studentSelector, HasHorizontalAlignment.ALIGN_LEFT);
		submissionPanel.add(activityStudent);
		submissionPanel.add(lastSave);
			
		VerticalPanel progressBarsPanel = new VerticalPanel();
		progressBarsPanel.add(progressStatusHtml);
		progressBarsPanel.add(progressPublishedHtml);
		progressBarsPanel.add(agreeStatusHtml);
		
		// Info label
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
		containerIcon.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				if(infoLabelPanel.isVisible()){
					infoLabelPanel.setVisible(false);
					visibilityToolbar = false;
			    	containerIcon.setHTML("<div style='font-size:2em;line-height: 20px; '>"+iconArrowDown.toString()+"</div>");
			    }else{
			    	visibilityToolbar = true;
			    	infoLabelPanel.setVisible(true);
					containerIcon.setHTML("<div style='font-size:2em;line-height: 20px;'>"+iconArrowUp.toString()+"</div>");
				}
			}
		});
		
		finishMarkingButton = new PushButton(MarkingInterface.messages.FinishMarking());
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
				// Verify that there is at least one rubric mark, that means the rubric was used
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
		
		chkContinue = new CheckBox(MarkingInterface.messages.JumpToNextStudent());
		chkContinue.addStyleName(Resources.INSTANCE.css().jumptonext());
		chkContinue.setVisible(false);

		buttonsPanel = new HorizontalPanel();
		
		buttonsPanel.addStyleName(Resources.INSTANCE.css().buttonspanel());		
		buttonsPanel.add(markingButtons);
		buttonsPanel.setCellHorizontalAlignment(markingButtons, HasHorizontalAlignment.ALIGN_LEFT);
		buttonsPanel.setCellWidth(markingButtons, "10%");
		
		// Add Arrow Icon
		buttonsPanel.add(containerIcon);
		buttonsPanel.setCellHorizontalAlignment(containerIcon, HasHorizontalAlignment.ALIGN_CENTER);
		buttonsPanel.setCellVerticalAlignment(containerIcon, HasVerticalAlignment.ALIGN_MIDDLE);
		buttonsPanel.setCellWidth(containerIcon, "38%");
		
		// Add statistical circles
		containerInformation = new HorizontalPanel();
		containerInformation.setHeight("24px");
		containerInformation.add(circleProgressCorrection);
		containerInformation.setCellHorizontalAlignment(circleProgressCorrection, HasHorizontalAlignment.ALIGN_RIGHT);
		containerInformation.setCellVerticalAlignment(circleProgressCorrection, HasVerticalAlignment.ALIGN_MIDDLE);
		containerInformation.setCellWidth(circleProgressCorrection,"20%");
		
		containerInformation.add(divMiddle);
		containerInformation.setCellWidth(divMiddle,"20%");
		
		containerInformation.add(circleProgressPublished);
		containerInformation.setCellHorizontalAlignment(circleProgressPublished, HasHorizontalAlignment.ALIGN_CENTER);
		containerInformation.setCellVerticalAlignment(circleProgressPublished, HasVerticalAlignment.ALIGN_MIDDLE);
		containerInformation.setCellWidth(circleProgressPublished,"20%");
		
		containerInformation.add(divLeft);
		containerInformation.setCellWidth(divLeft,"20%");
		
		containerInformation.add(circleAgreeStatus);
		containerInformation.setCellHorizontalAlignment(circleAgreeStatus, HasHorizontalAlignment.ALIGN_LEFT);
		containerInformation.setCellVerticalAlignment(circleAgreeStatus, HasVerticalAlignment.ALIGN_MIDDLE);
		containerInformation.setCellWidth(circleAgreeStatus,"20%");
		
		buttonsPanel.add(containerInformation);
		buttonsPanel.setCellHorizontalAlignment(containerInformation, HasHorizontalAlignment.ALIGN_CENTER);
		buttonsPanel.setCellVerticalAlignment(containerInformation, HasVerticalAlignment.ALIGN_MIDDLE);
		buttonsPanel.setCellWidth(containerInformation, "15%");

		buttonsPanel.add(notes);
		buttonsPanel.setCellVerticalAlignment(notes, HasVerticalAlignment.ALIGN_MIDDLE);
		buttonsPanel.setCellHorizontalAlignment(notes, HasHorizontalAlignment.ALIGN_CENTER);
		buttonsPanel.setCellWidth(notes, "4%");
		
		buttonsPanel.add(chkContinue);
		buttonsPanel.setCellHorizontalAlignment(chkContinue, HasHorizontalAlignment.ALIGN_RIGHT);
		buttonsPanel.setCellVerticalAlignment(chkContinue, HasVerticalAlignment.ALIGN_MIDDLE);
		buttonsPanel.setCellWidth(chkContinue, "10%");
		
		buttonsPanel.add(saveChangesButton);
		buttonsPanel.setCellHorizontalAlignment(saveChangesButton, HasHorizontalAlignment.ALIGN_RIGHT);
		buttonsPanel.setCellWidth(saveChangesButton, "5%");

		buttonsPanel.add(finishMarkingButton);
		buttonsPanel.setCellHorizontalAlignment(finishMarkingButton, HasHorizontalAlignment.ALIGN_RIGHT);
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
		progressStatusHtml.setHTML("EN CORRECCIÓN: <progress id='progressBar' value='"+MarkingInterface.getGeneralProgress()+"' max='100'></progress> "+MarkingInterface.getGeneralProgress()+"%");
		progressPublishedHtml.setHTML("PUBLICADAS: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<progress id='progressBar' value='"+MarkingInterface.getPublishedProgress()+"' max='100'></progress> "+MarkingInterface.getPublishedProgress()+"%");
		agreeStatusHtml.setHTML("NIVEL ACUERDO: &nbsp;<progress id='agreeBar' value='"+MarkingInterface.getGeneralAgree()+"' max='100'></progress> "+MarkingInterface.getGeneralAgree()+"%");
		
		if(!MarkingInterface.isAnonymous()) {
			this.courseName.setText(sdata.getCoursename());
			this.courseName.setTitle(sdata.getCourseshort());
			studentSelector.setText(sdata.getLastname() + ", " + sdata.getFirstname());
		} else {
			this.courseName.setText(MarkingInterface.messages.AnonymousCourse());
			studentSelector.setText(MarkingInterface.messages.StudentN(MarkingInterface.messages.Anonymous()));
		}
		this.activityName.setText(sdata.getActivityname());
		
		if(MarkingInterface.supervisor && !MarkingInterface.submissionData.isQualitycontrol()) {
			finishMarkingButton.setVisible(true);
		}
		
		this.submissionGrade.loadSubmissionData();
		this.notes.loadSubmissionData();
		
		
		markingButtons.loadCustomMarksButtons(sdata.getCustommarks());
		
		loadSubmissionTimeModified();
		
		if(visibilityToolbar){
			containerIcon.setHTML("<div style='font-size:2em;line-height: 20px;'>"+iconArrowUp.toString()+"</div>");
		}else{
			containerIcon.setHTML("<div style='font-size:2em;line-height: 20px;'>"+iconArrowDown.toString()+"</div>");
		}

		implementCircleCorrection();
		
		implementCirclePublished();
		
		implementCircleAgreeStatus();
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
	 * Implements the circle and the pop up that provides information about corrected tests
	 */
	private void implementCircleCorrection(){ 
		int percentageCircle = 0;
		// Number tens
		int tens = (int) (MarkingInterface.getGeneralProgress() - (int) (MarkingInterface.getGeneralProgress()/10));
		if(tens >= 5){
			percentageCircle= (int) MarkingInterface.getGeneralProgress() - tens;
		}else{
			percentageCircle= (int) MarkingInterface.getGeneralProgress() - tens - 10;
		}
		
		// Add image corresponding to the percentage
		switch (percentageCircle){
		
			case 0:  circleProgressCorrection.setResource(Resources.INSTANCE.por10());
				  break;
			case 10: circleProgressCorrection.setResource(Resources.INSTANCE.por20());
				  break;
			case 20: circleProgressCorrection.setResource(Resources.INSTANCE.por30());
				  break;
			case 30: circleProgressCorrection.setResource(Resources.INSTANCE.por40());
				  break;
			case 40: circleProgressCorrection.setResource(Resources.INSTANCE.por50());
				  break;
			case 50: circleProgressCorrection.setResource(Resources.INSTANCE.por60());
				  break;
			case 60: circleProgressCorrection.setResource(Resources.INSTANCE.por70());
				  break;
			case 70: circleProgressCorrection.setResource(Resources.INSTANCE.por80());
				  break;
			case 80: circleProgressCorrection.setResource(Resources.INSTANCE.por90());
				  break;
			case 90: circleProgressCorrection.setResource(Resources.INSTANCE.por100());
				  break;
			case 100: circleProgressCorrection.setResource(Resources.INSTANCE.por100());
				  break;
		}
		if(MarkingInterface.getGeneralProgress() == 0.00){
			circleProgressCorrection.setResource(Resources.INSTANCE.por0());
		}
		
		circleProgressCorrection.setHeight("2em");
		circleProgressCorrection.setWidth("2em");
		
		int progress = (int) (MarkingInterface.getGeneralProgress()*100);
		double progressValue = (progress/100);
		popUpProgressCorrection.setWidget(new Label("En corrección: "+ progressValue +"%"));
		circleProgressCorrection.addMouseMoveHandler(new MouseMoveHandler(){
			public void onMouseMove(MouseMoveEvent event){
				popUpProgressCorrection.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			          public void setPosition(int offsetWidth, int offsetHeight) {
			            int left = (int) ((Window.getClientWidth() - offsetWidth)*0.62);
			            int top = (int) ((Window.getClientHeight() - offsetHeight)*0.04);
			            popUpProgressCorrection.setPopupPosition(left, top);
			          }
			        });
				popUpProgressCorrection.show();
			}
		});
		circleProgressCorrection.addMouseOutHandler(new MouseOutHandler(){
			 public void onMouseOut(MouseOutEvent event){
				 popUpProgressCorrection.hide();
			 }
		});
	}
	
	/**
	 * Implements the circle to pop up that provides information about published tests
	 */
	private void implementCirclePublished(){
		int percentageCircle = 0;
		// Number tens
		int tens = (int) (MarkingInterface.getPublishedProgress() - (int) (MarkingInterface.getPublishedProgress()/10));
		if(tens >= 5){
			percentageCircle= (int) MarkingInterface.getPublishedProgress() - tens;
		}else{
			percentageCircle= (int) MarkingInterface.getPublishedProgress() - tens - 10;
		}
		
		// Add image corresponding to the percentage
		switch (percentageCircle){
		
			case 0:  circleProgressPublished.setResource(Resources.INSTANCE.por10());
				  break;
			case 10: circleProgressPublished.setResource(Resources.INSTANCE.por20());
				  break;
			case 20: circleProgressPublished.setResource(Resources.INSTANCE.por30());
				  break;
			case 30: circleProgressPublished.setResource(Resources.INSTANCE.por40());
				  break;
			case 40: circleProgressPublished.setResource(Resources.INSTANCE.por50());
				  break;
			case 50: circleProgressPublished.setResource(Resources.INSTANCE.por60());
				  break;
			case 60: circleProgressPublished.setResource(Resources.INSTANCE.por70());
				  break;
			case 70: circleProgressPublished.setResource(Resources.INSTANCE.por80());
				  break;
			case 80: circleProgressPublished.setResource(Resources.INSTANCE.por90());
				  break;
			case 90: circleProgressPublished.setResource(Resources.INSTANCE.por100());
				  break;
			case 100: circleProgressPublished.setResource(Resources.INSTANCE.por100());
				  break;
		}
		if(MarkingInterface.getPublishedProgress() == 0.00){
			circleProgressPublished.setResource(Resources.INSTANCE.por0());
		}
		circleProgressPublished.setHeight("2em");
		circleProgressPublished.setWidth("2em");
		
		int published = (int) (MarkingInterface.getPublishedProgress()*100);
		double publishedValue = (published/100);
 		popUpProgressPublished.setWidget(new Label("Publicadas: "+ publishedValue +"%"));
		circleProgressPublished.addMouseMoveHandler(new MouseMoveHandler(){
			public void onMouseMove(MouseMoveEvent event){
				popUpProgressPublished.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			          public void setPosition(int offsetWidth, int offsetHeight) {
			            int left = (int) ((Window.getClientWidth() - offsetWidth)*0.65);
			            int top = (int) ((Window.getClientHeight() - offsetHeight)*0.04);
			            popUpProgressPublished.setPopupPosition(left, top);
			          }
			        });
				popUpProgressPublished.show();
			}
		});
		circleProgressPublished.addMouseOutHandler(new MouseOutHandler(){
			 public void onMouseOut(MouseOutEvent event){
				 popUpProgressPublished.hide();
			 }
		});
	}
	
	/**
	 * Implements the circle to pop up that provides information about the level of agreement on correction
	 */
	private void implementCircleAgreeStatus(){
		int percentageCircle = 0;
		// Number tens
		int tens = (int) (MarkingInterface.getGeneralAgree() - (int) (MarkingInterface.getGeneralAgree()/10));
		if(tens >= 5){
			percentageCircle= (int) MarkingInterface.getGeneralAgree() - tens;
		}else{
			percentageCircle= (int) MarkingInterface.getGeneralAgree() - tens - 10;
		}
		
		// Add image corresponding to the percentage
		switch (percentageCircle){
				
			case 0:  circleAgreeStatus.setResource(Resources.INSTANCE.por10());
					break;
			case 10: circleAgreeStatus.setResource(Resources.INSTANCE.por20());
					break;
			case 20: circleAgreeStatus.setResource(Resources.INSTANCE.por30());
					break;
			case 30: circleAgreeStatus.setResource(Resources.INSTANCE.por40());
					break;
			case 40: circleAgreeStatus.setResource(Resources.INSTANCE.por50());
					break;
			case 50: circleAgreeStatus.setResource(Resources.INSTANCE.por60());
					break;
			case 60: circleAgreeStatus.setResource(Resources.INSTANCE.por70());
					break;
			case 70: circleAgreeStatus.setResource(Resources.INSTANCE.por80());
					break;
			case 80: circleAgreeStatus.setResource(Resources.INSTANCE.por90());
					break;
			case 90: circleAgreeStatus.setResource(Resources.INSTANCE.por100());
					break;
			case 100: circleAgreeStatus.setResource(Resources.INSTANCE.por100());
					break;
			}
			if(MarkingInterface.getGeneralAgree() == 0.00){
				circleAgreeStatus.setResource(Resources.INSTANCE.por0());
			}		
			circleAgreeStatus.setHeight("2em");
			circleAgreeStatus.setWidth("2em");
			
			int generalAgree = (int) (MarkingInterface.getGeneralAgree()*100);
			double generalAgreeValue = (generalAgree/100);
			popUpAgreeStatus.setWidget(new Label("Nivel Acuerdo: "+ generalAgreeValue +"%"));
			circleAgreeStatus.addMouseMoveHandler(new MouseMoveHandler(){
				public void onMouseMove(MouseMoveEvent event){
					popUpAgreeStatus.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
				          public void setPosition(int offsetWidth, int offsetHeight) {
				            int left = (int) ((Window.getClientWidth() - offsetWidth)*0.71);
				            int top = (int) ((Window.getClientHeight() - offsetHeight)*0.04);
				            popUpAgreeStatus.setPopupPosition(left, top);
				          }
				        });
					popUpAgreeStatus.show();
				}
			});
			circleAgreeStatus.addMouseOutHandler(new MouseOutHandler(){
				 public void onMouseOut(MouseOutEvent event){
					 popUpAgreeStatus.hide();
				 }
			});
	}
}
