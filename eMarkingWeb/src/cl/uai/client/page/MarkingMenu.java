/**
 * 
 */
package cl.uai.client.page;

import java.util.logging.Logger;

import cl.uai.client.EMarkingConfiguration;
import cl.uai.client.MarkingInterface;
import cl.uai.client.data.Criterion;
import cl.uai.client.data.Level;

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * This class represents a context menu (right click) which allows
 * to mark an exam on the spot
 * 
 * @author Jorge Villalon
 *
 */
public class MarkingMenu extends PopupPanel {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(MarkingMenu.class.getName());
	
	private MenuBar menu = null;
	
	public MarkingMenu(MarkingPage page, int x, int y) {
		super(true);
		
		this.setPopupPosition(x, y);
		
		menu = new MenuBar(true);
		
		// Calculate basic position and page number to add a Mark
		final int newposx = x - page.getAbsolutePanel().getAbsoluteLeft();
		final int newposy = y - page.getAbsolutePanel().getAbsoluteTop();

		for(int criterionid : MarkingInterface.submissionData.getRubricfillings().keySet()) {
			Criterion criterion = MarkingInterface.submissionData.getRubricfillings().get(criterionid);
			
			// If the criterion is already marked, ignore
			if(criterion.getSelectedLevel() != null)
				continue;
			
			MenuBar criterion1 = new MenuBar(true);
			for(Level lvl : criterion.getLevels().values()) {
				MarkingCommand cmd = new MarkingCommand(lvl, newposx, newposy, page) {			
					@Override
					public void execute() {
						super.execute();
						hide();
					}
				};
				String menutext = lvl.getDescription();
				if(menutext.length() > 20) {
					menutext = lvl.getDescription().substring(0, Math.min(20, lvl.getDescription().length())) + "...";
				}
				MenuItem item = criterion1.addItem(menutext, cmd);
				item.setTitle(lvl.getDescription());
			}
			String menutext = criterion.getDescription();
			if(menutext.length() > 20) {
				menutext = criterion.getDescription().substring(0, Math.min(20, criterion.getDescription().length())) + "...";
			}
			MenuItem item = menu.addItem(menutext, criterion1);
			item.setTitle(criterion.getDescription());
		}
		
		if(EMarkingConfiguration.isChatEnabled()) {
			MenuItem item = menu.addItem("Ask for help", new AskForHelpCommand(null));
			item.setTitle("Title");
		}
		
		this.add(menu);
	}
}
