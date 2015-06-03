package cl.uai.client.chat;

import com.google.gwt.core.client.JavaScriptObject;

public class Sos extends JavaScriptObject{
	
	protected Sos(){}

	public final native int getTime() /*-{ 
 	return this.unixtime;
 	 }-*/;
	
	public final native String getComment() /*-{ 
 	return this.comment;
 	 }-*/;
	
	public final native int getSubmissionId() /*-{ 
 	return this.submissionid;
 	 }-*/;
	
	public final native int getUserId() /*-{ 
 	return this.userid;
 	 }-*/;
	
	public final native int getRoom() /*-{ 
 	return this.room;
 	 }-*/;
	
	public final native int getStatus() /*-{ 
 	return this.status;
 	 }-*/;
	
	public final native String getUserName() /*-{ 
 	return this.username;
 	 }-*/;
}

