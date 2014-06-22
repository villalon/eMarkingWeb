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
import cl.uai.client.MarkingInterface;
import cl.uai.client.resources.Resources;
import cl.uai.client.rubric.RubricPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A comment dialog shown to ask for an inner comment in Comment Marks
 * and also provides a select for bonus values for Rubric Marks 
 * 
 * @author Jorge Villalón <villalon@gmail.com>
 *
 */
public class AddMarkDialog extends DialogBox {

	Logger logger = Logger.getLogger(AddMarkDialog.class.getName());
	
	/**
	 * If the dialog was cancelled
	 * @return
	 */
	public boolean isCancelled() {
		return cancelled;
	}

	/** Dialog's main panel **/
	private VerticalPanel mainPanel;

	private RubricPanel rubricPanel;
	
	/** Indicates if the dialog was cancelled **/
	private boolean cancelled = false;
	
	/** Level id of the selected option **/
	private int levelId = 0;

	private int rubricLeft;
	private int rubricTop;
	
	private MarkingPage page;
	
	/**
	 * @return the rubricLeft
	 */
	public int getRubricLeft() {
		return rubricLeft;
	}

	/**
	 * @param rubricLeft the rubricLeft to set
	 */
	public void setRubricLeft(int rubricLeft) {
		this.rubricLeft = rubricLeft;
	}

	/**
	 * @return the rubricRight
	 */
	public int getRubricTop() {
		return rubricTop;
	}

	/**
	 * @param rubricTop the rubricRight to set
	 */
	public void setRubricTop(int rubricTop) {
		this.rubricTop = rubricTop;
	}

	/**
	 * Creates a comment dialog at a specific position
	 */
	public AddMarkDialog(MarkingPage _parent) {
		super(true, false);
		
		this.page = _parent;
		
		this.mainPanel = new VerticalPanel();
		this.mainPanel.addStyleName(Resources.INSTANCE.css().addmarkdialog());

		this.rubricPanel = new RubricPanel();
		this.rubricPanel.setPopupInterface(true);
		
		this.mainPanel.add(rubricPanel);
		
		// Adds the CSS style and other settings
		this.addStyleName(Resources.INSTANCE.css().commentdialog());
		
		this.setAnimationEnabled(true);
		this.setGlassEnabled(true);

		this.setHTML(MarkingInterface.messages.AddEditMark());
		
		// Cancel button
		Button btnCancel = new Button(MarkingInterface.messages.Cancel());
		btnCancel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				cancelled = true;
				hide();
			}
		});

		// Add buttons
		HorizontalPanel hpanel = new HorizontalPanel();
		hpanel.add(btnCancel);
		mainPanel.add(hpanel);
		mainPanel.setCellHorizontalAlignment(hpanel, HasHorizontalAlignment.ALIGN_RIGHT);

		this.addCloseHandler(new CloseHandler<PopupPanel>() {			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if(!cancelled && levelId > 0) {
					EMarkingWeb.markingInterface.addRubricMark(
							levelId, 
							rubricLeft, 
							rubricTop,
							page);
				} else if(!cancelled) {
					logger.severe("Error adding mark! Level id " + levelId);
				}
			}
		});
		
		this.setWidget(mainPanel);
	}

	/**
	 * @return the levelId
	 */
	public int getLevelId() {
		return levelId;
	}

	/**
	 * @param levelId the levelId to set
	 */
	public void setLevelId(int levelId) {
		this.levelId = levelId;
	}
}
