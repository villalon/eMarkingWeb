/**
 * 
 */
package cl.uai.client.toolbar.buttons;

import java.util.logging.Logger;

import cl.uai.client.EMarkingConfiguration;
import cl.uai.client.EMarkingWeb;
import cl.uai.client.MarkingInterface;
import cl.uai.client.data.Submission;
import cl.uai.client.data.SubmissionGradeData;
import cl.uai.client.marks.Mark;

import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

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
	private MenuBar openAnswerKey = null;

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
		if(!EMarkingConfiguration.isColoredRubricForced()) {
			this.mainPanel.add(showColorsButton);
		}
		// this.mainPanel.add(minimizeAllRubricMarks);
	}

	@Override
	public void loadSubmissionData() {
		SubmissionGradeData sdata = MarkingInterface.submissionData;
		if(EMarkingConfiguration.getMarkingType() == EMarkingConfiguration.EMARKING_TYPE_PRINT_SCAN) {
			showColorsButton.setVisible(false);
		}
		if(EMarkingConfiguration.isReadonly() || EMarkingConfiguration.isColoredRubricForced()){
			mainPanel.remove(showColorsButton);
			mainPanel.remove(minimizeAllRubricMarks);
		}
		if(sdata.getAnswerKeys().size() == 0) {
			openAnswerKey = new MenuBar(true);
			for(int key : sdata.getAnswerKeys().keySet()) {
				final Submission s = sdata.getAnswerKeys().get(key);
				Command cmd = new Command() {
					@Override
					public void execute() {
						Window.open(EMarkingConfiguration.getMoodleUrl() + "/view.php?id=" + s.getId(), "", null);
					}
				};
				openAnswerKey.addItem(new MenuItem(MarkingInterface.messages.AnswerKey(), cmd));
			}
			mainPanel.add(openAnswerKey);
		}
	}
}
