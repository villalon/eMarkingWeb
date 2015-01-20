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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import cl.uai.client.EMarkingComposite;
import cl.uai.client.EMarkingWeb;
import cl.uai.client.MarkingInterface;
import cl.uai.client.data.Criterion;
import cl.uai.client.data.Level;
import cl.uai.client.marks.Mark;
import cl.uai.client.marks.RubricMark;
import cl.uai.client.page.AddMarkDialog;
import cl.uai.client.resources.Resources;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author Jorge Villalón <villalon@gmail.com>
 *
 */
public class RubricPanel extends EMarkingComposite {

	Logger logger = Logger.getLogger(RubricPanel.class.getName());

	/** Contains the rubric table **/
	private VerticalPanel mainPanel = null;
	private VerticalPanel rubricTable = null;

	/** The rubric title **/
	private Label rubricTitle = null;

	private Map<Integer, HorizontalPanel> rubricRows = null;
	private Map<Integer, Integer> rubricIndices = null;

	/** Buttons for toolbar **/
	private ListBox rubricFilter = null;

	/** Scroll panel for rubric table **/
	private ScrollPanel scrollPanel = null;

	/** If the rubric must include headers for each criterion **/
	private boolean popupInterface = false;


	/**
	 * @return the includeCriterionHeaders
	 */
	public boolean isPopupInterface() {
		return popupInterface;
	}

	/**
	 * @param popupInterface the includeCriterionHeaders to set
	 */
	public void setPopupInterface(boolean popupInterface) {
		this.popupInterface = popupInterface;
	}

	/**
	 * Gets the index of a specific criterion in the rubric interface
	 * 
	 * @param criterionId
	 * @return the criterion index
	 */
	public int getCriterionIndex(int criterionId) {
		if(rubricIndices.containsKey(criterionId)) {
			return rubricIndices.get(criterionId);
		} else {
			return 0;
		}
	}

