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

import java.util.logging.Logger;

import cl.uai.client.EMarkingComposite;
import cl.uai.client.resources.Resources;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.VerticalPanel;


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
	private VerticalPanel mainPanel = null;
	
	/** The rubric panel **/
	private RubricPanel rubricPanel = null;
	/** Panel containing tools for marking and students **/
	private ToolsPanel toolsPanel = null;
	
	/**
	 * Constructor
	 */
	public RubricInterface() {
		mainPanel = new VerticalPanel();
		mainPanel.addStyleName(Resources.INSTANCE.css().rubricinterface());
		mainPanel.setWidth((Window.getClientWidth()*0.35)+"px");

		rubricPanel = new RubricPanel();
		rubricPanel.setWidth((Window.getClientWidth()*0.35)+"px");
		mainPanel.add(rubricPanel);
		
		toolsPanel = new ToolsPanel();
		toolsPanel.setWidth((Window.getClientWidth()*0.35)+"px");
		mainPanel.add(toolsPanel);
		
		this.initWidget(mainPanel);
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		toolsPanel.setWidth((Window.getClientWidth()*0.35)+"px");
		resizeToolsPanel();
	}
	
	public void resizeToolsPanel() {
		if(true)
		return;
		int height = Window.getClientHeight() - toolsPanel.getAbsoluteTop();
		toolsPanel.setWidth("100%");
		toolsPanel.setHeight(height+"px");
	}
	
	/**
	 * @return the rubricPanel
	 */
	public RubricPanel getRubricPanel() {
		return rubricPanel;
	}

	public void restoreRubricPanel() {
		mainPanel.remove(rubricPanel);
		mainPanel.insert(rubricPanel, 0);
	}
	
	public ToolsPanel getToolsPanel() {
		return this.toolsPanel;
	}
}
