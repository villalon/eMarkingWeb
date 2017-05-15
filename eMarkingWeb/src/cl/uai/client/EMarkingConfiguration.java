/**
 * 
 */
package cl.uai.client;

import java.util.ArrayList;
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
	
	public static final int EMARKING_RUBRIC_SORT_LEVELS_ASCENDING = 1;
	public static final int EMARKING_RUBRIC_SORT_LEVELS_DESCENDING = 2;

	public static final int EMARKING_RUBRICMARK_ICON = 1;
	public static final int EMARKING_RUBRICMARK_TEXTBOX = 2;

	private static int rubricColorSaturation = 100;
	private static int rubricColorLightness = 85;
	
	private static int highlighterSize = 18;
		
	/** For logging purposes **/
	private static Logger logger = Logger.getLogger(EMarkingConfiguration.class.getName());
	
	/** eMarking version according to Moodle for debugging information **/
	private static int eMarkingVersion = 0;

	/** For messages including the admin's email **/
	private static String administratorEmail = null;

	/** Site wide marking buttons enabled **/
	private static List<Integer> markingButtonsEnabled = null;

	static {
		markingButtonsEnabled = new ArrayList<Integer>();
	}

	/** Indicates if the levels in the rubric should be sorted by score ascending or descending **/
	private static int rubricLevelsSorting = EMARKING_RUBRIC_SORT_LEVELS_ASCENDING;

	/** Indicates if the marking interface will include student anonymous information **/
	private static boolean studentAnonymous = true;
	
	private static boolean changeLogEnabled = false;
	
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

	/** String contains the keywords separated by comma **/
	private static String keywords;

	/** If colored rubric is enabled **/
	private static boolean coloredRubric = false;

	/** If colored rubric is forced **/
	private static boolean coloredRubricForced = false;

	/** The URL of the moodle installation with which EMarking is linked **/
	private static String moodleUrl = null;

	/** The marking type (normal, markers training, etc) **/
	private static int markingType = EMARKING_TYPE_PRINT_ONLY;
	
	/** If the chat is enabled **/
	private static boolean chatEnabled = false;

	/** If the chat server raised an error **/
	private static boolean chatServerError = false;

	private static Map<Integer, String> regradeMotives = null;

	/** If only formative feedback should be shown **/
	private static boolean formativeFeedbackOnly;
	
	private static int rubricMarkType = EMARKING_RUBRICMARK_ICON;
	/**
	 * @return the administratorEmail
	 */
	public static String getAdministratorEmail() {
		return administratorEmail;
	}

	/**
	 * @return the eMarkingVersion
	 */
	public static int geteMarkingVersion() {
		return eMarkingVersion;
	}

	public static int getHighlighterSize() {
		return highlighterSize;
	}

	/**
	 * @return the keywords
	 */
	public static String getKeywords() {
		return keywords;
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
	
	public static List<Integer> getMarkingButtonsEnabled() {
		return markingButtonsEnabled;
	}

	/**
	 * @return the markingType
	 */
	public static int getMarkingType() {
		return markingType;
	}

	/**
	 * @return the moodleUrl
	 */
	public static String getMoodleUrl() {
		return moodleUrl;
	}

	/**
	 * @return the nodejspath
	 */
	public static String getNodejspath() {
		return nodeJsPath;
	}

	/**
	 * An array to show all regrade motives in e-marking
	 * @return Map with motive ID and description
	 */
	public static Map<Integer, String> getRegradeMotives() {
		return regradeMotives;
	}
	
	public static int getRubricColorLightness() {
		return rubricColorLightness;
	}

	public static int getRubricColorSaturation() {
		return rubricColorSaturation;
	}

	public static int getRubricLevelsSorting() {
		return rubricLevelsSorting;
	}

	/**
	 * @return the sessKey
	 */
	public static String getSessKey() {
		return sessKey;
	}

	/**
	 * @return the manageDelphi
	 */
	public static boolean getUserCanManageDelphi() {
		return manageDelphi;
	}

	public static boolean isChangeLogEnabled() {
		return changeLogEnabled;
	}

	/**
	 * @return the collaborativefeatures
	 */
	public static boolean isChatEnabled() {
		return chatEnabled;
	}

	/**
	 * @return the chatServerError
	 */
	public static boolean isChatServerError() {
		return chatServerError;
	}

	/**
	 * @return the coloredRubric
	 */
	public static boolean isColoredRubric() {
		return coloredRubric;
	}

	public static boolean isColoredRubricForced() {
		return coloredRubricForced;
	}

	public static boolean isFormativeFeedbackOnly() {
		return formativeFeedbackOnly;
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
	 * Reads the configuration from a JSON obtained map
	 * @param value
	 * @throws Exception
	 */
	public static void loadConfiguration(Map<String, String> value) throws Exception {

		// Assign Moodle session key
		sessKey = value.get("sesskey");

		// Assign if the user can manage delphi process
		manageDelphi = value.get("managedelphi").equals("true");
		
		// Assign Moodle session key
		administratorEmail = value.get("adminemail");

		// Assign if the student is anonymous
		studentAnonymous = value.get("studentanonymous").equals("true");

		// Assign if the marker is anonymous
		markerAnonymous =  value.get("markeranonymous").equals("true");

		// Assign if the marking is readonly
		readonly = (value.get("readonly") != null && value.get("readonly").equals("true"));

		// Assign if the user is supervisor
		supervisor = (value.get("supervisor") != null && value.get("supervisor").equals("true"));

		// Gets the user id of the person in front of the interface
		markerId = Integer.parseInt(value.get("user"));

		// Gets the version of the Moodle module
		eMarkingVersion = Integer.parseInt(value.get("version"));

		// Indicates if the user owns the current submission
		ownDraft = markerId == Integer.parseInt(value.get("student"));

		// Read the marking type
		markingType = Integer.parseInt(value.get("markingtype"));

		// Link rubric colors if configured as
		coloredRubric = value.get("coloredrubric").equals("1");

		// If formative feedback is forced
		formativeFeedbackOnly = value.get("formativeonly").equals("1");

		// If formative feedback is forced
		changeLogEnabled = value.get("changelog").equals("10");

		// Link rubric colors if configured as
		coloredRubricForced = value.get("coloredrubricforced").equals("1");
		if(coloredRubricForced) {
			coloredRubric = true;
		}
		
		// Rubric levels sort order.
		rubricLevelsSorting = (value.get("rubriclevelsorting") != null && value.get("rubriclevelsorting").equals("2")) ? EMARKING_RUBRIC_SORT_LEVELS_DESCENDING : EMARKING_RUBRIC_SORT_LEVELS_ASCENDING;

		// Collaborative features (chat, wall) if configured as
		chatEnabled = value.get("collaborativefeatures").equals("1");

		// Obtain the nodejs path from Moodle configuration
		nodeJsPath = value.get("nodejspath");
		
		// Obtain the keyword for the feedback
		keywords = value.get("keywords");
		
		// What type of rubric marks to use.
		rubricMarkType = value.get("rubricmarktype") != null ? Integer.parseInt(value.get("rubricmarktype")) : EMARKING_RUBRICMARK_ICON;
		
		// Marking buttons enabled in the platform
		String[] buttons = value.get("buttons").split(",");
		for(int i=0;i<buttons.length;i++) {
			try {
				int buttonId = Integer.parseInt(buttons[i]);
				markingButtonsEnabled.add(buttonId);
			} catch (Exception e) {
				logger.severe("Invalid button id");
			}
		}
		
		JSONObject obj = new JSONObject(JsonUtils.safeEval(value.get("motives")));
		List<Map<String, String>> motives = AjaxRequest.getValuesFromResult(obj);
		
		regradeMotives = new TreeMap<Integer, String>();
		
		for(Map<String,String> motive : motives) {
			int motiveid = Integer.parseInt(motive.get("id"));
			String motiveName = motive.get("description");
			regradeMotives.put(motiveid, motiveName);
		}
		

		logger.fine("---------- E-Marking configuration -----------" +
				"\nStudent anonymous:" + studentAnonymous + 
				"\nMarkeranonymous:" + markerAnonymous + 
				"\nRead only: " + readonly +
				"\nMarking type: " + markingType +
				"\nMarker Id: " + markerId +
				"\nSupervisor: " + supervisor +
				"\nColored rubric: " + coloredRubric +
				"\nFormative feedback: " + formativeFeedbackOnly);
	}

	public static void setChangeLogEnabled(boolean changeLogEnabled) {
		EMarkingConfiguration.changeLogEnabled = changeLogEnabled;
	}

	/**
	 * @param collaborativefeatures the collaborativefeatures to set
	 */
	public static void setChatEnabled(boolean chatEnabled) {
		EMarkingConfiguration.chatEnabled = chatEnabled;
	}
	
	/**
	 * @param chatServerError the chatServerError to set
	 */
	public static void setChatServerError(boolean chatServerError) {
		EMarkingConfiguration.chatServerError = chatServerError;
	}

	/**
	 * @param coloredRubric the coloredRubric to set
	 */
	public static void setColoredRubric(boolean coloredRubric) {
		EMarkingConfiguration.coloredRubric = coloredRubric;
	}

	public static void setColoredRubricForced(boolean coloredRubricForced) {
		EMarkingConfiguration.coloredRubricForced = coloredRubricForced;
	}

	public static void setFormativeFeedbackOnly(boolean formativeFeedbackOnly) {
		EMarkingConfiguration.formativeFeedbackOnly = formativeFeedbackOnly;
	}

	public static void setHighlighterSize(int highlighterSize) {
		EMarkingConfiguration.highlighterSize = highlighterSize;
	}

	/**
	 * @param moodleUrl the moodleUrl to set
	 */
	public static void setMoodleUrl(String _moodleUrl) {
		moodleUrl = _moodleUrl;
	}

	public static void setRubricColorLightness(int rubricColorLightness) {
		EMarkingConfiguration.rubricColorLightness = rubricColorLightness;
	}

	public static void setRubricColorSaturation(int rubricColorSaturation) {
		EMarkingConfiguration.rubricColorSaturation = rubricColorSaturation;
	}

	public static void setRubricLevelsSorting(int _rubricLevelsSorting) {
		rubricLevelsSorting = _rubricLevelsSorting;
	}

	/**
	 * @param showRubricOnLoad the showRubricOnLoad to set
	 */
	public static void setShowRubricOnLoad(boolean showRubricOnLoad) {
		EMarkingConfiguration.showRubricOnLoad = showRubricOnLoad;
	}

	/**
	 * @return the rubricMarkType
	 */
	public static int getRubricMarkType() {
		return rubricMarkType;
	}

	/**
	 * @param rubricMarkType the rubricMarkType to set
	 */
	public static void setRubricMarkType(int rubricMarkType) {
		EMarkingConfiguration.rubricMarkType = rubricMarkType;
	}	
}
