/**
 * 
 */
package cl.uai.client.chat.messages;

import java.util.Date;

import cl.uai.client.MarkingInterface;
import cl.uai.client.chat.User;
import cl.uai.client.resources.Resources;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * @author Jorge Villalón
 *
 */
public class ChatMessage extends Composite {

	private Date date;
	private String message;
	private User user;
	
	protected HorizontalPanel mainPanel = null;
	private boolean ownMessage;

	/**
	 * Chat message constructor
	 *
	 * @param user
	 * @param date
	 * @param message
	 */
	public ChatMessage(User user, Date date, String message) {
		super();
		this.user = user;
		this.date = date;
		this.message = message;
		
		ownMessage = user.getId() == MarkingInterface.submissionData.getMarkerid();

		mainPanel = new HorizontalPanel();

		mainPanel.addStyleName(Resources.INSTANCE.css().chatmessage());
		
		DateTimeFormat fmt = DateTimeFormat.getFormat("YY/MM/dd HH:MM");

		// Message
		HTML lblMessage = new HTML("<span style=\"font-weight:bold;\">" + this.user.getNickname() 
				+ "</span>: " + this.message);
		lblMessage.setTitle(this.user.getFullname() + " - " + fmt.format(this.date));

		mainPanel.add(lblMessage);

		if(!ownMessage) {
			lblMessage.addStyleName(Resources.INSTANCE.css().chatothersmessage());
		} else {
			lblMessage.addStyleName(Resources.INSTANCE.css().chatownmessage());
		}

		initWidget(mainPanel);
	}

	public boolean isOwnMessage() {
		return ownMessage;
	}

}
