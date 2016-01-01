/**
 * 
 */
package cl.uai.client.toolbar.buttons;

import java.util.logging.Logger;

import cl.uai.client.EMarkingConfiguration;
import cl.uai.client.MarkingInterface;
import cl.uai.client.toolbar.TutorialDialogBox;

import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;

/**
 * @author Jorge Villalón
 *
 */
public class HelpButtons extends Buttons {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(HelpButtons.class.getName());
	
	private PushButton aboutButton = null;
	private PushButton tutorialsButton = null;

	public HelpButtons() {
		
		aboutButton = new PushButton(IconType.INFO_SIGN, MarkingInterface.messages.AboutEmarking());
		aboutButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				DialogBox dbox = new DialogBox();
				dbox.setModal(true);
				dbox.setGlassEnabled(true);
				dbox.setAutoHideEnabled(true);
				dbox.setWidth("300px");
				dbox.setHTML(MarkingInterface.messages.AboutEmarking());
				dbox.setWidget(new HTML(MarkingInterface.messages.AboutEmarkingDetail(""+EMarkingConfiguration.geteMarkingVersion())));
				dbox.center();
			}
		});
		
		tutorialsButton = new PushButton(IconType.BOOK, MarkingInterface.messages.Tutorials());
		tutorialsButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				TutorialDialogBox dbox = new TutorialDialogBox();
				dbox.center();
			}
		});

		this.mainPanel.add(aboutButton);
		this.mainPanel.add(tutorialsButton);
	}
	
	@Override
	public void loadSubmissionData() {		
	}
}
