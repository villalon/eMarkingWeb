/**
 * 
 */
package cl.uai.client;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Class representing an EMarking interface configuration
 * 
 * @author Jorge Villalón
 *
 */
public class EMarkingConfiguration {
	
	/** For logging purposes **/
	private static Logger logger = Logger.getLogger(EMarkingConfiguration.class.getName());
	
	/** eMarking version according to Moodle for debugging information **/
	private static int eMarkingVersion = 0;

	/** Indicates if the marking interface will include student anonymous information **/
	private static boolean studentAnonymous = true;

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

	/**
	 * @param coloredRubric the coloredRubric to set
	 */
	public static void setColoredRubric(boolean coloredRubric) {
		EMarkingConfiguration.coloredRubric = coloredRubric;
	}

	/** The marking type (normal, markers training, etc) **/
	private static int markingType = 0;

	/** If the chat is enabled **/
	private static boolean chatEnabled = false;

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
	 * Reads the configuration from a JSON obtained map
	 * @param value
	 * @throws Exception
	 */
	public static void readConfiguration(Map<String, String> value) throws Exception {

		// Assign Moodle session key
		sessKey = value.get("sesskey");

		// Assign if the student is anonymous
		studentAnonymous = value.get("studentanonymous").equals("true");

		// Assign if the marker is anonymous
		markerAnonymous =  value.get("markeranonymous").equals("true");

		// Assign if the assignment is anonymous
		readonly = (value.get("hascapability") != null && value.get("hascapability").equals("false"));

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
		coloredRubric = value.get("linkrubric").equals("1");

		// Collaborative features (chat, wall) if configured as
		chatEnabled = value.get("collaborativefeatures").equals("1");

		// Obtain the nodejs path from Moodle configuration
		nodeJsPath = value.get("nodejspath");

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
}
