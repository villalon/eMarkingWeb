package cl.uai.client.marks.collaborative;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DiscussionMessage extends VerticalPanel{

	private String date = null;
	private String user = null;
	private String text = null;
	private int markerid = -1;
	
	public DiscussionMessage(String date, String user, String text, int markerid) {
		this.date = date;
		this.user = user;
		this.text = text;
		this.markerid = markerid;
		
		HTML markername = new HTML("<b style='font-size:1.1em; text-decoration: underline; margin-left: 0.3em; margin-right: 0.3em; ' >"+this.user+"</b>");		
		HTML message = new HTML("<div style='font-size:1em; margin-left: 0.3em; margin-right: 0.3em;overflow-y: hidden !important;max-width: 474px; word-wrap: break-word; ' >"+this.text+"</div>");		
		HTML timedisplay = new HTML("<div style='font-size:0.8em;margin-right: 0.3em; color: #A4A4A4;'>"+this.date+"</div>");
		
		add(markername);
		add(new HTML("<div style='height: 0.3em' ></div>"));
		add(message);
		add(timedisplay);
		setCellHorizontalAlignment(timedisplay, HasAlignment.ALIGN_RIGHT);

	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getMarkerid() {
		return markerid;
	}
	public void setMarkerid(int markerid) {
		this.markerid = markerid;
	}
	
}
