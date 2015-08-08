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
public class AskForHelpCommand implements Command {

	Mark mark = null;
	
	public AskForHelpCommand(Mark m) {
		this.mark = m;
	}
	
	@Override
	public void execute() {
		EMarkingWeb.markingInterface.sos.center();
		EMarkingWeb.markingInterface.sos.show();
	}
}
