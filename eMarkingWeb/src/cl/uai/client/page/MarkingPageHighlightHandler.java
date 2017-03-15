package cl.uai.client.page;

import java.util.List;
import java.util.logging.Logger;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.shape.Path;

import cl.uai.client.EMarkingConfiguration;
import cl.uai.client.EMarkingWeb;
import cl.uai.client.MarkingInterface;
import cl.uai.client.data.Criterion;
import cl.uai.client.marks.HighlightMark;
import cl.uai.client.marks.Point;
import cl.uai.client.toolbar.buttons.ButtonFormat;

import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.PopupPanel;

public class MarkingPageHighlightHandler implements DrawHandler {
	
	private static Logger logger = Logger.getLogger(MarkingPageHighlightHandler.class.getName());
	
	private MarkingPage parentPage = null;
	public Path currentPath;
	public Point start;
	public Point end;
	public int selectedCriterion;
	private DrawingArea drawingArea = null;
	private AbsolutePanel absolutePanel = null;
	public MarkingPageHighlightHandler(AbsolutePanel panel,DrawingArea drawingArea, MarkingPage _parent) {
		this.parentPage = _parent;
		this.absolutePanel = panel;
		this.drawingArea = drawingArea;
		this.start = new Point(0, 0);
	}
	private boolean isPenActive(){
		return EMarkingWeb.markingInterface.getToolbar().getMarkingButtons().getSelectedButtonFormat() == ButtonFormat.BUTTON_HIGHLIGHT;
		
	}
	
	@Override
	public void drawStart(MouseMoveEvent event) {
		if(!isPenActive()){
			return;
		}
		this.start = new Point(
				event.getClientX()-absolutePanel.getAbsoluteLeft(),
				(event.getClientY()-absolutePanel.getAbsoluteTop()) - (event.getClientY()-absolutePanel.getAbsoluteTop())%HighlightMark.size);
		this.end = new Point(start.getX(), start.getY());
		this.currentPath = HighlightMark.createPath(start);
		this.drawingArea.add(this.currentPath);
	}
	

	@Override
	public void drawMove(MouseMoveEvent event) {
		if(!isPenActive()){
			return;
		}
		int currentY = (event.getClientY()-absolutePanel.getAbsoluteTop()) - (event.getClientY()-absolutePanel.getAbsoluteTop()) % HighlightMark.size;
		int currentX = event.getClientX()-absolutePanel.getAbsoluteLeft();
		
		end.setX(currentX);
		end.setY(currentY);
		
		List<Point> newpath = HighlightMark.calculatePath(start, end, absolutePanel.getOffsetWidth());

		drawingArea.remove(this.currentPath);
		for(int i=0; i<newpath.size(); i++) {
			int x = newpath.get(i).getX();
			int y = newpath.get(i).getY();
			if(i%2==0) {
				if(i==0) {
					this.currentPath = HighlightMark.createPath(start);
					drawingArea.add(this.currentPath);
				} else {
					this.currentPath.moveTo(x, y);
				}
			} else {
				this.currentPath.lineTo(x, y);
			}
		}
	}
	

	@Override
	public void drawEnd(MouseUpEvent event) {
		
		if(!isPenActive()){
			return;
		}

		final int newposx = event.getClientX();
		final int newposy = (event.getClientY()-absolutePanel.getAbsoluteTop()) - 
				(event.getClientY()-absolutePanel.getAbsoluteTop()) % HighlightMark.size;
		
		end.setX(event.getClientX() - absolutePanel.getAbsoluteLeft());
		end.setY(newposy);
		
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
				
				selectedCriterion = 0;
				
				if(EMarkingConfiguration.isColoredRubric()) {
					Criterion criterion = EMarkingWeb.markingInterface.getToolbar().getMarkingButtons().getSelectedCriterion();
					if(criterion != null) {
						selectedCriterion = criterion.getId();
					}
				}
				long unixtime = System.currentTimeMillis() / 1000L;
				int pageno = parentPage.getPageNumber();
				
				drawingArea.remove(currentPath);
				
				logger.fine("adding " + start + " " + end);
				int newy = start.getY();
				Point newstart = new Point(start.getX(), 0);
				Point newend = new Point(end.getX(), end.getY()-start.getY());
				start = newstart;
				end = newend;
				logger.fine("adding II " + start + " " + end);
				HighlightMark mark = new HighlightMark(
						0,
						0,
						newy,
						pageno,
						EMarkingConfiguration.getMarkerId(),
						parentPage.getWidth(),
						parentPage.getHeight(),
						dialogquestion.getTxtComment(),
						end.getX() + "," + newend.getY(),
						unixtime,
						selectedCriterion,
						MarkingInterface.submissionData.getMarkerfirstname());
				mark.setStart(start);
				mark.setEnd(end);
				mark.setMarkHTML();
				EMarkingWeb.markingInterface.addMark(mark, parentPage);
			}
		});
		dialogquestion.show();
		
	}

}