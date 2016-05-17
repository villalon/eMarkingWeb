/**
 * 
 */
package cl.uai.client;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import cl.uai.client.data.AjaxRequest;

import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.json.client.JSONObject;

/**
 * Class representing an EMarking interface configuration
 * 
 * @author Jorge Villalón
 *
 */
public class EMarkingConfiguration {
	
	/** Constants for emarking types. It must fit values un /mod/emarking/lib.php **/
	public static final int EMARKING_TYPE_PRINT_ONLY = 0;
	public static final int EMARKING_TYPE_NORMAL = 1;
	public static final int EMARKING_TYPE_MARKER_TRAINING = 2;
	public static final int EMARKING_TYPE_STUDENT_TRAINING = 3;
	public static final int EMARKING_TYPE_PEER_REVIEW = 4;
	public static final int EMARKING_TYPE_PRINT_SCAN = 5;
	
	/** Constants for collaboratives buttons types **/
	public static final int EMARKING_COLLABORATIVE_BUTTON_LIKE = 1;
	public static final int EMARKING_COLLABORATIVE_BUTTON_DISLIKE = 2;
	public static final int EMARKING_COLLABORATIVE_BUTTON_QUOTE = 3;
	public static final int EMARKING_COLLABORATIVE_BUTTON_DISCUSSION = 4;
	public static final int EMARKING_COLLABORATIVE_BUTTON_CLICKED = 1 ;

	/** For logging purposes **/
	private static Logger logger = Logger.getLogger(EMarkingConfiguration.class.getName());
	
	/** eMarking version according to Moodle for debugging information **/
	private static int eMarkingVersion = 0;

	/** For messages including the admin's email **/
	private static String administratorEmail = null;
	
	/** Indicates if the marking interface will include student anonymous information **/
	private static boolean studentAnonymous = true;

	/** Indicates if the user can manage delphi process **/
	private static boolean manageDelphi = false;

	/** Indicates if the marking interface will include marker anonymous information **/
	private static boolean markerAnonymous = true;

	/** Indicates if the marking interface is in read only mode **/
	private static boolean readonly = true;

	/** Indicates if the user is a supervisor (editingteacher) **/
	private static boolean supervisor = false;

	/** The id of the marker.**/
	private static int markerId=0;

	/** Indicates if the user owns the draft **/
	private static boolean ownDraft = false;

	/** Moodle session key for posting to marking ajax interface **/
	private static String sessKey = null;

	/** Div contains rubric icon  **/
	private static boolean showRubricOnLoad = true;

	/** Path for NodeJs server which implements the chat interface **/
	private static String nodeJsPath;

	/** If is enabled linkrubric (Marcelo's thesis) **/
	private static boolean coloredRubric = false;

	/** The URL of the moodle installation with which EMarking is linked **/
	private static String moodleUrl = null;
	
	/**
	 * @param coloredRubric the coloredRubric to set
	 */
	public static void setColoredRubric(boolean coloredRubric) {
		EMarkingConfiguration.coloredRubric = coloredRubric;
	}

	/** The marking type (normal, markers training, etc) **/
	private static int markingType = EMARKING_TYPE_PRINT_ONLY;

	/** If the chat is enabled **/
	private static boolean chatEnabled = false;

	/** If the chat server raised an error **/
	private static boolean chatServerError = false;

	private static Map<Integer, String> regradeMotives = null;
	/**
	 * @return the eMarkingVersion
	 */
	public static int geteMarkingVersion() {
		return eMarkingVersion;
	}

	/**
	 * @return the logger
	 */
	public static Logger getLogger() {
		return logger;
	}

	/**
	 * @return the markerid
	 */
	public static int getMarkerId() {
		return markerId;
	}

	/**
	 * @return the markingType
	 */
	public static int getMarkingType() {
		return markingType;
	}

	/**
	 * @return the nodejspath
	 */
	public static String getNodejspath() {
		return nodeJsPath;
	}

	/**
	 * @return the sessKey
	 */
	public static String getSessKey() {
		return sessKey;
	}

	/**
	 * @return the collaborativefeatures
	 */
	public static boolean isChatEnabled() {
		return chatEnabled;
	}

	/**
	 * @return the coloredRubric
	 */
	public static boolean isColoredRubric() {
		return coloredRubric;
	}

	/**
	 * @return the markerAnonymous
	 */
	public static boolean isMarkerAnonymous() {
		return markerAnonymous;
	}

	/**
	 * @return the ownDraft
	 */
	public static boolean isOwnDraft() {
		return ownDraft;
	}

	/**
	 * @return the readonly
	 */
	public static boolean isReadonly() {
		return readonly;
	}

	/**
	 * @return the showRubricOnLoad
	 */
	public static boolean isShowRubricOnLoad() {
		return showRubricOnLoad;
	}

	/**
	 * @return the studentAnonymous
	 */
	public static boolean isStudentAnonymous() {
		return studentAnonymous;
	}

