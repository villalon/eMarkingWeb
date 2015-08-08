package cl.uai.client.buttons;

import cl.uai.client.resources.Resources;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;

public abstract class BubbleButton extends HTML {

	protected Icon icon = null;
	protected int left = Window.getClientWidth()-40;
	protected int top = 0;
	
	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public BubbleButton(IconType _type, int _left, int _top) {
		super();

		this.addStyleName(Resources.INSTANCE.css().showrubricbutton());

		this.icon = new Icon(_type);
		this.setHTML(icon.toString());
		
		this.left = _left;
		this.top = _top;
		
		this.setVisible(false);
		
		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onButtonClick(event);
			}
		});
	}
	
	public void updatePosition(AbsolutePanel panel) {
		panel.setWidgetPosition(this, left, top);
	}
	
	protected abstract void onButtonClick(ClickEvent event);
}