	/**
	 * Creates the rubric panel
	 */
	public RubricPanel() {
		mainPanel = new VerticalPanel();
		mainPanel.addStyleName(Resources.INSTANCE.css().rubricpanel());
		//TODO: Cambio redimension
		mainPanel.setWidth((Window.getClientWidth()*0.35)	+"px");

		// Adds the title
		rubricTitle = new Label();
		rubricTitle.addStyleName(Resources.INSTANCE.css().rubrictitle());

		// Adds the checkbox
		rubricFilter = new ListBox(false);
		rubricFilter.addItem(MarkingInterface.messages.ShowRubric(), "all");
		rubricFilter.addItem(MarkingInterface.messages.ShowMarkingPending(), "unmarked");
		rubricFilter.addItem(MarkingInterface.messages.ShowRegradePending(), "regrade");
		rubricFilter.addItem(MarkingInterface.messages.HideRubric(), "hide");
		rubricFilter.addStyleName(Resources.INSTANCE.css().rubricfilterselect());
		rubricFilter.setSelectedIndex(0);
		rubricFilter.addChangeHandler(new RubricFilterListBoxValueChangeHandler());

		// An horizontal panel holds title and checkbox
		HorizontalPanel hpanelTitle = new HorizontalPanel();
		hpanelTitle.addStyleName(Resources.INSTANCE.css().rubrictitlepanel());
		hpanelTitle.add(rubricTitle);
		hpanelTitle.setCellHorizontalAlignment(rubricTitle, HasHorizontalAlignment.ALIGN_LEFT);
		hpanelTitle.add(rubricFilter);
		hpanelTitle.setCellHorizontalAlignment(rubricFilter, HasHorizontalAlignment.ALIGN_RIGHT);
		hpanelTitle.setCellVerticalAlignment(rubricFilter, HasVerticalAlignment.ALIGN_MIDDLE);
		mainPanel.add(hpanelTitle);

		// Adds the scroll panel containing the rubric table
		scrollPanel = new ScrollPanel();
		rubricTable = new VerticalPanel();
		rubricTable.addStyleName(Resources.INSTANCE.css().rubrictable());
		scrollPanel.add(rubricTable);
		mainPanel.add(scrollPanel);

		initWidget(mainPanel);
	}
	/**
	 * Adds a mark to the rubric
	 * 
	 * @param mark
	 * @param newgrade
	 * @param timemodified
	 */
	public void addMarkToRubric(final RubricMark mark) {
		LevelLabel lblLabel = deselectMarkFromRubric(mark);

		if(lblLabel == null) {
			logger.severe(MarkingInterface.messages.ErrorInvalidLevelId());
		} else {
			if(mark.getRegradeid() > 0) {
				lblLabel.setRegradeMotive(mark.getRegrademotive());
				lblLabel.setRegradeComment(mark.getRegradecomment());
				lblLabel.setRegradeRequested(true);
			}
			lblLabel.updateHtml();
			lblLabel.addStyleName(Resources.INSTANCE.css().rubricLevelSelected());
			HorizontalPanel hpanel = rubricRows.get(mark.getCriterionId());
			if(hpanel != null) {
				hpanel.removeStyleName(Resources.INSTANCE.css().rowCriterionNotSelected());
				Criterion criterion = MarkingInterface.submissionData.getRubricfillings().get(mark.getCriterionId());
				hpanel.addStyleName(getCriterionVisibilityCss(criterion));
				CriterionHeader cheader = (CriterionHeader) hpanel.getWidget(0);
				cheader.setBackGroundPercent(criterion.getHue());
				cheader.setCommentId(mark.getId());
				cheader.setBonus(criterion.getBonus());
				cheader.setCommentPage(mark.getPageno());
				if(mark.getRegradeid() > 0) {
					cheader.setRegradeData(mark.getRegradeid(), mark.getRegradeaccepted());
				}
				cheader.setMarkerVisible(!popupInterface);
				cheader.setRegradeData(mark.getRegradeid(), mark.getRegradeaccepted());
			}
		}
		
		EMarkingWeb.markingInterface.getRubricInterface().getToolsPanel().loadSumissionData();
	}

