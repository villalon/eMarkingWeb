package cl.uai.client.chat;

import cl.uai.client.EMarkingWeb;
import cl.uai.client.MarkingInterface;
import cl.uai.client.resources.Resources;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;


public class SendSosDialog extends DialogBox {
	
	private VerticalPanel vPanel;
	private TextArea textArea;
	
	public SendSosDialog(){

	vPanel = new VerticalPanel();
	textArea = new TextArea();
	textArea.addStyleName(Resources.INSTANCE.css().chatTextarea());
	
	Label commentLbl = new Label(MarkingInterface.messages.Comment());
	vPanel.add(commentLbl);
	vPanel.add(textArea);

	final ListBox urgencyLevel = new ListBox();
	urgencyLevel.addItem("1");
	urgencyLevel.addItem("2");
	urgencyLevel.addItem("3");
	urgencyLevel.addItem("4");
	urgencyLevel.addItem("5");
	Label urgencyLbl = new Label(MarkingInterface.messages.Priority());
	vPanel.add(urgencyLbl);
	vPanel.add(urgencyLevel);

	HorizontalPanel hpanel = new HorizontalPanel();
	
	Button sendButton=new Button(MarkingInterface.messages.Send());
	sendButton.addClickHandler(new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			EMarkingWeb.markingInterface.help.sendMessage(textArea.getValue(), 
					Integer.parseInt(urgencyLevel.getSelectedValue()), 1);
			textArea.setValue(null);
			hide();
		}
	}); 
	hpanel.add(sendButton);

	Button cancelButton=new Button(MarkingInterface.messages.Cancel());
	cancelButton.addClickHandler(new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	}); 
	hpanel.add(cancelButton);

	vPanel.add(hpanel);
	vPanel.setCellHorizontalAlignment(hpanel, HasAlignment.ALIGN_RIGHT);
	
	this.setHTML(MarkingInterface.messages.SendSOS());
		this.setAutoHideEnabled(true);
	    this.setWidget(vPanel);
	     
	}	
}
