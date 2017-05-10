package cl.uai.client.feedback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cl.uai.client.data.AjaxData;
import cl.uai.client.data.AjaxRequest;
import cl.uai.client.page.EditMarkDialog;
import cl.uai.client.resources.Resources;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FeedbackInterface extends VerticalPanel {
	
	/** Tab panel for resources MIT, Merlot, CS50 and Moodle resources **/
	private TabPanel resourcesPanel;
	private HorizontalPanel keywordsPanel;
	
	/** Panels for each search **/
	private VerticalPanel ocwMITPanel;
	private VerticalPanel merlotPanel;
	private VerticalPanel webcursosPanel;
	
	private EditMarkDialog parent;
	
	private String[] keywords;

	public FeedbackInterface(String keyword, final ArrayList<FeedbackObject> feedbackMoodleArray){
		
		if(keyword != null){
	
		keywordsPanel = new HorizontalPanel();
		keywordsPanel.addStyleName(Resources.INSTANCE.css().keywordspanel());
		
		// Tabs for each resource
		resourcesPanel = new TabPanel();
		resourcesPanel.addStyleName(Resources.INSTANCE.css().resorucespanel());	
		
		// Initialize all tabs
		ocwMITPanel = new VerticalPanel();
		ocwMITPanel.add(new HTML("Seleccione una palabra para realizar la busqueda."));
		
		merlotPanel = new VerticalPanel();
		merlotPanel.add(new HTML("Seleccione una palabra para realizar la busqueda."));
		
		webcursosPanel = new VerticalPanel();
		webcursosPanel.add(new HTML("Seleccione una palabra para realizar la busqueda."));
		
		HTML keywordText = new HTML("<h4><b>Keywords</b></h4>");
		keywordsPanel.add(keywordText);
		keywordsPanel.setCellVerticalAlignment(keywordText, HasVerticalAlignment.ALIGN_MIDDLE);
		
		// Create the keyword buttons
		keywords = keyword.split(",");
		int counter = 0;
		while (counter < this.keywords.length){		
			final int key = counter;
			Button btnKeyword = new Button(keywords[counter]);
			btnKeyword.addStyleName(Resources.INSTANCE.css().btnkeyword());
			btnKeyword.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					getResourcesByOER(keywords[key], "ocwmit", ocwMITPanel);
					getResourcesByOER(keywords[key], "merlot", merlotPanel);
				}
			});
			keywordsPanel.add(btnKeyword);
			keywordsPanel.setCellVerticalAlignment(btnKeyword, HasVerticalAlignment.ALIGN_TOP);
			counter++;
		}
		
		resourcesPanel.add(webcursosPanel, "Webcursos");
		resourcesPanel.add(ocwMITPanel, "OCW MIT");
		resourcesPanel.add(merlotPanel, "Merlot");
		resourcesPanel.selectTab(0);

		this.add(keywordsPanel);
		this.add(resourcesPanel);
		getResourcesMoodle(feedbackMoodleArray);
		}
	}
			

	private void getResourcesByOER(String word, final String nameOER, VerticalPanel panelForResults){
		
		panelForResults.clear();
		
		final VerticalPanel allresources = new VerticalPanel();
		allresources.addStyleName(Resources.INSTANCE.css().panelscrollresources());
		
		ScrollPanel scrollResourcesPanel = new ScrollPanel(allresources);
		scrollResourcesPanel.addStyleName(Resources.INSTANCE.css().scrollresources());
		
		String params = "action=" + nameOER + "&keywords=" + word;
		AjaxRequest.ajaxRequest(params, new AsyncCallback<AjaxData>() {			
			@Override
			public void onFailure(Throwable caught) {
			}
			
			@Override
			public void onSuccess(AjaxData result) {
				
				List<Map<String, String>> resources = AjaxRequest.getValuesFromResult(result);
				for(Map<String, String> info : resources) {

					final String rawName = info.get("name");
					final String rawLink = info.get("link");			
					
					final HTML name = new HTML(rawName);
					name.addStyleName(Resources.INSTANCE.css().resourcetitle());

					final Anchor link = new Anchor(rawLink, false, rawLink, "_blank");
					link.addStyleName(Resources.INSTANCE.css().resourcelink());
					
					final HTML auxLink = new HTML(link.toString());
					auxLink.addStyleName(Resources.INSTANCE.css().resourcelink());
					
					HTML hr = new HTML("<hr>");
					hr.addStyleName(Resources.INSTANCE.css().hrsize());
					
					Icon iconAdd = new Icon(IconType.PLUS_SIGN);
					HTML iconContainer = new HTML(iconAdd.toString());
					iconContainer.addStyleName(Resources.INSTANCE.css().plusicon());
					iconContainer.addClickHandler(new ClickHandler() {			
						@Override
						public void onClick(ClickEvent event) {		
							parent.addFeedback(rawName, link.toString(), nameOER, rawLink, 0);							
						}						
					});
					
					HorizontalPanel resourcePanel = new HorizontalPanel();
					resourcePanel.add(iconContainer);
					resourcePanel.add(name);
					resourcePanel.add(new HTML("<div style='width:10px'></div>"));
					resourcePanel.add(auxLink);
					
					allresources.add(resourcePanel);
					allresources.add(hr);
				}
				
			}
		});
		panelForResults.add(scrollResourcesPanel);
	}
	
	private void getResourcesMoodle(final ArrayList<FeedbackObject> feedbackMoodleArray){
		
		webcursosPanel.clear();
		
		final VerticalPanel allresources = new VerticalPanel();
		allresources.addStyleName(Resources.INSTANCE.css().panelscrollresources());
		
		ScrollPanel scrollResourcesPanel = new ScrollPanel(allresources);
		scrollResourcesPanel.addStyleName(Resources.INSTANCE.css().scrollresources());
		
		for( int iterator = 0; iterator < feedbackMoodleArray.size(); iterator++){
			
			final String rawName = feedbackMoodleArray.get(iterator).getName();
			final String rawLink = feedbackMoodleArray.get(iterator).getLink();

			final HTML name = new HTML(rawName);
			name.addStyleName(Resources.INSTANCE.css().resourcetitle());

			final Anchor link = new Anchor(rawLink, false, rawLink, "_blank");
			link.addStyleName(Resources.INSTANCE.css().resourcelink());
			
			final HTML auxLink = new HTML(link.toString());
			auxLink.addStyleName(Resources.INSTANCE.css().resourcelink());
			
			HTML hr = new HTML("<hr>");
			hr.addStyleName(Resources.INSTANCE.css().hrsize());
			
			Icon iconAdd = new Icon(IconType.PLUS_SIGN);
			HTML iconContainer = new HTML(iconAdd.toString());
			iconContainer.addStyleName(Resources.INSTANCE.css().plusicon());
			iconContainer.addClickHandler(new ClickHandler() {			
				@Override
				public void onClick(ClickEvent event) {		
					parent.addFeedback(
							rawName,
							link.toString(),
							"Webcursos",
							rawLink,
							0
					);							
				}						
			});
			
			HorizontalPanel resourcePanel = new HorizontalPanel();
			resourcePanel.add(iconContainer);
			resourcePanel.add(name);
			resourcePanel.add(new HTML("<div style='width:10px'></div>"));
			resourcePanel.add(auxLink);
			
			allresources.add(resourcePanel);
			allresources.add(hr);
		}
		webcursosPanel.add(scrollResourcesPanel);	
	}
	
	public void setParent(EditMarkDialog widget){
		this.parent = widget;
	}
}
