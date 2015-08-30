package cl.uai.client.chat;

import cl.uai.client.EMarkingConfiguration;
import cl.uai.client.MarkingInterface;

/**
 * The Wall allows supervisors (teacher or admin) to post messages that all markers
 * can see. Markers can't write on the wall
 * 
 * @author Jorge Villalón
 *
 */
public class WallInterface extends ChatInterface {

	public WallInterface() {
		super();
		
		this.source = NodeChat.SOURCE_WALL;
		this.setHTML(MarkingInterface.messages.Wall());
		
		if(!EMarkingConfiguration.isSupervisor()) {
			this.sendMessageTextArea.setVisible(false);
		}
	}
	
	@Override
	protected void sendMessage(String message) {
		if(!EMarkingConfiguration.isSupervisor())
			return;
		
		super.sendMessage(message);
	}
}
