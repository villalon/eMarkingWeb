package cl.uai.client.page;

import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;

public interface DrawHandler {

	public void drawStart(MouseMoveEvent event);
	public void drawMove(MouseMoveEvent event);
	public void drawEnd(MouseUpEvent event);
	
}
