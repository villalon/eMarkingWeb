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
package cl.uai.client.rubric;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;

import cl.uai.client.EMarkingComposite;
import cl.uai.client.EMarkingWeb;
import cl.uai.client.MarkingInterface;
import cl.uai.client.data.AjaxData;
import cl.uai.client.data.AjaxRequest;
import cl.uai.client.toolbar.RotatePagesDialog;
import cl.uai.client.toolbar.SortPagesDialog;

/**
 * @author jorge
 *
 */
public class SortPagesInterface extends EMarkingComposite {

	private VerticalPanel vpanel = null;
	private PushButton sortPagesButton = null;
	private PushButton rotatePagesButton = null;

	public SortPagesInterface() {
		this.vpanel = new VerticalPanel();
		sortPagesButton = new PushButton(MarkingInterface.messages.SortPagesChange());
		
		sortPagesButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				final SortPagesDialog dialog = new SortPagesDialog();
				dialog.addCloseHandler(new CloseHandler<PopupPanel>() {
					
					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if(dialog.isCancelled())
							return;
						
						String[] neworder = dialog.getSortPagesTable().getSortOrder();
						String neworderstring = "";
						for(int i=0; i<neworder.length; i++) {
							neworderstring += neworder[i];
							if(i<neworder.length-1)
								neworderstring += ",";
						}
						
						EMarkingWeb.markingInterface.addLoading(true);
						
						AjaxRequest.ajaxRequest("action=sortpages&neworder="+neworderstring, new AsyncCallback<AjaxData>() {
							
							@Override
							public void onSuccess(AjaxData result) {
								if(result.getError().equals("")) {
									EMarkingWeb.markingInterface.reloadPage();
								} else {
									Window.alert("Fatal error trying to resort pages");
									EMarkingWeb.markingInterface.finishLoading();
								}
							}
							
							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Fatal error trying to resort pages");
								EMarkingWeb.markingInterface.finishLoading();
							}
						});
					}
				});
				dialog.center();
			}
		});
		
		vpanel.add(sortPagesButton);
		
		rotatePagesButton = new PushButton(MarkingInterface.messages.RotatePages());
		
		rotatePagesButton.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				final RotatePagesDialog dialog = new RotatePagesDialog();
				dialog.addCloseHandler(new CloseHandler<PopupPanel>() {
					
					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if(dialog.isCancelled())
							return;
						
						EMarkingWeb.markingInterface.addLoading(true);
						
						AjaxRequest.ajaxRequest("action=rotatepage&pageno=" + dialog.getSelectedPage(), 
								new AsyncCallback<AjaxData>() {
							
							@Override
							public void onSuccess(AjaxData result) {
								if(result.getError().equals("")) {
									EMarkingWeb.markingInterface.reloadPage();
								} else {
									Window.alert("Fatal error trying to rotate page");
									EMarkingWeb.markingInterface.finishLoading();
								}
							}
							
							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Fatal error trying to rotate page");
								EMarkingWeb.markingInterface.finishLoading();
							}
						});

					}
				});
				
				dialog.center();
			}
		});

		vpanel.add(rotatePagesButton);
		
		initWidget(vpanel);
	}
}
