/**
 * 
 */
package cl.uai.client.chat;

import cl.uai.client.resources.Resources;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Jorge
 *
 */
public class ChatInterface extends DialogBox {
	
	private VerticalPanel vpanel;
	private VerticalPanel messagesPanel;
	private ScrollPanel scrollMessagesPanel;
	private HorizontalPanel usersHpanel;
	private TextArea message;
	
	public ChatInterface() {
		
		// Dialog parameters
		this.setAutoHideEnabled(true);
		this.setAnimationEnabled(true);
		this.setModal(true);

		messagesPanel = new VerticalPanel(); 
		scrollMessagesPanel = new ScrollPanel(messagesPanel);
		scrollMessagesPanel.setSize("270px", "233px");
		scrollMessagesPanel.scrollToBottom();

		usersHpanel = new HorizontalPanel();
		usersHpanel.setSize("200px", "47px");
		
		message = new TextArea();
		message.setWidth("258px");
		message.setVisibleLines(2);
		message.addStyleName(Resources.INSTANCE.css().chatTextarea());
		message.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					// onSendMessage(message.getText(),1);
					message.setText("");
				}
			}
		});

		// Vertical panel that contains everything
		vpanel = new VerticalPanel(); 

		vpanel.add(usersHpanel);
		vpanel.add(scrollMessagesPanel);
		vpanel.add(message);

		this.add(vpanel);
	}
}
