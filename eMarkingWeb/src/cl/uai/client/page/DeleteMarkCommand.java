/**
 * 
 */
package cl.uai.client.page;

import cl.uai.client.EMarkingWeb;
import cl.uai.client.marks.Mark;

import com.google.gwt.user.client.Command;

/**
 * @author jorgevillalon
 *
 */
public class DeleteMarkCommand implements Command {

	Mark mark = null;
	
	public DeleteMarkCommand(Mark m) {
		this.mark = m;
	}
	
	@Override
	public void execute() {
		EMarkingWeb.markingInterface.deleteMark((Mark) this.mark);
	}
}
