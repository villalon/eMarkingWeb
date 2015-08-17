/**
 * 
 */
package cl.uai.client.chat.messages;

import java.util.Date;

import cl.uai.client.MarkingInterface;
import cl.uai.client.chat.ConnectedUsersPanel;
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

		DateTimeFormat fmt = DateTimeFormat.getFormat("dd MMM HH:MM");

		// Message
		HTML lblMessage = new HTML();
		String html = (ownMessage ? "<div class=\"talk-bubble tri-right round border right-top\">" :
				  	  "<div class=\"talk-bubble tri-right round border left-top\">" ) +
					  "  <div class=\"talktext\">" +
					  "    <p>" + this.message + "</p>" +
					  "  </div>" +
					  "</div>";
		lblMessage.setHTML(html);
		lblMessage.setTitle(fmt.format(this.date));

		HTML lblAuthor = ConnectedUsersPanel.createUserIcon(this.user);
		lblAuthor.removeStyleName(Resources.INSTANCE.css().chatusers());
		lblAuthor.addStyleName(Resources.INSTANCE.css().chatuserssmall());

		if(ownMessage) {
			mainPanel.add(lblMessage);
			mainPanel.add(lblAuthor);
		} else {
			mainPanel.add(lblAuthor);
			mainPanel.add(lblMessage);			
		}

		initWidget(mainPanel);
	}

	public boolean isOwnMessage() {
		return ownMessage;
	}

}
