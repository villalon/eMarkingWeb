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
 * @copyright 2015-onwards Jorge Villalón <villalon@gmail.com>
 * 				   Francisco García <francisco.garcia.ralph@gmail.com>
 * @license   http://www.gnu.org/copyleft/gpl.html GNU GPL v3 or later
 */
package cl.uai.client.chat;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import cl.uai.client.EMarkingWeb;
import cl.uai.client.MarkingInterface;
import cl.uai.client.chat.messages.ChatMessage;
import cl.uai.client.data.AjaxData;
import cl.uai.client.data.AjaxRequest;
import cl.uai.client.data.SubmissionGradeData;
import cl.uai.client.resources.Resources;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Chat Interface class represents a dialog box which contains all the messages
 * in a chat session within e-marking.
 * 
 * @authors Francisco García y Jorge Villalón
 *
 */
public class ChatInterface extends DialogBox {

	/** For logging purposes */
	protected static Logger logger = Logger.getLogger(MarkingInterface.class.getName());

	/** The source (SOS, Wall or Chat) **/
	protected int source=0;

	/** If the history was already loaded **/
	protected boolean historyLoaded = false;
	/** Main panel contains the whole chat **/
	private VerticalPanel mainPanel;
	/** Panel that contains the messages **/
	private VerticalPanel messagesPanel;
	/** Scroll for the messages **/
	private ScrollPanel scrollMessagesPanel;
	/** Close button **/
	private HTML closeButton;
	
	/** Timer to update dates in the chat **/
	private Timer timer = null;
	
	/** Panel that shows what users are currently connected **/
	private ConnectedUsersPanel usersConnectedPanel;

	/** TextArea to send a new message **/
	protected TextArea sendMessageTextArea;

	private Date lastOpen = null;
	
	@Override
	public void show() {
		super.show();
		this.lastOpen = new Date();
		EMarkingWeb.markingInterface.removeNotificationToBubbleButton(this.source);
		scrollMessagesPanel.scrollToBottom();
		sendMessageTextArea.setFocus(true);
	}
	
