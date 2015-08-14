/**
 * 
 */
package cl.uai.client.chat.messages;

import java.util.Date;

import cl.uai.client.MarkingInterface;
import cl.uai.client.chat.ChatInterface;
import cl.uai.client.resources.Resources;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Jorge Villalón
 *
 */
public class ChatMessage extends Composite {

	int userid;
	Date date;
	String userAbbreviation;
	String userFullname;
	String message;
	int color;
	
	HorizontalPanel mainPanel = null;
	private boolean ownMessage;

	/**
	 * Chat message constructor
	 * 
	 * @param userid
	 * @param date
	 * @param userAbbreviation
	 * @param userFullname
	 * @param message
	 * @param color
	 */
	public ChatMessage(int userid, Date date, String userAbbreviation,
			String userFullname, String message, int color) {
		super();
		this.userid = userid;
		this.date = date;
		this.userAbbreviation = userAbbreviation;
		this.userFullname = userFullname;
		this.message = message;
		this.color = color;
		
		ownMessage = userid == MarkingInterface.submissionData.getMarkerid();

		mainPanel = new HorizontalPanel();

		mainPanel.addStyleName(Resources.INSTANCE.css().chatmessage());
		
		// Author with date as title
		Label authorLabel = new Label(this.userAbbreviation);
		authorLabel.addStyleName(Resources.INSTANCE.css().chatauthor());

		ChatInterface.addColorCSStoWidget(color, authorLabel);

		DateTimeFormat fmt = DateTimeFormat.getFormat("YYYY/MM/dd HH:MM");
		authorLabel.setTitle(userFullname + " " + fmt.format(date));

		// Message
		HTML lblMessage = new HTML("<span style=\"font-weight:bold;\">" + this.userAbbreviation 
				+ "</span>: " + message);

		mainPanel.add(lblMessage);

		if(!ownMessage) {
			lblMessage.addStyleName(Resources.INSTANCE.css().chatothersmessage());
		} else {
			lblMessage.addStyleName(Resources.INSTANCE.css().chatownmessage());
		}

		mainPanel.setCellVerticalAlignment(authorLabel, HasAlignment.ALIGN_TOP);
		
		initWidget(mainPanel);
	}

	public boolean isOwnMessage() {
		return ownMessage;
	}

}
