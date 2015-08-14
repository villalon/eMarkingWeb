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
import java.util.HashMap;
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

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
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
	/** Panel that shows what users are currently connected **/
	private HorizontalPanel usersConnectedPanel;
	/** TextArea to send a new message **/
	protected TextArea sendMessageTextArea;

	/** A list with all currently connected users **/
	protected Map<Integer, HTML> connectedUsers = new HashMap<Integer, HTML>();
	/** All users with messages or connected with their abbreviated names **/
	protected Map<Integer, String> allUsersAbbreviations = new HashMap<Integer, String>();
	/** All users with messages or connected with their abbreviated names **/
	protected Map<Integer, String> allUsersFullnames = new HashMap<Integer, String>();
	/** A list with all colors assigned to users **/
	protected Map<Integer, Integer> allUsersColors = new HashMap<Integer, Integer>();

	/**
	 * Creates a new chat interface
	 */
	public ChatInterface() {

		this.source = NodeChat.SOURCE_CHAT;

		this.addStyleName(Resources.INSTANCE.css().chatdialog());

		// Dialog parameters
		this.setAutoHideEnabled(true);
		this.setAnimationEnabled(true);
		this.setModal(true);

		// Initialize interfaces and assign css
		messagesPanel = new VerticalPanel();
		messagesPanel.addStyleName(Resources.INSTANCE.css().chatmessages());

		scrollMessagesPanel = new ScrollPanel(messagesPanel);
		scrollMessagesPanel.addStyleName(Resources.INSTANCE.css().chatscrollmessages());
		scrollMessagesPanel.scrollToBottom();

		usersConnectedPanel = new HorizontalPanel();

		sendMessageTextArea = new TextArea();
		sendMessageTextArea.setVisibleLines(2);
		sendMessageTextArea.addStyleName(Resources.INSTANCE.css().chatTextarea());

		// KeyDown for text area for sending message
		sendMessageTextArea.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					sendMessage(sendMessageTextArea.getValue());
					sendMessageTextArea.setValue("");
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

		this.add(mainPanel);
	}

	/**
	 * Adds a connected user to the interface
	 * @param userdata user data (id, name)
	 */
	public void addUser(UserData userdata) {

		int userid = Integer.parseInt(userdata.getId());
		String abbreviation = userdata.getFirstName().substring(0,1).toUpperCase() 
				+ userdata.getLastName().substring(0,1).toUpperCase();

		// If the user is already connected it means it was already added
		if(connectedUsers.containsKey(userid))
			return;

		addUserToChatHistory(userid, abbreviation, userdata.getFirstName() + " " + userdata.getLastName());

		HTML userConnectedIcon = new HTML();
		userConnectedIcon.setText(abbreviation);
		userConnectedIcon.addStyleName(Resources.INSTANCE.css().chatusers());
		userConnectedIcon.setTitle(userdata.getFirstName() + " " + userdata.getLastName());

		addColorCSStoWidget(allUsersColors.get(userid), userConnectedIcon);

		// We add the user to the list
		connectedUsers.put(userid, userConnectedIcon);

		// Add the icon
		usersConnectedPanel.add(userConnectedIcon);
	}

	/**
	 * Remove user from the interface
	 * @param userdata user data (id, name)
	 */
	public void removeUser(UserData userdata) {
		int userid = Integer.parseInt(userdata.getId());

		HTML userConnectedIcon= connectedUsers.get(userid);

		// If the user is there, remove its icon and remove it from the list
		if(userConnectedIcon != null && userConnectedIcon.getParent() != null) {
			userConnectedIcon.removeFromParent();
			connectedUsers.remove(userid);
		}
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
				"&draftid=" + MarkingInterface.getSubmissionId();

		AjaxRequest.ajaxRequest("action=addchatmessage"+ params, new AsyncCallback<AjaxData>() {
			@Override
			public void onSuccess(AjaxData result) {
			}
			@Override
			public void onFailure(Throwable caught) {
			}
		});

		EMarkingWeb.chatServer.sendMessage(sdata.getMarkerid(), message, source, MarkingInterface.getSubmissionId(), status, urgency);
	}

	/**
	 * Load all messages in history
	 */
	public void loadHistoryMessages() {

		if(historyLoaded)
			return;

		String params= "&ids="+ MarkingInterface.getSubmissionId() +
				"&room=" + MarkingInterface.submissionData.getCoursemoduleid() + 
				"&source=" + source;

		AjaxRequest.ajaxRequest("action=getchathistory"+ params, new AsyncCallback<AjaxData>() {
			@Override
			public void onSuccess(AjaxData result) {

				List<Map<String, String>> messageHistory = AjaxRequest.getValuesFromResult(result);

				for(Map<String, String> message : messageHistory) {

					Date today = NodeChat.dateFromUnixTime(message.get("timecreated"));
					int userid=Integer.parseInt(message.get("userid"));

					String firstname=message.get("firstname");
					String lastname=message.get("lastname");
					String abbreviation = firstname.substring(0,1).toUpperCase() 
							+ lastname.substring(0,1).toUpperCase();
					String msg = message.get("message");

					addUserToChatHistory(userid, abbreviation, firstname + " " + lastname);

					try {
						if(source == NodeChat.SOURCE_SOS) {
							int draftid = Integer.parseInt(message.get("draftid"));
							int status = Integer.parseInt(message.get("status"));
							int urgency = Integer.parseInt(message.get("urgencylevel"));
							EMarkingWeb.markingInterface.help.addMessage(today, userid, msg, draftid, status, urgency);
						} else {
							addMessage(today, userid, msg);
						}
					} catch (Exception e) {
						e.printStackTrace();
						logger.severe(e.getLocalizedMessage());
					}
				}

				historyLoaded = true;
				scrollMessagesPanel.scrollToBottom();
			}

			@Override
			public void onFailure(Throwable caught) {
				logger.warning("WTF ERROR");
			}
		});
	}

	/**
	 * Adds a user to the chat histoy of this interface
	 * @param userid user id
	 * @param abbreviation user name abbreviated
	 */
	protected void addUserToChatHistory(int userid, String abbreviation, String fullname) {
		if(!allUsersAbbreviations.containsKey(userid)) {
			allUsersAbbreviations.put(userid, abbreviation);
			int color = (allUsersAbbreviations.size() % 19) + 1;
			allUsersColors.put(userid, color);
		}
	}

	/**
	 * Adds a message to the interface
	 * 
	 * @param date
	 * @param author
	 * @param message
	 */
	public void addMessage(ChatMessage msg) throws Exception {

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
				userid, 
				date,
				allUsersAbbreviations.get(userid), 
				allUsersFullnames.get(userid),
				message,
				allUsersColors.get(userid));

		addMessage(chatMessage);
	}

	/**
	 * Adds a CSS class with a specific color to a widget as background color
	 * @param sequence sequence
	 * @param widget the widget to be painted
	 */
	public static void addColorCSStoWidget(int sequence, Widget widget) {

		switch(sequence) {
		case 1:  widget.addStyleName(Resources.INSTANCE.css().color1()); break;
		case 2:  widget.addStyleName(Resources.INSTANCE.css().color2()); break;
		case 3:  widget.addStyleName(Resources.INSTANCE.css().color3()); break;
		case 4:  widget.addStyleName(Resources.INSTANCE.css().color4()); break;
		case 5:  widget.addStyleName(Resources.INSTANCE.css().color5()); break;
		case 6:  widget.addStyleName(Resources.INSTANCE.css().color6()); break;
		case 7:  widget.addStyleName(Resources.INSTANCE.css().color7()); break;
		case 8:  widget.addStyleName(Resources.INSTANCE.css().color8()); break;
		case 9:  widget.addStyleName(Resources.INSTANCE.css().color9()); break;
		case 10:  widget.addStyleName(Resources.INSTANCE.css().color10()); break;
		case 11:  widget.addStyleName(Resources.INSTANCE.css().color11()); break;
		case 12:  widget.addStyleName(Resources.INSTANCE.css().color12()); break;
		case 13:  widget.addStyleName(Resources.INSTANCE.css().color13()); break;
		case 14:  widget.addStyleName(Resources.INSTANCE.css().color14()); break;
		case 15:  widget.addStyleName(Resources.INSTANCE.css().color15()); break;
		case 16:  widget.addStyleName(Resources.INSTANCE.css().color16()); break;
		case 17:  widget.addStyleName(Resources.INSTANCE.css().color17()); break;
		case 18:  widget.addStyleName(Resources.INSTANCE.css().color18()); break;
		case 19:  widget.addStyleName(Resources.INSTANCE.css().color19()); break;
		}
	}
}
