/**
 * 
 */
package cl.uai.client.chat.messages;

import java.util.Date;

/**
 * @author Jorge
 *
 */
public class WallMessage extends ChatMessage {

	public WallMessage(int userid, Date date, String userAbbreviation,
			String userFullname, String message, int color) {
		super(userid, date, userAbbreviation, userFullname, message, color);
	}
}
