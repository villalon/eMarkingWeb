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
package cl.uai.client.toolbar.buttons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import cl.uai.client.EMarkingComposite;
import cl.uai.client.EMarkingConfiguration;
import cl.uai.client.EMarkingWeb;
import cl.uai.client.MarkingInterface;
import cl.uai.client.data.Criterion;
import cl.uai.client.resources.Resources;
import cl.uai.client.toolbar.CriterionListBox;
import cl.uai.client.utils.Color;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ToggleButton;

/**
 * @author Jorge Villalón <villalon@gmail.com>
 *
 */
public class MarkingButtons extends EMarkingComposite {

	public static class EmarkingToggleButton extends ToggleButton {
		private int format;
		private ButtonFormat type;
		public EmarkingToggleButton(int _format, ButtonFormat _type, IconType _icon, String title) {
			this(_format, _type, (new Icon(_icon)).toString(), title);
		}
		public EmarkingToggleButton(int _format, ButtonFormat _type, String html, String title) {
			super();
			this.setTitle(title);
			this.format= _format;
			this.type = _type;
			this.setHTML(html);
			this.addStyleName(Resources.INSTANCE.css().rubricbutton());
		}
		public int getFormat() {
			return this.format;
		}
		public ButtonFormat getType() {
			return this.type;
		}
	}
	private static Logger logger = Logger.getLogger(MarkingButtons.class.getName());

	/** Main panel holding the buttons **/
	private HorizontalPanel markingButtonsPanel = null;

	/** The buttons **/
	private List<EmarkingToggleButton> buttons = null;

	private Map<String, Integer> customButtonIndex = null;
	/**
	 * @return the buttons
	 */
	public List<EmarkingToggleButton> getButtons() {
		return buttons;
	}

	/** Selected index **/
	private int selectedIndex = 0;
	/** The criterion selection list box **/
	private CriterionListBox criterionList = null;

	public void changeCriterionList(int index){
		criterionList.setSelectedIndex(index);
	}

	private EmarkingToolBarValueChangeHandler handler = new EmarkingToolBarValueChangeHandler();

	public static Map<Integer, EmarkingToggleButton> availableButtons;
	static {
		availableButtons = new HashMap<Integer, EmarkingToggleButton>();
		availableButtons.put(2, new EmarkingToggleButton(2, ButtonFormat.BUTTON_RUBRIC, IconType.TH, MarkingInterface.messages.RubricTitle()));
		availableButtons.put(1, new EmarkingToggleButton(1, ButtonFormat.BUTTON_COMMENT, IconType.COMMENT, MarkingInterface.messages.CommentTitle()));
		availableButtons.put(3, new EmarkingToggleButton(3, ButtonFormat.BUTTON_TICK, IconType.OK, MarkingInterface.messages.CheckTitle()));
		availableButtons.put(4, new EmarkingToggleButton(4, ButtonFormat.BUTTON_CROSS, IconType.REMOVE, MarkingInterface.messages.CrossTitle()));
		availableButtons.put(5, new EmarkingToggleButton(5, ButtonFormat.BUTTON_PEN, IconType.PENCIL, MarkingInterface.messages.PenTitle()));
		availableButtons.put(7, new EmarkingToggleButton(7, ButtonFormat.BUTTON_HIGHLIGHT, IconType.UNDERLINE, MarkingInterface.messages.MarkerTitle()));
		availableButtons.put(6, new EmarkingToggleButton(6, ButtonFormat.BUTTON_QUESTION, IconType.QUESTION_SIGN, MarkingInterface.messages.QuestionTitle()));
	}

	/**
	 * Stats like in Facebook jewels
	 */
	private Map<Integer, Label> buttonsStats;

	/**
	 * Creates the rubric buttons interface
	 */
	public MarkingButtons() {

		// main panel contains all buttons
		markingButtonsPanel = new HorizontalPanel();
		markingButtonsPanel.addStyleName(Resources.INSTANCE.css().toolbarbuttons());

		// Initialize the array
		buttons = new ArrayList<EmarkingToggleButton>();
		buttonsStats = new HashMap<Integer, Label>();

		criterionList = new CriterionListBox();

		this.initWidget(markingButtonsPanel);
	}

