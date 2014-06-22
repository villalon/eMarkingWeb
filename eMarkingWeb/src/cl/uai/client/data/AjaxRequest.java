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
package cl.uai.client.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import cl.uai.client.MarkingInterface;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * This class represents an ajax request to a Moodle installation
 * 
 * @author Jorge Villalón
 *
 */
public class AjaxRequest {
	
	private static Logger logger = Logger.getLogger(AjaxRequest.class.getName());
	
	/** Moodle base ajax url **/
	public static String moodleUrl = null;

	/**
	 * Performs a request to Moodle
	 * 
	 * @param params
	 * @param callback
	 */
	public static void ajaxRequest(String params, AsyncCallback<AjaxData> callback) {
		
		final String url = moodleUrl + "?ids=" + MarkingInterface.getSubmissionId() + "&" + params;
		
		logger.fine(url);

		JsonpRequestBuilder requestBuilder = new JsonpRequestBuilder();

		requestBuilder.setTimeout(30000);
		requestBuilder.setCallbackParam("callback");
		requestBuilder.requestObject(url, callback);		
	}
	
	/**
	 * Assuming a json object, parses the result and returns as a Hash
	 * @param result
	 * @return a Hash with key value pairs (all Strings)
	 */
	public static Map<String, String> getValueFromResult(AjaxData result) {
		JSONObject values = new JSONObject(result.getValues());
		Map<String, String> output = new HashMap<String,String>();
		for(String key2 : values.keySet()) {
			if(values.get(key2) != null && values.get(key2).isString() != null)
				output.put(key2, values.get(key2).isString().stringValue());
			else
				output.put(key2, values.get(key2).toString());
		}
		return output;
	}
	
	/**
	 * Assuming a json array of json objects, it transforms them in a list of Hashes
	 * @param result the result to parse
	 * @return a List of Hash with String key value pairs
	 */
	public static List<Map<String, String>> getValuesFromResult(AjaxData result) {
		JSONObject values = new JSONObject(result.getValues());
		return getValuesFromResult(values);
	}
	
	/**
	 * Assuming a json array of json objects, it transforms them in a list of Hashes
	 * @param values the result to parse
	 * @return a List of Hash with String key value pairs
	 */
	public static List<Map<String, String>> getValuesFromResult(JSONObject values) {
		List<Map<String, String>> output = new ArrayList<Map<String,String>>();
		for(String key : values.keySet()) {
			JSONValue student = values.get(key);
			JSONObject starr = student.isObject();
			Map<String, String> obj = new HashMap<String, String>();
			for(String key2 : starr.keySet()) {
				if(starr.get(key2) != null && starr.get(key2).isString() != null)
					obj.put(key2, starr.get(key2).isString().stringValue());
				else
					obj.put(key2, starr.get(key2).toString());
			}
			output.add(obj);
		}
		return output;	
	}
}
