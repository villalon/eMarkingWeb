package cl.uai.client.chat;

import cl.uai.client.MarkingInterface;


public class WallInterface extends ChatInterface {

	public WallInterface() {
		super();
		
		this.source = NodeChat.SOURCE_WALL;
		
		if(!MarkingInterface.submissionData.isSupervisor()) {
			this.sendMessageTextArea.setVisible(false);
		}
	}
	
	@Override
	protected void sendMessage(String message) {
		if(!MarkingInterface.submissionData.isSupervisor())
			return;
		
		super.sendMessage(message);
	}
}
