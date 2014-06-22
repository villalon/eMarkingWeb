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
package cl.uai.client.toolbar;

 import cl.uai.client.EMarkingWeb;
import cl.uai.client.MarkingInterface;
import cl.uai.client.resources.Resources;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Jorge Villalón <villalon@gmail.com>
 *
 */
public class RotatePagesDialog extends DialogBox {

	private VerticalPanel vpanel = null;
	private boolean cancelled = true;
	private VerticalPanel apanel = null;
	private ScrollPanel scrollpanel = null;
	private int selectedPage = -1;
	
	public RotatePagesDialog() {
		this.setHTML(MarkingInterface.messages.SortPages());
		this.addStyleName(Resources.INSTANCE.css().commentdialog());

		int pages = EMarkingWeb.markingInterface.getMarkingPagesInterface().getNumPages();
		
		this.setPixelSize(300, 300);
		
		scrollpanel = new ScrollPanel();
		scrollpanel.setPixelSize(300, 300);
		
		apanel = new VerticalPanel();
		for(int i=0; i<pages; i++) {
			PushButton btnPage = new PushButton(MarkingInterface.messages.RotatePage(i+1));
			btnPage.setTabIndex(i+1);
			btnPage.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					PushButton btn = (PushButton) event.getSource();
					selectedPage = btn.getTabIndex();
					cancelled = false;
					hide();
				}
			});
			apanel.add(btnPage);
		}
		scrollpanel.add(apanel);
		
		
		this.setModal(true);
		this.setAutoHideEnabled(false);
		this.setGlassEnabled(true);
		
		vpanel = new VerticalPanel();
		vpanel.add(new Label(MarkingInterface.messages.RotatePagesInstructions()));
		vpanel.add(scrollpanel);
		
		// Basic save/cancel buttons
		HorizontalPanel hpanel = new HorizontalPanel();
		
		Button cancelButton = new Button(MarkingInterface.messages.Cancel());
		cancelButton.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				cancelled = true;
				hide();
			}
		});
		
		hpanel.add(cancelButton);
		
		vpanel.add(hpanel);
		vpanel.setCellHorizontalAlignment(hpanel, HasHorizontalAlignment.ALIGN_RIGHT);
		
		this.setWidget(vpanel);
	}

	/**
	 * @return the cancelled
	 */
	public boolean isCancelled() {
		return cancelled;
	}
	
	public int getSelectedPage() {
		return this.selectedPage;
	}
}
