/**
 * 
 */
package cl.uai.client.toolbar;

import cl.uai.client.MarkingInterface;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Jorge
 *
 */
public class TutorialDialogBox extends DialogBox {

	private VerticalPanel mainPanel = null;
	private HTML html = null;
	private Button closeButton = null;
	private ListBox tutorialsListBox = null;
	
	private String[] links = {
		MarkingInterface.messages.HowToWhatIsEMarking(),	
		MarkingInterface.messages.HowToPrintAnExam(),	
		MarkingInterface.messages.HowToCreateRubric(),	
		MarkingInterface.messages.HowToAnonymousMarking(),	
		MarkingInterface.messages.HowToMarkingReports(),	
	};
	
	private String[] youtubevideos = {
			"_LfywvezuVQ",
			"HqA-3jMJoyI",
			"yHUkeo8-YZM",
			"mRLhOQ2SGis",
			"aOgdt3eXlSM"
	};
	
	public TutorialDialogBox() {
		this.setModal(true);
		this.setGlassEnabled(true);
		this.setAutoHideEnabled(true);
		
		this.mainPanel = new VerticalPanel();

		this.tutorialsListBox = new ListBox();
		int index = 0;
		for(String link : links) {
			this.tutorialsListBox.insertItem(link, index);
			index++;
		}
		this.tutorialsListBox.addChangeHandler(new ChangeHandler() {			
			@Override
			public void onChange(ChangeEvent event) {
				ListBox lb = (ListBox) event.getSource();
				html.setHTML(getYouTubeEmbed(lb.getSelectedIndex()));
			}
		});

		this.html = new HTML(getYouTubeEmbed(0));
		this.mainPanel.add(this.tutorialsListBox);
		this.mainPanel.add(this.html);
	
		this.closeButton = new Button(MarkingInterface.messages.Close());
		this.closeButton.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		HorizontalPanel hpanel = new HorizontalPanel();
		hpanel.add(closeButton);
		
		this.mainPanel.add(hpanel);
		this.mainPanel.setCellHorizontalAlignment(hpanel, HasAlignment.ALIGN_RIGHT);
		
		this.setHTML(MarkingInterface.messages.Help());
		this.setWidget(this.mainPanel);
	}
	
	private String getYouTubeEmbed(int index) {
		String html = "<iframe width=\"560\" height=\"315\" src=\"https://www.youtube.com/embed/";
		html += youtubevideos[index];
		html += "\" frameborder=\"0\" allowfullscreen></iframe>";
		return html;
	}
}
