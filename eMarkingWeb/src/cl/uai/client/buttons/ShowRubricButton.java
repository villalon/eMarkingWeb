package cl.uai.client.buttons;

import cl.uai.client.EMarkingWeb;

import java.util.Date;
import java.util.logging.Logger;

import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class ShowRubricButton extends BubbleButton {

	private static Logger logger = Logger.getLogger(ShowRubricButton.class.getName());
	public ShowRubricButton(int _left, int _top, int _source) {
		super(IconType.TH, _left, _top, _source);
	}
	
	@Override
	public void onButtonClick(ClickEvent event) {
		boolean visible = true;
		if(EMarkingWeb.markingInterface.getRubricInterface().isVisible()){
			EMarkingWeb.markingInterface.getRubricInterface().setVisible(false);
			setVisible(true);
		}else{
			EMarkingWeb.markingInterface.getRubricInterface().setVisible(true);
			setVisible(false);
			visible = false;
		}
		Date oneyear = new Date();
		CalendarUtil.addMonthsToDate(oneyear, 12);
		
		String value = visible ? "1" : "0";
		logger.info("Setting rubric visibility to: " + value);
		Cookies.setCookie("emarking_showrubric", value, oneyear);
	}
}
