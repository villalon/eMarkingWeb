/**
 * 
 */
package cl.uai.client.toolbar;

import java.util.logging.Logger;

import cl.uai.client.MarkingInterface;
import cl.uai.client.resources.Resources;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PushButton;

/**
 * @author Jorge Villalón
 *
 */
public class HelpButtons extends Composite {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(HelpButtons.class.getName());
	
	private HorizontalPanel mainPanel = null;
	
	private PushButton aboutButton = null;
	private PushButton tutorialsButton = null;

	public HelpButtons() {
		this.mainPanel = new HorizontalPanel();
		this.mainPanel.addStyleName(Resources.INSTANCE.css().buttonshpanel());
		
		Icon infoIcon = new Icon(IconType.INFO);
		aboutButton = new PushButton();
		aboutButton.setHTML(infoIcon.toString());
		aboutButton.setTitle(MarkingInterface.messages.ShowChat());
		aboutButton.addStyleName(Resources.INSTANCE.css().rubricbutton());
		aboutButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				DialogBox dbox = new DialogBox();
				dbox.setModal(true);
				dbox.setGlassEnabled(true);
				dbox.setAutoHideEnabled(true);
				dbox.setWidth("300px");
				dbox.setHTML(MarkingInterface.messages.AboutEmarking());
				dbox.setWidget(new HTML(MarkingInterface.messages.AboutEmarkingDetail()));
				dbox.center();
			}
		});
		
		Icon tutorialsIcon = new Icon(IconType.BOOK);
		tutorialsButton = new PushButton();
		tutorialsButton.setHTML(tutorialsIcon.toString());
		tutorialsButton.setTitle(MarkingInterface.messages.Tutorials());
		tutorialsButton.addStyleName(Resources.INSTANCE.css().rubricbutton());
		tutorialsButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				TutorialDialogBox dbox = new TutorialDialogBox();
				dbox.center();
			}
		});

		this.mainPanel.add(aboutButton);
		this.mainPanel.add(tutorialsButton);
		
		initWidget(this.mainPanel);
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		
		this.setWidth("0px");
	}
	
	public void loadSubmissionData() {		
	}
}
