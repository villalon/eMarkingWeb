package cl.uai.client.buttons;

import cl.uai.client.EMarkingWeb;

import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;

public class ShowRubricButton extends BubbleButton {

	public ShowRubricButton(int _left, int _top, int _source) {
		super(IconType.TH, _left, _top, _source);
	}
	
	@Override
	public void onButtonClick(ClickEvent event) {
		if(EMarkingWeb.markingInterface.getRubricInterface().isVisible()){
			EMarkingWeb.markingInterface.getRubricInterface().setVisible(false);
			setVisible(true);
		}else{
			EMarkingWeb.markingInterface.getRubricInterface().setVisible(true);
			setVisible(false);
		}
	}
}
