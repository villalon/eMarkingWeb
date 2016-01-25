package cl.uai.client.marks.collaborative;

import java.util.ArrayList;
import java.util.Date;

import cl.uai.client.EMarkingConfiguration;
import cl.uai.client.MarkingInterface;
import cl.uai.client.data.AjaxData;
import cl.uai.client.data.AjaxRequest;
import cl.uai.client.resources.Resources;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DiscussionDialog extends DialogBox {

	/** Dialog's main panel **/
	private VerticalPanel mainPanel;
	
	/** Panel that contains the messages **/
	private VerticalPanel messagesPanel = null;
	
	/** Scroll for the messages **/
	private ScrollPanel scrollMessagesPanel = null;
	
	/** The text box for the comment **/
	private TextArea sendMessage = null;
	
	private int commentid = -1;
	
	private String markername = null;
	
	private DiscussionMark mark = null;
	
	private  ArrayList<DiscussionMessage> messages = null;
	
	public DiscussionDialog() {
		super(true, false);
		this.setAnimationEnabled(true);
		this.setGlassEnabled(true);
		this.setPopupPosition(Window.getClientWidth()/4, Window.getClientHeight()/6);
		this.addStyleName(Resources.INSTANCE.css().commentdialog());
		
		mainPanel = new VerticalPanel();
		mainPanel.addStyleName(Resources.INSTANCE.css().discussiondialog());
		
		messagesPanel = new VerticalPanel();
		messagesPanel.addStyleName(Resources.INSTANCE.css().tablediscussionmessages());
		
		scrollMessagesPanel = new ScrollPanel(messagesPanel);
		scrollMessagesPanel.addStyleName(Resources.INSTANCE.css().chatscrollmessages());
		scrollMessagesPanel.scrollToBottom();
		
		mainPanel.add(scrollMessagesPanel);
		
		sendMessage = new TextArea();
		sendMessage.setVisibleLines(2);	
		sendMessage.getElement().getStyle().setMarginBottom(5, Unit.PT);
		sendMessage.setWidth("95%");
		
		// Add text input for discussion comment
		HorizontalPanel hpanelComment = new HorizontalPanel();
		hpanelComment.setWidth("100%");
		Label comment = new Label(MarkingInterface.messages.Comment());
		hpanelComment.add(comment);
		hpanelComment.setCellWidth(comment, "12%");
		hpanelComment.add(sendMessage);
		hpanelComment.setCellHorizontalAlignment(sendMessage, HasHorizontalAlignment.ALIGN_RIGHT);

		HorizontalPanel hr = new HorizontalPanel();
		hr.setWidth("100%");
		hr.setBorderWidth(1);
		hr.setHeight("1px");
		hr.getElement().getStyle().setBackgroundColor("#CBCBCB");
		
		mainPanel.add(new HTML("<div style='height: 0.2em;' ></div>"));
		mainPanel.add(hr);
		mainPanel.add(new HTML("<div style='height: 0.4em;' ></div>"));
		mainPanel.add(hpanelComment);
		mainPanel.setCellHorizontalAlignment(hpanelComment, HasHorizontalAlignment.ALIGN_RIGHT);
		
		// Save button
		Button btnSave = new Button(MarkingInterface.messages.Save());
		btnSave.addStyleName(Resources.INSTANCE.css().btnsave());
		btnSave.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(!sendMessage.getValue().equals("") && !sendMessage.getValue().equals(" ") ){
					String sendVars = "type=4&status=1&markerid="+EMarkingConfiguration.getMarkerId()+"&commentid="+commentid
							+"&text="+sendMessage.getValue();
					AjaxRequest.ajaxRequest("action=clickcollaborativebuttons&"+sendVars, new AsyncCallback<AjaxData>() {
					
						@Override
						public void onFailure(Throwable caught) {
							hide();
						}
						
						@Override
						public void onSuccess(AjaxData result) {
							//long unixTime = System.currentTimeMillis() / 1000L;							
							Date time = new Date ();	
							DateTimeFormat fmt = DateTimeFormat.getFormat("HH:MM yyy-MM-dd");
							String[] part = fmt.format(time).split(" ");
							DiscussionMessage newmessage = new DiscussionMessage(
									part[0]+" &nbsp &nbsp"+part[1],
									markername,
									sendMessage.getValue(), 
									EMarkingConfiguration.getMarkerId()
							);
							messages.add(newmessage);
							sendMessage.setText("");
							reLoadComments();
							mark.reLoadIcon();
						}
					});
				}else{
					hide();
				}
				hide();
			}
		});

		// Cancel button
		Button btnCancel = new Button(MarkingInterface.messages.Cancel());
		btnSave.addStyleName(Resources.INSTANCE.css().btncancel());
		btnCancel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				sendMessage.setText("");
				hide();
			}
		});
		
		// Add buttons
		HorizontalPanel hpanel = new HorizontalPanel();
		hpanel.setSpacing(2);
		hpanel.setWidth("100%");
		hpanel.add(btnSave);
		hpanel.add(btnCancel);
		hpanel.setCellWidth(btnSave, "100%");
		hpanel.setCellWidth(btnCancel, "0px");
		hpanel.setCellHorizontalAlignment(btnCancel, HasHorizontalAlignment.ALIGN_RIGHT);
		hpanel.setCellHorizontalAlignment(btnSave, HasHorizontalAlignment.ALIGN_RIGHT);
		mainPanel.add(hpanel);
		mainPanel.setCellHorizontalAlignment(hpanel, HasHorizontalAlignment.ALIGN_RIGHT);
		
		this.setWidget(mainPanel);
	}

	public ArrayList<DiscussionMessage> getMessages() {
		return messages;
	}

	public void setMessages(ArrayList<DiscussionMessage> newmessages) {
		this.messages = newmessages;

		for(DiscussionMessage message: messages ){
			messagesPanel.add(message);	
			message.setWidth("78%");
			if(message.getMarkerid() == EMarkingConfiguration.getMarkerId()){
				message.addStyleName(Resources.INSTANCE.css().mediscussionmessage());
				messagesPanel.setCellHorizontalAlignment(message, HasAlignment.ALIGN_RIGHT);
			}else{
				message.addStyleName(Resources.INSTANCE.css().discussionmessage());
				messagesPanel.setCellHorizontalAlignment(message, HasAlignment.ALIGN_LEFT);
			}
		}
	}

	public int getCommentid() {
		return commentid;
	}

	public void setCommentid(int commentid) {
		this.commentid = commentid;
	}
	
	public String getMarkername() {
		return markername;
	}

	public void setMarkername(String markername) {
		this.markername = markername;
	}

	public void reLoadComments(){
		messagesPanel.clear();
		for(DiscussionMessage message: messages ){
			messagesPanel.add(message);	
			message.setWidth("78%");
			if(message.getMarkerid() == EMarkingConfiguration.getMarkerId()){
				message.addStyleName(Resources.INSTANCE.css().mediscussionmessage());
				messagesPanel.setCellHorizontalAlignment(message, HasAlignment.ALIGN_RIGHT);
			}else{
				message.addStyleName(Resources.INSTANCE.css().discussionmessage());
				messagesPanel.setCellHorizontalAlignment(message, HasAlignment.ALIGN_LEFT);
			}
		}
	}
	
	@Override
	public void show() {
		super.show();;
		scrollMessagesPanel.scrollToBottom();
		sendMessage.setFocus(true);
	}

	public DiscussionMark getMark() {
		return mark;
	}

	public void setMark(DiscussionMark mark) {
		this.mark = mark;
	}

}