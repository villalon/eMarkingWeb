// This file is part of Moodle - http://moodle.org/
//
// Moodle is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// Moodle is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Moodle.  If not, see <http://www.gnu.org/licenses/>.

/**
 * @package   eMarking
 * @copyright 2013 Jorge Villalón <villalon@gmail.com>
 * @license   http://www.gnu.org/copyleft/gpl.html GNU GPL v3 or later
 */
package cl.uai.client;

import java.util.ArrayList;
import java.util.logging.Logger;

import cl.uai.client.chat.NodeChat;
import cl.uai.client.resources.Resources;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Navigator;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class EMarkingWeb implements EntryPoint {

	/** For logging purposes */
	private static Logger logger = Logger.getLogger(EMarkingWeb.class.getName());

	/** Main marking interface **/
	public static MarkingInterface markingInterface = null;
	
	public static NodeChat chatServer = null;
	
	/** JSNI function to close and reload a window **/
	public static native boolean closeAndReload() /*-{
        var opener = null;

        if ($wnd.dialogArguments) // Internet Explorer supports window.dialogArguments
        { 
            opener = $wnd.dialogArguments;
        } 
        else // Firefox, Safari, Google Chrome and Opera supports window.opener
        {        
            if ($wnd.opener) 
            {
                opener = $wnd.opener;
            }
        }       
		if (opener) {
			var loc = opener.location.href;
    		var index = loc.indexOf('&tsort');

			if (index > 0) {
  				loc = loc.substring(0, index);
			}
	  		opener.location = loc;
        	$wnd.close();
        	return true;
		} else {
			return false;
		}
	}-*/;
	
	public static native int screenWidth() 
	/*-{ 
	      return screen.availWidth; 
	}-*/; 

	public static native int screenHeight() 
	/*-{ 
	      return screen.availHeight;
	}-*/; 
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		// Pointer to CSS manager. It has to go first!
		GWT.<Resources>create(Resources.class).css().ensureInjected();
		// Log messages
		logger.fine("Loading eMarkingWeb interface");
		logger.fine(Navigator.getPlatform());
		logger.fine(Navigator.getUserAgent());
		logger.fine(Navigator.getAppName());
		logger.fine(Navigator.getAppCodeName());
		logger.fine(Navigator.getAppVersion());

		// List of errors after trying to initialize
		ArrayList<String> errors = new ArrayList<String>();

		// Get id for eMarkingWeb's DIV tag
		final String eMarkingDivId = "emarking";
		if(RootPanel.get(eMarkingDivId)==null) {
			errors.add("Can not initalize. eMarkingWeb requires an existing DIV tag with id: emarking.");
			return;
		}
		
		RootPanel.get(eMarkingDivId).add(new Label("Loading"));		

		int draftId = 0;
		int preferredWidth = 860;
		boolean showRubric = true;
		boolean showColors = false;

		try {
			// First, if there's a URL parameter, replace the value
			if(Window.Location.getParameter("id") != null) {
				draftId = Integer.parseInt(Window.Location.getParameter("id")); 
			}
			
			// Validate that the submission id is a positive integer
			if(draftId <= 0) {
				errors.add("Submission id must be a non negative integer.");
			}
			
			String cookie_width = Cookies.getCookie("emarking_width");
			
			if(cookie_width != null) {
				preferredWidth = Integer.parseInt(cookie_width); 				
			}
			
			// Validate that the preferredWidth is a positive integer greater than 10
			if(preferredWidth <= 10) {
				errors.add("Preferred width should be a positive integer greater than 10.");
			}

			// Validate that the preferredWidth is a positive integer greater than 10
			if(preferredWidth <= 10) {
				errors.add("Preferred width should be a positive integer greater than 10.");
			}
			
			String cookie_showrubric = Cookies.getCookie("emarking_showrubric");
			
			if(cookie_showrubric != null) {
				showRubric = Integer.parseInt(cookie_showrubric) == 1;
			}
			
			String cookie_showcolors = Cookies.getCookie("emarking_showcolors");
			
			if(cookie_showcolors != null) {
				showColors = Integer.parseInt(cookie_showcolors) == 1;
			}
			
			logger.fine("ShowRubric: " + showRubric + 
					" Show colors:" + showColors + 
					" Preferred width:" + preferredWidth);
		} catch (Exception e) {
			logger.severe(e.getMessage());
			errors.add("Error in HTML for eMarkingWeb can not initalize. Invalid submissionId value (must be integer).");
		}

		// Read div attribute for readonly
		String moodleurl = null;
		if(RootPanel.get(eMarkingDivId).getElement().getAttribute("moodleurl") != null)
			moodleurl = RootPanel.get(eMarkingDivId).getElement().getAttribute("moodleurl");
		logger.fine("Moodle ajax url: " + moodleurl);

		if(moodleurl == null)
			errors.add("Invalid Moodle ajax url");

		// If there are errors die with a configuration message
		if(errors.size() > 0) {
			Label errorsLabel = new Label();
			String text = "";
			for(int i=0; i<errors.size(); i++) {
				text += "\n" + errors.get(i);
			}
			errorsLabel.setText(text);
			errorsLabel.setTitle("Fatal error while initializing eMarking-Web");

			RootPanel.get(eMarkingDivId).clear();
			RootPanel.get(eMarkingDivId).add(errorsLabel);
		} else {
			// Set eMarking's main interface submission id according to HTML
			MarkingInterface.setDraftId(draftId);

			EMarkingConfiguration.setShowRubricOnLoad(showRubric);
			EMarkingConfiguration.setColoredRubric(showColors);

			// Ajax URL in moodle
			EMarkingConfiguration.setMoodleUrl(moodleurl);

			// Automagically resize popup to use most of the window
			int width = screenWidth();
			int height = screenHeight();

			// Preferred width can not be bigger than the screen
			if(width < preferredWidth) {
				preferredWidth = width;
			}
			
			//  Resize the popup window and move it to the top left corner
			Window.resizeTo(preferredWidth, height);
			Window.moveTo(0, 0);

			// Initialize eMarking's interface
			markingInterface = new MarkingInterface();
			
			// Add eMarking to the browser
			RootPanel.get(eMarkingDivId).clear();
			RootPanel.get(eMarkingDivId).add(markingInterface);
			RootPanel.getBodyElement().focus();
		}
	}
}
