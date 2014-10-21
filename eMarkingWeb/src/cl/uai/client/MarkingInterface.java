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
package cl.uai.client;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import cl.uai.client.data.AjaxData;
import cl.uai.client.data.AjaxRequest;
import cl.uai.client.data.Criterion;
import cl.uai.client.data.SubmissionGradeData;
import cl.uai.client.marks.CustomMark;
import cl.uai.client.marks.Mark;
import cl.uai.client.marks.RubricMark;
import cl.uai.client.page.EditMarkDialog;
import cl.uai.client.page.MarkingPage;
import cl.uai.client.page.MarkingPagesInterface;
import cl.uai.client.resources.EmarkingMessages;
import cl.uai.client.resources.Resources;
import cl.uai.client.rubric.RubricInterface;
import cl.uai.client.toolbar.MarkingToolBar;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.github.gwtbootstrap.client.ui.ProgressBar;
import com.github.gwtbootstrap.client.ui.base.ProgressBarBase;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Marking interface is the main interface which holds all interactions
 * for eMarking
 * 
 * @author Jorge Villalon <villalon@gmail.com>
 *
 */
public class MarkingInterface extends EMarkingComposite {

	/** For logging purposes */
	private static Logger logger = Logger.getLogger(MarkingInterface.class.getName());
	
	/** Static resource for i18n messages **/
	public static EmarkingMessages messages = GWT.create(EmarkingMessages.class);

	/** eMarking version according to Moodle for debugging information **/
	private static int eMarkingVersion = 0;
	
	/** Submission id to obtain from HTML **/
	private static int submissionId = -1;

	/** Wait dialog to make sure some things occur linearly **/
	private static DialogBox waitDialog = null;
	
	/** Counter for how many interfaces are currently loading simultaneously **/
	private int loading=0;

	/** Indicates if the marking interface will include anonymous information **/
	private static boolean anonymous = false;

	/** Indicates if the marking interface is in read only mode **/
	public static boolean readonly = true;
	
	/** Indicates if the user is a supervisor (editingteacher) **/
	public static boolean supervisor = false;
	
	/** The id of the marker.**/
	public static int markerid=0;
	
	/** Indicates if the user owns the submission **/
	public static boolean ownSubmission = false;
	
	/** Moodle session key for posting to marking ajax interface **/
	public static String sessKey = null;

	/** Submission data (student, course, grade, marker) **/
	public static SubmissionGradeData submissionData = null;

	/**
	 * @return the eMarkingVersion
	 */
	public static int geteMarkingVersion() {
		return eMarkingVersion;
	}
	/**
	 * The id of the submission the interface is working with
	 * @return
	 */
	public static int getSubmissionId() {
		return submissionId;
	}
	/**
	 * Indicates if the marking process is anonymous
	 * 
	 * @return true if the marking is anonymous
	 */
	public static boolean isAnonymous() {
		return anonymous;
	}
	/**
	 * Sets if the marking process will be anonymous
	 * @param anon
	 */
	public static void setAnonymous(boolean anon) {
		anonymous = anon;
	}
	
	/**
	 * @param eMarkingVersion the eMarkingVersion to set
	 */
	public static void seteMarkingVersion(int _eMarkingVersion) {
		eMarkingVersion = _eMarkingVersion;
	}

	public static void setSubmissionId(int subid) {
		submissionId = subid;
	}
	/** Main panels for layout **/
	private VerticalPanel mainPanel = null;

	private AbsolutePanel markingPanel = null;
	private HorizontalPanel interfacePanel = null;

	private FocusPanel focusPanel = null;
	private HTML loadingMessage = null;
	
	/** Main eMarking interfaces **/
	private MarkingToolBar toolbar = null;
	private MarkingPagesInterface markingPagesInterface = null;

	private RubricInterface rubricInterface = null;

	/** Drag and Drop controler for marking interface **/
	public PickupDragController dragController = null;
	/** Timer related variables **/
	private Timer timer = null;
	private int timerWaitingTurns = 1;
	
	private Timer heartBeatTimer = null;
	
	private int ticksUntilTrying = 0;

	/** Suggester for previous comments **/
	public MultiWordSuggestOracle previousCommentsOracle = new MultiWordSuggestOracle() ;

	private static int linkrubric = 0;

	private static int collaborativefeatures = 0;
	
	public static int getCollaborativeFeatures(){
		return collaborativefeatures;
	}
	
