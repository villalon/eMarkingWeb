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
import cl.uai.client.data.AjaxData;
import cl.uai.client.data.AjaxRequest;
import cl.uai.client.resources.Resources;
import cl.uai.client.rubric.PreviousCommentLabel;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;

/**
 * @author Jorge Villalón <villalon@gmail.com>
 *
 */
public class StarIcon extends HTML {

	private Logger logger = Logger.getLogger(StarIcon.class.getName());
	
	/**The mark related to the icon **/
	protected PreviousCommentLabel lbl = null;
	
	/**
	 * Creates a trash icon
	 */
	public StarIcon() {
		Icon icon = new Icon(IconType.STAR);
		this.setHTML(icon.toString());
		this.addStyleName(Resources.INSTANCE.css().staricon());
		
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
	public void setLabel(PreviousCommentLabel _lbl) {
		this.lbl = _lbl;
	}
	
	public PreviousCommentLabel getLabel() {
		return this.lbl;
	}
	/**
	 * Processes its command when icon was clicked
	 * 
	 * @param event
	 */
	protected void processCommand(ClickEvent event) {
		logger.info("Making comment " + this.lbl.getText() + " favorite");
		EMarkingWeb.markingInterface.addLoading(true);
		this.setVisible(false);
		AjaxRequest.ajaxRequest("action=addprevcomment&comment=" + URL.encode(this.lbl.getText()) + "&favorite=1", new AsyncCallback<AjaxData>() {
			
			@Override
			public void onSuccess(AjaxData result) {
				if(!result.getError().equals("")) {
					Window.alert("Error saving comment as favorite");
				} else {
					EMarkingWeb.markingInterface.getRubricInterface().getToolsPanel().getPreviousComments().setCommentAsFavorite(lbl.getText());
				}
				EMarkingWeb.markingInterface.finishLoading();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error making comment favorite");
				EMarkingWeb.markingInterface.finishLoading();
			}
		});
		EMarkingWeb.markingInterface.addPreviousComment(this.lbl.getHTML(), true);
	}
}