	@Override
	protected void onLoad() {
		super.onLoad();

		if(MarkingInterface.submissionData == null || MarkingInterface.submissionData.getRubricfillings() == null) {
			logger.severe("Submission data should not be null when loading the rubric interface!");
			return;
		}

		//TODO
		// Once loaded, we know all sizes
		scrollPanel.setWidth("100%");
		int height = Window.getClientHeight() - scrollPanel.getAbsoluteTop();
		if(popupInterface)
			height = (Window.getClientHeight() * 35) / 100;
		else if(!MarkingInterface.readonly)
			height = (Window.getClientHeight() * 35) / 100;
		else
			height = (Window.getClientHeight() * 35) / 100;
		scrollPanel.setHeight(height+"px");
		scrollPanel.setStyleName("rubricscroll");
		scrollPanel.getElement().getStyle().setProperty("MaxHeight", height+"px");

		rubricTable.clear();

		rubricRows = new HashMap<Integer, HorizontalPanel>();
		rubricIndices = new HashMap<Integer, Integer>();
		rubricTitle.setText(MarkingInterface.submissionData.getRubricname());

		int rownumber = 0;
		int index = 0;
		for(int criterionId : MarkingInterface.submissionData.getRubricfillings().keySet()) {
			Criterion criterion = MarkingInterface.submissionData.getRubricfillings().get(criterionId);
			index++;
			HorizontalPanel rowPanel = new HorizontalPanel();
			rowPanel.addStyleName(Resources.INSTANCE.css().rubricrow());

			CriterionHeader header = new CriterionHeader(
					index, 
					criterionId, 
					criterion.getDescription(), 
					criterion.getBonus(),
					criterion.getRegradeid(),
					criterion.getRegradeaccepted());
			
			rowPanel.add(header);

			rubricIndices.put(criterionId, index);
			boolean criterionSelected = false;
			for(int levelid : criterion.getLevels().keySet()) {
				Level level = criterion.getLevels().get(levelid);
				LevelLabel levelLabel = new LevelLabel(level.getId());
				levelLabel.addStyleName(Resources.INSTANCE.css().criterionDescription());

				if(criterion.getSelectedLevel() != null && criterion.getSelectedLevel().getId() == level.getId()) {
					levelLabel.addStyleName(Resources.INSTANCE.css().rubricLevelSelected());
					criterionSelected = true;

					header.setCommentId(level.getMarkId());
					header.setCommentPage(level.getPage());
					header.setBackGroundPercent(criterion.getHue());
					
					if(criterion.getRegradeid() > 0) {
						levelLabel.setRegradeRequested(true);
						levelLabel.setRegradeComment(criterion.getRegradeComment());
						levelLabel.updateHtml();
					}
				}

				if(!popupInterface) {
					if(!MarkingInterface.readonly) {
						EMarkingWeb.markingInterface.dragController.makeDraggable(levelLabel);
					}
				} else if(this.getParent().getParent().getParent() instanceof AddMarkDialog) {
					final AddMarkDialog dialog = (AddMarkDialog) this.getParent().getParent().getParent();
					final int lvlid = level.getId();
					if(criterion.getSelectedLevel() == null || criterion.getSelectedLevel().getId() != level.getId()) {
						levelLabel.addStyleName(Resources.INSTANCE.css().rubriclabelclickable());
						levelLabel.addClickHandler(new ClickHandler() {						
							@Override
							public void onClick(ClickEvent event) {
								dialog.setLevelId(lvlid);
								dialog.hide();
							}
						});
					}
				} else {
					logger.severe("Problem adding cick handler");
					logger.severe(this.getParent().getClass().getName());
					logger.severe(this.getParent().getParent().getClass().getName());
					logger.severe(this.getParent().getParent().getParent().getClass().getName());
				}
				rowPanel.add(levelLabel);

			}

			rownumber++;
			if(criterionSelected) {
				header.setMarkerVisible(!popupInterface);
				rowPanel.addStyleName(getCriterionVisibilityCss(criterion));
				if(isCriterionVisible(criterion))
					rownumber--;
			} else {
				header.setMarkerVisible(false);
				rowPanel.addStyleName(getCriterionVisibilityCss(criterion));						
			}
			if(rownumber %  2 == 0) {
				rowPanel.addStyleName(Resources.INSTANCE.css().oddrow());						
			}

			rubricRows.put(criterionId, rowPanel);
			
			rubricTable.add(rowPanel);
		}

	}

	private String getCriterionVisibilityCss(Criterion criterion) {
		if(!isCriterionVisible(criterion)) {
			return Resources.INSTANCE.css().rowCriterionSelectedHidden();
		} else if(criterion.getSelectedLevel() != null) {
			return Resources.INSTANCE.css().rowCriterionSelected();
		} else {
			return Resources.INSTANCE.css().rowCriterionNotSelected();
		}
	}

