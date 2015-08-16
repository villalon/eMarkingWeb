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
 * @copyright 2013 Jorge VillalÃ³n <villalon@gmail.com>
 * @license   http://www.gnu.org/copyleft/gpl.html GNU GPL v3 or later
 */
package cl.uai.client.marks;

import java.util.Map;

import cl.uai.client.resources.Resources;
import cl.uai.client.utils.Color;

/**
 * 
 * @author JoseMerino <jomerinog@gmail.com>
 *
 */
public class PathMark extends Mark{

	/**
	 * Creates a relative PathMark object at a specific position in a page
	 * @param posx X coordinate in the page
	 * @param posy Y coordinate in the page
	 * @param pageno the page number (1 to N)
	 * @param data the path data
	 */
	public PathMark(
			int id,
			int posx,
			int posy,
			int pageno,
			int markerid,
			int width,
			int height,
			String data,
			long timecreated,
			int criterionid,
			String markername) {
		super(id, posx, posy, pageno, markerid, timecreated, criterionid, markername, data);
		this.format = 5;
		this.width = width;
		this.height = height;
		this.setWidth(width+"px");
		this.setHeight(height+"px");
		this.setStylePrimaryName(Resources.INSTANCE.css().pathmark());

	}

	/**
	 * Creates a PathMark from a Hash with Strings as key value pairs,
	 * parsing the values in the map and casting them to the proper
	 * types
	 * 
	 * @param mark the Hash
	 * @return a PathMark object
	 */
	public static PathMark createFromMap(Map<String, String> markMap) {
		PathMark pathobj = null;

		try {
			String pathData = markMap.get("rawtext");

			pathobj = new PathMark(
					Integer.parseInt(markMap.get("id")), 
					Integer.parseInt(markMap.get("posx")), 
					Integer.parseInt(markMap.get("posy")), 
					Integer.parseInt(markMap.get("pageno")),
					Integer.parseInt(markMap.get("markerid")),
					Integer.parseInt(markMap.get("width")), 
					Integer.parseInt(markMap.get("height")), 
					pathData,
					Long.parseLong(markMap.get("timecreated")), 
					Integer.parseInt(markMap.get("criterionid")),
					markMap.get("markername")
					);
		} catch (Exception e) {
			e.printStackTrace();
			logger.severe(e.getMessage());
		}

		return pathobj;
	}

	@Override
	public void setMarkHTML() {

		String color = "red";
		if(this.criterionid > 0) {
			color = Color.getCSSHueColor(criterionid);
		}

		String html = 
				"<svg style=\"overflow:visible;\"><path title=\"" + this.markername + "\" style=\"stroke:" + color 
				+ "\" stroke-width=\"2\" fill=\"none\" d=\"" + this.rawtext + "\"></path></svg>";

		this.setHTML(html);
	}
}
