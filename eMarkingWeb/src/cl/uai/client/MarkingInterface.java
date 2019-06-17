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
package cl.uai.client;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import cl.uai.client.buttons.BubbleButton;
import cl.uai.client.buttons.ShowChatButton;
import cl.uai.client.buttons.ShowHelpButton;
import cl.uai.client.buttons.ShowRubricButton;
import cl.uai.client.buttons.ShowWallButton;
import cl.uai.client.chat.ChatInterface;
import cl.uai.client.chat.NodeChat;
import cl.uai.client.chat.SendSosDialog;
import cl.uai.client.chat.HelpInterface;
import cl.uai.client.chat.WallInterface;
import cl.uai.client.data.AjaxData;
import cl.uai.client.data.AjaxRequest;
import cl.uai.client.data.Criterion;
import cl.uai.client.data.SubmissionGradeData;
import cl.uai.client.marks.CustomMark;
import cl.uai.client.marks.HighlightMark;
import cl.uai.client.marks.Mark;
import cl.uai.client.marks.PathMark;
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
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.CalendarUtil;


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

	/** Event bus for e-marking **/
	public static EventBus EVENT_BUS = GWT.create(SimpleEventBus.class);
	
	/** Static resource for i18n messages **/
	public static EmarkingMessages messages = GWT.create(EmarkingMessages.class);

	/** Id of the draft to visualize **/
	private static int draftId = -1;
	
	/** Wait dialog to make sure some things occur linearly **/
	private static DialogBox waitDialog = null;

	/** Counter for how many interfaces are currently loading simultaneously **/
	private int loading=0;

	/** Submission data (student, course, grade, marker) **/
	public static SubmissionGradeData submissionData = null;

	/** Show buttons **/
	private List<BubbleButton> bubbleButtons = null;

	/** Chat intefaces **/
	public ChatInterface chat = null;
	public WallInterface wall = null;
	public SendSosDialog sos = null;
	public HelpInterface help = null;

	/**
	 * The id of the submission the interface is working with
	 * @return
	 */
	public static int getDraftId() {
		return draftId;
	}

	public static void setDraftId(int draftid) {
		draftId = draftid;
	}
	/** Main panels for layout **/
	private VerticalPanel mainPanel = null;

	private AbsolutePanel markingPanel = null;
	private SplitLayoutPanel interfacePanel = null;

	private FocusPanel focusPanel = null;
	private HTML loadingMessage = null;

	/** Main eMarking interfaces **/
	private MarkingToolBar toolbar = null;
	private MarkingPagesInterface markingPagesInterface = null;

	private int rubricMinSize = 100;
	private RubricInterface rubricInterface = null;

	/** Drag and Drop controler for marking interface **/
	public PickupDragController dragController = null;
	/** Timer related variables **/
	private Timer timer = null;
	private int timerWaitingTurns = 1;

	private Timer resizeTimer = null;
	private Date resizeTime = new Date();
	private boolean resizeTimeout = false;

	private Timer heartBeatTimer = null;

	private int ticksUntilTrying = 0;

	/** Suggester for previous comments **/
	public MultiWordSuggestOracle previousCommentsOracle = new MultiWordSuggestOracle() ;

	/**
	/**
	 * 
	 */
	public MarkingInterface() {

		// The timer will check if no other resize events have been called in the last 200 ms
		resizeTimer = new Timer() {			
			@Override
			public void run() {
				Date now = new Date();
				long diff = now.getTime() - resizeTime.getTime();
				// The last resize was in less than 200 ms
				if(diff < 200) {
					resizeTimer.schedule(200);
				} else {					
					// More than 200 ms, we accept no more resize is being done
					resizeTimeout = false;

					Date oneyear = new Date();
					CalendarUtil.addMonthsToDate(oneyear, 12);

					Cookies.setCookie("emarking_width", Integer.toString(Window.getClientWidth()), oneyear);
					EMarkingWeb.markingInterface.loadSubmissionData();
				}
			}
		};

		// Focus panel to catch key events
		focusPanel = new FocusPanel();

		// Main panel has two rows: Toolbar and Marking panel
		mainPanel = new VerticalPanel();
		mainPanel.addStyleName(Resources.INSTANCE.css().interfaceMainPanel());

		focusPanel.setWidget(mainPanel);

		// Toolbar goes up
		toolbar = new MarkingToolBar();
		mainPanel.add(toolbar);

		focusPanel.addKeyDownHandler(new MarkingInterfaceKeyDownHandler(toolbar));
		
		// Marking panel containing the marking interface
		interfacePanel = new SplitLayoutPanel() {
			@Override
			public void onResize() {
				super.onResize();
				markingPagesInterface.resizePage(this.getCenterWidth());
			};
		};
		interfacePanel.animate(180);
		interfacePanel.addStyleName(Resources.INSTANCE.css().interfacepanel());

		loadingMessage = new HTML(messages.Loading() + " " + EMarkingConfiguration.getMoodleUrl());

		bubbleButtons = new ArrayList<BubbleButton>();

		bubbleButtons.add(new ShowRubricButton(Window.getClientWidth()-40, 0, 0));
		bubbleButtons.add(new ShowChatButton(Window.getClientWidth()-40, 45, NodeChat.SOURCE_CHAT));
		bubbleButtons.add(new ShowWallButton(Window.getClientWidth()-40, 90, NodeChat.SOURCE_WALL));
		bubbleButtons.add(new ShowHelpButton(Window.getClientWidth()-40, 135, NodeChat.SOURCE_SOS));

		interfacePanel.add(loadingMessage);
		// interfacePanel.setCellHorizontalAlignment(loadingMessage, HasAlignment.ALIGN_CENTER);		

		markingPanel = new AbsolutePanel();
		markingPanel.add(interfacePanel);

		for(BubbleButton b : bubbleButtons) {
			markingPanel.add(b);
		}

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
					loadingMessage.setHTML(messages.Loading() + " " + EMarkingConfiguration.getMoodleUrl());					
					onLoad();
				}
			}
		};

		heartBeatTimer = new  Timer() {
			@Override
			public void run() {
				String extradata = "";
				if(submissionData != null)
					extradata = "&marker=" + submissionData.getMarkerid() + "&draft=" + submissionData.getId();
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
		// Drag and Drop controller attached to marking panel
		dragController = new PickupDragController(markingPanel, false);

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

	public void updateGeneralFeedback(final String generalFeedback) {
		addLoading(true);
		// Invokes the ajax Moodle interface to save the mark
				AjaxRequest.ajaxRequest("action=updgeneralfeedback"
						+ "&feedback=" + URL.encode(generalFeedback), 
						new AsyncCallback<AjaxData>() {					
					@Override
					public void onSuccess(AjaxData result) {
						// Parse json results and check if there was an error
						if(!result.getError().equals("")) {
							logger.severe(result.getError());
							Window.alert(result.getError());
							return;
						}
						
						finishLoading();
					}					
					@Override
					public void onFailure(Throwable caught) {
						if(EMarkingConfiguration.isDebugging()) {
							caught.printStackTrace();
							logger.severe(caught.getMessage());
						}
						finishLoading();
					}
				});
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
		if(EMarkingConfiguration.isColoredRubric()) {
			Criterion c = EMarkingWeb.markingInterface.getToolbar().getMarkingButtons().getSelectedCriterion();
			if(c != null) {
				cid = c.getId();
			}
		}

		int markposx = mark.getPosx();
		int markposy = mark.getPosy();
		
		String path = "";
		if(mark instanceof HighlightMark) {
			HighlightMark hmark = (HighlightMark) mark;
			path = "&path=" + URL.encode(hmark.getEnd().getX() + "," + hmark.getEnd().getY());
			logger.fine("sending " + markposx + "," + markposy + " -> " + hmark.getEnd().getX() + "," + hmark.getEnd().getY() + " path:" + path);
		} else if(mark instanceof PathMark) {
			path = "&path=" + URL.encode(((PathMark) mark).getPath());
		} 
		// Invokes the ajax Moodle interface to save the mark
		AjaxRequest.ajaxRequest("action=addcomment" +
				"&comment=" + URL.encode(mark.getRawtext()) +
				"&posx=" + markposx +
				"&posy=" + markposy +
				"&width=" + page.getWidth() +
				"&height=" + page.getHeight() +
				"&format=" + mark.getFormat() +
				"&pageno=" + mark.getPageno() +
				"&criterionid="+cid + 
				path +
				"&colour="+mark.getCriterionId() +
				"&windowswidth=" + page.getWidth() +
				"&windowsheight=" + page.getHeight()
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
					getPreviousComments().addMarkAsCommentToInterface(mark, true);
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
					final String feedbackToAjax;
					if(dialog.haveFeedback()){
						feedbackToAjax = dialog.getFeedback();
					}else{
						feedbackToAjax = "";
					}
					// Ajax URL for adding mark
					String url = "action=addmark"+
							"&level="+levelid+
							"&posx="+posx+
							"&posy="+posy+
							"&pageno="+(page.getPageNumber())+
							"&sesskey="+EMarkingConfiguration.getSessKey()+
							"&bonus="+bonus+
							"&comment="+URL.encode(comment) +
							"&windowswidth=" + page.getWidth() +
							"&windowsheight=" + page.getHeight() +
							"&feedback=" + feedbackToAjax;

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
							int criterionId = Integer.parseInt(values.get("criterionid"));

							// If there was a previous mark with the same level, remove it
							if(previd > 0) {
								page.deleteMarkWidget(previd);
							}

							long unixtime = System.currentTimeMillis() / 1000L;

							// Add mark to marking pages interface
							RubricMark mark = new RubricMark(
									newid,
									posx, 
									posy,
									pageno,
									EMarkingConfiguration.getMarkerId(), 
									dialog.getLevelId(),
									unixtime,
									criterionId,
									markername,
									comment); 

							if(dialog.haveFeedback()){
								mark.setFeedback(dialog.getFeedbackArray());
							}
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
							toolbar.getMarkingButtons().changeColor(criterion.getId());

							EMarkingWeb.markingInterface.getRubricInterface().getToolsPanel().
							getPreviousComments().addMarkAsCommentToInterface(mark, true);							

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
					"&sesskey="+EMarkingConfiguration.getSessKey();
		} else {
			// Ajax URL for deleting mark
			url = "action=deletemark"+
					"&level="+ rubricMark.getLevelId() +
					"&sesskey="+EMarkingConfiguration.getSessKey();

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
					if(EMarkingConfiguration.getMarkingType() == EMarkingConfiguration.EMARKING_TYPE_MARKER_TRAINING){
						rubricMark.removeCollaborativeButtons();
					}
					rubricInterface.getToolsPanel().getPreviousComments().deletePreviousComment(rubricMark.getRawtext());
					setFinalgrade(newgrade, timemodified);
					Mark.markPopup.setVisible(false);
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

	public void setColoredRubric(boolean colored) {
		
		Date oneyear = new Date();
		CalendarUtil.addMonthsToDate(oneyear, 12);
				
		Cookies.setCookie("emarking_showcolors", colored ? "1" : "0", oneyear);
		EMarkingConfiguration.setColoredRubric(colored);
		
		EMarkingWeb.markingInterface.loadInterface();
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

		for(BubbleButton b : bubbleButtons) {
			b.setLeft(Window.getClientWidth()-40);
			b.updatePosition(markingPanel);
			if(b instanceof ShowRubricButton) {
				b.setVisible(!EMarkingConfiguration.isShowRubricOnLoad());
			} else {
				b.setVisible(EMarkingConfiguration.isChatEnabled());
			}
			
			b.setVisible(false);
		}		

		
		int rubricWidth = (int) (Window.getClientWidth() - (Window.getClientWidth() / 1.61803));
		rubricMinSize = (int) Math.max(rubricWidth, 300);
		if(EMarkingConfiguration.getMarkingType() == EMarkingConfiguration.EMARKING_TYPE_PRINT_SCAN) {
			rubricWidth = rubricMinSize - 10;
		}
		interfacePanel.addEast(rubricInterface, rubricWidth);
		interfacePanel.setWidgetMinSize(rubricInterface, 10);
		interfacePanel.add(markingPagesInterface);
		interfacePanel.setWidgetMinSize(markingPagesInterface, rubricMinSize);
		interfacePanel.setHeight((Window.getClientHeight() - toolbar.getOffsetHeight()) + "px");

		// When we set the rubric visibility we call the loadinterface in the markinginterface object
		rubricInterface.setVisible(
				EMarkingConfiguration.isShowRubricOnLoad() &&
				EMarkingConfiguration.getMarkingType() != EMarkingConfiguration.EMARKING_TYPE_PRINT_SCAN);
		logger.info("Rubric interface visibility: \n"
				+ "isShowRubricOnLoad: " + (EMarkingConfiguration.isShowRubricOnLoad() ? "SI" : "NO")
				+ "EmarkingType: " + EMarkingConfiguration.getMarkingType());
	}

	public void setShowRubricButtonVisible(boolean visible) {
		bubbleButtons.get(0).setVisible(false);
	}

	/**
	 * Loads submission data using global submission id
	 */
	public void loadSubmissionData() {

		// Checks that global submission id is valid
		if(MarkingInterface.draftId <= 0)
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
				submissionData = SubmissionGradeData.createFromConfiguration(values);
				
				if(submissionData != null) {
					EMarkingWeb.markingInterface.loadInterface();
				} else {
					Window.alert(MarkingInterface.messages.InvalidSubmissionData());
				}

				Window.setTitle("Emarking " + submissionData.getCoursename() + " " + submissionData.getActivityname());

				if(EMarkingConfiguration.isChatEnabled()) {
					activateChat();
				}

				finishLoading();
				
				Window.addResizeHandler(new ResizeHandler() {			
					@Override
					public void onResize(ResizeEvent event) {
						resizeTime = new Date();
						if(!resizeTimeout) {
							resizeTimeout=true;
							resizeTimer.schedule(200);
						}
					}
				});
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

					// Parse Json values
					Map<String, String> value = AjaxRequest.getValueFromResult(result);

					try {
						EMarkingConfiguration.loadConfiguration(value);
					} catch (Exception e) {
						e.printStackTrace(System.out);
						logger.severe(MarkingInterface.messages.ErrorLoadingSubmission() + e.toString());
						Window.alert(MarkingInterface.messages.ErrorLoadingSubmission() + e.toString());
						return;
					}

					// Clear interface (submission and rubric interfaces)
					interfacePanel.clear();

					// Cancel timer, we don't need to ping again
					timer.cancel();

					// Load submission data
					loadSubmissionData();

					focusPanel.getElement().focus();

					// Schedule heartbeat if configured as
					if((value.get("heartbeat") != null && value.get("heartbeat").equals("1"))) {
						// Every two minutes
						heartBeatTimer.scheduleRepeating(2 * 60 * 1000);
					}
				} else {
					// Keep trying if something fails every few seconds
					timer.scheduleRepeating(1000);
				}
			}
		});
	}

	private void activateChat() {

		final String nodepath = EMarkingConfiguration.getNodejspath() + "/socket.io/socket.io.js";

		ScriptInjector.fromUrl(nodepath).setCallback(new Callback<Void, Exception>() {

			@Override
			public void onFailure(Exception reason) {
				logger.severe("Could not find node server " + nodepath);
				EMarkingConfiguration.setChatServerError(true);
				disableCollaboration();
			}

			@Override
			public void onSuccess(Void result) {
				try {
					
					if(EMarkingWeb.chatServer != null)
						return;
					
					chat = new ChatInterface();
					wall = new WallInterface();
					sos = new SendSosDialog();
					help = new HelpInterface();

					EMarkingWeb.chatServer = 
							new NodeChat();

				} catch (Exception e) {
					e.printStackTrace();
					logger.severe("Fatal error trying to load NodeJS. Disabling collaborative features.");
					EMarkingConfiguration.setChatServerError(true);
					disableCollaboration();
				}					
			}
		}).inject();
	}

	public void regradeMark(final RubricMark mark, final String comment, final int motive) {

		RootPanel.get().getElement().getStyle().setCursor(Cursor.WAIT);
		final MarkingPage page = markingPagesInterface.getPageByIndex(mark.getPageno()-1);
		if(page == null) {
			logger.severe("Page is null for page index " + mark.getPageno());
			return;
		}
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
				mark.setMarkHTML();

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
			Window.alert("Invalid submission id value " + draftId);
			return;
		}

		draftId = newSubmissionId;

		reloadPage();
	}

	public void reloadPage() {
		String madeURL = Window.Location.getProtocol()+"//"+Window.Location.getHost()+Window.Location.getPath()+"?";
		for(String key : Window.Location.getParameterMap().keySet()) {
			String value = Window.Location.getParameterMap().get(key).get(0);
			if(key.equals("ids")) {
				value = draftId+"";
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
		toolbar.getGrade().loadSubmissionData();
		focusPanel.getElement().focus();
	}

	/**
	 * A new grade was assigned through marking
	 * 
	 * @param newgrade new final grade
	 * @param timemodified when was modified
	 */
	public void setTimemodified(long timemodified) {
		submissionData.setDatemodified(timemodified);
	}
	private float setBonus = -1;
	//Sets a preset bonus for the next time the addrubric dialog is opened (used in delphi)
	public void setDialogNewBonus(float bonus){
		this.setBonus = bonus;
	}
	
	public void addNotificationToBubbleButton(int source) {
		for(BubbleButton btn : this.bubbleButtons) {
			if(btn.getSource() == source) {
				btn.addNotification();
			}
		}
		getToolbar().getChatButtons().addNotification(source);
	}

	public void removeNotificationToBubbleButton(int source) {
		for(BubbleButton btn : this.bubbleButtons) {
			if(btn.getSource() == source) {
				btn.removeNotification();
			}
		}
		getToolbar().getChatButtons().removeNotification(source);
	}
	
	public void disableCollaboration() {
		for(BubbleButton b : bubbleButtons) {
			if(!(b instanceof ShowRubricButton)) {
				b.setVisible(false);
			}
		}
		this.toolbar.getChatButtons().loadSubmissionData();
	}
	
	public void hideRubric() {
		Mark.hideIcons();
		Mark.markPopup.setVisible(false);
		if(interfacePanel.getWidgetSize(rubricInterface) < 10) {
			interfacePanel.setWidgetSize(rubricInterface, rubricMinSize);			
			markingPagesInterface.resizePage(interfacePanel.getOffsetWidth() - rubricMinSize);
		} else {
			interfacePanel.setWidgetSize(rubricInterface, 0);
			markingPagesInterface.resizePage(interfacePanel.getOffsetWidth());
		}
	}
	public AbsolutePanel getMarkingPanel() {
		return this.markingPanel;
	}
	/**
	 * 
	 * @param html
	 * @param b
	 */
	public void addPreviousComment(String html, boolean b) {
		
	}
}
