// This file is part of Moodle - http://moodle.org/
//
// Moodle is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// Moodle is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Moodle.  If not, see <http://www.gnu.org/licenses/>.

/**
 * @package   eMarking
 * @copyright 2013 Jorge Villalón <villalon@gmail.com>
 * @license   http://www.gnu.org/copyleft/gpl.html GNU GPL v3 or later
 */
package cl.uai.client.page;

import java.util.logging.Logger;

import cl.uai.client.EMarkingWeb;
import cl.uai.client.marks.Mark;
import cl.uai.client.marks.RubricMark;
import cl.uai.client.resources.Resources;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author Jorge Villalón <villalon@gmail.com>
 *
 */
public class RegradeIcon extends HTML {

	private Logger logger = Logger.getLogger(RegradeIcon.class.getName());
	/**The mark related to the icon **/
	protected Mark mark = null;
	
	/**
	 * Creates a trash icon
	 */
	public RegradeIcon() {
		Icon icon = new Icon(IconType.COMMENTS);
		this.setHTML(icon.toString());
		this.addStyleName(Resources.INSTANCE.css().regradeicon());
		
		this.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {				
				event.stopPropagation();
				processCommand(event);
			}
		});
	}
	
	/**
	 * Sets the corresponding Mark for the icon
	 * 
	 * @param sourcemark current mark
	 */
	public void setMark(Mark sourcemark) {
		this.mark = sourcemark;
	}
	
	/**
	 * Processes its command when icon was clicked
	 * 
	 * @param event
	 */
	protected void processCommand(ClickEvent event) {
		Mark.hideIcons();
		
		final RubricMark rmark = (RubricMark) mark;

		final RequestRegradeDialog dialog = new RequestRegradeDialog();
		
		logger.fine("Regrade id: " + rmark.getRegradeid() + " motive: " + rmark.getRegrademotive() + " comment: " + rmark.getRegradecomment());
		
		dialog.getComment().setText(rmark.getRegradecomment());
		dialog.getMotive().setSelectedIndex(getMotiveIndex(rmark.getRegrademotive()));
		
		dialog.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if(dialog.isCancelled())
					return;
				
				String comment = dialog.getComment().getText();
				int motive = Integer.parseInt(dialog.getMotive().getValue(dialog.getMotive().getSelectedIndex()));
				
				EMarkingWeb.markingInterface.regradeMark(rmark, comment, motive);
			}
		});
		
		dialog.center();
		dialog.show();
	}
	
	private int getMotiveIndex(int motive) {
		if(motive <= 4)
			return motive;
		if(motive==10)
			return 5;
		return 0;
	}
}
