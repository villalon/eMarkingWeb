package cl.uai.client.chat;

import com.google.gwt.core.client.JavaScriptObject;

public class UserJS extends JavaScriptObject{

	protected UserJS() {}
	
	public final native String getFirstName() /*-{ 
	 	return this.first; 
	}-*/;
	
	public final native String getLastName() /*-{ 
 		return this.last; 
 	}-*/;
	
	public final native String getEmail() /*-{ 
 		return this.email; 
 	}-*/;
	
	public final native String getId() /*-{ 
		return this.userid; 
	}-*/;
}




