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
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Jorge Villalón
 *
 */
public class ChatMessage extends Composite {

	private Date date;
	private String message;
	private User user;
	
	protected HorizontalPanel mainPanel = null;
	private boolean ownMessage = false;
	private HTML elapsedTimeWidget = null;

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
		
		VerticalPanel vpanel = new VerticalPanel();
		vpanel.add(lblMessage);
		this.elapsedTimeWidget = new HTML();
		vpanel.add(elapsedTimeWidget);
		vpanel.setCellHorizontalAlignment(elapsedTimeWidget, this.isOwnMessage() ? HasAlignment.ALIGN_RIGHT : HasAlignment.ALIGN_LEFT);
		updateElapsedTime();
		
		if(ownMessage) {
			mainPanel.add(vpanel);
			mainPanel.add(lblAuthor);
		} else {
			mainPanel.add(lblAuthor);
			mainPanel.add(vpanel);			
		}

		initWidget(mainPanel);
	}

	public boolean isOwnMessage() {
		return ownMessage;
	}
	
	/**
	 * Calculates the elapsed time between a specific date and now and
	 * creates an HTML widget showing the info
	 * 
	 * @param date
	 * @return
	 */
	private void updateTimeElapsedTime(Date date) {
		HTML html = this.elapsedTimeWidget;
		html.addStyleName(Resources.INSTANCE.css().chatelapsedtime());
		
		Date today = new Date();
		long elapsed = today.getTime() - date.getTime();
		elapsed = elapsed / 1000; // From millis to seconds
		if(elapsed < 60) {
			html.setHTML(MarkingInterface.messages.JustNow());
		}
		if(elapsed < 60 * 60) { // Less than an hour
			elapsed = (long) Math.floor((double) elapsed / 60);
			html.setHTML(elapsed + " " + (elapsed == 1 ? MarkingInterface.messages.MinuteAgo() : MarkingInterface.messages.MinutesAgo()));
		} else if(elapsed < 60 * 60 * 24) { // Less than a day
			elapsed = (long) Math.floor((double) elapsed / (60 * 60));
			html.setHTML(elapsed + " " + (elapsed == 1 ? MarkingInterface.messages.HourAgo() : MarkingInterface.messages.HoursAgo()));
		} else if(elapsed < 60 * 60 * 24 * 7) {
			elapsed = (long) Math.floor((double) elapsed / (60 * 60 * 24));
			html.setHTML(elapsed + " " + (elapsed == 1 ? MarkingInterface.messages.DayAgo() : MarkingInterface.messages.DaysAgo()));
		} else {
			DateTimeFormat fmt = DateTimeFormat.getFormat("dd MMM HH:MM");
			html.setHTML(fmt.format(date));
		}
	}

	public void updateElapsedTime() {
		updateTimeElapsedTime(date);
	}
}
