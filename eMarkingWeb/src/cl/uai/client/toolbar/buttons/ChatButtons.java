/**
 * 
 */
package cl.uai.client.toolbar.buttons;

import java.util.logging.Logger;

import cl.uai.client.EMarkingConfiguration;
import cl.uai.client.EMarkingWeb;
import cl.uai.client.MarkingInterface;

import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;

/**
 * @author Jorge Villalón
 *
 */
public class ChatButtons extends Buttons {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(ChatButtons.class.getName());
	
	private PushButton showChatButton = null;
	private PushButton showWallButton = null;
	private PushButton showHelpButton = null;
	
	private HTML lblNoChatAvailable = null;

	public ChatButtons() {
		
		String notification = "!";

		showChatButton = new PushButton(IconType.COMMENT_ALT, null, MarkingInterface.messages.ShowChat(), notification);
		showChatButton.setVisible(false);
		showChatButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				EMarkingWeb.markingInterface.chat.show();				
			}
		});
		
		showWallButton = new PushButton(IconType.BULLHORN, null, MarkingInterface.messages.ShowWall(), notification);
		showWallButton.setVisible(false);
		showWallButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				EMarkingWeb.markingInterface.wall.show();				
			}
		});
		
		showHelpButton = new PushButton(IconType.AMBULANCE, null, MarkingInterface.messages.ShowHelp(), notification);
		showHelpButton.setVisible(false);
		showHelpButton.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				EMarkingWeb.markingInterface.help.show();				
			}
		});

		this.mainPanel.add(showChatButton);
		this.mainPanel.add(showWallButton);
		this.mainPanel.add(showHelpButton);
	}
	
	@Override
	public void loadSubmissionData() {
		if(EMarkingConfiguration.isChatEnabled()) {
			if(!EMarkingConfiguration.isChatServerError()) {
				showWallButton.setVisible(true);
				showChatButton.setVisible(true);
				showHelpButton.setVisible(true);				
			} else {
				setChatServerError();
			}
		} else {
			setChatDisabled();
		}
	}

	public void addNotification(int source) {
		switch(source) {
		case 1:
			showChatButton.showNotification();
			break;
		case 2:
			showWallButton.showNotification();
			break;
		case 3:
			showHelpButton.showNotification();
			break;
		}
	}
	
	public void removeNotification(int source) {
		switch(source) {
		case 1:
			showChatButton.hideNotification();
			break;
		case 2:
			showWallButton.hideNotification();
			break;
		case 3:
			showHelpButton.hideNotification();
			break;
		}
	}
	
	public void setChatDisabled() {
		String moodleUrl = EMarkingConfiguration.getMoodleUrl().replaceAll("mod/emarking/ajax/a.php", "");
		moodleUrl += "course/modedit.php?update=" + MarkingInterface.submissionData.getCoursemoduleid() + "&return=1";
		if(EMarkingConfiguration.getUserCanManageDelphi()){
			lblNoChatAvailable = new HTML(MarkingInterface.messages.NoChatAvailable(moodleUrl));
		}else{
			lblNoChatAvailable = new HTML(MarkingInterface.messages.NoChatAvailableForMarker());
		}
		lblNoChatAvailable.setHeight("27px");
		this.mainPanel.clear();
		this.mainPanel.add(lblNoChatAvailable);		
	}
	
	public void setChatServerError() {
		HTML lblChatServerError = new HTML(MarkingInterface.messages.ChatServerError(EMarkingConfiguration.getAdministratorEmail()));
		lblChatServerError.setHeight("27px");
		this.mainPanel.clear();
		this.mainPanel.add(lblChatServerError);
	}
}
