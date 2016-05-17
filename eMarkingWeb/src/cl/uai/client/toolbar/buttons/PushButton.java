/**
 * 
 */
package cl.uai.client.toolbar.buttons;

import java.util.logging.Logger;

import cl.uai.client.resources.Resources;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

/**
 * @author Jorge Villalon
 *
 */
public class PushButton extends Composite {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(PushButton.class.getName());
	
	private IconType icon = null;
	private IconType iconPushed = null;
	private String label = null;
	private String notificationText = null;
	private HTML notification = null;
	private AbsolutePanel mainPanel = null;
	private com.google.gwt.user.client.ui.PushButton button = null;
	
	/**
	 * @param icon
	 * @param label
	 */
	public PushButton(IconType icon, String label) {
		this(icon, (IconType) null, label);
		
	}
	
	/**
	 * @param icon
	 * @param label
	 */
	public PushButton(IconType icon, IconType iconPushed, String label) {		
		this.icon = icon;
		this.label = label;
		
		this.mainPanel = new AbsolutePanel();

		this.button = new com.google.gwt.user.client.ui.PushButton();		
		this.button.addStyleName(Resources.INSTANCE.css().rubricbutton());
		this.button.setTitle(this.label);
		
		this.notification = new HTML();
		this.notification.addStyleName(Resources.INSTANCE.css().rubricbuttonjewel());
		
		this.setPushed(false);

		this.mainPanel.add(this.button);
		initWidget(mainPanel);
	}
	
	public PushButton(IconType icon, IconType iconPushed, String label, String notificationText) {
		this(icon, iconPushed, label);
		this.setNotification(notificationText);
	}
	
	public void setPushed(boolean pushed) {
		Icon i = (pushed && this.iconPushed != null) ?
				new Icon(this.iconPushed) : new Icon(this.icon);
		this.button.setHTML(i.toString() + 
				"<div class=\""+Resources.INSTANCE.css().rubricbuttontext()+"\">" +
				this.label + "</div>");
	}
	
	public void setNotification(String notificationText) {
		this.notificationText = notificationText;
		this.notification.setHTML(notificationText);

		if(this.mainPanel.getWidgetIndex(this.notification) < 0) {
			this.mainPanel.add(this.notification, 23, 0);					
		}
		
		this.showNotification();
	}
	
	public void showNotification() {
		if(this.notificationText == null)
			return;
		
		this.notification.setVisible(true);
	}
	
	public void hideNotification() {
		this.notification.setVisible(false);
	}
	
	public void addClickHandler(ClickHandler handler) {
		this.button.addClickHandler(handler);
	}
}
