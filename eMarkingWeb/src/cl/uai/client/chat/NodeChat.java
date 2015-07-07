package cl.uai.client.chat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import cl.uai.client.resources.Resources;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class NodeChat {
	/** For logging purposes */
	private static Logger logger = Logger.getLogger(NodeChat.class.getName());
	private String path;
	private VerticalPanel Vpanel;
	private VerticalPanel wallVpanel;
	private VerticalPanel wallMessageVpanel;
	private VerticalPanel MessageVpanel;
	private TextArea wallMessage;
	private ScrollPanel scrollWallPanel;
	private HorizontalPanel UsersHpanel;
	private ScrollPanel scrollPanel;
	private TextArea message;
	public static String username = null;
	public static int userid = 0;
	public static int coursemodule = 0;
	/** Connected users div**/
	private Map<Integer, HTML> UserIcon = new HashMap<Integer, HTML>();
	public static String userRole;
	private VerticalPanel askSosVpanel;
	public String moodleurl=null;
	public static int submissionId=0;
	private TextArea sosComent;
	private ListBox urgencyLevel;
	private VerticalPanel helpVpanel;


	public NodeChat(){

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
				Window.alert("Servicio Node.js en: \n "+path+"\n CAIDO !!!!");
			}
		}).inject(); 
	}


	//////////////// CONEXIÓN CON EL SERVIDOR NODE /////////////////////////////
	private native void loadNodeJS(String path, String Username, int coursemodule, int userid, int submissionId) /*-{
	 //Utilzamos <websocket>,si la conexion falla entonces utiliza <xhr-polling>
	 //la conexion  <xhr-polling> no controla el evento "disconnet",la conexion es mantenida unos 30 segundos
	 //aparentemente es un error de OPENSHIFT!!!
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

				tmp.@cl.uai.client.chat.NodeChat::userJoin(Lcl/uai/client/chat/UserData;Lcom/google/gwt/core/client/JsArray;Lcom/google/gwt/core/client/JsArray;Lcom/google/gwt/core/client/JsArray;)(obj.user,obj.people,obj.chatHistory,obj.chatHistory); 
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


	/////////////////////////funciones para unir información node y gwt/////////////////

	private void userJoin(UserData user, JsArray<UserData> people, JsArray<Message> chatHistory, JsArray<Sos> sosHistory){
		for(int i=0;i<chatHistory.length();i++){
			formatearMensaje(chatHistory.get(i).getTime(),chatHistory.get(i).getUser() ,chatHistory.get(i).getMessage(),0,chatHistory.get(i).getSource());
			scrollPanel.scrollToBottom();
		}
		for(int i=0;i<sosHistory.length();i++){
			formatearSos(
					sosHistory.get(i).getUserName(),
					sosHistory.get(i).getTime(),
					sosHistory.get(i).getComment(),
					3,
					sosHistory.get(i).getSubmissionId(),
					sosHistory.get(i).getStatus()
					);


		}
		for(int i=0;i<people.length();i++){
			if(people.get(i).getName().equals(user.getName())|| people.get(i).getRoom()!=user.getRoom())continue;
			adduser(people.get(i).getName(),Integer.parseInt(people.get(i).getId()),people.get(i).getColor());
		}

		adduser(user.getName(),Integer.parseInt(user.getId()),user.getColor());
	}

	private void onBeginChatOther(UserData user){

		adduser(user.getName(),Integer.parseInt(user.getId()),user.getColor());

	}

	private void onRemoveChatUser(UserData user){

		HTML userIcon = UserIcon.get(Integer.parseInt(user.getId()));

		if(userIcon != null) {
			UsersHpanel.remove(userIcon);
			UserIcon.remove(Integer.parseInt(user.getId()));
		}

	}
	public native void onSendMessage(String message, int source) /*-{
	   var msn={};
	   msn.source=source;
	   msn.message=message;

	   $wnd.socket.emit("onSendMessage",JSON.stringify(msn));


	}-*/;
	public native void onSendSos(String comment, int urgencyLevel) /*-{
	   var data={};
	   data.urgencyLevel=urgencyLevel;
	   data.comment=comment;
	   $wnd.socket.emit("onSendSos",JSON.stringify(data));


	}-*/;

	private void onCatchMesage(Message message){

		formatearMensaje(
				message.getTime(),
				message.getUser(),
				message.getMessage(),
				message.getColor(),
				message.getSource());
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
		helpVpanel.add(vp);
		String url = moodleurl+"?action=emarking"+"&ids="+submissionId;
		Anchor link = new Anchor("Link to bar", url);
		link.setTarget("_blank");
		helpVpanel.add(link);
		HTML usersIcon = new HTML();
		usersIcon.addStyleName(Resources.INSTANCE.css().chatusers());
		helpVpanel.add(usersIcon);
	}

	private void formatearMensaje(int time,String name,String  mensaje,int color, int source)
	{

		long ltime = (long) (time/ .001);
		Date today = new Date(ltime);
		DateTimeFormat fmt = DateTimeFormat.getFormat("dd/MM/yyyy h:mm");
		String cad=name+":"+mensaje;
		Label lbl = new Label(cad);
		if(color > 0){
			switch(color) {
			case 1:  lbl.addStyleName(Resources.INSTANCE.css().color1()); break;
			case 2:  lbl.addStyleName(Resources.INSTANCE.css().color2()); break;
			case 3:  lbl.addStyleName(Resources.INSTANCE.css().color3()); break;
			case 4:  lbl.addStyleName(Resources.INSTANCE.css().color4()); break;
			case 5:  lbl.addStyleName(Resources.INSTANCE.css().color5()); break;
			case 6:  lbl.addStyleName(Resources.INSTANCE.css().color6()); break;
			case 7:  lbl.addStyleName(Resources.INSTANCE.css().color7()); break;
			case 8:  lbl.addStyleName(Resources.INSTANCE.css().color8()); break;
			case 9:  lbl.addStyleName(Resources.INSTANCE.css().color9()); break;
			case 10:  lbl.addStyleName(Resources.INSTANCE.css().color10()); break;
			case 11:  lbl.addStyleName(Resources.INSTANCE.css().color11()); break;
			case 12:  lbl.addStyleName(Resources.INSTANCE.css().color12()); break;
			case 13:  lbl.addStyleName(Resources.INSTANCE.css().color13()); break;
			case 14:  lbl.addStyleName(Resources.INSTANCE.css().color14()); break;
			case 15:  lbl.addStyleName(Resources.INSTANCE.css().color15()); break;
			case 16:  lbl.addStyleName(Resources.INSTANCE.css().color16()); break;
			case 17:  lbl.addStyleName(Resources.INSTANCE.css().color17()); break;
			case 18:  lbl.addStyleName(Resources.INSTANCE.css().color18()); break;
			case 19:  lbl.addStyleName(Resources.INSTANCE.css().color19()); break;
			}	
		}

		lbl.setTitle(fmt.format(today));
		switch(source) {

		case 1: 
			MessageVpanel.add(lbl);
			scrollPanel.scrollToBottom();
			break;
		case 2:
			wallVpanel.add(lbl);
			scrollWallPanel.scrollToBottom();
			break;
		}
	}
	private void adduser(String userName,int id, int color){
		String[] ary = userName.split("");

		HTML usersIcon = new HTML();
		usersIcon.setText(ary[1].toUpperCase());
		usersIcon.addStyleName(Resources.INSTANCE.css().chatusers());
		usersIcon.setTitle(userName);

		switch(color) {
		case 1:  usersIcon.addStyleName(Resources.INSTANCE.css().color1()); break;
		case 2:  usersIcon.addStyleName(Resources.INSTANCE.css().color2()); break;
		case 3:  usersIcon.addStyleName(Resources.INSTANCE.css().color3()); break;
		case 4:  usersIcon.addStyleName(Resources.INSTANCE.css().color4()); break;
		case 5:  usersIcon.addStyleName(Resources.INSTANCE.css().color5()); break;
		case 6:  usersIcon.addStyleName(Resources.INSTANCE.css().color6()); break;
		case 7:  usersIcon.addStyleName(Resources.INSTANCE.css().color7()); break;
		case 8:  usersIcon.addStyleName(Resources.INSTANCE.css().color8()); break;
		case 9:  usersIcon.addStyleName(Resources.INSTANCE.css().color9()); break;
		case 10:  usersIcon.addStyleName(Resources.INSTANCE.css().color10()); break;
		case 11:  usersIcon.addStyleName(Resources.INSTANCE.css().color11()); break;
		case 12:  usersIcon.addStyleName(Resources.INSTANCE.css().color12()); break;
		case 13:  usersIcon.addStyleName(Resources.INSTANCE.css().color13()); break;
		case 14:  usersIcon.addStyleName(Resources.INSTANCE.css().color14()); break;
		case 15:  usersIcon.addStyleName(Resources.INSTANCE.css().color15()); break;
		case 16:  usersIcon.addStyleName(Resources.INSTANCE.css().color16()); break;
		case 17:  usersIcon.addStyleName(Resources.INSTANCE.css().color17()); break;
		case 18:  usersIcon.addStyleName(Resources.INSTANCE.css().color18()); break;
		case 19:  usersIcon.addStyleName(Resources.INSTANCE.css().color19()); break;


		}

		this.UserIcon.put(id, usersIcon);
		UsersHpanel.add(usersIcon);

	}


	//////////////////////////////INTERFAZ/////////////////////////////////////////

	//interfaz del chat, creo que se puede seguir mejorando	
	public void chatInterface(){
		ChatInterface chat = new ChatInterface();
		chat.show();
	}	
	//interfaz del muro, creo que se puede seguir mejorando

	public void wallInterface(){
		final DialogBox dlgMuro=new DialogBox();
		dlgMuro.setAutoHideEnabled(true);
		dlgMuro.setAnimationEnabled(true);
		dlgMuro.setModal(true);
		//create scrollpanel with content
		wallVpanel= new VerticalPanel(); 
		wallMessageVpanel = new VerticalPanel(); 
		scrollWallPanel = new ScrollPanel(wallMessageVpanel);
		scrollWallPanel.setSize("270px", "233px");
		scrollWallPanel.scrollToBottom();
		wallMessage = new TextArea();
		wallMessage.setWidth("258px");
		wallMessage.setVisibleLines(2);
		wallMessage.addStyleName(Resources.INSTANCE.css().chatTextarea());
		wallMessage.addKeyDownHandler(new KeyDownHandler(){

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					onSendMessage(wallMessage.getText(),2);
					wallMessage.setText("");
				}
			}
		});
		wallVpanel.add(scrollWallPanel);
		if(userRole.equals("teacher")){
			wallVpanel.add(wallMessage);
		}
		AbsolutePanel MuroApanel = new AbsolutePanel();
		MuroApanel.setSize("270px", "337px");
		//MuroApanel.add(scrollPanel);
		MuroApanel.add(wallVpanel);
		dlgMuro.add(MuroApanel);

		Button btnmuro=new Button("MURO");
		btnmuro.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				dlgMuro.center();

			}
		});

		RootPanel.get("chat").add(btnmuro);


	}
	//interfaz del SOS (pedir ayuda), creo que se puede seguir mejorando
	public void askHelpInterface(){

		final DialogBox dlgSos=new DialogBox();
		dlgSos.setAutoHideEnabled(true);
		dlgSos.setAnimationEnabled(true);
		dlgSos.setModal(true);

		askSosVpanel = new VerticalPanel();

		sosComent = new TextArea();
		sosComent.addStyleName(Resources.INSTANCE.css().chatTextarea());
		Label commentLbl = new Label("Comentario");
		askSosVpanel.add(commentLbl);
		askSosVpanel.add(sosComent);

		final ListBox urgencyLevel = new ListBox();
		urgencyLevel.addItem("1");
		urgencyLevel.addItem("2");
		urgencyLevel.addItem("3");
		urgencyLevel.addItem("4");
		urgencyLevel.addItem("5");
		Label urgencyLbl = new Label("Nivel de urgencia");
		askSosVpanel.add(urgencyLbl);
		askSosVpanel.add(urgencyLevel);

		Button sendButton=new Button("Enviar");
		sendButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub

				onSendSos(sosComent.getText(),Integer.parseInt(urgencyLevel.getSelectedItemText()));
				dlgSos.hide();
			}
		}); 
		askSosVpanel.add(sendButton);
		AbsolutePanel sosApanel = new AbsolutePanel();
		sosApanel.setSize("270px", "170px");
		//MuroApanel.add(scrollPanel);
		sosApanel.add(askSosVpanel);
		dlgSos.add(sosApanel);
		Button btnsos=new Button("SOS!");
		btnsos.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				dlgSos.center();


			}
		});
		RootPanel.get("chat").add(btnsos);



	}

	public void helpInterface(){

		final DialogBox dlg=new DialogBox();
		dlg.setAutoHideEnabled(true);
		dlg.setAnimationEnabled(true);
		dlg.setModal(true);

		helpVpanel = new VerticalPanel();
		AbsolutePanel Apanel = new AbsolutePanel();
		Apanel.setSize("450px", "450px");
		//MuroApanel.add(scrollPanel);
		
		scrollPanel = new ScrollPanel(helpVpanel);
		scrollPanel.setSize("449px", "449px");
		scrollPanel.scrollToBottom();
		Apanel.add(scrollPanel);
		dlg.add(Apanel);
		Button btnhelp=new Button("Ayudar");
		btnhelp.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				dlg.center();


			}
		});
		RootPanel.get("chat").add(btnhelp);

	}

}
