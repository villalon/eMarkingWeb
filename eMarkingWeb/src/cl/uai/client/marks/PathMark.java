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
			int posx,
			int posy,
			int pageno,
			int markerid,
			int width,
			int height,
			String data,
			long timecreated) {
		super(posx, posy, pageno, markerid, timecreated);
		this.format = 5;
		this.setRawtext(data);
		this.width = width;
		this.height = height;
		this.setWidth(width+"px");
		this.setHeight(height+"px");
		this.setStylePrimaryName(Resources.INSTANCE.css().pathmark());

	}


	@Override
	protected void setMarkHTML() {
		
		int svgheight = this.height<15?15:this.height;
		int svgwidth = this.width;
		
		String html = 
				"<svg  height=\""+svgheight+"\" width=\""+ svgwidth +"\"  style=\"overflow:visible;\"><path stroke=\"red\" stroke-width=\"2\" fill=\"none\" d=\""+this.getRawtext()+"\"></path></svg>";
		this.setHTML(html);
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
				Integer.parseInt(markMap.get("posx")), 
				Integer.parseInt(markMap.get("posy")), 
				Integer.parseInt(markMap.get("pageno")),
				Integer.parseInt(markMap.get("markerid")),
				Integer.parseInt(markMap.get("width")), 
				Integer.parseInt(markMap.get("height")), 
				pathData,
				Long.parseLong(markMap.get("timecreated"))
				);

		pathobj.setId(Integer.parseInt(markMap.get("id"))); 
		pathobj.setRawtext(markMap.get("rawtext"));
		pathobj.setMarkername(markMap.get("markername"));
		} catch (Exception e) {
			e.printStackTrace();
			logger.severe(e.getMessage());
		}
		
		return pathobj;
	}

}
