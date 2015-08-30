/**
 * 
 */
package cl.uai.client.toolbar;

import java.util.logging.Logger;

import cl.uai.client.EMarkingConfiguration;
import cl.uai.client.EMarkingWeb;
import cl.uai.client.MarkingInterface;
import cl.uai.client.data.AjaxRequest;
import cl.uai.client.resources.Resources;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;

/**
 * @author Jorge Villalón
 *
 */
public class ChatButtons extends Composite {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(ChatButtons.class.getName());
	
	private HorizontalPanel mainPanel = null;
	
	private PushButton showChatButton = null;
	private PushButton showWallButton = null;
	private PushButton showHelpButton = null;

	private HTML lblChatNotification = null;
	private HTML lblWallNotification = null;
	private HTML lblHelpNotification = null;
	
	public ChatButtons() {
		this.mainPanel = new HorizontalPanel();
		this.mainPanel.addStyleName(Resources.INSTANCE.css().buttonshpanel());
		
		Icon chatIcon = new Icon(IconType.COMMENT_ALT);
		showChatButton = new PushButton();
		showChatButton.setHTML(chatIcon.toString());
		showChatButton.setTitle(MarkingInterface.messages.ShowChat());
		showChatButton.addStyleName(Resources.INSTANCE.css().rubricbutton());
		showChatButton.setVisible(false);
		showChatButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				EMarkingWeb.markingInterface.chat.show();				
			}
		});
		
		Icon wallIcon = new Icon(IconType.BULLHORN);
		showWallButton = new PushButton();
		showWallButton.setHTML(wallIcon.toString());
		showWallButton.setTitle(MarkingInterface.messages.ShowWall());
		showWallButton.addStyleName(Resources.INSTANCE.css().rubricbutton());
		showWallButton.setVisible(false);
		showWallButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				EMarkingWeb.markingInterface.wall.show();				
			}
		});
		
		Icon chkContinueIcon = new Icon(IconType.AMBULANCE);
		showHelpButton = new PushButton();
		showHelpButton.setHTML(chkContinueIcon.toString());
		showHelpButton.setTitle(MarkingInterface.messages.ShowHelp());
		showHelpButton.addStyleName(Resources.INSTANCE.css().rubricbutton());
		showHelpButton.setVisible(false);
		showHelpButton.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				EMarkingWeb.markingInterface.help.show();				
			}
		});
		
		Icon icon = new Icon(IconType.BELL_ALT);
		lblChatNotification = new HTML(icon.toString());
		lblChatNotification.addStyleName(Resources.INSTANCE.css().rubricbuttonjewel());
		lblChatNotification.setVisible(false);

		lblWallNotification = new HTML(icon.toString());
		lblWallNotification.addStyleName(Resources.INSTANCE.css().rubricbuttonjewel());
		lblWallNotification.setVisible(false);

		lblHelpNotification = new HTML(icon.toString());
		lblHelpNotification.addStyleName(Resources.INSTANCE.css().rubricbuttonjewel());
		lblHelpNotification.setVisible(false);

		addButtonAndNotification(showChatButton,lblChatNotification);
		addButtonAndNotification(showWallButton,lblWallNotification);
		addButtonAndNotification(showHelpButton,lblHelpNotification);
		
		initWidget(this.mainPanel);
	}
	
	private void addButtonAndNotification(PushButton button, Label notification) {
		AbsolutePanel vpanel = new AbsolutePanel();
		vpanel.add(button);
		vpanel.add(notification, 23, 2);
		this.mainPanel.add(vpanel);		
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		
		this.setWidth("0px");
	}
	
	public void loadSubmissionData() {		
		if(!EMarkingConfiguration.isChatEnabled()) {
				String moodleUrl = AjaxRequest.moodleUrl.replaceAll("mod/emarking/ajax/a.php", "");
				moodleUrl += "course/modedit.php?update=" + MarkingInterface.submissionData.getCoursemoduleid() + "&return=1";
				HTML lblNoChatAvailable = new HTML(MarkingInterface.messages.NoChatAvailable(moodleUrl));
				this.mainPanel.add(lblNoChatAvailable);
		} else {
			showWallButton.setVisible(true);
			showChatButton.setVisible(true);
			showHelpButton.setVisible(true);
		}
	}

	public void addNotification(int source) {
		switch(source) {
		case 1:
			lblChatNotification.setVisible(true);
			break;
		case 2:
			lblWallNotification.setVisible(true);
			break;
		case 3:
			lblHelpNotification.setVisible(true);
			break;
		}
	}
	
	public void removeNotification(int source) {
		switch(source) {
		case 1:
			lblChatNotification.setVisible(false);
			break;
		case 2:
			lblWallNotification.setVisible(false);
			break;
		case 3:
			lblHelpNotification.setVisible(false);
			break;
		}
	}
}
