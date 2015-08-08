package cl.uai.client.chat;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import cl.uai.client.EMarkingWeb;
import cl.uai.client.MarkingInterface;
import cl.uai.client.data.AjaxData;
import cl.uai.client.data.AjaxRequest;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HelpInterface  extends DialogBox {
	
	private static Logger logger = Logger.getLogger(MarkingInterface.class.getName());
	private int source=0;
	private VerticalPanel vpanel;
	private ScrollPanel scrollMessagesPanel;
	
	
	public HelpInterface(){
		
		// Dialog parameters
		this.setAutoHideEnabled(true);
		this.setAnimationEnabled(true);
		this.setModal(true);

		

		// Vertical panel that contains everything
		vpanel = new VerticalPanel(); 
		

		scrollMessagesPanel = new ScrollPanel(vpanel);
		scrollMessagesPanel.setSize("270px", "233px");
		scrollMessagesPanel.scrollToBottom();

		
		this.add(vpanel);
		
		
		
	}
	
	public void setSource(int source){

		this.source=source;

	}
	
	public void addHistorySos() {

		String params= "&ids=" + EMarkingWeb.chatServer.getDraftid() + 
				"&room=" + EMarkingWeb.chatServer.getCoursemodule() + 
				"&source=" + source;
		
		AjaxRequest.ajaxRequest("action=getchathistory"+ params, new AsyncCallback<AjaxData>() {
			@Override
			public void onSuccess(AjaxData result) {


				VerticalPanel vp = new VerticalPanel();
				List<Map<String, String>> sosHistory = AjaxRequest.getValuesFromResult(result);
				for(Map<String, String> help : sosHistory) {
				
				
				Label status = new Label("Estado: "+help.get("status"));
				Label name = new Label(help.get("firstname")+" "+help.get("lastname"));
				Label text = new Label("\""+help.get("message")+"\"");
				Label urgencyLevel = new Label("Nivel de urgencia: "+help.get("urgencylevel"));
				String url =help.get("url")+"?&id="+help.get("draftid");
				Anchor link = new Anchor("Link a la prueba", url);
				link.setTarget("_blank");
				
				vp.add(status);
				vp.add(name);
				vp.add(text);
				vp.add(urgencyLevel);
				vp.add(link);

				vpanel.add(vp);
				}
			}
			@Override
			public void onFailure(Throwable caught) {
				logger.warning("WTF ERROR");
			}
		});
	}
	public void addReceivedSos(String username, int time, String message, int draftid, int status, int urgencylevel) {
		
		
		VerticalPanel vp = new VerticalPanel();
		Label statusLabel = new Label("Estado: "+status);
		Label name = new Label(username);
		Label text = new Label(message);
		Label urgencyLevel = new Label("Nivel de urgencia: " +urgencylevel);
		//String url =help.get("url")+"?&id="+help.get("draftid");
		//Anchor link = new Anchor("Link a la prueba", url);
		//	link.setTarget("_blank");
		
		vp.add(statusLabel);
		vp.add(name);
		vp.add(text);
		vp.add(urgencyLevel);
		//vp.add(link);

		vpanel.add(vp);
	}
}
