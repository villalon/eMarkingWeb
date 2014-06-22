/**
 * 
 */
package cl.uai.client.page;

import cl.uai.client.marks.Mark;

import com.google.gwt.user.client.Command;

/**
 * @author jorgevillalon
 *
 */
public class EditMarkCommand implements Command {

	Mark mark = null;
	int posx;
	int posy;
	
	public EditMarkCommand(Mark m, int x, int y) {
		this.mark = m;
		this.posx = x;
		this.posy = y;
	}
	
	@Override
	public void execute() {
		this.mark.updateMark(posx, posy);
	}
}
