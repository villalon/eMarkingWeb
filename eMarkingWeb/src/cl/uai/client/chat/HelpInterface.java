package cl.uai.client.chat;

import java.util.Date;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class HelpInterface extends ChatInterface {
	
	private Icon[] urgencyIcons = {
			new Icon(IconType.FIRE),
			new Icon(IconType.FIRE),
			new Icon(IconType.UMBRELLA),
			new Icon(IconType.TINT),
			new Icon(IconType.TINT),
	};
	
	private Icon[] statusIcons = {
			new Icon(IconType.CHECK),
			new Icon(IconType.CHECK_EMPTY),
	};
	
	public HelpInterface() {
		super();
		
		this.source = NodeChat.SOURCE_SOS;
		this.sendMessageTextArea.setVisible(false);
	}
	
	@Override
	public HorizontalPanel addMessage(Date date, int userid, String message) throws Exception {
		throw new Exception("Invalid call");
	}
	
	public HorizontalPanel addMessage(Date date, int userid, String message, int draftid, int status, int urgency) throws Exception {
		HorizontalPanel hpanel = super.addMessage(date, userid, message);
		
		// draft id
		Icon icon = new Icon(IconType.SIGNIN);
		hpanel.add(new HTML("<a href=\"#\">"+icon.toString()+"</a>")); ;
		// status
		Icon iconStatus = statusIcons[status];
		hpanel.add(new HTML(iconStatus.toString()));
		// urgency level
		Icon iconUrgency = urgencyIcons[urgency];
		hpanel.add(new HTML(iconUrgency.toString()));

		return hpanel;		
	}
	
	@Override
	protected void sendMessage(String message) {
		super.sendMessage(message);
	}
}
