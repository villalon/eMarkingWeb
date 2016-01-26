package cl.uai.client.marks.collaborative;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cl.uai.client.MarkingInterface;
import cl.uai.client.data.AjaxData;
import cl.uai.client.data.AjaxRequest;
import cl.uai.client.marks.RubricMark;

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
import com.google.gwt.user.client.ui.PopupPanel;

public class DiscussionMark extends CollaborativeMark{
	
	private  ArrayList<DiscussionMessage> messages = null;
	private DiscussionDialog DiscussionInterface = null;
	private MyPopup popupMarkers = null;
	
	public DiscussionMark(){
		super();
		icon = new Icon(IconType.COMMENTS);
		markers = new HTML();
		messages = new ArrayList<DiscussionMessage>();
		popupMarkers = new MyPopup();
		DiscussionInterface = new DiscussionDialog();
		DiscussionInterface.setMark(this);
		
		onClick = new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {				
				event.stopPropagation();
				mark.onClick(event);
				
					processClick();
				
			}			
		};
		handlerRecord = this.addClickHandler(onClick);
		
		this.addMouseMoveHandler(new MouseMoveHandler(){		
			@Override
			public void onMouseMove(MouseMoveEvent event){
				popupMarkers.setWidget(markers);
				popupMarkers.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
					public void setPosition(int offsetWidth, int offsetHeight) {
			            int left = (int) mark.getAbsoluteLeft()+100;
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
		DiscussionInterface.show();
	}		
	
	public void addMessage(String date, String user, String text, int markerid){
		DiscussionMessage message = new DiscussionMessage(date, user, text, markerid);
		messages.add(message);
		DiscussionInterface.setMessages(messages);
	}

	public void instanceDialog() {
		DiscussionInterface.setCommentid(mark.getId());
		DiscussionInterface.setMarkername(mark.getMarkername());
		DiscussionInterface.setHTML("<b style='font-size:1.2em;' >"+((RubricMark)mark).getLevel().getCriterion().getDescription()
				+": "+RubricMark.scoreFormat(((RubricMark) mark).getLevel().getScore() + ((RubricMark) mark).getLevel().getBonus(),false)
				+"/"+ RubricMark.scoreFormat(((RubricMark) mark).getLevel().getCriterion().getMaxscore(),false)+"</b>"
				+"<br/>"+MarkingInterface.messages.Marker()+": "+mark.getMarkername());
	}

	public DiscussionDialog getDiscussionInterface() {
		return DiscussionInterface;
	}

	public void setDiscussionInterface(DiscussionDialog discussionInterface) {
		DiscussionInterface = discussionInterface;	
	}

	public void reLoadIcon() {
		String url = "ids="+MarkingInterface.getDraftId()+"&commentid="+mark.getId()+"&type=4";
		AjaxRequest.ajaxRequest("action=getvaluescollaborativebuttons&"+url, new AsyncCallback<AjaxData>() {		
			@Override
			public void onFailure(Throwable caught) {
			}
			
			@Override
			public void onSuccess(AjaxData result) {
				List<Map<String, String>> valuesCollaborativesButtons = AjaxRequest.getValuesFromResult(result);

				if(valuesCollaborativesButtons.size() > 0){
					setValue(valuesCollaborativesButtons.size());
				}
				setMarkHTML();
				setStyleClicked();
			}
			
		});

	}
	
}
