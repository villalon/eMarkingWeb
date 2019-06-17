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

import cl.uai.client.EMarkingComposite;
import cl.uai.client.MarkingInterface;
import cl.uai.client.data.AjaxData;
import cl.uai.client.data.AjaxRequest;
import cl.uai.client.resources.Resources;

import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Jorge Villalón
 *
 */
public class ChangeLogInterface extends EMarkingComposite {

	private static Logger logger = Logger.getLogger(ChangeLogInterface.class.getName());
	private VerticalPanel mainPanel = null;
	
	private Label title = null;
	private TextArea changeLogText = null;
	
	private Button saveLog = null;
	
	public ChangeLogInterface() {
		this.mainPanel = new VerticalPanel();
		this.mainPanel.addStyleName(Resources.INSTANCE.css().previouscomments());
		
		this.title = new Label(MarkingInterface.messages.ChangeLogInstructions());
		
		this.changeLogText = new TextArea();
		this.changeLogText.addStyleName(Resources.INSTANCE.css().generalfeedbacktxt());
		this.changeLogText.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				logger.fine("Changed!");
			}
		});
		
		this.saveLog = new Button(MarkingInterface.messages.SaveChanges());
		this.saveLog.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				AjaxRequest.ajaxRequest("action=addchangelog&txt=" + URL.encode(getChangeLogText()) , new AsyncCallback<AjaxData>() {					
					@Override
					public void onSuccess(AjaxData result) {
						Window.alert("Success!");
					}
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Falied! :-( Please try again later");
					}
				});
			}
		});
		
		mainPanel.add(title);
		mainPanel.add(changeLogText);
		mainPanel.add(saveLog);
		mainPanel.setCellHorizontalAlignment(saveLog, HasHorizontalAlignment.ALIGN_RIGHT);
		
		this.initWidget(mainPanel);
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		
		if(MarkingInterface.submissionData == null)
			return;
		
		// TODO: Check why this F*ck is not working
		String changelog = "";
		if(!MarkingInterface.submissionData.getChangelog().equals("null")) {
			changelog = MarkingInterface.submissionData.getChangelog();
		}
		
		this.changeLogText.setText(changelog);
	}
	
	public String getChangeLogText() {
		return this.changeLogText.getText();
	}
}
