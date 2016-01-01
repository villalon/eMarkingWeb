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
import cl.uai.client.resources.Resources;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Jorge Villalón
 *
 */
public class GeneralFeedbackInterface extends EMarkingComposite {

	private VerticalPanel mainPanel = null;
	
	private Label title = null;
	private TextArea feedbackText = null;
	
	public GeneralFeedbackInterface() {
		this.mainPanel = new VerticalPanel();
		this.mainPanel.addStyleName(Resources.INSTANCE.css().previouscomments());
		
		this.title = new Label(MarkingInterface.messages.GeneralFeedbackInstructions());
		
		this.feedbackText = new TextArea();
		this.feedbackText.addStyleName(Resources.INSTANCE.css().generalfeedbacktxt());
		
		mainPanel.add(title);
		mainPanel.add(feedbackText);
		
		this.initWidget(mainPanel);
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		
		if(MarkingInterface.submissionData == null)
			return;
		
		String feedback = "";
		if(!MarkingInterface.submissionData.getFeedback().equals("null")) {
			feedback = MarkingInterface.submissionData.getFeedback();
		}
		
		this.feedbackText.setText(feedback);
	}
	
	public String getFeedbackText() {
		return this.feedbackText.getText();
	}
}
