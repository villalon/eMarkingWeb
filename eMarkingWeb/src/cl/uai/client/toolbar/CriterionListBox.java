/**
 * 
 */
package cl.uai.client.toolbar;

import java.util.logging.Logger;

import cl.uai.client.EMarkingWeb;
import cl.uai.client.MarkingInterface;
import cl.uai.client.data.Criterion;
import cl.uai.client.resources.Resources;
import cl.uai.client.utils.Color;

import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;

/**
 * @author Jorge Villalón
 *
 */
public class CriterionListBox extends ListBox {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(CriterionListBox.class.getName());
	
	public CriterionListBox() {
		this.addStyleName(Resources.INSTANCE.css().criterionlistbox());		
	}
	
	@Override
	public void setSelectedIndex(int index) {
		super.setSelectedIndex(index);
		changeColors();
	}

	public void loadSubmissionData() {
		this.clear();
		this.addItem(MarkingInterface.messages.NoCriterion(),"0");

		for(int criterionId : MarkingInterface.submissionData.getRubricfillings().keySet()) {
			Criterion c = MarkingInterface.submissionData.getRubricfillings().get(criterionId);
			this.addItem(c.getDescription(), Integer.toString(criterionId));
		}
		
		//assign color
		SelectElement selectElement = SelectElement.as(this.getElement());
		com.google.gwt.dom.client.NodeList<OptionElement> options = selectElement.getOptions();
		
		for (int i = 1; i < options.getLength(); i++) {
			int cid = Integer.parseInt(this.getValue(i));
			 options.getItem(i).setAttribute("style", "color:" + Color.getCSSHueColor(cid));;
		}
		
		this.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				changeColors();
			}
		});	
	}
	
	private void changeColors() {
		Criterion crit = EMarkingWeb.markingInterface.getToolbar().getMarkingButtons().getSelectedCriterion();
		int color = crit == null ? 0 : crit.getId();
		EMarkingWeb.markingInterface.getToolbar().getMarkingButtons().changeColorButtons();
		Color.setWidgetFontHueColor(color, this);		
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
	}
}
