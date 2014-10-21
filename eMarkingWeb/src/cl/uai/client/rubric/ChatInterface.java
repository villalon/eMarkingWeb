package cl.uai.client.rubric;

import cl.uai.client.MarkingInterface;
import cl.uai.client.resources.Resources;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ChatInterface extends Composite{
	
	

	private VerticalPanel mainPanel;
	private Label title;


	/**
	 * Creates the interface
	 */
	public ChatInterface() {
		this.mainPanel = new VerticalPanel();
		this.mainPanel.addStyleName(Resources.INSTANCE.css().previouscomments());
		
		this.title = new Label("Acá va el chat");
		
		mainPanel.add(title);
		
		this.initWidget(mainPanel);
		
		
	}
	

	@Override
	protected void onLoad() {
		super.onLoad();
		
		if(MarkingInterface.getCollaborativeFeatures() == 0)
			return;
	}

}
