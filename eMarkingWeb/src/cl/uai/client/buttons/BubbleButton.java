package cl.uai.client.buttons;

import cl.uai.client.resources.Resources;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.user.client.ui.HTML;

public abstract class BubbleButton extends HTML {

	protected Icon icon = null;
	
	public BubbleButton(IconType _type) {
		super();

		this.addStyleName(Resources.INSTANCE.css().showrubricbutton());

		this.icon = new Icon(_type);
		this.setHTML(icon.toString());
		
		this.setVisible(false);
	}
}
