/**
 * 
 */
package cl.uai.client.page;

import java.util.logging.Logger;

import cl.uai.client.MarkingInterface;
import cl.uai.client.marks.CommentMark;
import cl.uai.client.marks.Mark;
import cl.uai.client.marks.RubricMark;

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * This class represents a context menu (right click) which allows
 * to mark an exam on the spot
 * 
 * @author Jorge Villalon
 *
 */
public class EditMarkMenu extends PopupPanel {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(EditMarkMenu.class.getName());

	private MenuBar menu = null;

	public EditMarkMenu(Mark m, int x, int y) {
		super(true);

		this.setPopupPosition(x, y);

		menu = new MenuBar(true);

		if(m instanceof RubricMark || m instanceof CommentMark) {
			EditMarkCommand cmd = new EditMarkCommand(m, x, y) {			
				@Override
				public void execute() {
					super.execute();
					hide();
					Mark.hideIcons();
				}
			};
			menu.addItem(MarkingInterface.messages.Edit(), cmd);
		}

		DeleteMarkCommand dcmd = new DeleteMarkCommand(m) {			
			@Override
			public void execute() {
				super.execute();
				hide();
				Mark.hideIcons();
			}
		};
		menu.addItem(MarkingInterface.messages.Delete(), dcmd);

		this.add(menu);
	}
}
