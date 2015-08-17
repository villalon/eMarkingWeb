/**
 * 
 */
package cl.uai.client.chat;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import cl.uai.client.resources.Resources;
import cl.uai.client.utils.Color;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * @author Jorge Villalón
 *
 */
public class ConnectedUsersPanel extends HorizontalPanel {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(ConnectedUsersPanel.class.getName());
	
	/** A list with all currently connected users **/
	protected Map<Integer, HTML> connectedUsers = null;

	/** All users with messages or connected with their abbreviated names **/
	protected static Map<Integer, User> allUsers = new HashMap<Integer, User>();

	public ConnectedUsersPanel() {
		this.connectedUsers = new HashMap<Integer, HTML>();
	}

	/**
	 * Adds a user to the chat histoy of this interface
	 * @param userid user id
	 * @param abbreviation user name abbreviated
	 */
	public static User addUserFromHistory(User user) {
		if(!allUsers.containsKey(user.getId())) {
			user.setColor(allUsers.size());
			allUsers.put(user.getId(), user);
		} else {
			user = allUsers.get(user.getId());
		}
		return user;
	}

	/**
	 * Adds a connected user to the interface
	 * @param userdata user data (id, name)
	 */
	public void addUser(UserJS userdata) {

		User user = User.createFromJS(userdata);

		user = addUserFromHistory(user);
		
		// If the user is already connected it means it was already added
		if(connectedUsers.containsKey(user.getId()))
			return;

		HTML userConnectedIcon = createUserIcon(user);

		// We add the user to the list
		connectedUsers.put(user.getId(), userConnectedIcon);
		
		// Add the icon
		this.add(userConnectedIcon);
	}
	
	public static HTML createUserIcon(User user) {
		HTML userConnectedIcon = new HTML();
		userConnectedIcon.setText(user.getNickname());
		userConnectedIcon.addStyleName(Resources.INSTANCE.css().chatusers());
		userConnectedIcon.setTitle(user.getFullname());

		Color.setWidgetBackgroundHueColor(user.getColor(), userConnectedIcon);
		return userConnectedIcon;
	}

	
	/**
	 * Remove user from the interface
	 * @param userdata user data (id, name)
	 */
	public void removeUser(UserJS userdata) {
		int userid = Integer.parseInt(userdata.getId());

		HTML userConnectedIcon= connectedUsers.get(userid);

		// If the user is there, remove its icon and remove it from the list
		if(userConnectedIcon != null && userConnectedIcon.getParent() != null) {
			userConnectedIcon.removeFromParent();
			connectedUsers.remove(userid);
		}
	}
	
}
