package cl.uai.client.page;

import cl.uai.client.EMarkingWeb;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;

public class PageSelectionHandler implements SelectionHandler<Integer> {
	
	MarkingPagesInterface markingPagesInterface = null;
	
	public PageSelectionHandler(MarkingPagesInterface markingPagesInterface) {
		this.markingPagesInterface = markingPagesInterface;
	}
	
		@Override
		public void onSelection(SelectionEvent<Integer> event) {
			// Gets the tab
			int newtab = event.getSelectedItem();
			
			// Update selected page index
			this.markingPagesInterface.scrollToPage(newtab);

			EMarkingWeb.markingInterface.getToolbar().getMarkingButtons().updateStats();
		}
}
