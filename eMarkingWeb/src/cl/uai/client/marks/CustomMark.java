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
package cl.uai.client.marks;

import java.util.Map;

import cl.uai.client.MarkingInterface;
import cl.uai.client.resources.Resources;

/**
 * 
 * @author Jorge Villalón <villalon@gmail.com>
 *
 */
public class CustomMark extends Mark {
	
	private String title;
	
	/**
	 * Creates a CheckMark object at a specific position in a page
	 * @param posx X coordinate in the page
	 * @param posy Y coordinate in the page
	 * @param pageno the page number (1 to N)
	 */
	public CustomMark(
			String title,
			int posx,
			int posy,
			int pageno,
			int markerid,
			long timecreated,
			String colour) {
		super(posx, posy, pageno, markerid, timecreated, colour);
		
		this.format = 1000;
		this.title = title;

		this.addStyleName(Resources.INSTANCE.css().checkmark());
	}

	@Override
	public void setMarkHTML() {
		String html = "<div class=\""+Resources.INSTANCE.css().innercomment()+" "+ MarkingInterface.getMapCss().get(colour) +"\">"+this.getTitle()+"</div>";
		this.setHTML(html);		
	}
	
	/**
	 * Creates a CheckMark from a Hash with Strings as key value pairs,
	 * parsing the values in the map and casting them to the proper
	 * types
	 * 
	 * @param mark the Hash
	 * @return a CheckMark object
	 */
	public static CustomMark createFromMap(Map<String, String> markMap) {
		String title = markMap.get("rawtext").split(":")[0];
		
		CustomMark commentobj = new CustomMark(
				title,
				Integer.parseInt(markMap.get("posx")), 
				Integer.parseInt(markMap.get("posy")), 
				Integer.parseInt(markMap.get("pageno")),
				Integer.parseInt(markMap.get("markerid")),
				Long.parseLong(markMap.get("timecreated")),
				String.valueOf(markMap.get("colour")));

		commentobj.setId(Integer.parseInt(markMap.get("id"))); 
		commentobj.setRawtext(markMap.get("rawtext"));
		commentobj.setMarkername(markMap.get("markername"));

		return commentobj;
	}
	
	public String getTitle() {
		return this.title;
	}
}
