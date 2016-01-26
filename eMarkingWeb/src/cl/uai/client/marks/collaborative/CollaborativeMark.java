package cl.uai.client.marks.collaborative;

import cl.uai.client.EMarkingConfiguration;
import cl.uai.client.marks.Mark;
import cl.uai.client.resources.Resources;

import com.github.gwtbootstrap.client.ui.Icon;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

abstract class CollaborativeMark extends HTML{	
	
	/**The mark related to the icon **/
	protected Mark mark = null;
	protected Icon icon = null;
	protected int value = -1;
	protected boolean canClick = false;
	protected int isClicked = -1;
	protected HTML markers = null;
	protected ClickHandler onClick = null;
	protected HandlerRegistration handlerRecord = null;
	
	protected static class MyPopup extends PopupPanel {
		public MyPopup() {
			super(true);
	    }
	};
	 
	public CollaborativeMark(){
		this.addStyleName(Resources.INSTANCE.css().likeIcon());
	}
	
	/**
	 * Sets the corresponding Mark for the icon
	 * 
	 * @param sourcemark current mark
	 */
	public void setMark(Mark sourcemark) {
		this.mark = sourcemark;
	}
	
	public void setValue(int count){
		value = count;
	}
	
	public int getValue(){
		return value;
	}
	
	public void setIsClicked(int value){
		isClicked = value;
	}
	
	public int getIsClicked(){
		return isClicked;
	}
	
	public abstract void processClick();
	
	
	public void setCanClick(boolean value){
		canClick = value;
	}
	
	public boolean getCanClick(){
		return canClick;
	}
	
	public void setMarkHTML() {
		this.setHTML(icon.toString()+value);
	}
	
	public void addValue(int counter) {
		value = value + counter;
	}
	
	public void setMarkers(String names){
		markers.setHTML(names);
	}
	
	public HTML getMarkers(){
		return markers;
	}
	
	public void setFormat(int markerid, String markername) {
		
		if(markerid == EMarkingConfiguration.getMarkerId()){
			
			this.setIsClicked(EMarkingConfiguration.EMARKING_COLLABORATIVE_BUTTON_CLICKED);
			setStyleClicked();
			
		}else if(!this.getCanClick()){
			
			String markers = this.getMarkers().toString();
			markers += " <li> " + markername+"</li>";
			this.setMarkers(markers);
		}		
	}
	
	public void setStyleClicked(){
		this.removeStyleName(Resources.INSTANCE.css().likeIcon());
		this.addStyleName(Resources.INSTANCE.css().likeIconclicked());
	}
	
	public void setStyleUnClicked(){
		this.removeStyleName(Resources.INSTANCE.css().likeIconclicked());
		this.addStyleName(Resources.INSTANCE.css().likeIcon());
	}
	
}
