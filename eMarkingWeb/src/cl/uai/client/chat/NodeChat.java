package cl.uai.client.chat;

import java.util.logging.Logger;

import cl.uai.client.data.AjaxData;
import cl.uai.client.data.AjaxRequest;
import cl.uai.client.resources.Resources;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class NodeChat {
	
	public static int SOURCE_CHAT = 1;
	public static int SOURCE_WALL = 2;
	public static int SOURCE_SOS = 3;
	
	/** For logging purposes */
	private static Logger logger = Logger.getLogger(NodeChat.class.getName());
	private static String path;
	public static String username = null;
	public static int userid = 0;
	public static int coursemodule = 0;
	public static ChatInterface chat = null;
	/** Connected users div**/
	public static String userRole;
	public static String moodleurl=null;
	public static int submissionId=0;


	public NodeChat() {
		path="http://127.0.0.1:9091/";

		GWT.log("RUTA NODE: "+ path);

		ScriptInjector.fromUrl(path+"socket.io/socket.io.js").setCallback(new Callback<Void, Exception>() {

			@Override
			public void onSuccess(Void result) {
				// TODO Auto-generated method stub

				loadNodeJS(path, username, coursemodule,userid,submissionId);

			}
			@Override
			public void onFailure(Exception reason) {
				// TODO Auto-generated method stub
				Window.alert("Servicio de chat en: \n "+path+"\n CAIDO !!!!");
			}
		}).inject(); 

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
				 tmp.@cl.uai.client.chat.NodeChat::onCatchMesage(Lcl/uai/client/chat/Message;)(data); 
			  });
             $wnd.socket.on('onCatchSos', function (data) {

			      var data=JSON.parse(data);
				 tmp.@cl.uai.client.chat.NodeChat::onCatchSos(Lcl/uai/client/chat/Sos;)(data); 
			  });

	 }); 

}-*/;

	//////////////////////////////INTERFAZ/////////////////////////////////////////




	/////////////////////////funciones para unir información node y gwt/////////////////

	private void userJoin(UserData user, JsArray<UserData> people){

		String params= "&ids2=&room="+coursemodule+"&source=1";
		AjaxRequest.ajaxRequest("action=getchathistory"+ params, new AsyncCallback<AjaxData>() {
			@Override
			public void onSuccess(AjaxData result) {
				logger.info("Heartbeat! ");

				

			}
			@Override
			public void onFailure(Throwable caught) {
				logger.warning("Failure on heartbeat");
			}
		});

		for(int i=0;i<people.length();i++){
			if(people.get(i).getName().equals(user.getName())|| people.get(i).getRoom()!=user.getRoom())continue;
			chat.adduser(people.get(i).getName(),Integer.parseInt(people.get(i).getId()),people.get(i).getColor());

		}

		chat.adduser(user.getName(),Integer.parseInt(user.getId()),1);
	}

	private void onBeginChatOther(UserData user){

		chat.adduser(user.getName(),Integer.parseInt(user.getId()),user.getColor());

	}

	private void onRemoveChatUser(UserData user){

		chat.removeUser(Integer.parseInt(user.getId()));

	}
	public native void onSendMessage(String message, int source) /*-{
	   var msn={};
	   msn.source=source;
	   msn.message=message;

	   $wnd.socket.emit("onSendMessage",JSON.stringify(msn));


	}-*/;
	public  native void onSendSos(String comment, int urgencyLevel) /*-{
	   var data={};
	   data.urgencyLevel=urgencyLevel;
	   data.comment=comment;
	   $wnd.socket.emit("onSendSos",JSON.stringify(data));


	}-*/;

	private void onCatchMesage(Message message){

		//chat.formatearMensaje(
		//	message.getTime(),
		//message.getUser(),
		//message.getMessage(),
		//message.getColor(),
		//message.getSource());
	}

	private void onCatchSos(Sos sos){

		formatearSos(sos.getUserName(),
				sos.getTime(),
				sos.getComment(),
				3,
				sos.getSubmissionId(),
				sos.getStatus()
				);

	}

	private void formatearSos(String username, int time, String comment, int source, int submissionId, int status) {
		VerticalPanel vp = new VerticalPanel();
		Label name = new Label(username+":");
		vp.add(name);
		Label text = new Label("\""+comment+"\"");
		vp.add(text);
		//	helpVpanel.add(vp);
		String url = moodleurl+"?action=emarking"+"&ids="+submissionId;
		Anchor link = new Anchor("Link to bar", url);
		link.setTarget("_blank");
		//	helpVpanel.add(link);
		HTML usersIcon = new HTML();
		usersIcon.addStyleName(Resources.INSTANCE.css().chatusers());
		//	helpVpanel.add(usersIcon);
	}




}