	private boolean isCriterionVisible(Criterion criterion) {
		String filterValue = rubricFilter.getValue(rubricFilter.getSelectedIndex());
		if(!criterion.isMarkerIsAssigned())
			return false;
		if(filterValue.equals("all")) {
			return true;
		} else if(filterValue.equals("unmarked") && criterion.getSelectedLevel() != null) {
			return false;
		} else if(filterValue.equals("regrade") && criterion.getRegradeid() == 0) {
			return false;
		}
		return true;
	}
	/**
	 * Handles when the checkbox changes its value. Hiding/Showing marked criteria
	 * 
	 * @author Jorge Villalón <villalon@gmail.com>
	 *
	 */
	private class RubricFilterListBoxValueChangeHandler implements ChangeHandler {
		@Override
		public void onChange(ChangeEvent event) {
			ListBox filter = (ListBox) event.getSource();
			String value = filter.getValue(filter.getSelectedIndex());
			
			// Hide rubric when hide is selected
			scrollPanel.setVisible(!value.equals("hide"));
			
			EMarkingWeb.markingInterface.getRubricInterface().resizeToolsPanel();
			
			// We have to update visibility
			int rownumber = 0;
			for(int criterionId : rubricRows.keySet()) {
				Criterion criterion = MarkingInterface.submissionData.getRubricfillings().get(criterionId);
				rownumber++;
				HorizontalPanel hpanel = rubricRows.get(criterionId);
				hpanel.removeStyleName(Resources.INSTANCE.css().oddrow());
				if(rownumber % 2 == 0)
					hpanel.addStyleName(Resources.INSTANCE.css().oddrow());
				hpanel.removeStyleName(Resources.INSTANCE.css().rowCriterionSelectedHidden());
				hpanel.removeStyleName(Resources.INSTANCE.css().rowCriterionSelected());
				hpanel.addStyleName(getCriterionVisibilityCss(criterion));
			}
		}		
	}
	/**
	 * Removes a Mark from the rubric panel
	 * 
	 * @param mark the mark to remove
	 * @param newgrade the new grade
	 * @param timemodified when modified
	 */
	public void deleteMarkFromRubric(RubricMark mark) {
		LevelLabel markLevelLabel = deselectMarkFromRubric(mark);

		// Set final grade in the main interface and change criterion header styles
		if(markLevelLabel == null) {
			Window.alert(MarkingInterface.messages.ErrorInvalidLevelId());
		} else {
			HorizontalPanel hpanel = rubricRows.get(mark.getCriterionId());
			if(hpanel != null) {
				CriterionHeader cheader = (CriterionHeader) hpanel.getWidget(0);
				cheader.setMarkerVisible(false);
				hpanel.addStyleName(Resources.INSTANCE.css().rowCriterionNotSelected());
				Criterion criterion = MarkingInterface.submissionData.getRubricfillings().get(cheader.getCriterionId());
				hpanel.removeStyleName(getCriterionVisibilityCss(criterion));
			}
		}
	}

	private LevelLabel deselectMarkFromRubric(RubricMark mark) {
		// Remove selected CSS styles from all levels in the criterion row
		LevelLabel found = null;
		HorizontalPanel hpanel = rubricRows.get(mark.getCriterionId());
		for(int i=1; i < hpanel.getWidgetCount(); i++) {
			LevelLabel lblLabel = (LevelLabel) hpanel.getWidget(i);
			lblLabel.removeStyleName(Resources.INSTANCE.css().rubricLevelSelected());				
			if(lblLabel.getLevelId() == mark.getLevelId()) {
				found = lblLabel;
			}
		}

		return found;
	}
	
	private LevelLabel getSelectedLevelLabel(int criterionid) {
		// Remove selected CSS styles from all levels in the criterion row
		LevelLabel found = null;
		HorizontalPanel hpanel = rubricRows.get(criterionid);
		for(int i=1; i < hpanel.getWidgetCount(); i++) {
			LevelLabel lblLabel = (LevelLabel) hpanel.getWidget(i);
			Level lvl = MarkingInterface.submissionData.getLevelById(lblLabel.getLevelId());
			Criterion criterion = lvl.getCriterion();
			if(criterion.getSelectedLevel() != null
					&& criterion.getSelectedLevel().getId() == lvl.getId()) {
				found = lblLabel;
			}
		}

		return found;		
	}

	/**
	 * Highlights a criterion within the rubric panel
	 * 
	 * @param criterionid id of the criterion to highlight
	 */
	public void highlightRubricCriterion(int criterionid) {
		// Find the row in the hash map
		HorizontalPanel hpanel = rubricRows.get(criterionid);
		if(hpanel == null)
			return;

		// Remove style in case it already has it
		hpanel.removeStyleName(Resources.INSTANCE.css().rubricmarkhover());
		hpanel.addStyleName(Resources.INSTANCE.css().rubricmarkhover());

		Criterion criterion = MarkingInterface.submissionData.getRubricfillings().get(criterionid);

		// If the marked criteria is shown, scroll to it
		if(isCriterionVisible(criterion)) {
			int top = scrollPanel.getVerticalScrollPosition() + (hpanel.getAbsoluteTop() - scrollPanel.getAbsoluteTop());
			scrollPanel.setVerticalScrollPosition(top);
		}
	}

