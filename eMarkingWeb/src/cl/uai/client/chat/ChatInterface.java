/**
 * 
 */
package cl.uai.client.chat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import cl.uai.client.EMarkingWeb;
import cl.uai.client.MarkingInterface;
import cl.uai.client.data.AjaxData;
import cl.uai.client.data.AjaxRequest;
import cl.uai.client.resources.Resources;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
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

	/** For logging purposes */
	private static Logger logger = Logger.getLogger(MarkingInterface.class.getName());
	private int source=0;
	private VerticalPanel vpanel;
	private VerticalPanel messagesPanel;
	private ScrollPanel scrollMessagesPanel;
	private HorizontalPanel usersHpanel;
	private TextArea message;
	private Map<Integer, HTML> iconHashMap = new HashMap<Integer, HTML>();
	private Map<Integer, HTML> iconMessegeHashMap = new HashMap<Integer, HTML>();
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
					sendMessage(EMarkingWeb.chatServer.getUsername(), message.getText());
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
		addHistoryMessages();
	}

	public void setSource(int source){

		this.source=source;

	}

	public void adduser(String userName,int id, int color){
		String[] ary = userName.split("");
		usersIcon = new HTML();
		usersIcon.setText(ary[1].toUpperCase());
		usersIcon.addStyleName(Resources.INSTANCE.css().chatusers());
		usersIcon.setTitle(userName);

		color(color,usersIcon);

		this.iconHashMap.put(EMarkingWeb.chatServer.getUserid(), usersIcon);
		usersHpanel.add(usersIcon);

	}

	public void removeUser(int id){

		final HTML userIcon= iconHashMap.get(id);

		if(userIcon != null) {

			usersHpanel.remove(userIcon);
			iconHashMap.remove(id);
		}

	}

	private void sendMessage(String name,String  message){

		HTML icon = new HTML();
		icon.addStyleName(Resources.INSTANCE.css().chaticonmessage());
		icon.addStyleName(Resources.INSTANCE.css().chatsendcolor());


		Date today = new Date();
		DateTimeFormat fmt = DateTimeFormat.getFormat("dd/MM/yyyy h:mm");
		String cad=message;
		Label lbl = new Label(cad);
		messageIconPanel = new HorizontalPanel();
		messageIconPanel.add(icon);
		messageIconPanel.add(lbl);
		int messageId = iconMessegeHashMap.size();
		iconMessegeHashMap.put(messageId, icon);
		lbl.setTitle(fmt.format(today));

		messagesPanel.add(messageIconPanel);
		scrollMessagesPanel.scrollToBottom();

		String params = "&message=" + message + 
				"&source=" + source + 
				"&userid=" + EMarkingWeb.chatServer.getUserid() + 
				"&room=" + EMarkingWeb.chatServer.getCoursemodule() + 
				"&draftid=" + EMarkingWeb.chatServer.getDraftid();
		
		// TODO: A callback without code makes no sense
		AjaxRequest.ajaxRequest("action=addchatmessage"+ params, new AsyncCallback<AjaxData>() {
			@Override
			public void onSuccess(AjaxData result) {
			}
			@Override
			public void onFailure(Throwable caught) {
			}
		});
		
		EMarkingWeb.chatServer.onSendMessage(message, source,messageId);
	}

	public void addHistoryMessages(){

		String params= "&ids="+ EMarkingWeb.chatServer.getDraftid() +
				"&room=" + EMarkingWeb.chatServer.getCoursemodule() + 
				"&source=" + source;
		
		AjaxRequest.ajaxRequest("action=getchathistory"+ params, new AsyncCallback<AjaxData>() {
			@Override
			public void onSuccess(AjaxData result) {


				List<Map<String, String>> messageHistory = AjaxRequest.getValuesFromResult(result);
				for(Map<String, String> message : messageHistory) {
					int timeCreated= Integer.parseInt(message.get("timecreated"));
					long unixTime = (long) (timeCreated/ .001);
					Date today = new Date(unixTime);
					DateTimeFormat date = DateTimeFormat.getFormat("dd/MM/yyyy H:mm");
					String cad=message.get("firstname")+":"+message.get("message");
					Label lbl = new Label(cad);
					lbl.setTitle(message.get("firstname")+" "+message.get("lastname") +" "+ date.format(today));
					messagesPanel.add(lbl);
					scrollMessagesPanel.scrollToBottom();

				}

			}
			@Override
			public void onFailure(Throwable caught) {
				logger.warning("WTF ERROR");
			}
		});


	}

	public void addReceivedMessage(int time,String name,String  mensaje,int color){


		long ltime = (long) (time/ .001);
		Date today = new Date(ltime);
		DateTimeFormat fmt = DateTimeFormat.getFormat("dd/MM/yyyy h:mm");
		String message=null;
		Label lbl = new Label();
		messageIconPanel = new HorizontalPanel();



		switch(source){

		case 1:
			HTML icon = new HTML();
			icon.addStyleName(Resources.INSTANCE.css().chaticonmessage());
			color(color,icon);
			messageIconPanel.add(icon);
			message=mensaje;
			lbl.setText(message);
			break;
		case 2:
			message= name+" : "+mensaje;
			lbl.setText(message);
			break;
		}
		lbl.setTitle(fmt.format(today));
		messageIconPanel.add(lbl);
		messagesPanel.add(messageIconPanel);
		
		scrollMessagesPanel.scrollToBottom();

	}

	public void mensajeEnvidoCorrectamente(int id){

		final HTML messageIcon= iconMessegeHashMap.get(id);


		Timer timer = new Timer()
		{
			@Override
			public void run()
			{
				messageIcon.addStyleName(Resources.INSTANCE.css().color1());
				messageIcon.removeStyleName(Resources.INSTANCE.css().chatsendcolor());
			}
		};

		timer.schedule(1000);



	}


	public void color(int color, HTML icon){

		switch(color) {
		case 1:  icon.addStyleName(Resources.INSTANCE.css().color1()); break;
		case 2:  icon.addStyleName(Resources.INSTANCE.css().color2()); break;
		case 3:  icon.addStyleName(Resources.INSTANCE.css().color3()); break;
		case 4:  icon.addStyleName(Resources.INSTANCE.css().color4()); break;
		case 5:  icon.addStyleName(Resources.INSTANCE.css().color5()); break;
		case 6:  icon.addStyleName(Resources.INSTANCE.css().color6()); break;
		case 7:  icon.addStyleName(Resources.INSTANCE.css().color7()); break;
		case 8:  icon.addStyleName(Resources.INSTANCE.css().color8()); break;
		case 9:  icon.addStyleName(Resources.INSTANCE.css().color9()); break;
		case 10:  icon.addStyleName(Resources.INSTANCE.css().color10()); break;
		case 11:  icon.addStyleName(Resources.INSTANCE.css().color11()); break;
		case 12:  icon.addStyleName(Resources.INSTANCE.css().color12()); break;
		case 13:  icon.addStyleName(Resources.INSTANCE.css().color13()); break;
		case 14:  icon.addStyleName(Resources.INSTANCE.css().color14()); break;
		case 15:  icon.addStyleName(Resources.INSTANCE.css().color15()); break;
		case 16:  icon.addStyleName(Resources.INSTANCE.css().color16()); break;
		case 17:  icon.addStyleName(Resources.INSTANCE.css().color17()); break;
		case 18:  icon.addStyleName(Resources.INSTANCE.css().color18()); break;
		case 19:  icon.addStyleName(Resources.INSTANCE.css().color19()); break;

		}


	}



}
