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
import java.util.logging.Logger;

import cl.uai.client.EMarkingWeb;
import cl.uai.client.MarkingInterface;
import cl.uai.client.data.SubmissionGradeData;

import com.google.gwt.core.client.JsArray;

/**
 * This class represents a connection to a NodeJS which should work with Moodle
 */
public class NodeChat {

	/** For logging purposes */
	private static Logger logger = Logger.getLogger(NodeChat.class.getName());

	/** Sources or rooms **/
	public static int SOURCE_CHAT = 1;
	public static int SOURCE_WALL = 2;
	public static int SOURCE_SOS = 3;

	/**
	 * NodeChat constructor, representing a connection to the NodeJs server
	 */
	public NodeChat() throws Exception {
		// Submission data has all the info we need
		SubmissionGradeData sdata = MarkingInterface.submissionData;
		
		// But if it is null, the chat is being created too soon
		if(sdata == null) {
			throw new Exception("Invalid data to start NodeJs server. Submission data must be loaded first.");
		}
		
		logger.info("Attempting to connect to Node server in: "+ MarkingInterface.nodejspath);

		// Let's try to connect to Node
		loadNodeJS(MarkingInterface.nodejspath, 
				sdata.getMarkerfirstname(), 
				sdata.getMarkerlastname(), 
				sdata.getMarkeremail(), 
				sdata.getCoursemoduleid(), 
				sdata.getMarkerid());
	}

	/**
	 * This method tries to connect the GWT client with NodeJS
	 * 
	 * @param path URL of the Node server
	 * @param firstname Current user first name
	 * @param lastname Current user last name
	 * @param email Current user email
	 * @param coursemodule Course module (used as room)
	 * @param userid Current user id
	 */
	private  native void loadNodeJS(String path, String firstname, String lastname, String email, int coursemodule, int userid) /*-{
		$wnd.socket = io.connect(path);

	 	var tmp=this;
	 	
	 	// When the socket connects successfully
	 	$wnd.socket.on('connect', function () {

	 		// Listen to joinserver events
	    	$wnd.socket.on('onJoinServer', function (data) {
				var data=JSON.parse(data);
				tmp.@cl.uai.client.chat.NodeChat::onJoinServer(Lcom/google/gwt/core/client/JsArray;)(data); 
			});
	 		// Listen to disconnection events
	    	$wnd.socket.on('onDisconnect', function (data) {
            	var data=JSON.parse(data);
				tmp.@cl.uai.client.chat.NodeChat::onDisconnect(Lcl/uai/client/chat/UserData;)(data); 
			});
			// Listen to messages events
	    	$wnd.socket.on('onSendMessage', function (data) {
				var data=JSON.parse(data);
				tmp.@cl.uai.client.chat.NodeChat::onSendMessage(Lcl/uai/client/chat/Message;)(data); 
			});
			
	 		// We create the object with data
			var conectionData={};
	    	conectionData.first=firstname;
	    	conectionData.last=lastname;
	    	conectionData.email=email;
	    	conectionData.cm=coursemodule;
	    	conectionData.userid=userid;

	    	// We emit a joinserver event with the connection data
	    	$wnd.socket.emit("joinserver", JSON.stringify(conectionData));	
	 	}); 
	}-*/;

	/**
	 * This event is linked to the userJoin (emit)
	 * @param connectedUsers a collection of one UserData per connected user
	 */
	private void onJoinServer(JsArray<UserData> connectedUsers) {
		// Add every user to the interface
		for(int i=0;i<connectedUsers.length();i++) {
			EMarkingWeb.markingInterface.chat.addUser(connectedUsers.get(i));
			EMarkingWeb.markingInterface.wall.addUser(connectedUsers.get(i));
			EMarkingWeb.markingInterface.help.addUser(connectedUsers.get(i));
		}
		// Load previous messages
		EMarkingWeb.markingInterface.chat.loadHistoryMessages();
		EMarkingWeb.markingInterface.wall.loadHistoryMessages();
		EMarkingWeb.markingInterface.help.loadHistoryMessages();
	}

	/**
	 * Called if a message was sent to our room in NodeJS
	 * 
	 * @param message Message object
	 * @throws Exception 
	 */
	private void onSendMessage(Message message) throws Exception {
		Date today = dateFromUnixTime(message.getTime());
		int userid = Integer.parseInt(message.getUserId());
		if(message.getSource() == NodeChat.SOURCE_CHAT) {
			EMarkingWeb.markingInterface.chat.addMessage(today, userid, message.getMessage());
		} else if(message.getSource() == NodeChat.SOURCE_WALL) {
			EMarkingWeb.markingInterface.wall.addMessage(today, Integer.parseInt(message.getUserId()), message.getMessage());
		} else if(message.getSource() == NodeChat.SOURCE_SOS) {
			EMarkingWeb.markingInterface.help.addMessage(today, userid, message.getMessage(), message.getDraftId(), message.getStatus(), message.getUrgency());
		} else {
			logger.severe("Something is very wrong");
		}
	}

	/**
	 * This event is linked to the disconnect from NodeJs
	 * @param user
	 */
	private void onDisconnect(UserData user) {
		EMarkingWeb.markingInterface.chat.removeUser(user);
	}

	/**
	 * Sends a message to NodeJS
	 * @param userid the user sending the message
	 * @param message the message
	 * @param source the subroom to where it belongs (wall, sos or chat)
	 * @param draftid draft id
	 * @param status the help status
	 * @param urgency the urgency level
	 */
	public native void sendMessage(int userid, String message, int source, int draftid, int status, int urgency) /*-{
	   var msn={};
	   msn.userid=userid;
	   msn.source=source;
	   msn.message=message;
	   msn.draftid=draftid;
	   msn.status=status;
	   msn.urgency=urgency;

	   $wnd.socket.emit("sendmessage", JSON.stringify(msn));
	}-*/;

	/**
	 * Converts a sting containing a unix time to Date
	 * 
	 * @param unixtime
	 * @return
	 */
	public static Date dateFromUnixTime(String unixtime) {
		int timeCreated= Integer.parseInt(unixtime);
		return dateFromUnixTime(timeCreated);
	}

	/**
	 * Converts an int containing a unix time to Date
	 * 
	 * @param unixtime
	 * @return
	 */
	public static Date dateFromUnixTime(int unixtime) {
		long unixTime = (long) (unixtime/.001);
		Date today = new Date(unixTime);
		return today;
	}


}
