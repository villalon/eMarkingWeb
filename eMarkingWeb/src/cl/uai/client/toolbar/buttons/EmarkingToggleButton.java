package cl.uai.client.toolbar.buttons;

import com.github.gwtbootstrap.client.ui.Icon;
import com.google.gwt.user.client.ui.ToggleButton;

import cl.uai.client.resources.Resources;

public class EmarkingToggleButton extends ToggleButton {
	private int format;
	private ButtonFormat type;
	private Icon icon;
	public EmarkingToggleButton(int _format, ButtonFormat _type, Icon _icon, String title) {
		this(_format, _type, _icon.toString(), title);
	}
	public Icon getIcon() {
		return icon;
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
