package cl.uai.client.chat;

import java.util.ArrayList;

import com.google.gwt.core.client.JavaScriptObject;

public class UserData extends JavaScriptObject{

	protected UserData (){}
		
		
		 public final native int getRoom() /*-{ 
		 	return this.room;
		 	 }-*/;
		 public final native String getName() /*-{ 
		 	return this.username; 
		 	}-*/;

	}


	