	public static int getLinkRubric() {
		return linkrubric;
	}
	public static Map<String, String> MapCss = new HashMap<String, String>();
	
	public static Map<String, String> getMapCss(){
		return MapCss;
	}

	/**
	 * 
	 */
	public MarkingInterface() {

		logger.fine("Initializing eMarking");

		// Focus panel to catch key events
		focusPanel = new FocusPanel();
		focusPanel.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if(event.getNativeKeyCode() >= 49 && event.getNativeKeyCode() <= 57) {
					toolbar.getMarkingButtons().setButtonPressed(event.getNativeKeyCode() - 49, false);
				} else if(event.getNativeKeyCode() == 48) {
					toolbar.getMarkingButtons().setButtonPressed(91, false);
				}
			}
		});
		
		// Main panel has two rows: Toolbar and Marking panel
		mainPanel = new VerticalPanel();
		mainPanel.addStyleName(Resources.INSTANCE.css().interfaceMainPanel());

		focusPanel.setWidget(mainPanel);
		
		// Toolbar goes up
		toolbar = new MarkingToolBar();
		mainPanel.add(toolbar);

		// Marking panel containing the marking interface
		interfacePanel = new HorizontalPanel();
		interfacePanel.addStyleName(Resources.INSTANCE.css().interfacepanel());

		loadingMessage = new HTML(messages.Loading() + " " + AjaxRequest.moodleUrl);
		
		interfacePanel.add(loadingMessage);
		interfacePanel.setCellHorizontalAlignment(loadingMessage, HasAlignment.ALIGN_CENTER);
		
		markingPanel = new AbsolutePanel();
		markingPanel.add(interfacePanel);

		mainPanel.add(markingPanel);

		// Timer for pinging system
		timer = new Timer() {
			@Override
			public void run() {
				// If there are ticks to wait before trying again, update message and update ticks
				if(ticksUntilTrying > 0) {
					loadingMessage.setHTML(messages.CantReachServerRetrying(ticksUntilTrying));					
					ticksUntilTrying--;
				} else {
					// Updating next trial for one more second and trying onLoad again
					timerWaitingTurns++;
					ticksUntilTrying = Math.min(timerWaitingTurns, 60);
					loadingMessage.setHTML(messages.Loading() + " " + AjaxRequest.moodleUrl);					
					onLoad();
				}
			}
		};
		
		heartBeatTimer = new  Timer() {
			@Override
			public void run() {
				String extradata = "";
				if(submissionData != null)
					extradata = "&marker=" + submissionData.getMarkerid() + "&student=" + submissionData.getStudentid();
				final String requestUrl = extradata;
				AjaxRequest.ajaxRequest("action=heartbeat" + extradata, new AsyncCallback<AjaxData>() {
					@Override
					public void onSuccess(AjaxData result) {
						logger.info("Heartbeat! " + requestUrl);
					}
					@Override
					public void onFailure(Throwable caught) {
						logger.warning("Failure on heartbeat");
					}
				});
			}
		};
		
		MapCss.put("criterion0", Resources.INSTANCE.css().criterion0());
		MapCss.put("criterion1", Resources.INSTANCE.css().criterion1());
		MapCss.put("criterion2", Resources.INSTANCE.css().criterion2());
		MapCss.put("criterion3", Resources.INSTANCE.css().criterion3());
		MapCss.put("criterion4", Resources.INSTANCE.css().criterion4());
		MapCss.put("criterion5", Resources.INSTANCE.css().criterion5());
		MapCss.put("criterion6", Resources.INSTANCE.css().criterion6());
		MapCss.put("criterion7", Resources.INSTANCE.css().criterion7());
		MapCss.put("criterion8", Resources.INSTANCE.css().criterion8());
		MapCss.put("criterion9", Resources.INSTANCE.css().criterion9());
		MapCss.put("criterion10", Resources.INSTANCE.css().criterion10());
		MapCss.put("criterion11", Resources.INSTANCE.css().criterion11());
		MapCss.put("criterion12", Resources.INSTANCE.css().criterion12());
		MapCss.put("criterion13", Resources.INSTANCE.css().criterion13());
		MapCss.put("criterion14", Resources.INSTANCE.css().criterion14());
		MapCss.put("criterion15", Resources.INSTANCE.css().criterion15());
		MapCss.put("criterion16", Resources.INSTANCE.css().criterion16());
		MapCss.put("criterion17", Resources.INSTANCE.css().criterion17());
		MapCss.put("criterion18", Resources.INSTANCE.css().criterion18());
		MapCss.put("criterion19", Resources.INSTANCE.css().criterion19());
		MapCss.put("criterion20", Resources.INSTANCE.css().criterion20());
		MapCss.put("criterion21", Resources.INSTANCE.css().criterion21());
		MapCss.put("criterion22", Resources.INSTANCE.css().criterion22());
		MapCss.put("criterion23", Resources.INSTANCE.css().criterion23());
		MapCss.put("criterion25", Resources.INSTANCE.css().criterion24());
		
		MapCss.put("color1", Resources.INSTANCE.css().color1());
		MapCss.put("color2", Resources.INSTANCE.css().color2());
		MapCss.put("color3", Resources.INSTANCE.css().color3());
		MapCss.put("color4", Resources.INSTANCE.css().color4());
		MapCss.put("color5", Resources.INSTANCE.css().color5());
		MapCss.put("color6", Resources.INSTANCE.css().color6());
		MapCss.put("color7", Resources.INSTANCE.css().color7());
		MapCss.put("color8", Resources.INSTANCE.css().color8());
		MapCss.put("color9", Resources.INSTANCE.css().color9());
		MapCss.put("color10", Resources.INSTANCE.css().color10());
		MapCss.put("color11", Resources.INSTANCE.css().color11());
		MapCss.put("color12", Resources.INSTANCE.css().color12());
		MapCss.put("color13", Resources.INSTANCE.css().color13());
		MapCss.put("color14", Resources.INSTANCE.css().color14());
		MapCss.put("color15", Resources.INSTANCE.css().color15());
		MapCss.put("color16", Resources.INSTANCE.css().color16());
		MapCss.put("color17", Resources.INSTANCE.css().color17());
		MapCss.put("color18", Resources.INSTANCE.css().color18());
		MapCss.put("color19", Resources.INSTANCE.css().color19());
		MapCss.put("color20", Resources.INSTANCE.css().color20());
		MapCss.put("color21", Resources.INSTANCE.css().color21());
		MapCss.put("color22", Resources.INSTANCE.css().color22());
		MapCss.put("color23", Resources.INSTANCE.css().color23());
		MapCss.put("color25", Resources.INSTANCE.css().color24());

		// Drag and Drop controller attached to marking panel
		dragController = new PickupDragController(markingPanel, false);
		dragController.addDragHandler(new MarkingInterfaceDragHandler());
		
		waitDialog = new DialogBox(false, true);
		waitDialog.setGlassEnabled(true);
		waitDialog.addStyleName(Resources.INSTANCE.css().commentdialog());
		waitDialog.setHTML(MarkingInterface.messages.Loading());
		ProgressBar pbar = new ProgressBar(ProgressBarBase.Style.STRIPED);
		pbar.setActive(true);
		pbar.setPercent(100);
		waitDialog.setWidget(pbar);

		this.initWidget(focusPanel);
	}

	public void addLoading(boolean saving) {
		this.loading++;
		
		if(this.loading > 0) {
			if(saving) {
				waitDialog.setHTML(MarkingInterface.messages.Saving());
			} else {
				waitDialog.setHTML(MarkingInterface.messages.Loading());				
			}
			waitDialog.center();
		}
	}

	/**
	 * Add a mark to both the page and update the rubric interface
	 * 
	 * @param mark the mark to add
	 */
	public void addMark(final Mark mark, final MarkingPage page) {
		
		RootPanel.get().getElement().getStyle().setCursor(Cursor.WAIT);
		
		Mark.loadingIcon.removeFromParent();
		page.getAbsolutePanel().add(Mark.loadingIcon, mark.getPosx(), mark.getPosy());
		Mark.loadingIcon.setVisible(true);
		
		//por defecto el criterionid = 0 y la clase para el color es criterion0
		int cid = 0;
		if(MarkingInterface.linkrubric == 1){
			Criterion c = EMarkingWeb.markingInterface.getToolbar().getMarkingButtons().getSelectedCriterion();
			cid = c.getId();
		}

		// Invokes the ajax Moodle interface to save the mark
		AjaxRequest.ajaxRequest("action=addcomment" +
				"&comment=" + URL.encode(mark.getRawtext()) +
				"&posx=" + mark.getPosx() +
				"&posy=" + mark.getPosy() +
				"&width=" + mark.getWidth() +
				"&height=" + mark.getHeight() +
				"&format=" + mark.getFormat() +
				"&pageno=" + mark.getPageno() +
				"&criterionid="+cid + 
				"&colour="+mark.getColour()
				, new AsyncCallback<AjaxData>() {

			@Override
			public void onFailure(Throwable caught) {
				Mark.loadingIcon.setVisible(false);
				logger.severe("Exception adding mark.");
				logger.severe(caught.getMessage());
				Window.alert(caught.getMessage());
				RootPanel.get().getElement().getStyle().setCursor(Cursor.AUTO);
			}

			@Override
			public void onSuccess(AjaxData result) {
				Mark.loadingIcon.setVisible(false);
				
				Map<String, String> values = AjaxRequest.getValueFromResult(result);

				// Parse json results and check if there was an error
				if(!result.getError().equals("")) {
					Window.alert(messages.ErrorAddingMark());
					RootPanel.get().getElement().getStyle().setCursor(Cursor.AUTO);
					return;
				}

				// Parses important values from result
				int id = Integer.parseInt(values.get("id"));
				long timemodified = Long.parseLong(values.get("timemodified"));
				int markerid = Integer.parseInt(values.get("markerid"));
				String markername = values.get("markername");
				float newgrade = 0;
				if(mark instanceof RubricMark) {
					newgrade = Float.parseFloat(values.get("grade"));
				} else {
					newgrade = MarkingInterface.submissionData.getFinalgrade();
				}
				
				// Sets the values for the new mark
				mark.setId(id);
				mark.setMarkerid(markerid);
				mark.setMarkername(markername);
				mark.setTimeCreated(timemodified);

				// Adds the mark to the marking interface
				markingPagesInterface.addMarkWidget(mark, -1, page);
				
				// If it is a comment And to the rubric interface
				toolbar.getMarkingButtons().updateStats();
				
				if(!(mark instanceof CustomMark) && mark.getFormat() != 5) {
					EMarkingWeb.markingInterface.getRubricInterface().getToolsPanel().
						getPreviousComments().addMarkAsCommentToInterface(mark);
				}
				
				// Updates toolbar
				setTimemodified(timemodified);
				
				// Update the marking interface with the final grade and time
				EMarkingWeb.markingInterface.setFinalgrade(newgrade, timemodified);

				RootPanel.get().getElement().getStyle().setCursor(Cursor.AUTO);
			}
		});

	}
	
	/**
	 * Adds a mark to current submission.
	 * 
	 * @param level the mark to assign
	 * @param posx X coordinate where to draw mark in page
	 * @param posy Y coordinate where to draw mark in page
	 */
	public void addRubricMark(final int level, final int posx, final int posy, final MarkingPage page) {

			// Shows comment dialog
			// 0 for regradeid as we are creating a mark
			final EditMarkDialog dialog = new EditMarkDialog(posx, posy, level, 0);
			if(this.setBonus>=0){
				dialog.setBonus(this.setBonus);
				this.setBonus=-1;
			}
			dialog.addCloseHandler(new CloseHandler<PopupPanel>() {
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {

					// If dialog was not cancelled and contains a comment, try to add the mark
					if(!dialog.isCancelled()) {

						// Calculate parameters for adding mark
						final String comment = dialog.getTxtComment(); // Comment from dialog
						final float bonus = dialog.getBonus();
						final int levelid = dialog.getLevelId();
						// TODO: Check if coordinates can be calculated better (as a page percentage?)

						// Ajax URL for adding mark
						String url = "action=addmark"+
								"&level="+levelid+
								"&posx="+posx+
								"&posy="+posy+
								"&pageno="+(page.getPageNumber())+
								"&sesskey="+sessKey+
								"&bonus="+bonus+
								"&comment="+URL.encode(comment);

						// Add loading icon
						Mark.loadingIcon.removeFromParent();
						page.getAbsolutePanel().add(Mark.loadingIcon, posx, posy);
						Mark.loadingIcon.setVisible(true);
						
						rubricInterface.getRubricPanel().loadingRubricCriterion(levelid);
						
						// Make Ajax request 
						AjaxRequest.ajaxRequest(url, 
								new AsyncCallback<AjaxData>() {

							@Override
							public void onFailure(Throwable caught) {
								Mark.loadingIcon.setVisible(false);
								rubricInterface.getRubricPanel().finishloadingRubricCriterion(levelid);

								logger.severe("Error adding mark to Moodle!");
								logger.severe(caught.getMessage());
								Window.alert(caught.getMessage());
							}

							@Override
							public void onSuccess(AjaxData result) {
								Mark.loadingIcon.setVisible(false);
								rubricInterface.getRubricPanel().finishloadingRubricCriterion(levelid);
								Mark.hideIcons();
								
								// Parse Json values
								Map<String, String> values = AjaxRequest.getValueFromResult(result);

								if(!values.get("error").equals("")) {
									Mark.loadingIcon.setVisible(false);
									rubricInterface.getRubricPanel().finishloadingRubricCriterion(levelid);

									logger.severe("Error adding mark to Moodle!");
									Window.alert(MarkingInterface.messages.ErrorAddingMark());
									return;
								}
								
								// Get main values
								int previd = Integer.parseInt(values.get("replaceid"));
								int newid = Integer.parseInt(values.get("id"));
								float newgrade = Float.parseFloat(values.get("grade"));
								long timemodified = Long.parseLong(values.get("timemodified"));
								String markername = values.get("markername");
								int pageno = page.getPageNumber();
								int regradeid = Integer.parseInt(values.get("regradeid"));
								int regradeaccepted = Integer.parseInt(values.get("regradeaccepted"));
								int regrademotive = Integer.parseInt(values.get("regrademotive"));
								String regradecomment = values.get("regradecomment");
								String regrademarkercomment = values.get("regrademarkercomment");
								String colour = values.get("colour");

								// If there was a previous mark with the same level, remove it
								if(previd > 0) {
									page.deleteMarkWidget(previd);
								}

								long unixtime = System.currentTimeMillis() / 1000L;
								
								// Add mark to marking pages interface
								RubricMark mark = new RubricMark(
										posx, 
										posy,
										pageno,
										MarkingInterface.markerid, 
										dialog.getLevelId(),
										unixtime,
										colour); 
								mark.setId(newid);
								mark.setRawtext(comment);
								mark.setMarkername(markername);
								mark.setRegradeid(regradeid);
								mark.setRegradeaccepted(regradeaccepted);
								mark.setRegradecomment(regradecomment);
								mark.setRegrademarkercomment(regrademarkercomment);
								mark.setRegrademotive(regrademotive);
								
								Criterion criterion = MarkingInterface.submissionData.getLevelById(mark.getLevelId()).getCriterion();
								criterion.setSelectedLevel(mark.getLevelId());
								criterion.setBonus(bonus);
								markingPagesInterface.addMarkWidget(mark, previd, page);
								rubricInterface.getRubricPanel().addMarkToRubric(mark);
								toolbar.getMarkingButtons().updateStats();
								if(MarkingInterface.getLinkRubric() == 1)
									toolbar.getMarkingButtons().changeColor(criterion.getId());
								
								EMarkingWeb.markingInterface.getRubricInterface().getToolsPanel().
								getPreviousComments().addMarkAsCommentToInterface(mark);							

								setFinalgrade(newgrade, timemodified);
							}
						});
					}
				}
			});
			dialog.center();
	}

	/**
	 * Deletes a mark from the interface
	 * 
	 * @param mark The Mark to be deleted
	 */
	public void deleteMark(final Mark mark) {
		// Check if the mark is a rubric mark
		final RubricMark rubricMark = mark instanceof RubricMark ? (RubricMark) mark : null;

		logger.fine("Deleting comment " + mark.getId());

		// Delete from view for user
		mark.setVisible(false);

		// Set cursor waiting
		RootPanel.get().getElement().getStyle().setCursor(Cursor.WAIT);

		String url = null;

		if(rubricMark == null) {
			// Ajax URL for deleting comment
			url = "action=deletecomment"+
					"&id="+mark.getId()+
					"&sesskey="+sessKey;
		} else {
			// Ajax URL for deleting mark
			url = "action=deletemark"+
					"&level="+ rubricMark.getLevelId() +
					"&sesskey="+sessKey;
			
			rubricInterface.getRubricPanel().loadingRubricCriterion(rubricMark.getLevelId());
		}


		// Make Ajax request 
		AjaxRequest.ajaxRequest(url, 
				new AsyncCallback<AjaxData>() {

			@Override
			public void onFailure(Throwable caught) {
				logger.severe("Exception deleting mark.");
				logger.severe(caught.getMessage());
				if(rubricMark != null)
					rubricInterface.getRubricPanel().finishloadingRubricCriterion(rubricMark.getLevelId());
				Window.alert(caught.getMessage());
				mark.setVisible(true);
				RootPanel.get().getElement().getStyle().setCursor(Cursor.AUTO);
			}

			@Override
			public void onSuccess(AjaxData result) {
				// Parse Json values
				Map<String, String> values = AjaxRequest.getValueFromResult(result);
				
				if(rubricMark != null)
					rubricInterface.getRubricPanel().finishloadingRubricCriterion(rubricMark.getLevelId());

				// Get main values
				float newgrade = 0;
				if(rubricMark != null)
					newgrade = Float.parseFloat(values.get("grade"));

				// Get main values
				long timemodified = Long.parseLong(values.get("timemodified"));

				if(rubricMark != null) {
					// Then add resulting mark it to the rubric interface
					rubricMark.getLevel().getCriterion().setSelectedLevel(0);
					rubricInterface.getRubricPanel().deleteMarkFromRubric(rubricMark);
					rubricInterface.getToolsPanel().getPreviousComments().deletePreviousComment(rubricMark.getRawtext());
					setFinalgrade(newgrade, timemodified);
				} else {
					markingPagesInterface.deleteMarkWidget(mark.getId());
					setTimemodified(timemodified);
				}
				
				toolbar.getMarkingButtons().updateStats();
				
				EMarkingWeb.markingInterface.getRubricInterface().getToolsPanel().loadSumissionData();

				RootPanel.get().getElement().getStyle().setCursor(Cursor.AUTO);
			}
		});

	}

	public void finishLoading() {
		this.loading--;
		if(this.loading <= 0)
			waitDialog.hide();
	}

	/**
	 * Gets the drag controller for main interface
	 * @return
	 */
	public PickupDragController getDragController() {
		return dragController;
	}

	public MarkingPagesInterface getMarkingPagesInterface() {
		return markingPagesInterface;
	}

	public RubricInterface getRubricInterface() {
		return rubricInterface;
	}

	/**
	 * @return the toolbar
	 */
	public MarkingToolBar getToolbar() {
		return toolbar;
	}

	/**
	 * Loads marking interface according to current submission
	 */
	public void loadInterface() {

		toolbar.loadSubmissionData();

		dragController.unregisterDropControllers();
		interfacePanel.clear();

		markingPagesInterface = new MarkingPagesInterface();
		rubricInterface = new RubricInterface();

		interfacePanel.add(markingPagesInterface);
		
		interfacePanel.add(rubricInterface);
		interfacePanel.setCellHorizontalAlignment(rubricInterface, HasHorizontalAlignment.ALIGN_LEFT);

		if(RootPanel.get().getOffsetWidth() > 1024) {
			interfacePanel.setCellWidth(rubricInterface, "100%");
		} else {
			rubricInterface.setVisible(false);
		}

		Scheduler.get().scheduleFinally(new Command() {
			@Override
			public void execute() {
				toolbar.getStudentSelector();
			}
		});
		/** Codigo Implantado tesis **/
		if(linkrubric == 1){
			toolbar.getMarkingButtons().setCriterionList();
		}
		/** FIN **/
		
		markingPagesInterface.loadInterface();
	}
	
	/**
	 * Loads submission data using global submission id
	 */
	public void loadSubmissionData() {

		// Checks that global submission id is valid
		if(MarkingInterface.submissionId <= 0)
			return;

		addLoading(false);
		
		// Ajax requesto to submission data
		AjaxRequest.ajaxRequest("action=getsubmission", new AsyncCallback<AjaxData>() {

			@Override
			public void onFailure(Throwable caught) {
				logger.severe("Error getting submission from Moodle!");
				logger.severe(caught.getMessage());
				Window.alert(caught.getMessage());
				finishLoading();
			}

			@Override
			public void onSuccess(AjaxData result) {
				// Parse Json values
				Map<String, String> values = AjaxRequest.getValueFromResult(result);

				// Reset submission data
				submissionData = new SubmissionGradeData();
				submissionData.setIsgraded(false);

				// Loads basic data that shouldn't be null and validate types by parsing
				try {
					submissionData.setId(Integer.parseInt(values.get("id")));
					submissionData.setGrademin(Float.parseFloat(values.get("grademin")));
					submissionData.setGrademax(Float.parseFloat(values.get("grademax")));
					submissionData.setCourseid(Integer.parseInt(values.get("courseid")));
					submissionData.setCoursename(values.get("coursename"));
					submissionData.setCourseshort(values.get("courseshort"));
					submissionData.setEmail(values.get("email"));
					submissionData.setFirstname(values.get("firstname"));
					submissionData.setLastname(values.get("lastname"));
					submissionData.setStudentid(Integer.parseInt(values.get("studentid")));
					submissionData.setMarkeremail(values.get("markeremail"));
					submissionData.setMarkerfirstname(values.get("markerfirstname"));
					submissionData.setMarkerlastname(values.get("markerlastname"));
					submissionData.setMarkerid(Integer.parseInt(values.get("markerid")));
					submissionData.setActivityname(values.get("activityname"));
					submissionData.setFeedback(values.get("feedback"));
					submissionData.setCustommarks(values.get("custommarks"));
				} catch(Exception e) {
					// If something goes wrong, data is invalid and we can't work with it
					logger.severe("Exception parsing submission data.");
					logger.severe(e.getMessage());
					submissionData = null;
				}

				// For a non marked submission datecreated could be null
				try {
					submissionData.setDatecreated(Long.parseLong(values.get("timecreated")));					
				} catch(Exception e) {
					logger.severe("Exception parsing submission timecreated data.");
					logger.severe(e.getMessage());
				}

				// Regrade restrictions dates and info
				try {
					submissionData.setRegraderestrictdates(values.get("regraderestrictdates").equals("1"));
					submissionData.setRegradeopendate(new Date(Long.parseLong(values.get("regradesopendate")) * 1000));					
					submissionData.setRegradeclosedate(new Date(Long.parseLong(values.get("regradesclosedate")) * 1000));					
				} catch(Exception e) {
					logger.severe("Exception parsing submission regrade restriction data.");
					logger.severe(e.getMessage());
				}

				// Final grade and update time could be null
				try {
					submissionData.setFinalgrade(Float.parseFloat(values.get("finalgrade")));
					submissionData.setDatemodified(Long.parseLong(values.get("timemodified")));
					submissionData.setIsgraded(true);
				} catch(Exception e) {
					logger.severe("Exception parsing submission finalgrade and timemodified data.");
					logger.severe(e.getMessage());
				}

				// Load interfaces if everything is all right
				if(submissionData != null) {
					submissionData.loadRubricFromMap();
				} else {
					logger.severe("Error parsing submission data!");
					Window.alert(messages.InvalidSubmissionData());
				}
				finishLoading();
			}
		});
	}

	@Override
	protected void onLoad() {

		// Ajax request to load submission data
		AjaxRequest.ajaxRequest("action=ping", new AsyncCallback<AjaxData>() {
			@Override
			public void onFailure(Throwable caught) {
				// Keep trying if something fails every few seconds
				timer.scheduleRepeating(1000);
			}
			@Override
			public void onSuccess(AjaxData result) {
				//Check if values are ok
				if(result.getError().equals("")) {
					// Clear interface (submission and rubric interfaces)
					interfacePanel.clear();
					// Cancel timer, we don't need to ping again
					timer.cancel();
					// Parse Json values
					Map<String, String> value = AjaxRequest.getValueFromResult(result);
					// Assign Moodle session key
					sessKey = value.get("sesskey");

					// Assign if the assignment is anonymous
					anonymous = value.get("anonymous").equals("true");
					
					logger.fine("Anonymous mode: " + anonymous);
					
					// Assign if the assignment is anonymous
					readonly = (value.get("hascapability") != null && value.get("hascapability").equals("false"));
					
					// Assign if the user is supervisor
					supervisor = (value.get("supervisor") != null && value.get("supervisor").equals("true"));
					
					// Gets the user id of the person in front of the interface
					markerid = Integer.parseInt(value.get("user"));
					
					int student = Integer.parseInt(value.get("student"));
					
					// Indicates if the user owns the current submission
					ownSubmission = markerid == student;
					
					logger.fine("Read only mode: " + readonly);

					// Schedule heartbeat if configured as
					if((value.get("heartbeat") != null && value.get("heartbeat").equals("1"))) {
						// Every two minutes
						heartBeatTimer.scheduleRepeating(2 * 60 * 1000);
					}
										
					linkrubric = Integer.parseInt(value.get("linkrubric"));
					
					collaborativefeatures = Integer.parseInt(value.get("collaborativefeatures"));

					// Load submission data
					loadSubmissionData();
					
					focusPanel.getElement().focus();
				} else {
					// Keep trying if something fails every few seconds
					timer.scheduleRepeating(1000);
				}
			}
		});
	}
	
	public void regradeMark(final RubricMark mark, final String comment, final int motive) {
		
		RootPanel.get().getElement().getStyle().setCursor(Cursor.WAIT);

		final MarkingPage page = markingPagesInterface.getPageByIndex(mark.getPageno());
		Mark.loadingIcon.removeFromParent();
		page.getAbsolutePanel().add(Mark.loadingIcon, mark.getPosx(), mark.getPosy());
		Mark.loadingIcon.setVisible(true);

		// Invokes the ajax Moodle interface to save the mark
		AjaxRequest.ajaxRequest("action=addregrade" +
				"&comment=" + URL.encode(comment) +
				"&motive=" + motive +
				"&level=" + mark.getLevelId()
				, new AsyncCallback<AjaxData>() {

			@Override
			public void onFailure(Throwable caught) {
				Mark.loadingIcon.setVisible(false);
				logger.severe("Exception regrading mark.");
				logger.severe(caught.getMessage());
				Window.alert(caught.getMessage());
				RootPanel.get().getElement().getStyle().setCursor(Cursor.AUTO);
			}

			@Override
			public void onSuccess(AjaxData result) {
				Mark.loadingIcon.setVisible(false);
				
				Map<String, String> values = AjaxRequest.getValueFromResult(result);

				// Parse json results and check if there was an error
				if(!result.getError().equals("")) {
					Window.alert(messages.ErrorAddingMark());
					RootPanel.get().getElement().getStyle().setCursor(Cursor.AUTO);
					return;
				}

				// Parses important values from result
				int regradeid = Integer.parseInt(values.get("regradeid"));
				long timemodified = Long.parseLong(values.get("timemodified"));
				
				// Sets the values for the new mark
				mark.setRegradeid(regradeid);
				mark.setRegradecomment(comment);
				mark.setRegrademotive(motive);

				// Add the mark to the rubric so it updates the information in the rubric panel
				EMarkingWeb.markingInterface.getRubricInterface().getRubricPanel().addMarkToRubric(mark);
				

				
				// Updates toolbar
				setTimemodified(timemodified);
				
				RootPanel.get().getElement().getStyle().setCursor(Cursor.AUTO);
			}
		});
	}
	
	/**
	 * Reloads main interface for a new submission
	 * 
	 * @param newSubmissionId the new submission id
	 */
	public void reload(int newSubmissionId) {

		// Validate new submission id value
		if(newSubmissionId < 0) {
			Window.alert("Invalid submission id value " + submissionId);
			return;
		}

		submissionId = newSubmissionId;

		reloadPage();
	}
	
	public void reloadPage() {
		String madeURL = Window.Location.getProtocol()+"//"+Window.Location.getHost()+Window.Location.getPath()+"?";
		for(String key : Window.Location.getParameterMap().keySet()) {
			String value = Window.Location.getParameterMap().get(key).get(0);
			if(key.equals("ids")) {
				value = submissionId+"";
			}
			madeURL += key + "=";
			madeURL += value;
			madeURL += "&";
		}
		Window.Location.replace(madeURL);
	}
	
	/**
	 * A new grade was assigned through marking
	 * 
	 * @param newgrade new final grade
	 * @param timemodified when was modified
	 */
	public void setFinalgrade(float newgrade, long timemodified) {
		submissionData.setFinalgrade(newgrade);
		this.setTimemodified(timemodified);
		toolbar.loadSubmissionData();
		focusPanel.getElement().focus();
		EMarkingWeb.notifyDelphi();
	}
	
	/**
	 * A new grade was assigned through marking
	 * 
	 * @param newgrade new final grade
	 * @param timemodified when was modified
	 */
	public void setTimemodified(long timemodified) {
		submissionData.setDatemodified(timemodified);

		toolbar.loadSubmissionTimeModified();
	}
	private float setBonus = -1;
	//Sets a preset bonus for the next time the addrubric dialog is opened (used in delphi)
	public void setDialogNewBonus(float bonus){
		this.setBonus = bonus;
	}
}
