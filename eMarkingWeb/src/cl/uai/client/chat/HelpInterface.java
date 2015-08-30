package cl.uai.client.chat;

import java.util.Date;

import cl.uai.client.MarkingInterface;
import cl.uai.client.chat.messages.SosMessage;

public class HelpInterface extends ChatInterface {
	
	public HelpInterface() {
		super();
		
		this.source = NodeChat.SOURCE_SOS;
		this.setHTML(MarkingInterface.messages.SOS());

		this.sendMessageTextArea.setVisible(false);
	}
	
	@Override
	public void addMessage(Date date, int userid, String message) throws Exception {
		throw new Exception("Invalid call");
	}
	
	public void addMessage(Date date, int userid, String message, int draftid, int status, int urgency) throws Exception {

		// The message panel
		SosMessage chatMessage = new SosMessage(
				ConnectedUsersPanel.allUsers.get(userid),
				date,
				message, 
				draftid, 
				status, 
				urgency);

		addMessage(chatMessage);
	}
	
	@Override
	protected void sendMessage(String message) {
		super.sendMessage(message);
	}
}
