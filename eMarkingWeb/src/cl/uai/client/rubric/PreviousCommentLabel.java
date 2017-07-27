/**
 * 
 */
package cl.uai.client.rubric;

import java.util.logging.Logger;

import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;

import cl.uai.client.EMarkingWeb;
import cl.uai.client.page.StarIcon;

/**
 * @author Jorge Villalón
 *
 */
public class PreviousCommentLabel extends HTML {
	
	/** For logging purposes **/
	protected static Logger logger = Logger.getLogger(PreviousCommentLabel.class.getName());

	protected static StarIcon favicon;
	
	static {
		favicon = new StarIcon();
	}
	
	private static void showFavIcon(PreviousCommentLabel lbl, int left, int top) {
		if(favicon.getLabel() != null && favicon.getLabel() == lbl) {
			return;
		}
		favicon.setLabel(lbl);
		AbsolutePanel abspanel = EMarkingWeb.markingInterface.getMarkingPanel();
		int idx =  abspanel.getWidgetIndex(favicon);
		if(idx < 0) {
			abspanel.add(favicon, left, top);
		}
		abspanel.setWidgetPosition(favicon, left, top);
		favicon.setVisible(true);
	}
	public PreviousCommentLabel() {
		addMouseOverHandler(new MouseOverHandler() {			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				PreviousCommentLabel lbl = (PreviousCommentLabel) event.getSource();
				// Gets the absolute panel which contains the mark to calculate its coordinates

				AbsolutePanel abspanel = EMarkingWeb.markingInterface.getMarkingPanel();
				int top = lbl.getAbsoluteTop() - abspanel.getAbsoluteTop();
				int left = lbl.getAbsoluteLeft()  + lbl.getOffsetWidth();
				
				showFavIcon(lbl, left, top);
			}
		});
	}
}
