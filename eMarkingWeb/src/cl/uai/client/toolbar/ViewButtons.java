/**
 * 
 */
package cl.uai.client.toolbar;

import java.util.logging.Logger;

import cl.uai.client.EMarkingConfiguration;
import cl.uai.client.EMarkingWeb;
import cl.uai.client.MarkingInterface;
import cl.uai.client.resources.Resources;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PushButton;

/**
 * @author Jorge Villalón
 *
 */
public class ViewButtons extends Composite {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(ViewButtons.class.getName());

	private HorizontalPanel mainPanel = null;

	private PushButton showRubricButton = null;
	private PushButton showColorsButton = null;

	public ViewButtons() {
		this.mainPanel = new HorizontalPanel();

		Icon finishIcon = new Icon(IconType.EYE_OPEN);
		showRubricButton = new PushButton();
		showRubricButton.setHTML(finishIcon.toString());
		showRubricButton.setTitle(MarkingInterface.messages.FinishMarking());
		showRubricButton.addStyleName(Resources.INSTANCE.css().finishmarkingbutton());
		showRubricButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				EMarkingWeb.markingInterface.getRubricInterface().setVisible(
						!EMarkingWeb.markingInterface.getRubricInterface().isVisible());
			}
		});

		Icon showColorsIcon = new Icon(IconType.TINT);
		showColorsButton = new PushButton();
		showColorsButton.setHTML(showColorsIcon.toString());
		showColorsButton.setTitle(MarkingInterface.messages.ShowColors());
		showColorsButton.addStyleName(Resources.INSTANCE.css().finishmarkingbutton());
		showColorsButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				EMarkingWeb.markingInterface.setColoredRubric(
						!EMarkingConfiguration.isColoredRubric());
			}
		});
		
		this.mainPanel.add(showRubricButton);
		this.mainPanel.add(showColorsButton);

		initWidget(this.mainPanel);
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		
		this.mainPanel.setWidth("0px");
	}
	
	public void setRubricVisibility(boolean visible) {
		if(visible) {
			Icon finishIcon = new Icon(IconType.EYE_CLOSE);
			showRubricButton.setHTML(finishIcon.toString());
			showRubricButton.setTitle(MarkingInterface.messages.HideRubric());
		} else {
			Icon finishIcon = new Icon(IconType.EYE_OPEN);
			showRubricButton.setHTML(finishIcon.toString());
			showRubricButton.setTitle(MarkingInterface.messages.ShowRubric());
		}
	}

	public void loadSubmissionData() {
		// TODO Auto-generated method stub
		
	}
}
