/**
 * 
 */
package cl.uai.client.toolbar.buttons;

import cl.uai.client.resources.Resources;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * @author Jorge Villalon
 *
 */
public abstract class Buttons extends Composite {

	protected HorizontalPanel mainPanel = null;
	
	public Buttons() {
		this.mainPanel = new HorizontalPanel();
		this.mainPanel.addStyleName(Resources.INSTANCE.css().buttonshpanel());

		initWidget(this.mainPanel);
	}

	@Override
	protected void onLoad() {
		super.onLoad();
	}
	
	public abstract void loadSubmissionData();
}
