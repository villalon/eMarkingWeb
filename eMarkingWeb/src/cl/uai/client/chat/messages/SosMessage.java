/**
 * 
 */
package cl.uai.client.chat.messages;

import java.util.Date;

import cl.uai.client.chat.User;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.user.client.ui.HTML;

/**
 * @author Jorge
 *
 */
public class SosMessage extends ChatMessage {

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
	

	private int draftid;
	private int status;
	private int urgency;
	
	public SosMessage(User user, Date date, String message, 
			int draftid, int status, int urgency) {
		super(user, date, message);
		
		this.draftid = draftid;
		this.status = status;
		this.urgency = urgency;
		
		
		// draft id
		Icon icon = new Icon(IconType.SIGNIN);
		mainPanel.add(new HTML("<a href=\"#?id="+this.draftid+"\">"+icon.toString()+"</a>")); ;
		// status
		Icon iconStatus = statusIcons[this.status-1];
		mainPanel.add(new HTML(iconStatus.toString()));
		// urgency level
		Icon iconUrgency = urgencyIcons[this.urgency-1];
		mainPanel.add(new HTML(iconUrgency.toString()));
	}
}
