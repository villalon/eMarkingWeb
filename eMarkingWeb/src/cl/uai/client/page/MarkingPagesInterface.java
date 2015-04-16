// This file is part of Moodle - http://moodle.org/
//
// Moodle is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// Moodle is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Moodle.  If not, see <http://www.gnu.org/licenses/>.

/**
 * @package   eMarking
 * @copyright 2013 Jorge Villalón <villalon@gmail.com>
 * 				   Hans C. Jeria <hansj@live.cl>
 * @license   http://www.gnu.org/copyleft/gpl.html GNU GPL v3 or later
 */
package cl.uai.client.page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import cl.uai.client.EMarkingComposite;
import cl.uai.client.EMarkingWeb;
import cl.uai.client.MarkingInterface;
import cl.uai.client.data.AjaxData;
import cl.uai.client.data.AjaxRequest;
import cl.uai.client.marks.Mark;
import cl.uai.client.marks.RubricMark;
import cl.uai.client.resources.Resources;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * Marking pages contains all pages from a submission and their corresponding marks.
 * New marks can be dropped in a submission page and clicks on them should also
 * add new marks.
 * 
 * @author Jorge Villalon <villalon@gmail.com>
 *
 */
public class MarkingPagesInterface extends EMarkingComposite {

	Logger logger = Logger.getLogger(MarkingPagesInterface.class.getName());
	
	/** Pages interface is organized as a tab panel **/
	private VerticalPanel pagesPanel = null;
	private ScrollPanel scrollContainerForPages = null;
	

	/** Number of pages **/
	private int numPages = -1;
	/**
	 * @return the numPages
	 */
	public int getNumPages() {
		return numPages;
	}

	/** Width and Height Page**/
	int widthPage = -1;
	int heightPage = -1;

	/**
	 * Creates the main interface for pages
	 */
	public MarkingPagesInterface() {

		// Initialize tab pabel and assign CSS class
		pagesPanel = new VerticalPanel();
		
		scrollContainerForPages = new ScrollPanel();
		scrollContainerForPages.add(pagesPanel);
		
		this.initWidget(scrollContainerForPages);
	}

	/**
	 * Adds a mark to the current selected page. Firstly it removes any
	 * mark with the same level from all pages.
	 *  
	 * @param mark the mark to add
	 * @param previd the comment id from the mark with the same level
	 * @return
	 */
	public Mark addMarkWidget(Mark mark, int previd, MarkingPage page) {

		// If there was a previous mark with the same rubric level, loop through all pages to remove it
		if(mark instanceof RubricMark && previd > 0) {
			deleteMarkWidget(previd);
		}

		return page.addMarkWidget(mark);
	}

	/**
	 * Deletes a mark widget from all pages (if exist)
	 * 
	 * @param markId the mark's id
	 */
	public void deleteMarkWidget(int markId) {
		// Loop through pages
		for(int i=0;i<pagesPanel.getWidgetCount();i++) {
			// Get widget
			Widget selectedWidget = pagesPanel.getWidget(i);

			// If it's a label then ignore it
			if(!(selectedWidget instanceof MarkingPage))
				continue;

			// If it's a page, remove the mark
			MarkingPage currentPage = (MarkingPage) selectedWidget;
			if(currentPage.getMarkWidgets().containsKey(markId)) {
				currentPage.deleteMarkWidget(markId);
			}
		}		
	}
	
	/**
	 * Highlights a mark in the corresponding page. For the moment
	 * highlighting is to scroll to its position
	 * 
	 * @param markId the mark's id
	 * @param markPage the mark's page
	 */
	public void highlightRubricMark(int markId, int markPage) {
		// Select the page and load if necessary
		scrollToPage(markPage - 1);
	}

	public void scrollToPage(int page) {
		int position = (int) (((float) pagesPanel.getOffsetHeight() / (float) numPages) * (float) page);
		scrollContainerForPages.setVerticalScrollPosition(position);
	}
	
	public ScrollPanel getScrollPanel() {
		return this.scrollContainerForPages;
	}
	
