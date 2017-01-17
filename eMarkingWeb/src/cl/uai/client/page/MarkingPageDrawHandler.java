package cl.uai.client.page;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.shape.Path;
import org.vaadin.gwtgraphics.client.shape.path.LineTo;

import cl.uai.client.EMarkingConfiguration;
import cl.uai.client.EMarkingWeb;
import cl.uai.client.MarkingInterface;
import cl.uai.client.data.Criterion;
import cl.uai.client.marks.PathMark;
import cl.uai.client.toolbar.buttons.ButtonFormat;

import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.PopupPanel;

public class MarkingPageDrawHandler implements DrawHandler {
	private MarkingPage parentPage = null;
	public Path currentPath;
	public int lastX;
	public int lastY;
	public int selectedCriterion;
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
		return EMarkingWeb.markingInterface.getToolbar().getMarkingButtons().getSelectedButtonFormat() == ButtonFormat.BUTTON_PEN;
		
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
		
		int newposx = event.getClientX();
		int newposy = event.getClientY();
		
		final EditMarkDialog dialogquestion = new EditMarkDialog(
				newposx, 
				newposy,
				0, // No level id for a text comment
				0); // No regradeid either
		
		dialogquestion.addCloseHandler(new CloseHandler<PopupPanel>() {				
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if(dialogquestion.isCancelled()) {
					EMarkingWeb.markingInterface.getElement().focus();
					return;
				}
			}
		});
		dialogquestion.show();			

		drawingArea.remove(this.currentPath);
		int pathX = this.currentPath.getX();
		int pathY = this.currentPath.getY();
		int top = 11000;
		int left = 11000;
		int right = -1;
		int bottom = -1;
		
		selectedCriterion = 0;
		
		if(EMarkingConfiguration.isColoredRubric()) {
			Criterion criterion = EMarkingWeb.markingInterface.getToolbar().getMarkingButtons().getSelectedCriterion();
			if(criterion != null) {
				selectedCriterion = criterion.getId();
			}
		}
		
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
				0,
				left,
				top,
				pageno,
				EMarkingConfiguration.getMarkerId(),
				right-left,bottom-top,
				dialogquestion.getTxtComment(),
				this.currentPath.getElement().getAttribute("d"),
				unixtime,
				selectedCriterion,
				MarkingInterface.submissionData.getMarkerfirstname());

		EMarkingWeb.markingInterface.addMark(mark, this.parentPage);
	}

}