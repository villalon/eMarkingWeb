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

import cl.uai.client.EMarkingConfiguration;
import cl.uai.client.MarkingInterface;
import cl.uai.client.resources.Resources;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A comment dialog shown to ask for an inner comment in Comment Marks
 * and also provides a select for bonus values for Rubric Marks 
 * 
 * @author Jorge Villalón <villalon@gmail.com>
 *
 */
public class RequestRegradeDialog extends DialogBox {

	public ListBox getMotive() {
		return motive;
	}

	public void setMotive(ListBox motive) {
		this.motive = motive;
	}

	public TextArea getComment() {
		return comment;
	}

	public void setComment(TextArea comment) {
		this.comment = comment;
	}

	Logger logger = Logger.getLogger(RequestRegradeDialog.class.getName());
	
	/**
	 * If the dialog was cancelled
	 * @return
	 */
	public boolean isCancelled() {
		return cancelled;
	}

	/** Dialog's main panel **/
	private VerticalPanel mainPanel;

	/** Indicates if the dialog was cancelled **/
	private boolean cancelled = false;
	
	/** Level id of the selected option **/
	private int levelId = 0;
	private ListBox motive = null;
	private TextArea comment = null;

	/**
	 * Creates a comment dialog at a specific position
	 */
	public RequestRegradeDialog() {
		super(true, false);
		
		this.addStyleName(Resources.INSTANCE.css().requestregradedialog());

		this.mainPanel = new VerticalPanel();
		this.mainPanel.setWidth("100%");
		
		motive = new ListBox();
		motive.setWidth("390px");
		
		motive.addItem(MarkingInterface.messages.Select(), "0");
		for(int motiveId : EMarkingConfiguration.getRegradeMotives().keySet()) {
			String motiveName = EMarkingConfiguration.getRegradeMotives().get(motiveId);
			motive.addItem(motiveName, Integer.toString(motiveId));
		}
		
		HorizontalPanel hpanel = new HorizontalPanel();
		hpanel.setWidth("100%");
		hpanel.add(new Label(MarkingInterface.messages.Motive()));
		hpanel.add(motive);
		hpanel.setCellHorizontalAlignment(motive, HasAlignment.ALIGN_RIGHT);
		this.mainPanel.add(hpanel);
		
		comment = new TextArea();
		comment.setVisibleLines(10);
		comment.setWidth("380px");
		comment.setHeight("150px");
		
		hpanel = new HorizontalPanel();
		hpanel.setWidth("100%");
		hpanel.add(new Label(MarkingInterface.messages.CommentForMarker()));
		hpanel.add(comment);
		hpanel.setCellHorizontalAlignment(comment, HasAlignment.ALIGN_RIGHT);
		this.mainPanel.add(hpanel);
		
		// Adds the CSS style and other settings
		this.addStyleName(Resources.INSTANCE.css().commentdialog());
		
		this.setAnimationEnabled(true);
		this.setGlassEnabled(true);

		this.setHTML(MarkingInterface.messages.RequestRegrade());

		// Save button
		Button btnSave = new Button(MarkingInterface.messages.Save());
		btnSave.addStyleName(Resources.INSTANCE.css().btnsave());
		btnSave.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(comment.getText().length() > 500) {
					Window.alert(MarkingInterface.messages.RequestMaximumLength(comment.getText().length()));
					return;
				} else if(motive.isItemSelected(0)){
					Window.alert(MarkingInterface.messages.MotiveIsMandatory());
					return;
				} else {
					cancelled = false;
					hide();
				}
			}
		});

		// Cancel button
		Button btnCancel = new Button(MarkingInterface.messages.Cancel());
		btnSave.addStyleName(Resources.INSTANCE.css().btncancel());
		btnCancel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				cancelled = true;
				hide();
			}
		});

		// Add buttons
		hpanel = new HorizontalPanel();
		hpanel.add(btnSave);
		hpanel.add(btnCancel);
		mainPanel.add(hpanel);
		mainPanel.setCellHorizontalAlignment(hpanel, HasHorizontalAlignment.ALIGN_RIGHT);
		
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
