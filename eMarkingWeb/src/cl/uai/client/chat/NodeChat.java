package cl.uai.client.chat;

import java.util.logging.Logger;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.ScriptInjector;

/**
 * This class represents a connection to a NodeJS which should work with Moodle
 * 
 * @author Francisco García
 *
 */
public class NodeChat {

	/**
	 * Private class for Callback to simplify code in constructor
	 * 
	 * @author Jorge
	 *
	 */
	private class NodeSetupCallback implements Callback<Void, Exception> {
		@Override
		public void onFailure(Exception reason) {
			logger.severe("Could not find Nodejs server!");
			working = false;
		}

		@Override
		public void onSuccess(Void result) {
			loadNodeJS(path, username, coursemodule,userid,draftid);			
			working = true;
		}
	}
	
	/** Sources or rooms **/
	public static int SOURCE_CHAT = 1;
	public static int SOURCE_WALL = 2;
	public static int SOURCE_SOS = 3;

	public static int SOURCE_HELP= 3;

	/** For logging purposes */
	private static Logger logger = Logger.getLogger(NodeChat.class.getName());

	/** If the chat server is working **/
	public static boolean working = false;

	/** Interfaces **/
	public static ChatInterface chat = null;
	public static SosInterface sos = null;
	public static ChatInterface wall = null;
	public static HelpInterface help = null;

	/** Data for a client to work **/
	private String username = null;
	private int userid = 0;
	private int coursemodule = 0;
	private String userRole;
	private int draftid=0;
	private String path = "http://127.0.0.1:9091/";

	/**
	 * NodeChat constructor, representing a connection to the NodeJs server
	 * 
	 * @param _username
	 * @param _userid
	 * @param _coursemodule
	 * @param _userrole
	 * @param _draftid
	 */
	public NodeChat(String _username, int _userid, int _coursemodule, String _userrole, int _draftid) {
		this.username = _username;
		this.userid = _userid;
		this.coursemodule = _coursemodule;
		this.userRole = _userrole;
		this.draftid = _draftid;

		logger.info("Starting Node server in: "+ path);
		logger.info("Username: "+ this.username 
				+ " User id: "+ this.userid
				+ " Course module: "+ this.coursemodule
				+ " User role: "+ this.userRole
				+ " Draft id: "+ this.draftid);

		ScriptInjector.fromUrl(path+"socket.io/socket.io.js").setCallback(new NodeSetupCallback()).inject();
	}

	public int getCoursemodule() {
		return coursemodule;
	}
	public int getDraftid() {
		return draftid;
	}
	public String getPath() {
		return path;
	}
	public int getUserid() {
		return userid;
	}
	public String getUsername() {
		return username;
	}

	public String getUserRole() {
		return userRole;
	}

	//////////////// CONEXIÓN CON EL SERVIDOR NODE /////////////////////////////
	private  native void loadNodeJS(String path, String Username, int coursemodule, int userid, int submissionId) /*-{
	 $wnd.socket = io.connect(path);

	 var tmp=this;
	 $wnd.socket.on('connect', function () {
		   var conectionData={};
	       conectionData.Username=Username;
	       conectionData.cm=coursemodule;
	       conectionData.userid=userid;
	       conectionData.submissionId=submissionId
	       $wnd.socket.emit("joinserver",JSON.stringify(conectionData));
	       $wnd.socket.on('userJoin', function (data) {

			      var obj=JSON.parse(data);

				tmp.@cl.uai.client.chat.NodeChat::userJoin(Lcl/uai/client/chat/UserData;Lcom/google/gwt/core/client/JsArray;)(obj.user,obj.people); 
			  });
	     $wnd.socket.on('onRemoveChatUser', function (data) {

			      var data=JSON.parse(data);
				 tmp.@cl.uai.client.chat.NodeChat::onRemoveChatUser(Lcl/uai/client/chat/UserData;)(data); 
			  });
	  $wnd.socket.on('onBeginChatOther', function (data) {

			      var data=JSON.parse(data);
				 tmp.@cl.uai.client.chat.NodeChat::onBeginChatOther(Lcl/uai/client/chat/UserData;)(data); 
			  });
	  $wnd.socket.on('onCatchMesage', function (data) {

			      var data=JSON.parse(data);
				 tmp.@cl.uai.client.chat.NodeChat::onCatchMessage(Lcl/uai/client/chat/Message;)(data); 
			  });
             $wnd.socket.on('onCatchSos', function (data) {

			      var data=JSON.parse(data);
				 tmp.@cl.uai.client.chat.NodeChat::onCatchSos(Lcl/uai/client/chat/Sos;)(data); 
			  });
			   $wnd.socket.on('onMessegeSent', function (data) {

			      var data=JSON.parse(data);
				 tmp.@cl.uai.client.chat.NodeChat::onMessageSent(Lcl/uai/client/chat/Message;)(data); 
			  });

	 }); 

}-*/;
	private void onBeginChatOther(UserData user){

		chat.adduser(user.getName(),Integer.parseInt(user.getId()),user.getColor());

	}
	private void onCatchMessage(Message message){

		switch(message.getSource()){

		case 1:
			chat.addReceivedMessage(message.getTime(), message.getUser(), message.getMessage(), message.getColor());
			break;
		case 2:
			wall.addReceivedMessage(message.getTime(), message.getUser(), message.getMessage(), message.getColor());
			break;
		}

	}
	private void onCatchSos(Sos sos){



		help.addReceivedSos(sos.getUserName(),
				sos.getTime(),
				sos.getComment(),
				sos.getDraftId(),
				sos.getStatus(),
				sos.getUrgencyLevel()
				);

	}

	private void onMessageSent(Message message){

		switch(message.getSource()){

		case 1:
			chat.mensajeEnvidoCorrectamente(message.getId());
			break;
		case 2:
			wall.mensajeEnvidoCorrectamente(message.getId());
			break;
		}

	}

	private void onRemoveChatUser(UserData user){

		chat.removeUser(Integer.parseInt(user.getId()));

	}

	public native void onSendMessage(String message, int source, int messageid) /*-{
	   var msn={};
	   msn.source=source;
	   msn.message=message;
	   msn.messageid=messageid;

	   $wnd.socket.emit("onSendMessage",JSON.stringify(msn));


	}-*/;

	//////////////////////////////INTERFAZ/////////////////////////////////////////




	/////////////////////////funciones para unir información node y gwt/////////////////

	public  native void onSendSos(String comment, int urgencyLevel) /*-{
	   var data={};
	   data.urgencyLevel=urgencyLevel;
	   data.comment=comment;
	   $wnd.socket.emit("onSendSos",JSON.stringify(data));


	}-*/;

	public void setCoursemodule(int coursemodule) {
		this.coursemodule = coursemodule;
	}

	public void setDraftid(int draftid) {
		this.draftid = draftid;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public void setUserid(int userid) {
		this.userid = userid;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}
	
	private void userJoin(UserData user, JsArray<UserData> people){


		chat.addHistoryMessages();
		wall.addHistoryMessages();
		help.addHistorySos();

		for(int i=0;i<people.length();i++){
			if(people.get(i).getName().equals(user.getName())|| people.get(i).getRoom()!=user.getRoom())continue;
			chat.adduser(people.get(i).getName(),Integer.parseInt(people.get(i).getId()),people.get(i).getColor());

		}

		chat.adduser(user.getName(),Integer.parseInt(user.getId()),1);
	}





}
