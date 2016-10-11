/**
 * 
 */
package cl.uai.client.page;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;

import cl.uai.client.marks.Mark;
import cl.uai.client.marks.RubricMark;
import cl.uai.client.resources.Resources;

/**
 * @author Jorge
 *
 */
public class MinimizeIcon extends TrashIcon {

	/**
	 * 
	 */
	public MinimizeIcon() {
		super();
		
		// Sets inner HTML
		Icon icon = new Icon(IconType.MINUS);
		this.setHTML(icon.toString());
		
		// Removes trash CSS style
		this.removeStyleName(Resources.INSTANCE.css().trashicon());
		
		// Adds own CSS style
		this.addStyleName(Resources.INSTANCE.css().editicon());		
	}
	
	@Override
	protected void processCommand(ClickEvent event) {
		MinimizeIcon icon = (MinimizeIcon) event.getSource();
		
		// Hide icons as edit was clicked
		Mark.hideIcons();

		Mark mark = (Mark) icon.mark;
		if(mark instanceof RubricMark) {
			RubricMark rmark = (RubricMark) mark;
			rmark.setHeaderOnly(!rmark.isHeaderOnly());
			mark.setMarkHTML();
			Mark.hideIcons();
			Mark.markPopup.setVisible(false);
		}
	}

	@Override
	public void setMark(Mark sourcemark) {
		super.setMark(sourcemark);
		
		if(sourcemark instanceof RubricMark) {
			RubricMark rmark = (RubricMark) mark;
			// Sets inner HTML
			Icon icon = new Icon(rmark.isHeaderOnly() ? IconType.PLUS : IconType.MINUS);
			this.setHTML(icon.toString());
		}
	}
}
