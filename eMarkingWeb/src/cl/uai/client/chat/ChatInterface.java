/**
 * 
 */
package cl.uai.client.chat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cl.uai.client.EMarkingWeb;
import cl.uai.client.data.AjaxData;
import cl.uai.client.data.AjaxRequest;
import cl.uai.client.resources.Resources;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Jorge
 *
 */
public class ChatInterface extends DialogBox {
	
	private VerticalPanel vpanel;
	private VerticalPanel messagesPanel;
	private ScrollPanel scrollMessagesPanel;
	private HorizontalPanel usersHpanel;
	private TextArea message;
	private Map<Integer, HTML> iconHashMap = new HashMap<Integer, HTML>();
	private HTML usersIcon;
	private HorizontalPanel messageIconPanel;
	
	public ChatInterface() {
		
		// Dialog parameters
		this.setAutoHideEnabled(true);
		this.setAnimationEnabled(true);
		this.setModal(true);

		messagesPanel = new VerticalPanel(); 
		scrollMessagesPanel = new ScrollPanel(messagesPanel);
		scrollMessagesPanel.setSize("270px", "233px");
		scrollMessagesPanel.scrollToBottom();
		
		usersHpanel = new HorizontalPanel();
		usersHpanel.setSize("200px", "47px");
		
		message = new TextArea();
		message.setWidth("258px");
		message.setVisibleLines(2);
		message.addStyleName(Resources.INSTANCE.css().chatTextarea());
		message.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					// onSendMessage(message.getText(),1);
					sendMessage(2125452132,"Yo",message.getText(), 1);
					message.setText("");
				}
			}
		});

		// Vertical panel that contains everything
		vpanel = new VerticalPanel(); 

		vpanel.add(usersHpanel);
		vpanel.add(scrollMessagesPanel);
		vpanel.add(message);

		this.add(vpanel);
	}
	
	public void adduser(String userName,int id, int color){
		String[] ary = userName.split("");
		usersIcon = new HTML();
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

		//this.UserIcon.put(id, usersIcon);
		this.iconHashMap.put(1, usersIcon);
		usersHpanel.add(usersIcon);
		
	}
	
	public void removeUser(int id){
		
		HTML usersIcon = iconHashMap.get(id);
		if(usersIcon != null) {
			usersHpanel.remove(usersIcon);
			iconHashMap.remove(id);
		}
		
	}
	
	private void sendMessage(int time,String name,String  mensaje, int source){
		
		HTML icon = new HTML();
		icon.addStyleName(Resources.INSTANCE.css().chaticonmessage());
		icon.addStyleName(Resources.INSTANCE.css().chatsendcolor());
		
		long ltime = (long) (time/ .001);
		Date today = new Date(ltime);
		DateTimeFormat fmt = DateTimeFormat.getFormat("dd/MM/yyyy h:mm");
		String cad=mensaje;
		Label lbl = new Label(cad);
		messageIconPanel = new HorizontalPanel();
		messageIconPanel.add(icon);
		messageIconPanel.add(lbl);
		lbl.setTitle(fmt.format(today));
		switch(source) {

		case 1: 
			messagesPanel.add(messageIconPanel);
			scrollMessagesPanel.scrollToBottom();
			break;
		}
		
		String params= "&userid="++"=&room="+coursemodule+"&source=1";
		AjaxRequest.ajaxRequest("action=getchathistory"+ params, new AsyncCallback<AjaxData>() {
			@Override
			public void onSuccess(AjaxData result) {
				//logger.info("Heartbeat! ");

				

			}
			@Override
			public void onFailure(Throwable caught) {
				//logger.warning("Failure on heartbeat");
			}
		});
		
		
		EMarkingWeb.chatServer.onSendMessage(mensaje, NodeChat.SOURCE_CHAT);
		
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
			case 10: lbl.addStyleName(Resources.INSTANCE.css().color10()); break;
			case 11: lbl.addStyleName(Resources.INSTANCE.css().color11()); break;
			case 12: lbl.addStyleName(Resources.INSTANCE.css().color12()); break;
			case 13: lbl.addStyleName(Resources.INSTANCE.css().color13()); break;
			case 14: lbl.addStyleName(Resources.INSTANCE.css().color14()); break;
			case 15: lbl.addStyleName(Resources.INSTANCE.css().color15()); break;
			case 16: lbl.addStyleName(Resources.INSTANCE.css().color16()); break;
			case 17: lbl.addStyleName(Resources.INSTANCE.css().color17()); break;
			case 18: lbl.addStyleName(Resources.INSTANCE.css().color18()); break;
			case 19: lbl.addStyleName(Resources.INSTANCE.css().color19()); break;
			}	
		}

		lbl.setTitle(fmt.format(today));
		switch(source) {

		case 1: 
			messagesPanel.add(lbl);
			scrollMessagesPanel.scrollToBottom();
			break;
		//case 2:
			//wallVpanel.add(lbl);
			//scrollWallPanel.scrollToBottom();
			//break;
		}
	}
	

	

}
