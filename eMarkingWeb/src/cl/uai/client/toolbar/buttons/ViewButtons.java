/**
 * 
 */
package cl.uai.client.toolbar.buttons;

import java.util.logging.Logger;

import cl.uai.client.EMarkingConfiguration;
import cl.uai.client.EMarkingWeb;
import cl.uai.client.MarkingInterface;
import cl.uai.client.marks.Mark;

import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * @author Jorge Villalón
 *
 */
public class ViewButtons extends Buttons {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(ViewButtons.class.getName());

	private PushButton showRubricButton = null;
	private PushButton showColorsButton = null;
	private PushButton minimizeAllRubricMarks = null;

	public ViewButtons() {
		showRubricButton = new PushButton(IconType.TH, MarkingInterface.messages.ShowRubric());
		showRubricButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				EMarkingWeb.markingInterface.getRubricInterface().setVisible(
						!EMarkingWeb.markingInterface.getRubricInterface().isVisible());
			}
		});

		showColorsButton = new PushButton(IconType.TINT, MarkingInterface.messages.ShowColors());
		showColorsButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				EMarkingWeb.markingInterface.setColoredRubric(
						!EMarkingConfiguration.isColoredRubric());
				EMarkingWeb.markingInterface.getToolbar().setSelectedTab(1);
			}
		});
		
		minimizeAllRubricMarks = new PushButton(IconType.MINUS, MarkingInterface.messages.MinimizeAllRubricMarks());
		minimizeAllRubricMarks.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				EMarkingWeb.markingInterface.getMarkingPagesInterface().minimizeAllRubricMarks();
				Mark.hideIcons();
			}
		});
		
		this.mainPanel.add(showRubricButton);
		this.mainPanel.add(showColorsButton);
		this.mainPanel.add(minimizeAllRubricMarks);
	}

	@Override
	public void loadSubmissionData() {
		if(EMarkingConfiguration.getMarkingType() == 5) {
			showColorsButton.setVisible(false);
		}
	}
}
