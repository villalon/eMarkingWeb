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

import cl.uai.client.EMarkingWeb;
import cl.uai.client.resources.Resources;
import cl.uai.client.utils.Color;

/**
 * 
 * @author JoseMerino <jomerinog@gmail.com>
 *
 */
public class PathMark extends Mark{

	public String getPath() {
		return path;
	}

	protected String path;
	
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
			String path,
			long timecreated,
			int criterionid,
			String markername) {
		super(id, posx, posy, pageno, markerid, timecreated, criterionid, markername, data);
		this.format = 5;
		this.width = width;
		this.height = height;
		this.path = path;
		this.rawtext = data;
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
			String pathData = markMap.get("path");
			String M = pathData.substring(1, pathData.indexOf("l", 0));
			float startx = Float.parseFloat(M.split(" ")[0]);
			float starty = Float.parseFloat(M.split(" ")[1]);
			int winwidth = Integer.parseInt(markMap.get("width")); 
			int winheight = Integer.parseInt(markMap.get("height"));
			int currentwidth = EMarkingWeb.markingInterface.getMarkingPagesInterface().getOffsetWidth();
			int currentheight = EMarkingWeb.markingInterface.getMarkingPagesInterface().getOffsetHeight();
			float scalex = ((float) winwidth / (float) currentwidth);
			float scaley = ((float) winheight / (float) currentheight);
			startx = startx / scalex;
			starty = starty / scaley;
			String newpath = "M" + startx + " "  +starty;
			int current = 0;
			for(String ss : pathData.split("l")) {
				if(current == 0) {
					current++;
					continue;
				}
				float prex = Float.parseFloat(ss.split(" ")[0]);
				float prey = Float.parseFloat(ss.split(" ")[1]);
				float x = prex / scalex;
				float y = prey / scaley;
				newpath += "l" + x + " " + y;
				current++;
			}
			pathData = newpath;

			pathobj = new PathMark(
					Integer.parseInt(markMap.get("id")), 
					Integer.parseInt(markMap.get("posx")), 
					Integer.parseInt(markMap.get("posy")), 
					Integer.parseInt(markMap.get("pageno")),
					Integer.parseInt(markMap.get("markerid")),
					Integer.parseInt(markMap.get("width")), 
					Integer.parseInt(markMap.get("height")),
					markMap.get("rawtext"),
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
				"<svg style=\"overflow:visible;width:10px;height:10px;\"><path title=\"" + this.rawtext + "" + this.markername + "\" style=\"stroke:" + color 
				+ "\" stroke-width=\"2\" fill=\"none\" d=\"" + this.path + "\"></path></svg>";

		this.setHTML(html);
	}
}