	/**
	 * Update a criterion row bonus
	 * 
	 * @param criterionid id of the criterion
	 * @param bonus the new bonus value
	 * @param percent the percent for coloring and background
	 */
	public void updateRubricCriterion(int criterionid, float bonus, int percent, int levelid, int regradeid, int regradeaccepted) {
		HorizontalPanel hpanel = rubricRows.get(criterionid);
		if(hpanel == null)
			return;

		LevelLabel lbl = getSelectedLevelLabel(criterionid);
		lbl.updateHtml();
		
		CriterionHeader cheader = (CriterionHeader) hpanel.getWidget(0);
		cheader.setBackGroundPercent(percent);
		cheader.setBonus(bonus);
		cheader.setRegradeData(regradeid, regradeaccepted);
		cheader.setMarkerVisible(!popupInterface);
	}

	/**
	 * De-highlights a criterion row
	 * @param criterionid id of the criterion
	 */
	public void dehighlightRubricCriterion(int criterionid) {
		// Finds the row
		HorizontalPanel hpanel = rubricRows.get(criterionid);
		if(hpanel == null)
			return;

		// Removes the CSS style
		hpanel.removeStyleName(Resources.INSTANCE.css().rubricmarkhover());
	}

	public void loadingRubricCriterion(int levelid) {
		// If we don't have info to identify the criterion return
		if(MarkingInterface.submissionData == null || MarkingInterface.submissionData.getLevelById(levelid) == null) {
			logger.severe("Something's wrong with the level id " + levelid);
			return;
		}

		// The criterion id corresponding to this level
		int criterionid = MarkingInterface.submissionData.getLevelById(levelid).getCriterion().getId();

		// Find the criterion row in the hash map
		HorizontalPanel hpanel = rubricRows.get(criterionid);
		if(hpanel == null)
			return;

		CriterionHeader cheader = (CriterionHeader) hpanel.getWidget(0);
		cheader.setMarkerVisible(false);
		cheader.setLoadingVisible(true);
	}


	public void finishloadingRubricCriterion(int levelid) {
		// If we don't have info to identify the criterion return
		if(MarkingInterface.submissionData == null || MarkingInterface.submissionData.getLevelById(levelid) == null)
			return;

		// The criterion id corresponding to this level
		int criterionid = MarkingInterface.submissionData.getLevelById(levelid).getCriterion().getId();

		// Find the criterion row in the hash map
		HorizontalPanel hpanel = rubricRows.get(criterionid);
		if(hpanel == null)
			return;

		CriterionHeader cheader = (CriterionHeader) hpanel.getWidget(0);
		cheader.setMarkerVisible(true);
		cheader.setLoadingVisible(false);
	}

	public void onMarkAdded(Mark mark, int oldLevelId) {
		// If mark is a RubricMark then update it in the rubric panel
		if(mark instanceof RubricMark) {
			RubricMark rmark = (RubricMark) mark;

			Level newLevel = rmark.getLevel();
			this.finishloadingRubricCriterion(rmark.getLevelId());

			// If the level id changed remove the mark and then add it when updated
			if(oldLevelId != newLevel.getId()) {

				this.deleteMarkFromRubric(rmark);

				this.addMarkToRubric(rmark);
			} else {
				// If not update the bonus
				Criterion criterion = newLevel.getCriterion();
				this.updateRubricCriterion(
						criterion.getId(), 
						newLevel.getBonus(), 
						criterion.getHue(), 
						newLevel.getId(),
						rmark.getRegradeid(),
						rmark.getRegradeaccepted());
			}
		}

	}

}
