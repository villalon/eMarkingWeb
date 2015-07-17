package cl.uai.client;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HelpInterface extends DialogBox {
	
	private VerticalPanel vPanel;
	private ScrollPanel scrollPanel;
	
	public HelpInterface(){
		
		
		vPanel = new VerticalPanel();
		AbsolutePanel Apanel = new AbsolutePanel();
		Apanel.setSize("450px", "450px");
		scrollPanel = new ScrollPanel(vPanel);
		scrollPanel.setSize("449px", "449px");
		scrollPanel.scrollToBottom();
		Apanel.add(scrollPanel);
		
		this.setAutoHideEnabled(true);
	    this.add(vPanel);

	}
	

}
