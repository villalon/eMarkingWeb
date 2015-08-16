/**
 * 
 */
package cl.uai.client.chat;

import java.util.Map;

/**
 * @author Jorge
 *
 */
public class User {

	private int id;
	private String fullname;
	private String nickname;
	private int color;
	
	public int getId() {
		return id;
	}

	public String getFullname() {
		return fullname;
	}

	public String getNickname() {
		return nickname;
	}

	/**
	 * @param id
	 * @param fullname
	 * @param nickname
	 */
	public User(int id, String fullname, String nickname) {
		super();
		this.id = id;
		this.fullname = fullname;
		this.nickname = nickname;
	}
	
	public static User createFromJS(UserJS userjs) {
		int userid = Integer.parseInt(userjs.getId());
		String fullname = userjs.getFirstName() + " " + userjs.getLastName();
		String nickname = userjs.getFirstName().substring(0, 1).toUpperCase() +
				userjs.getLastName().substring(0, 1).toUpperCase();
		User user = new User(userid, fullname, nickname);
		return user;
	}

	public static User createFromJson(Map<String, String> json) {		
		int userid=Integer.parseInt(json.get("userid"));
		String firstname=json.get("firstname");
		String lastname=json.get("lastname");
		String nickname = firstname.substring(0,1).toUpperCase() 
				+ lastname.substring(0,1).toUpperCase();
		String fullname = firstname + " " + lastname;
		User user = new User(userid, fullname, nickname);
		return user;
	}

	public int getColor() {
		return this.color;
	}
	
	public void setColor(int color) {
		this.color = color;
	}
}
