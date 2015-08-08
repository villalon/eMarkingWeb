package cl.uai.client;

import cl.uai.client.chat.ChatInterface;
import cl.uai.client.chat.NodeChat;

public class WallInterface extends ChatInterface {

	public WallInterface() {
		super();
		
		this.source = NodeChat.SOURCE_WALL;
	}
}
