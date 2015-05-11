package cl.uai.client.chat;

import com.google.gwt.core.client.JavaScriptObject;

public class Message extends JavaScriptObject{

	protected Message(){}
		
		 public final native String getMessage() /*-{ 
		 	return this.message;
		 	 }-*/;
		 public final native String getUser() /*-{ 
		 	return this.username; 
		 	}-*/;
		 public final native int getRoom() /*-{ 
		 	return this.room; 
		 	}-*/;
		 public final native int getTime() /*-{ 
		 	return this.time;
		 	 }-*/;
		 
		
	
}
