package cl.uai.client.chat;

import cl.uai.client.EMarkingWeb;
import cl.uai.client.data.AjaxData;
import cl.uai.client.data.AjaxRequest;
import cl.uai.client.resources.Resources;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;


public class SosInterface extends DialogBox {
	
	private VerticalPanel vPanel;
	private TextArea textArea;
	private int source=0;
	
	public SosInterface(){


	vPanel = new VerticalPanel();
	textArea = new TextArea();
	textArea.addStyleName(Resources.INSTANCE.css().chatTextarea());
	Label commentLbl = new Label("Comentario");
	vPanel.add(commentLbl);
	vPanel.add(textArea);

	final ListBox urgencyLevel = new ListBox();
	urgencyLevel.addItem("1");
	urgencyLevel.addItem("2");
	urgencyLevel.addItem("3");
	urgencyLevel.addItem("4");
	urgencyLevel.addItem("5");
	Label urgencyLbl = new Label("Nivel de urgencia");
	vPanel.add(urgencyLbl);
	vPanel.add(urgencyLevel);

	Button sendButton=new Button("Enviar");
	sendButton.addClickHandler(new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			// TODO Auto-generated method stub
			createSos(textArea.getText(),Integer.parseInt(urgencyLevel.getSelectedItemText()));
			EMarkingWeb.chatServer.onSendSos(textArea.getText(),Integer.parseInt(urgencyLevel.getSelectedItemText()));
			textArea.setText("");
			hide();
		}
	}); 
	vPanel.add(sendButton);
		this.setAutoHideEnabled(true);
	    this.add(vPanel);
	     
	}

	public void setSource(int source){

		this.source=source;

	}
	public void createSos(String message,int urgencyLevel){
		
		String params= "&message="+message+"&source="+source+"&userid="+NodeChat.userid+"&room="+NodeChat.coursemodule+"&draftid="+NodeChat.draftid+"&urgencylevel="+urgencyLevel+"&status=1";
		AjaxRequest.ajaxRequest("action=addchatmessage"+ params, new AsyncCallback<AjaxData>() {
			@Override
			public void onSuccess(AjaxData result) {
				
			}
			@Override
			public void onFailure(Throwable caught) {
				
			}
		});
		
		
	}
	
	
	

}
