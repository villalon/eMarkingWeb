/**
 * 
 */
package cl.uai.client;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;

import cl.uai.client.marks.Mark;
import cl.uai.client.toolbar.MarkingToolBar;

/**
 * @author Jorge
 *
 */
public class MarkingInterfaceKeyDownHandler implements KeyDownHandler {

	private MarkingToolBar toolbar = null;
	
	public MarkingInterfaceKeyDownHandler(MarkingToolBar _toolbar) {
		this.toolbar = _toolbar;
	}
	
	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.KeyDownHandler#onKeyDown(com.google.gwt.event.dom.client.KeyDownEvent)
	 */
	@Override
	public void onKeyDown(KeyDownEvent event) {
		if(event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
			Mark.hideIcons();
		} else if(event.getNativeKeyCode() >= KeyCodes.KEY_ONE && event.getNativeKeyCode() <= KeyCodes.KEY_NINE) {
			toolbar.getMarkingButtons().setButtonPressed(event.getNativeKeyCode() - KeyCodes.KEY_ONE, false);
		} else if(event.getNativeKeyCode() == KeyCodes.KEY_ZERO) {
			toolbar.getMarkingButtons().setButtonPressed(91, false);
		} else if(event.getNativeKeyCode() == KeyCodes.KEY_ZERO) {
			toolbar.getMarkingButtons().setButtonPressed(91, false);
		}
	}

}
