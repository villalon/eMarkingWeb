/**
 * 
 */
package cl.uai.client.page;

import cl.uai.client.EMarkingWeb;
import cl.uai.client.MarkingInterface;
import cl.uai.client.marks.Mark;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;

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
		if(Window.confirm(MarkingInterface.messages.DeleteMarkConfirm())) {
			EMarkingWeb.markingInterface.deleteMark((Mark) this.mark);
		}
	}
}
