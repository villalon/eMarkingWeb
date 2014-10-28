package cl.uai.client.rubric;

import cl.uai.client.MarkingInterface;
import cl.uai.client.resources.Resources;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ChatInterface extends Composite{

	private VerticalPanel mainPanel;
	//private Label title;
	private Frame frame;
	
	/**
	 * Creates the interface
	 */
	public ChatInterface() {
		this.mainPanel = new VerticalPanel();
		this.mainPanel.addStyleName(Resources.INSTANCE.css().previouscomments());
		
		/**
		*Embedded node.js chat
		*mongoDB MUST be listening
		*Node.js+socket.io file "app.js" MUST be running on 127.0.0.1:3000 (node server instance).
		**/
		String usernameOnline = MarkingInterface.getUsername();
		Integer idOnline = MarkingInterface.getUser();
		this.frame = new Frame("http://127.0.0.1:3000/chat.html?username="+usernameOnline+"&id="+idOnline);
		frame.setSize("100%", "100%");
		
		
		//mainPanel.add(title);
		mainPanel.add(frame);
		
		this.initWidget(mainPanel);
		
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		
		if(MarkingInterface.getCollaborativeFeatures() == 0)
			return;
	}

}
