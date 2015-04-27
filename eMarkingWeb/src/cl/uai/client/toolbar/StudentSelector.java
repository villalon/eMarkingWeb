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

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import cl.uai.client.EMarkingComposite;
import cl.uai.client.EMarkingWeb;
import cl.uai.client.MarkingInterface;
import cl.uai.client.data.AjaxData;
import cl.uai.client.data.AjaxRequest;
import cl.uai.client.resources.Resources;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;


/**
 * The student selector is a list box which shows the current student and others
 * 
 * @author Jorge Villalon <villalon@gmail.com>
 *
 */
public class StudentSelector extends EMarkingComposite {

	Logger logger = Logger.getLogger(StudentSelector.class.getName());

	/** main interface panel **/
	private HorizontalPanel mainPanel = null;
	private ListBox students = null;
	private boolean loaded = false;

	private class StudentChangeHandler implements ChangeHandler {
		@Override
		public void onChange(ChangeEvent event) {
			ListBox selector = (ListBox) event.getSource();
			selector.setEnabled(false);
			int newSubmissionId = Integer.parseInt(students.getValue(students.getSelectedIndex()));
			// Reload the whole interface by reloading the page
			EMarkingWeb.markingInterface.reload(newSubmissionId);
		}
	}

	/**
	 * Creates the student selector
	 */
	public StudentSelector() {
		mainPanel = new HorizontalPanel();
		students = new ListBox();
		students.addChangeHandler(new StudentChangeHandler());

		mainPanel.add(students);
		mainPanel.addStyleName(Resources.INSTANCE.css().studentSelector());
		students.addStyleName(Resources.INSTANCE.css().studentSelector());

		this.initWidget(mainPanel);
	}

	@Override
	protected void onLoad() {
		super.onLoad();

		// If no submission id is set, no sense to do this. Same if it was already loaded.
		if(MarkingInterface.getSubmissionId() <= 0 || loaded || MarkingInterface.submissionData == null)
			return;

		AjaxRequest.ajaxRequest("action=getstudents", new AsyncCallback<AjaxData>() {
			@Override
			public void onFailure(Throwable caught) {
				logger.severe("Error getting students from Moodle!");
				logger.severe(caught.getMessage());
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(AjaxData result) {
				List<Map<String, String>> values = AjaxRequest.getValuesFromResult(result);
				students.clear();
				int index=0;
				for(Map<String, String> value : values) {
					String studentname = MarkingInterface.messages.StudentN(MarkingInterface.messages.Anonymous()+" "+(index+1));
					if(!MarkingInterface.isStudentAnonymous()) {
						studentname = value.get("lastname") + ", " + value.get("firstname");
					}
					studentname += "\t\t["+getStringFromStatus(value.get("status")) + "]";
					int studentId = Integer.parseInt(value.get("studentid"));
					if(!MarkingInterface.readonly || studentId == MarkingInterface.submissionData.getStudentid()) {
						students.addItem(studentname, value.get("id"));
						if(studentId == MarkingInterface.submissionData.getStudentid()) {
							students.setSelectedIndex(index);
						}
						index++;
					}
				}
				loaded = true;
			}
		});

		students.addItem(MarkingInterface.messages.Loading());
	}

	/**
	 * Gets a user consumable string (internationalized) from a submission status
	 * @param status
	 * @return a string representing the status
	 */
	private String getStringFromStatus(String status) {
		int stat = Integer.parseInt(status);

		switch(stat) {
		case 40:
			return MarkingInterface.messages.StatusAccepted();
		case 15:
			return MarkingInterface.messages.StatusGrading();
		case 0:
			return MarkingInterface.messages.StatusMissing();
		case 30:
			return MarkingInterface.messages.StatusRegrading();
		case 20:
			return MarkingInterface.messages.StatusResponded();
		case 10:
			return MarkingInterface.messages.StatusSubmitted();
		default:
			return MarkingInterface.messages.StatusError();
		}
	}

	/**
	 * Enables the student selector
	 */
	public void enableSelector() {
		this.students.setEnabled(true);
	}
}
