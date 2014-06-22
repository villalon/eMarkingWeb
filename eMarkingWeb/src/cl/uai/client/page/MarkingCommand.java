/**
 * 
 */
package cl.uai.client.page;

import cl.uai.client.EMarkingWeb;
import cl.uai.client.data.Level;

import com.google.gwt.user.client.Command;

/**
 * @author jorgevillalon
 *
 */
public class MarkingCommand implements Command {

	MarkingPage page = null;
	Level level = null;
	int posx;
	int posy;
	
	public MarkingCommand(Level lvl, int x, int y, MarkingPage p) {
		this.level = lvl;
		this.posx = x;
		this.posy = y;
		this.page = p;
	}
	
	@Override
	public void execute() {
		// If it is a rubric level add a rubric mark
		EMarkingWeb.markingInterface.addRubricMark(
				level.getId(), 
				posx, 
				posy,
				page);
	}
}
