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

import cl.uai.client.EMarkingComposite;
import cl.uai.client.MarkingInterface;
import cl.uai.client.data.SubmissionGradeData;
import cl.uai.client.marks.RubricMark;
import cl.uai.client.resources.Resources;

import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * @author Jorge Villalón <villalon@gmail.com>
 *
 */
public class SubmissionGrade extends EMarkingComposite {

	/** The main panel holding the submission's grade **/
	private HorizontalPanel mainPanel = null;
	private Label lblGrade = null;
	private Label lblScale = null;
	
	/**
	 * Creates the interface
	 */
	public SubmissionGrade() {
		this.mainPanel = new HorizontalPanel();

		this.initWidget(mainPanel);
	}

	/**
	 * Loads submission data from the interface
	 */
	public void loadSubmissionData() {
		mainPanel.clear();

		SubmissionGradeData sdata = MarkingInterface.submissionData;
		
		if(sdata == null)
			return;
		
		
		lblGrade = new Label(RubricMark.scoreFormat(sdata.getFinalgrade(), false)+"");
		lblGrade.addStyleName(Resources.INSTANCE.css().grade());
		
		lblScale = new Label(RubricMark.scoreFormat(sdata.getGrademin(), false) + " / " + RubricMark.scoreFormat(sdata.getGrademax(), false));
		lblScale.addStyleName(Resources.INSTANCE.css().scale());
		
		VerticalPanel gradePanel = new VerticalPanel();
		gradePanel.addStyleName(Resources.INSTANCE.css().gradepanel());
		gradePanel.add(lblGrade);
		gradePanel.add(lblScale);
		gradePanel.setCellHorizontalAlignment(lblGrade, HasAlignment.ALIGN_RIGHT);
		gradePanel.setCellHorizontalAlignment(lblScale, HasAlignment.ALIGN_RIGHT);
		
		mainPanel.add(gradePanel);		
	}
}
