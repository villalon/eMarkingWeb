package cl.uai.client.marks.collaborative;

import cl.uai.client.EMarkingConfiguration;
import cl.uai.client.data.AjaxData;
import cl.uai.client.data.AjaxRequest;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;

public class DisLikeMark extends CollaborativeMark{
	
	private MyPopup popupMarkers = null;
	
	public DisLikeMark(){
		super();
		icon = new Icon(IconType.THUMBS_DOWN);
		markers = new HTML();
		popupMarkers = new MyPopup();
		
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
			            int left = (int) mark.getAbsoluteLeft()+35;
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
		handlerRecord.removeHandler();
		// Ajax request, i can clicked
		if(canClick){
			if(isClicked == EMarkingConfiguration.EMARKING_COLLABORATIVE_BUTTON_CLICKED){
				// remove like
				String sendVars = "type=2&status=0&markerid="+EMarkingConfiguration.getMarkerId()+"&commentid="+mark.getId();
				AjaxRequest.ajaxRequest("action=clickcollaborativebuttons&"+sendVars, new AsyncCallback<AjaxData>() {
					
					@Override
					public void onFailure(Throwable caught) {
						addClickHandler(onClick);
					}
					
					@Override
					public void onSuccess(AjaxData result) {
						setIsClicked(0);
						addValue(-1);
						setStyleUnClicked();
						setMarkHTML();
						addClickHandler(onClick);
					}
				});
				
			}else{
				// add like
				String sendVars = "type=2&status=1&markerid="+EMarkingConfiguration.getMarkerId()+"&commentid="+mark.getId();
				AjaxRequest.ajaxRequest("action=clickcollaborativebuttons&"+sendVars, new AsyncCallback<AjaxData>() {
					
					@Override
					public void onFailure(Throwable caught) {
						addClickHandler(onClick);
					}
					
					@Override
					public void onSuccess(AjaxData result) {
						HorizontalPanel buttons = (HorizontalPanel) getParent();
						if(buttons.getWidget(0) instanceof LikeMark && 
								((LikeMark) buttons.getWidget(0)).getIsClicked() == EMarkingConfiguration.EMARKING_COLLABORATIVE_BUTTON_CLICKED){
							((LikeMark) buttons.getWidget(0)).processClick();
						}
						setIsClicked(1);
						addValue(1);
						setStyleClicked();
						setMarkHTML();
						addClickHandler(onClick);
					}
				});
			}		
		}	
	}
	
}
