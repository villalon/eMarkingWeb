package cl.uai.client.page;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.shape.Path;
import org.vaadin.gwtgraphics.client.shape.path.LineTo;

import cl.uai.client.EMarkingWeb;
import cl.uai.client.MarkingInterface;
import cl.uai.client.marks.PathMark;
import cl.uai.client.toolbar.MarkingButtons;

import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class MarkingPageDrawHandler implements DrawHandler {
	private MarkingPage parentPage = null;
	public Path currentPath;
	public int lastX;
	public int lastY;
	private DrawingArea drawingArea = null;
	private AbsolutePanel absolutePanel = null;
	public MarkingPageDrawHandler(AbsolutePanel panel,DrawingArea drawingArea, MarkingPage _parent) {
		this.parentPage = _parent;
		this.absolutePanel = panel;
		this.drawingArea = drawingArea;
		this.lastX = 0;
		this.lastY = 0;
	}
	private boolean isPenActive(){
		return EMarkingWeb.markingInterface.getToolbar().getMarkingButtons().getSelectedButton() == MarkingButtons.Buttons.BUTTON_PEN;
		
	}
	
	@Override
	public void drawStart(MouseMoveEvent event) {
		if(!isPenActive()){
			return;
		}
		this.lastX = event.getClientX()-absolutePanel.getAbsoluteLeft();
		this.lastY =  event.getClientY()-absolutePanel.getAbsoluteTop();
		this.currentPath = new Path(this.lastX,this.lastY);
		this.currentPath.setFillOpacity(0);
		this.drawingArea.add(this.currentPath);
		
	}

	@Override
	public void drawMove(MouseMoveEvent event) {
		if(!isPenActive()){
			return;
		}
		int currentY = event.getClientY()-absolutePanel.getAbsoluteTop();
		int currentX = event.getClientX()-absolutePanel.getAbsoluteLeft();
		
		int relX = currentX - this.lastX;
		int relY = currentY - this.lastY;
		this.lastX = currentX;
		this.lastY =  currentY;
		this.currentPath.lineRelativelyTo(relX, relY);
	}

	@Override
	public void drawEnd(MouseUpEvent event) {
		if(!isPenActive()){
			return;
		}
		drawingArea.remove(this.currentPath);
		int pathX = this.currentPath.getX();
		int pathY = this.currentPath.getY();
		int top = 11000;
		int left = 11000;
		int right = -1;
		int bottom = -1;
		
		int x = this.currentPath.getX();
		int y = this.currentPath.getY();
		for(int i = 0; i < this.currentPath.getStepCount();i++){
			if(this.currentPath.getStep(i) instanceof LineTo){
				x += ((LineTo)this.currentPath.getStep(i)).getX();
				y += ((LineTo)this.currentPath.getStep(i)).getY();
				left = x<left? x:left;
				top = y<top? y:top;
				right = x>right? x:right;
				bottom = y>bottom? y:bottom;
			}
		}
		this.currentPath.setX(pathX-left);
		this.currentPath.setY(pathY-top);
		long unixtime = System.currentTimeMillis() / 1000L;
		int pageno = this.parentPage.getPageNumber();
		PathMark mark = new PathMark(
				left,
				top,
				pageno,
				MarkingInterface.markerid,
				right-left,bottom-top, 
				this.currentPath.getElement().getAttribute("d"),
				unixtime);

		EMarkingWeb.markingInterface.addMark(mark, this.parentPage);
		//currentPath.setY(event.getClientY()-absolutePanel.getAbsoluteTop()+20);
		
	}

}