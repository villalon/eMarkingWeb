package cl.uai.client.marks.collaborative;

import cl.uai.client.EMarkingConfiguration;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

public class QuoteMark extends CollaborativeMark{
	
	private MyPopup popupMarkers = new MyPopup();
	
	public QuoteMark(){
		super();
		icon = new Icon(IconType.SHARE);
		popupMarkers = new MyPopup();
		markers = new HTML();

		onClick = new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {				
				event.stopPropagation();
				mark.onClick(event);
				if(canClick){
					processClick();
				}
			}			
		};
		handlerRecord = this.addClickHandler(onClick);
		
		this.addMouseMoveHandler(new MouseMoveHandler(){		
			@Override
			public void onMouseMove(MouseMoveEvent event){
				popupMarkers.setWidget(markers);
				popupMarkers.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
					public void setPosition(int offsetWidth, int offsetHeight) {
			            int left = (int) mark.getAbsoluteLeft();
			            int top = (int) mark.getAbsoluteTop()+mark.getHeight()+14;
			            if(markers.getHTML().equals("")){
			            	left = -100;
			            	top = -100;
			            }
			            popupMarkers.setPopupPosition(left, top);
			         }
			     });
			}
		});
		
		this.addMouseOutHandler(new MouseOutHandler(){
			@Override
			public void onMouseOut(MouseOutEvent event) {
				popupMarkers.hide();				
			}			
		});
	}

	@Override
	public void processClick() {

		// Ajax request, i can clicked
		if(canClick){
			if(isClicked != EMarkingConfiguration.EMARKING_COLLABORATIVE_BUTTON_CLICKED){
				
				mark.updateMark(mark.getAbsoluteLeft(), mark.getAbsoluteTop());	
				
			}else{

			}		
		}
	}
	
}