	/**
	 * Creates a new chat interface
	 */
	public ChatInterface() {

		this.source = NodeChat.SOURCE_CHAT;

		this.addStyleName(Resources.INSTANCE.css().chatdialog());

		// Dialog parameters
		this.setAutoHideEnabled(false);
		this.setAnimationEnabled(true);
		this.setModal(false);
		this.setHTML(MarkingInterface.messages.Chat());

		// Initialize interfaces and assign css
		messagesPanel = new VerticalPanel();
		messagesPanel.addStyleName(Resources.INSTANCE.css().chatmessages());

		scrollMessagesPanel = new ScrollPanel(messagesPanel);
		scrollMessagesPanel.addStyleName(Resources.INSTANCE.css().chatscrollmessages());
		scrollMessagesPanel.scrollToBottom();

		usersConnectedPanel = new ConnectedUsersPanel();

		sendMessageTextArea = new TextArea();
		sendMessageTextArea.setVisibleLines(2);
		sendMessageTextArea.addStyleName(Resources.INSTANCE.css().chatTextarea());

		// KeyDown for text area for sending message
		sendMessageTextArea.addKeyDownHandler(new KeyDownHandler() {
			
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					event.stopPropagation();
					String msg = sendMessageTextArea.getValue();
					msg = msg.replace("\n", "");
					sendMessage(msg);
					sendMessageTextArea.setValue(null);
				}
			}
		});
		
		// Vertical panel that contains everything
		mainPanel = new VerticalPanel(); 
		mainPanel.addStyleName(Resources.INSTANCE.css().chatmainpanel());

		mainPanel.add(usersConnectedPanel);
		mainPanel.setCellHorizontalAlignment(usersConnectedPanel, HasAlignment.ALIGN_CENTER);
		mainPanel.add(scrollMessagesPanel);
		mainPanel.add(sendMessageTextArea);
		
		closeButton = new HTML(MarkingInterface.messages.Close());
		closeButton.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		mainPanel.add(closeButton);
		mainPanel.setCellHorizontalAlignment(closeButton, HasAlignment.ALIGN_RIGHT);

		timer = new Timer() {
			@Override
			public void run() {
				for(int i=0; i<messagesPanel.getWidgetCount();i++) {
					if(messagesPanel.getWidget(i) instanceof ChatMessage) {
						ChatMessage chatMessage = (ChatMessage) messagesPanel.getWidget(i);
						chatMessage.updateElapsedTime();
					}
				}
			}
		};
		
		timer.scheduleRepeating(1000 * 60); // Every minute
		
		this.setWidget(mainPanel);
	}
	
	/**
	 * Adds a message to the interface
	 * 
	 * @param date
	 * @param author
	 * @param message
	 */
	public void addMessage(ChatMessage msg) throws Exception {

		if(!this.isShowing() && (lastOpen == null || lastOpen.before(NodeChat.lastMessages.get(this.source)))) {
			EMarkingWeb.markingInterface.addNotificationToBubbleButton(this.source);
		}
		
		// Panel is added and interface scrolled to the bottom
		messagesPanel.add(msg);
		messagesPanel.setCellHorizontalAlignment(msg, msg.isOwnMessage() 
				? HasAlignment.ALIGN_RIGHT : HasAlignment.ALIGN_LEFT);
		scrollMessagesPanel.scrollToBottom();
	}

	/**
	 * Adds a message to the interface
	 * 
	 * @param date
	 * @param author
	 * @param message
	 */
	public void addMessage(Date date, int userid, String message) throws Exception {

		// The message panel
		ChatMessage chatMessage = new ChatMessage(
				ConnectedUsersPanel.allUsers.get(userid),
				date,
				message);

		addMessage(chatMessage);
	}


	public ConnectedUsersPanel getUsersConnectedPanel() {
		return usersConnectedPanel;
	}

	/**
	 * Load all messages in history
	 */
	public void loadHistoryMessages() {

		if(historyLoaded)
			return;

		String params= "&ids="+ MarkingInterface.getDraftId() +
				"&room=" + MarkingInterface.submissionData.getCoursemoduleid() + 
				"&source=" + source;

		AjaxRequest.ajaxRequest("action=getchathistory"+ params, new AsyncCallback<AjaxData>() {
			@Override
			public void onFailure(Throwable caught) {
				logger.warning("WTF ERROR");
			}

			@Override
			public void onSuccess(AjaxData result) {

				List<Map<String, String>> messageHistory = AjaxRequest.getValuesFromResult(result);

				for(Map<String, String> message : messageHistory) {

					Date today = NodeChat.dateFromUnixTime(message.get("timecreated"));
					String msg = message.get("message");

					User user = User.createFromJson(message);
					user = ConnectedUsersPanel.addUserFromHistory(user);

					try {
						if(source == NodeChat.SOURCE_SOS) {
							int draftid = Integer.parseInt(message.get("draftid"));
							int status = Integer.parseInt(message.get("status"));
							int urgency = Integer.parseInt(message.get("urgencylevel"));
							EMarkingWeb.markingInterface.help.addMessage(today, user.getId(), msg, draftid, status, urgency);
						} else {
							addMessage(today, user.getId(), msg);
						}
					} catch (Exception e) {
						e.printStackTrace();
						logger.severe(e.getLocalizedMessage());
					}
				}

				historyLoaded = true;
				scrollMessagesPanel.scrollToBottom();
			}
		});
	}

	/**
	 * Sends a message to the node server
	 * @param message the message
	 */
	protected void sendMessage(String  message) {
		sendMessage(message, 0, 0);
	}

	/**
	 * Sends a message to the node server
	 * @param message the message
	 */
	protected void sendMessage(String  message, int urgency, int status) {
		SubmissionGradeData sdata = MarkingInterface.submissionData;

		String params = "&message=" + message + 
				"&source=" + source + 
				"&userid=" + sdata.getMarkerid() + 
				"&room=" + sdata.getCoursemoduleid() + 
				"&status=" + status +
				"&urgencylevel=" + urgency + 
				"&draftid=" + MarkingInterface.getDraftId();

		AjaxRequest.ajaxRequest("action=addchatmessage"+ params, new AsyncCallback<AjaxData>() {
			@Override
			public void onFailure(Throwable caught) {
			}
			@Override
			public void onSuccess(AjaxData result) {
			}
		});

		EMarkingWeb.chatServer.sendMessage(sdata.getMarkerid(), message, source, MarkingInterface.getDraftId(), status, urgency);
	}
}
