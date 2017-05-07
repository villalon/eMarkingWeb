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
 * 				   Hans C. Jeria <hansj@live.cl>
 * @license   http://www.gnu.org/copyleft/gpl.html GNU GPL v3 or later
 */
package cl.uai.client.rubric;

import java.util.Date;
import java.util.logging.Logger;

import cl.uai.client.EMarkingComposite;
import cl.uai.client.EMarkingConfiguration;
import cl.uai.client.EMarkingWeb;
import cl.uai.client.resources.Resources;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.CalendarUtil;


/**
 * A rubric interface holds a rubric and its fillings according to the student's marks
 * 
 * @author Jorge Villalon <villalon@gmail.com>
 *
 */
public class RubricInterface extends EMarkingComposite {

	/** For logging purposes **/
	Logger logger = Logger.getLogger(RubricInterface.class.getName());

	/** Main panel, which includes titles and interfaces for rubric and previous comments **/
	private SplitLayoutPanel mainPanel = null;
	
	/** The rubric panel **/
	private RubricPanel rubricPanel = null;
	/** Panel containing tools for marking and students **/
	private ToolsPanel toolsPanel = null;

	private ScrollPanel scrollRubric;

	private ScrollPanel scrollTools;
	
	/**
	 * Constructor
	 */
	public RubricInterface() {
		mainPanel = new SplitLayoutPanel();
		mainPanel.addStyleName(Resources.INSTANCE.css().rubricinterface());
		// Set width of the rubric at 35% of the window
		//mainPanel.setWidth((Window.getClientWidth()*0.35)+"px");

		rubricPanel = new RubricPanel();
		scrollRubric = new ScrollPanel(rubricPanel);
		mainPanel.addNorth(scrollRubric, 300);
		
		toolsPanel = new ToolsPanel();
		scrollTools = new ScrollPanel(toolsPanel);
		mainPanel.add(scrollTools);
		
		this.initWidget(mainPanel);
	}

	public ScrollPanel getScrollRubric() {
		return scrollRubric;
	}

	public ScrollPanel getScrollTools() {
		return scrollTools;
	}

	@Override
	protected void onLoad() {
		mainPanel.setHeight(getParent().getOffsetHeight() + "px");
		super.onLoad();		
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		Date oneyear = new Date();
		CalendarUtil.addMonthsToDate(oneyear, 12);
				
		Cookies.setCookie("emarking_showrubric", visible ? "1" : "0", oneyear);
		EMarkingConfiguration.setShowRubricOnLoad(visible);
		
		EMarkingWeb.markingInterface.getMarkingPagesInterface().loadInterface();
		EMarkingWeb.markingInterface.setShowRubricButtonVisible(visible);
	}
	
	/**
	 * @return the rubricPanel
	 */
	public RubricPanel getRubricPanel() {
		return rubricPanel;
	}

	public ToolsPanel getToolsPanel() {
		return this.toolsPanel;
	}
}