	/**
	 * @return the supervisor
	 */
	public static boolean isSupervisor() {
		return supervisor;
	}

	/**
	 * @return the manageDelphi
	 */
	public static boolean getUserCanManageDelphi() {
		return manageDelphi;
	}

	/**
	 * Reads the configuration from a JSON obtained map
	 * @param value
	 * @throws Exception
	 */
	public static void loadConfiguration(Map<String, String> value) throws Exception {

		// Assign Moodle session key
		logger.fine(value.get("sesskey"));
		sessKey = value.get("sesskey");

		// Assign if the user can manage delphi process
		manageDelphi = value.get("managedelphi").equals("true");
		
		// Assign Moodle session key
		logger.fine(value.get("adminemail"));
		administratorEmail = value.get("adminemail");

		// Assign if the student is anonymous
		logger.fine(value.get("studentanonymous"));
		studentAnonymous = value.get("studentanonymous").equals("true");

		// Assign if the marker is anonymous
		logger.fine(value.get("markeranonymous"));
		markerAnonymous =  value.get("markeranonymous").equals("true");

		// Assign if the marking is readonly
		logger.fine(value.get("readonly"));
		readonly = (value.get("readonly") != null && value.get("readonly").equals("true"));

		// Assign if the user is supervisor
		logger.fine(value.get("supervisor"));
		supervisor = (value.get("supervisor") != null && value.get("supervisor").equals("true"));

		// Gets the user id of the person in front of the interface
		logger.fine(value.get("user"));
		markerId = Integer.parseInt(value.get("user"));

		// Gets the version of the Moodle module
		logger.fine(value.get("version"));
		eMarkingVersion = Integer.parseInt(value.get("version"));

		// Indicates if the user owns the current submission
		logger.fine(value.get("student"));
		ownDraft = markerId == Integer.parseInt(value.get("student"));

		// Read the marking type
		logger.fine(value.get("markingtype"));
		markingType = Integer.parseInt(value.get("markingtype"));

		// Link rubric colors if configured as
		logger.fine(value.get("linkrubric"));
		coloredRubric = value.get("linkrubric").equals("1");

		// Collaborative features (chat, wall) if configured as
		logger.fine(value.get("collaborativefeatures"));
		chatEnabled = value.get("collaborativefeatures").equals("1");

		// Obtain the nodejs path from Moodle configuration
		logger.fine(value.get("nodejspath"));
		nodeJsPath = value.get("nodejspath");
		
		logger.fine(value.get("motives"));
		JSONObject obj = new JSONObject(JsonUtils.safeEval(value.get("motives")));
		List<Map<String, String>> motives = AjaxRequest.getValuesFromResult(obj);
		
		regradeMotives = new TreeMap<Integer, String>();
		
		for(Map<String,String> motive : motives) {
			int motiveid = Integer.parseInt(motive.get("id"));
			String motiveName = motive.get("description");
			logger.fine(motiveid + " " + motiveName);
			regradeMotives.put(motiveid, motiveName);
		}
		

		logger.fine("---------- E-Marking configuration -----------" +
				"\nStudent anonymous:" + studentAnonymous + 
				"\nMarkeranonymous:" + markerAnonymous + 
				"\nRead only: " + readonly +
				"\nMarking type: " + markingType +
				"\nMarker Id: " + markerId +
				"\nSupervisor: " + supervisor +
				"\nColored rubric: " + coloredRubric);
	}

	/**
	 * @param collaborativefeatures the collaborativefeatures to set
	 */
	public static void setChatEnabled(boolean chatEnabled) {
		EMarkingConfiguration.chatEnabled = chatEnabled;
	}

	/**
	 * @param showRubricOnLoad the showRubricOnLoad to set
	 */
	public static void setShowRubricOnLoad(boolean showRubricOnLoad) {
		EMarkingConfiguration.showRubricOnLoad = showRubricOnLoad;
	}

	/**
	 * @return the administratorEmail
	 */
	public static String getAdministratorEmail() {
		return administratorEmail;
	}

	/**
	 * An array to show all regrade motives in e-marking
	 * @return Map with motive ID and description
	 */
	public static Map<Integer, String> getRegradeMotives() {
		return regradeMotives;
	}
	
	/**
	 * @return the chatServerError
	 */
	public static boolean isChatServerError() {
		return chatServerError;
	}

	/**
	 * @param chatServerError the chatServerError to set
	 */
	public static void setChatServerError(boolean chatServerError) {
		EMarkingConfiguration.chatServerError = chatServerError;
	}

	/**
	 * @return the moodleUrl
	 */
	public static String getMoodleUrl() {
		return moodleUrl;
	}

	/**
	 * @param moodleUrl the moodleUrl to set
	 */
	public static void setMoodleUrl(String _moodleUrl) {
		moodleUrl = _moodleUrl;
	}
}
