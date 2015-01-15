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
import cl.uai.client.MarkingInterface;
import cl.uai.client.data.AjaxData;
import cl.uai.client.data.AjaxRequest;
import cl.uai.client.data.SubmissionGradeData;
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
	
	private HorizontalPanel infoPor = null;
	
	private final Icon icon = new Icon(IconType.CHEVRON_DOWN);
	private final Icon icon2 = new Icon(IconType.CHEVRON_UP);
	private  HTML aux = new HTML("");
	private Image circuloEnCorreccion = new Image();
	private Image circuloPublicadas = new Image();
	private Image circuloNivelAcuerdo = new Image();
	private HTML espacio = new HTML("");
	private HTML espacio2 = new HTML("");
	private HTML espacio3 = new HTML("&ensp;");
	private MyPopup popEnCorreccion = new MyPopup();
	private MyPopup popPublicadas = new MyPopup();
	private MyPopup popNivelAcuerdo = new MyPopup();
	
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
	private SubmissionGradeMini notas = null;

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
	      //setWidget(new Label("En corrección: "+MarkingInterface.getGeneralProgress()));
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
		notas = new SubmissionGradeMini();

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
		activityStudent.add(espacio3);
		activityStudent.add(studentSelector);
		activityStudent.setCellVerticalAlignment(studentSelector, HasVerticalAlignment.ALIGN_MIDDLE);
		activityStudent.setCellHorizontalAlignment(studentSelector, HasHorizontalAlignment.ALIGN_LEFT);
		//submissionPanel.add(activityName);
		//submissionPanel.add(studentSelector);
		submissionPanel.add(activityStudent);
		submissionPanel.add(lastSave);
		
		//TODO AGREGAR LOS VALORES A LOS LABELS A TRAVES DEL MarkingInterface
		//Progress bars panel		
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
		//infoLabelPanel.add(progressBarsPanel);
		infoLabelPanel.add(submissionGrade);
		infoLabelPanel.setCellHorizontalAlignment(submissionGrade, HasHorizontalAlignment.ALIGN_RIGHT);
		
		//mainPanel.add(infoLabelPanel);
		
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
		buttonsPanel.setCellWidth(markingButtons, "10%");
		
		//FLECHA
		
		buttonsPanel.add(aux);
		buttonsPanel.setCellHorizontalAlignment(aux, HasHorizontalAlignment.ALIGN_CENTER);
		buttonsPanel.setCellVerticalAlignment(aux, HasVerticalAlignment.ALIGN_MIDDLE);
		buttonsPanel.setCellWidth(aux, "38%");
		
		//// BARRA DE PROGRESO
		infoPor = new HorizontalPanel();
		infoPor.setHeight("24px");
		infoPor.add(circuloEnCorreccion);
		infoPor.setCellHorizontalAlignment(circuloEnCorreccion, HasHorizontalAlignment.ALIGN_RIGHT);
		infoPor.setCellVerticalAlignment(circuloEnCorreccion, HasVerticalAlignment.ALIGN_MIDDLE);
		infoPor.setCellWidth(circuloEnCorreccion,"20%");
		
		infoPor.add(espacio2);
		infoPor.setCellWidth(espacio2,"20%");
		
		infoPor.add(circuloPublicadas);
		infoPor.setCellHorizontalAlignment(circuloPublicadas, HasHorizontalAlignment.ALIGN_CENTER);
		infoPor.setCellVerticalAlignment(circuloPublicadas, HasVerticalAlignment.ALIGN_MIDDLE);
		infoPor.setCellWidth(circuloPublicadas,"20%");
		
		infoPor.add(espacio);
		infoPor.setCellWidth(espacio,"20%");
		
		infoPor.add(circuloNivelAcuerdo);
		infoPor.setCellHorizontalAlignment(circuloNivelAcuerdo, HasHorizontalAlignment.ALIGN_LEFT);
		infoPor.setCellVerticalAlignment(circuloNivelAcuerdo, HasVerticalAlignment.ALIGN_MIDDLE);
		infoPor.setCellWidth(circuloNivelAcuerdo,"20%");
		
		buttonsPanel.add(infoPor);
		buttonsPanel.setCellHorizontalAlignment(infoPor, HasHorizontalAlignment.ALIGN_CENTER);
		buttonsPanel.setCellVerticalAlignment(infoPor, HasVerticalAlignment.ALIGN_MIDDLE);
		buttonsPanel.setCellWidth(infoPor, "15%");
		
		///
		//notas pequeñas
		buttonsPanel.add(notas);
		buttonsPanel.setCellHorizontalAlignment(notas, HasHorizontalAlignment.ALIGN_CENTER);
		buttonsPanel.setCellWidth(notas, "4%");
		
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
		
		infoLabelPanel.setVisible(true);
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
		
		if(MarkingInterface.supervisor) {
			finishMarkingButton.setVisible(true);
		}
		
		this.submissionGrade.loadSubmissionData();
		this.notas.loadSubmissionData();
		
		
		markingButtons.loadCustomMarksButtons(sdata.getCustommarks());
		
		loadSubmissionTimeModified();
		/// cargar las nuevas cosas de la barra
		
		// FLECHA
		aux.setHTML("<div style='font-size:2em;line-height: 20px;'>"+icon.toString()+"</div>");
		aux.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				//revisa si en mainPanel esta infoLabelPanel
				int h=0;
				for(int i=0; i<mainPanel.getWidgetCount() ;i++){
					if(infoLabelPanel == mainPanel.getWidget(i)){
						//esta la info
						h++;
					}
			    }
				if(h==1){
			    	infoLabelPanel.removeFromParent();
			    	aux.setHTML("<div style='font-size:2em;line-height: 20px;'>"+icon.toString()+"</div>");
			    }
				if(h==0){
					mainPanel.insert(infoLabelPanel, 0);
					aux.setHTML("<div style='font-size:2em;line-height: 20px;'>"+icon2.toString()+"</div>");
				}
			}
		});
		// Circulos Progreso
		//1. En corrección
		int trun = (int) (MarkingInterface.getGeneralProgress()/10);
		int h = (int) (MarkingInterface.getGeneralProgress()-trun*10);
		h= (int) MarkingInterface.getGeneralProgress() - h;
		switch (h){
		
			case 0: circuloEnCorreccion.setResource(Resources.INSTANCE.por10());
				  break;
			case 10: circuloEnCorreccion.setResource(Resources.INSTANCE.por20());
				  break;
			case 20: circuloEnCorreccion.setResource(Resources.INSTANCE.por30());
				  break;
			case 30: circuloEnCorreccion.setResource(Resources.INSTANCE.por40());
				  break;
			case 40: circuloEnCorreccion.setResource(Resources.INSTANCE.por50());
				  break;
			case 50: circuloEnCorreccion.setResource(Resources.INSTANCE.por60());
				  break;
			case 60: circuloEnCorreccion.setResource(Resources.INSTANCE.por70());
				  break;
			case 70: circuloEnCorreccion.setResource(Resources.INSTANCE.por80());
				  break;
			case 80: circuloEnCorreccion.setResource(Resources.INSTANCE.por90());
				  break;
			case 90: circuloEnCorreccion.setResource(Resources.INSTANCE.por100());
				  break;
			case 100: circuloEnCorreccion.setResource(Resources.INSTANCE.por100());
				  break;
		}
		if(MarkingInterface.getGeneralProgress() == 0.00){
			circuloEnCorreccion.setResource(Resources.INSTANCE.por0());
		}
		circuloEnCorreccion.setHeight("2em");
		circuloEnCorreccion.setWidth("2em");
		popEnCorreccion.setWidget(new Label("En corrección: "+MarkingInterface.getGeneralProgress()+"%"));
		circuloEnCorreccion.addMouseMoveHandler(new MouseMoveHandler(){
			public void onMouseMove(MouseMoveEvent event){
				popEnCorreccion.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			          public void setPosition(int offsetWidth, int offsetHeight) {
			            int left = (int) ((Window.getClientWidth() - offsetWidth)*0.62);
			            int top = (int) ((Window.getClientHeight() - offsetHeight)*0.04);
			            popEnCorreccion.setPopupPosition(left, top);
			          }
			        });
				popEnCorreccion.show();
			}
		});
		circuloEnCorreccion.addMouseOutHandler(new MouseOutHandler(){
			 public void onMouseOut(MouseOutEvent event){
				 popEnCorreccion.hide();
			 }
		});
		
		//2. Publicadas		
		trun = (int) (MarkingInterface.getPublishedProgress()/10);
		h = (int) (MarkingInterface.getPublishedProgress()-trun*10);
		h= (int) MarkingInterface.getPublishedProgress() - h;
		switch (h){
		
			case 0:  circuloPublicadas.setResource(Resources.INSTANCE.por10());
				  break;
			case 10: circuloPublicadas.setResource(Resources.INSTANCE.por20());
				  break;
			case 20: circuloPublicadas.setResource(Resources.INSTANCE.por30());
				  break;
			case 30: circuloPublicadas.setResource(Resources.INSTANCE.por40());
				  break;
			case 40: circuloPublicadas.setResource(Resources.INSTANCE.por50());
				  break;
			case 50: circuloPublicadas.setResource(Resources.INSTANCE.por60());
				  break;
			case 60: circuloPublicadas.setResource(Resources.INSTANCE.por70());
				  break;
			case 70: circuloPublicadas.setResource(Resources.INSTANCE.por80());
				  break;
			case 80: circuloPublicadas.setResource(Resources.INSTANCE.por90());
				  break;
			case 90: circuloPublicadas.setResource(Resources.INSTANCE.por100());
				  break;
			case 100: circuloPublicadas.setResource(Resources.INSTANCE.por100());
				  break;
		}
		if(MarkingInterface.getPublishedProgress() == 0.00){
			circuloPublicadas.setResource(Resources.INSTANCE.por0());
		}
		circuloPublicadas.setHeight("2em");
		circuloPublicadas.setWidth("2em");
		popPublicadas.setWidget(new Label("Publicadas: "+MarkingInterface.getPublishedProgress()+"%"));
		circuloPublicadas.addMouseMoveHandler(new MouseMoveHandler(){
			public void onMouseMove(MouseMoveEvent event){
				popPublicadas.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			          public void setPosition(int offsetWidth, int offsetHeight) {
			            int left = (int) ((Window.getClientWidth() - offsetWidth)*0.65);
			            int top = (int) ((Window.getClientHeight() - offsetHeight)*0.04);
			            popPublicadas.setPopupPosition(left, top);
			          }
			        });
				popPublicadas.show();
			}
		});
		circuloPublicadas.addMouseOutHandler(new MouseOutHandler(){
			 public void onMouseOut(MouseOutEvent event){
				 popPublicadas.hide();
			 }
		});
		
		//3. Nivel Acuerdo		
		trun = (int) (MarkingInterface.getGeneralAgree()/10);
		h = (int) (MarkingInterface.getGeneralAgree()-trun*10);
		h = (int) MarkingInterface.getGeneralAgree() - h;
		switch (h){
				
			case 0:  circuloNivelAcuerdo.setResource(Resources.INSTANCE.por10());
					break;
			case 10: circuloNivelAcuerdo.setResource(Resources.INSTANCE.por20());
					break;
			case 20: circuloNivelAcuerdo.setResource(Resources.INSTANCE.por30());
					break;
			case 30: circuloNivelAcuerdo.setResource(Resources.INSTANCE.por40());
					break;
			case 40: circuloNivelAcuerdo.setResource(Resources.INSTANCE.por50());
					break;
			case 50: circuloNivelAcuerdo.setResource(Resources.INSTANCE.por60());
					break;
			case 60: circuloNivelAcuerdo.setResource(Resources.INSTANCE.por70());
					break;
			case 70: circuloNivelAcuerdo.setResource(Resources.INSTANCE.por80());
					break;
			case 80: circuloNivelAcuerdo.setResource(Resources.INSTANCE.por90());
					break;
			case 90: circuloNivelAcuerdo.setResource(Resources.INSTANCE.por100());
					break;
			case 100: circuloNivelAcuerdo.setResource(Resources.INSTANCE.por100());
					break;
			}
			if(MarkingInterface.getGeneralAgree() == 0.00){
				circuloNivelAcuerdo.setResource(Resources.INSTANCE.por0());
			}		
			circuloNivelAcuerdo.setHeight("2em");
			circuloNivelAcuerdo.setWidth("2em");
			popNivelAcuerdo.setWidget(new Label("Nivel Acuerdo: "+MarkingInterface.getGeneralAgree()+"%"));
			circuloNivelAcuerdo.addMouseMoveHandler(new MouseMoveHandler(){
				public void onMouseMove(MouseMoveEvent event){
					popNivelAcuerdo.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
				          public void setPosition(int offsetWidth, int offsetHeight) {
				            int left = (int) ((Window.getClientWidth() - offsetWidth)*0.71);
				            int top = (int) ((Window.getClientHeight() - offsetHeight)*0.04);
				            popNivelAcuerdo.setPopupPosition(left, top);
				          }
				        });
					popNivelAcuerdo.show();
				}
			});
			circuloNivelAcuerdo.addMouseOutHandler(new MouseOutHandler(){
				 public void onMouseOut(MouseOutEvent event){
					 popNivelAcuerdo.hide();
				 }
			});
			
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
	
}
