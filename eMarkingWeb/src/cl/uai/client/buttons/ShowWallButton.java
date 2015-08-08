package cl.uai.client.buttons;

import cl.uai.client.EMarkingWeb;

import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;

public class ShowWallButton extends BubbleButton {

	public ShowWallButton(int _left, int _top) {
		super(IconType.INBOX, _left, _top);
	}
	
	@Override
	public void onButtonClick(ClickEvent event) {
		EMarkingWeb.markingInterface.wall.show();
	}
}
