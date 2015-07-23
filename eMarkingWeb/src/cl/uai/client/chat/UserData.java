package cl.uai.client.chat;

import com.google.gwt.core.client.JavaScriptObject;

public class UserData extends JavaScriptObject{

	protected UserData (){}
		 public final native int getRoom() /*-{ 
		 	return this.room;
		 	 }-*/;
		 public final native String getName() /*-{ 
		 	return this.username; 
		 	}-*/;
		 public final native String getId() /*-{ 
		 	return this.userid; 
		 	}-*/;
		 public final native int getColor() /*-{ 
		 	return this.color; 
		 	}-*/;
	}


	

