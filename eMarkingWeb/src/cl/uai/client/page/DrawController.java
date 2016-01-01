package cl.uai.client.page;

import java.util.ArrayList;


import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;

public class DrawController implements MouseDownHandler, MouseUpHandler,MouseMoveHandler{


	private boolean isMouseDown;
	private boolean isDrawing;
	ArrayList<DrawHandler> listeners = new ArrayList<DrawHandler>();
	
	public DrawController(){
	}
	public void addListener(DrawHandler handler){
		this.listeners.add(handler);
	}
	
	private void notifyDrawStart(MouseMoveEvent event){
		for(DrawHandler handler : this.listeners){
			handler.drawStart(event);
		}
	}
	private void notifyDrawMove(MouseMoveEvent event){
		for(DrawHandler handler : this.listeners){
			handler.drawMove(event);
		}
	}
	private void notifyDrawEnd(MouseUpEvent event){
		for(DrawHandler handler : this.listeners){
			handler.drawEnd(event);
		}
	}
	
	@Override
	public void onMouseDown(MouseDownEvent event) {
		this.isMouseDown = true;
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		this.isMouseDown = false;
		if(this.isDrawing){
			this.isDrawing = false;
			this.notifyDrawEnd(event);
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if(this.isMouseDown){
			if(this.isDrawing){
				this.notifyDrawMove(event);
			}else{
				this.isDrawing=true;
				this.notifyDrawStart(event);
			}
		}
	}
	public void listenTo(HasAllMouseHandlers source) {		
		source.addMouseDownHandler(this);
		source.addMouseMoveHandler(this);
		source.addMouseUpHandler(this);
	}

}