	// Loads interface
	public void loadInterface() {
		// Clear tabs and load the first one
		pagesPanel.clear();
		loadAllTabs();
	}

	// Loads a tab according to its index
	public void loadAllTabs() {
		logger.fine("Loading all tabs");

		// Check if submission data is invalid
		if(MarkingInterface.submissionData.getId() <= 0) {
			return;
		}

		// Checks if the assignment is anonymous
		String anonymous = MarkingInterface.isAnonymous() ? "1" : "0";

		EMarkingWeb.markingInterface.addLoading(false);
		
		// Ajax request to get the tab image and number of pages
		AjaxRequest.ajaxRequest("action=getalltabs" +
				"&anonymous=" + anonymous, 
				new AsyncCallback<AjaxData>() {			
			@Override
			public void onFailure(Throwable caught) {
				logger.severe("Error getting tab info from Moodle!");
				logger.severe(caught.getMessage());
				Window.alert(caught.getMessage());
				EMarkingWeb.markingInterface.finishLoading();				
			}
			
			@Override
			public void onSuccess(AjaxData result) {

				// If something goes wrong clear the tabs and show error message 
				if(!result.getError().equals("")) {
					pagesPanel.clear();
					pagesPanel.add(new Label(MarkingInterface.messages.ErrorLoadingSubmission()));
					EMarkingWeb.markingInterface.finishLoading();				
					return;
				}

				// Parse Json values
				List<Map<String, String>> alltabs = AjaxRequest.getValuesFromResult(result);

				int tabnum = 0;
				for(Map<String, String> tabinfo : alltabs) {

					tabnum++;

					// Get image info in case we don't have it
					if(numPages <= 0) {
						numPages = Integer.parseInt(tabinfo.get("totalpages"));
					}

					// Get the image width and height
					int width = Integer.parseInt(tabinfo.get("width"));
					int height = Integer.parseInt(tabinfo.get("height"));
					boolean showmarker = Integer.parseInt(tabinfo.get("showmarker")) == 1;

					double screenWidth = Window.getClientWidth()+1;
					int newwidth = (int) screenWidth;
					widthPage = newwidth;
					float ratio = (float) width / (float) height;
					int newheight = (int) (newwidth / ratio);
					heightPage = newheight;
					MarkingPage page = new MarkingPage(tabnum, tabinfo.get("url"), newwidth, newheight);
					if(!showmarker) {
						page.setVisible(false);
					}
					pagesPanel.insert(page, tabnum-1);
				}
				scrollToPage(0);

				EMarkingWeb.markingInterface.finishLoading();	
			}
		});
	}
	
	public MarkingPage getPageByIndex(int index) {
		for(int i=0; i<pagesPanel.getWidgetCount();i++) {
			Widget w = pagesPanel.getWidget(i);
			if(i==index && w instanceof MarkingPage)
				return (MarkingPage) w;
		}
		return null;
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		this.addStyleName(Resources.INSTANCE.css().pagespanel());
		int height = Window.getClientHeight() - this.getAbsoluteTop();
		this.scrollContainerForPages.setHeight(height + "px");
	}

	public Map<Integer, Integer> getMarkStatistics() {
		Map<Integer, Integer> allstats = new HashMap<Integer, Integer>();
		for(int i=0; i<pagesPanel.getWidgetCount(); i++) {
			Widget w = pagesPanel.getWidget(i);
			if(w instanceof MarkingPage) {
				MarkingPage page = (MarkingPage) w;
				Map<Integer, Integer> stats = page.getMarkStatistics();
				for(int key : stats.keySet()) {
					int value = stats.get(key);
					if(!allstats.containsKey(key)) {
						allstats.put(key, 0);
					}
					int finalvalue = allstats.get(key) + value;
					allstats.put(key, finalvalue);
				}
			}
		}
		return allstats;
	}

	
	public VerticalPanel getPagesPanel(){
		return pagesPanel;
	}
	
	public int getWidthPage(){
		return widthPage;
	}
	
	public int getHeightPage(){
		return heightPage;
	}
	
}
