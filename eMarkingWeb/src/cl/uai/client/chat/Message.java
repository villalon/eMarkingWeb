package cl.uai.client.chat;

import com.google.gwt.core.client.JavaScriptObject;

public class Message extends JavaScriptObject{

	protected Message(){}
		
		 public final native String getMessage() /*-{ 
		 	return this.message;
		 }-*/;
		 public final native String getUserId() /*-{ 
		 	return this.userid; 
		 }-*/;
		 public final native int getTime() /*-{ 
		 	return this.time;
		 }-*/;
		 public final native int getSource() /*-{ 
		 	return this.source;
		 }-*/;
		 public final native int getDraftId() /*-{ 
		 	return this.draftid;
		 }-*/;
		 public final native int getUrgency() /*-{ 
		 	return this.urgency;
		 }-*/;
		 public final native int getStatus() /*-{ 
		 	return this.status;
		 }-*/;
}
