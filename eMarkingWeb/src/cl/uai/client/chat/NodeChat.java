package cl.uai.client.chat;



import java.util.Date;

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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class NodeChat {

	private String path;
	private VerticalPanel Vpanel;
	private VerticalPanel MuroVpanel;
	private VerticalPanel MessageVpanel;
	private HorizontalPanel UsersHpanel;
	private ScrollPanel scrollPanel;
	private TextArea message;
	public static String username = null;
	public static int userid = 0;
	public static int coursemodule = 0;
	

	
	native void consoleLog( String message) /*-{
    console.log( "me:" + message );
}-*/;
	public NodeChat(){

		path="http://127.0.0.1:9091/";
		
		GWT.log("RUTA NODE: "+ path);
		
		ScriptInjector.fromUrl(path+"socket.io/socket.io.js").setCallback(new Callback<Void, Exception>() {
			
			@Override
			public void onSuccess(Void result) {
				// TODO Auto-generated method stub
				
				loadNodeJS(path, username, coursemodule,userid);
				
			}
			@Override
			public void onFailure(Exception reason) {
				// TODO Auto-generated method stub
				Window.alert("Servicio Node.js en: \n "+path+"\n CAIDO !!!!");
			}
		}).inject(); 
	}
	

	//////////////// CONEXIÓN CON EL SERVIDOR NODE /////////////////////////////
	private native void loadNodeJS(String path, String Username, int coursemodule, int userid) /*-{
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
	       $wnd.socket.emit("joinserver",JSON.stringify(conectionData));
	       
	       $wnd.socket.on('userJoin', function (data) {
			 
			      var obj=JSON.parse(data);
			     
				tmp.@cl.uai.client.chat.NodeChat::userJoin(Lcl/uai/client/chat/UserData;Lcom/google/gwt/core/client/JsArray;Lcom/google/gwt/core/client/JsArray;)(obj.user,obj.people,obj.chatHistory); 
			  });
	     $wnd.socket.on('onRemoveChatUser', function (data) {
			     
			      var data=JSON.parse(data);
				 tmp.@cl.uai.client.chat.NodeChat::onRemoveChatUser(Lcl/uai/client/chat/UserData;)(data); 
			  });
	  $wnd.socket.on('onBeginChatOther', function (data) {
			     
			      var data=JSON.parse(data);
				 tmp.@cl.uai.client.chat.NodeChat::onBeginChatOther(Lcl/uai/client/chat/UserData;)(data); 
			  });
	  $wnd.socket.on('onCatchMesageChatUsers', function (data) {
			     
			      var data=JSON.parse(data);
				 tmp.@cl.uai.client.chat.NodeChat::onCatchMesageChatUsers(Lcl/uai/client/chat/Message;)(data); 
			  });
	
	
	 }); 
	   
}-*/;
	
	
	/////////////////////////funciones para unir información node y gwt/////////////////
	
	private void userJoin(UserData user, JsArray<UserData> people, JsArray<Message> chatHistory){
		
			for(int i=0;i<chatHistory.length();i++){
				formatearMensaje(chatHistory.get(i).getTime(),chatHistory.get(i).getUser() ,chatHistory.get(i).getMessage(),"grey");
			}
	}
	
	private void onBeginChatOther(UserData user){

		HorizontalPanel hpanel = (HorizontalPanel) Vpanel.getWidget(0);
		ListBox users =(ListBox)  hpanel.getWidget(1);
		users.addItem(user.getName());
		
	}
	
	private void onRemoveChatUser(UserData user){
		
		HorizontalPanel hpanel = (HorizontalPanel) Vpanel.getWidget(0);
		ListBox users =(ListBox)  hpanel.getWidget(1);
		
		for(int i=0;i<users.getItemCount();i++){
			if(users.getItemText(i).equals(user.getName())){
				
				users.removeItem(i);
				break;
			}
		}
		
		
	
		
		
	}
	public native void onSendMessageChatUser(String message) /*-{
	   var msn={};
	   msn.message=message;
	   
	  
	   $wnd.socket.emit("onSendMessageChatUser",JSON.stringify(msn));
	}-*/;
	private void onCatchMesageChatUsers(Message message){
	    
		formatearMensaje(message.getTime(),message.getUser(),message.getMessage(),"black");
	}

	
	//////////////////////////////INTERFAZ/////////////////////////////////////////
		public void start(){
		
		final DialogBox dlg=new DialogBox();
		dlg.setAutoHideEnabled(true);
		dlg.setAnimationEnabled(true);
		dlg.setModal(true);
	
		Vpanel= new VerticalPanel(); 
		MessageVpanel= new VerticalPanel(); 
		
		scrollPanel = new ScrollPanel(MessageVpanel);
	    scrollPanel.setSize("270px", "220px");
	    
		scrollPanel.scrollToBottom();
	    
		UsersHpanel= new HorizontalPanel();
	    UsersHpanel.setSize("200px", "65px");
	    HTML usersIcon = new HTML();
	    usersIcon.setText("MC");
	    usersIcon.addStyleName(Resources.INSTANCE.css().chatusers());
	    
	    
	   
	    UsersHpanel.add(usersIcon);
;
		
	    message = new TextArea();
	   
	    message.setWidth("258px");
	    message.setVisibleLines(2);
	   
	    message.addKeyDownHandler(new KeyDownHandler() {

	        @Override
	        public void onKeyDown(KeyDownEvent event) {
	         if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
	        	 onSendMessageChatUser(message.getText());
				 message.setText("");
				 scrollPanel.scrollToBottom();
				 scrollPanel.setVerticalScrollPosition(scrollPanel.getMaximumVerticalScrollPosition()+1);
	               }
	        }
	    });
	   
	    Vpanel.add(UsersHpanel);
	    Vpanel.add(scrollPanel);
	    Vpanel.add(message);
	    
	    AbsolutePanel Apanel = new AbsolutePanel();
	    Apanel.setSize("270px", "337px");
	    Apanel.add(Vpanel);
	    dlg.add(Apanel);
		
		Button btnchat=new Button("CHATEAR");
		btnchat.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				dlg.center();
			}
		});
		
		
	
		final DialogBox dlgMuro=new DialogBox();
		dlgMuro.setAutoHideEnabled(true);
		dlgMuro.setAnimationEnabled(true);
		dlgMuro.setModal(true);
	      //create scrollpanel with content
		

	
		AbsolutePanel MuroApanel = new AbsolutePanel();
		MuroApanel.setSize("540px", "600px");
	    //MuroApanel.add(scrollPanel);
		
	    dlgMuro.add(MuroApanel);
	    
		Button btnmuro=new Button("MURO");
		btnmuro.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				dlgMuro.center();
				
			}
		});
		RootPanel.get("chat").add(btnchat);
		RootPanel.get("chat").add(btnmuro);
		
		
		
	
		
	}
	
	private void formatearMensaje(int time,String name,String  mensaje,String color)
	{
		
		long ltime = (long) (time/ .001);
		Date today = new Date(ltime);
		DateTimeFormat fmt = DateTimeFormat.getFormat("h:mm");
		String cad="["+fmt.format(today)+"] "+name+":"+mensaje;
		Label lbl = new Label(cad);
		MessageVpanel.add(lbl);
		scrollPanel.scrollToBottom();
	}
	

}