	@Override
	protected void onLoad() {
		super.onLoad();

		this.markingButtonsPanel.setWidth("0px");
	}

	public Criterion getSelectedCriterion(){

		int id = Integer.parseInt(criterionList.getValue(criterionList.getSelectedIndex()));
		if(id==0){
			return null;
		}

		return MarkingInterface.submissionData.getRubricfillings().get(id);
	}

	/**
	 * Handles when a button was clicked, selecting it and deselecting others in the toolbar
	 * 
	 * @author Jorge Villalón <villalon@gmail.com>
	 *
	 */
	private class EmarkingToolBarValueChangeHandler implements ValueChangeHandler<Boolean> {
		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			ToggleButton tbutton = (ToggleButton) event.getSource();
			pushButton(buttons.indexOf(tbutton), true);
			EMarkingWeb.markingInterface.getElement().getStyle().setCursor(Cursor.DEFAULT);
		}
	}

	private void pushButton(int index, boolean fromEvent) {
		if(index >= buttons.size())
			return;

		ToggleButton tbutton = buttons.get(index);

		if(tbutton == null) {
			return;
		}
		if (fromEvent && !tbutton.isDown()) {
			tbutton.setValue(true);
			return;
		}

		for(int i=0; i < buttons.size(); i++) {
			ToggleButton button = buttons.get(i);
			if(button.isDown() && i != index) {
				button.setValue(false);
			}
			if(i == index) {
				button.setValue(true);
			}
		}

		selectedIndex = index;
	}

	/**
	 * 
	 * @return the selected button in the toolbar
	 */
	public ButtonFormat getSelectedButtonFormat() {
		return buttons.get(selectedIndex).type;
	}

	/**
	 * 
	 */
	public void updateStats() {
		Map<Integer, Integer> stats = EMarkingWeb.markingInterface.getMarkingPagesInterface()
				.getMarkStatistics();

		for(int format : stats.keySet()) {
			int value = stats.get(format);

			if(buttonsStats.get(format) == null) {
				logger.severe("Format " + format + " could not be found");
				continue;
			}
			buttonsStats.get(format).setText(Integer.toString(value));

			if(value > 0) {
				buttonsStats.get(format).setVisible(true);
			} else {
				buttonsStats.get(format).setVisible(false);
			}
		}



	}

	public void loadSubmissionData() {

		loadButtonsFromConfiguration();
		
		criterionList.loadSubmissionData();
		criterionList.setVisible(EMarkingConfiguration.isColoredRubric());
		changeColorButtons();

		this.loadCustomMarksButtons(MarkingInterface.submissionData.getCustommarks());

		markingButtonsPanel.add(criterionList);
		markingButtonsPanel.setCellHorizontalAlignment(criterionList, HasHorizontalAlignment.ALIGN_LEFT);

		if(EMarkingConfiguration.getMarkingType() == EMarkingConfiguration.EMARKING_TYPE_PRINT_SCAN
				&& buttons.size() > 0) {
			buttons.get(0).setVisible(false);
		}
	}
	
	private void loadButtonsFromConfiguration() {
		// Initialize the array
		buttons = new ArrayList<EmarkingToggleButton>();
		buttonsStats = new HashMap<Integer, Label>();
		// Creates all buttons and adds a general value change handler
		// First creates all buttonstats as they have to be referenced by buttons
		for(int format : availableButtons.keySet()) {
			EmarkingToggleButton button = availableButtons.get(format);
			if(EMarkingConfiguration.getMarkingButtonsEnabled().contains(format)) {
				Label lblstat = new Label();
				lblstat.addStyleName(Resources.INSTANCE.css().rubricbuttonjewel());
				buttonsStats.put(format, lblstat);			
				addToggleButton(button);
			}
		}

		if(EMarkingConfiguration.getMarkingType() == EMarkingConfiguration.EMARKING_TYPE_PRINT_SCAN) {
			selectedIndex = 1;
			buttons.get(0).setEnabled(false);
		}
		if(buttons.size() > selectedIndex) {
			buttons.get(selectedIndex).setValue(true);
			buttons.get(selectedIndex).setDown(true);
		}		
	}

	public void changeColorButtons() {
		Criterion crit = EMarkingWeb.markingInterface.getToolbar().getMarkingButtons().getSelectedCriterion();
		int c = crit == null ? 0 : crit.getId();

		for (int i = 0; i < buttons.size(); i++) {
			if(c > 0) {
				Color.setWidgetFontHueColor(c, buttons.get(i));
			} else {
				buttons.get(i).getElement().removeAttribute("style");
			}
		}
		// EMarkingWeb.markingInterface.getToolbar().getMarkingButtons().updateStats();
	}

	private void loadCustomMarksButtons(String customMarks) {
		if(customMarks == null 
				|| customMarks.trim().length() == 0)
			return;

		String[] lines = customMarks.replaceAll("\r\n", "\n").split("\n");
		String customButtons = "";
		String customButtonsTitles = "";
		for(int i=0;i<lines.length;i++) {
			String[] lineparts = lines[i].split("#");
			if(lineparts.length != 2)
				continue;
			customButtons += lineparts[0] + ",";
			customButtonsTitles += lineparts[1] + ",";
		}

		customButtonIndex = new HashMap<String, Integer>();

		String[] partsButtonLabels = customButtons.split(",");
		String[] partsButtonTitles = customButtonsTitles.split(",");

		if(partsButtonLabels.length != partsButtonTitles.length) {
			logger.severe("Invalid parameters loading custom buttons");
			return;
		}

		for(int j=0;j<partsButtonLabels.length;j++) {
			if(partsButtonLabels[j].trim().length()>0) {

				int currentButtonIndex = buttons.size() + 1;

				Label lblstat = buttonsStats.get(customButtonIndex);
				if(lblstat == null) {
					lblstat = new Label();
					lblstat.addStyleName(Resources.INSTANCE.css().rubricbuttonjewel());
					buttonsStats.put(currentButtonIndex, lblstat);
				}

				EmarkingToggleButton btn = new EmarkingToggleButton(1000, ButtonFormat.BUTTON_CUSTOM, partsButtonLabels[j], partsButtonTitles[j]);
				addToggleButton(btn);
				customButtonIndex.put(partsButtonLabels[j]+": "+partsButtonTitles[j], currentButtonIndex);
			}
		}
	}

	/**
	 * @return the customButtonIndex
	 */
	public Map<String, Integer> getCustomButtonIndex() {
		return customButtonIndex;
	}

	/**
	 * Adds a toggle button to the toolbar
	 * 
	 * @param label
	 * @param title
	 * @param cssStyle
	 * @param buttonIndex
	 */
	private void addToggleButton(EmarkingToggleButton button) {
		button.addValueChangeHandler(handler);
		buttons.add(button);

		Label lblstat = buttonsStats.get(button.format);

		AbsolutePanel vpanel = new AbsolutePanel();
		vpanel.add(button);
		vpanel.add(lblstat, 23, 0);
		markingButtonsPanel.add(vpanel);	
	}

	public String getSelectedButtonLabel() {
		ToggleButton btn = this.buttons.get(selectedIndex);
		return btn.getHTML();
	}

	public String getSelectedButtonTitle() {
		ToggleButton btn = this.buttons.get(selectedIndex);
		return btn.getTitle();
	}

	public void setButtonPressed(int index, boolean fromEvent) {
		pushButton(index, fromEvent);
	}

	public void changeColor(int id) {
		if(!EMarkingConfiguration.isColoredRubric()) {
			return;
		}

		for (int i = 0; i < criterionList.getItemCount(); i++) {
			if(Integer.parseInt(criterionList.getValue(i)) == id){
				criterionList.setSelectedIndex(i);
			}
		}		
	}
}


