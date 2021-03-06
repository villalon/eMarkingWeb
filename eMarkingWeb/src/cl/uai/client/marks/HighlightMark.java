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


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.vaadin.gwtgraphics.client.shape.Path;

import cl.uai.client.EMarkingConfiguration;
import cl.uai.client.EMarkingWeb;
import cl.uai.client.resources.Resources;
import cl.uai.client.utils.Color;

/**
 * 
 * @author JoseMerino <jomerinog@gmail.com>
 *
 */
public class HighlightMark extends PathMark{
	
	/** Size of the highlighter **/
	public static int size = 18;

	public Point getEnd() {
		return end;
	}

	public void setEnd(Point end) {
		this.end = end;
	}

	private Point end;

	/**
	 * Creates a relative PathMark object at a specific position in a page
	 * @param posx X coordinate in the page
	 * @param posy Y coordinate in the page
	 * @param pageno the page number (1 to N)
	 * @param data the path data
	 */
	public HighlightMark(
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
		super(id, posx, posy, pageno, markerid, width, height, data, path, timecreated, criterionid, markername);
		this.format = 7;
		this.width = width;
		this.height = height;
		this.path = path;
		this.rawtext = data;
		this.setWidth("1px");
		this.setHeight("1px");
		this.setStylePrimaryName(Resources.INSTANCE.css().pathmark());
		
		HighlightMark.size = EMarkingConfiguration.getHighlighterSize();
	}

	/**
	 * Creates a PathMark from a Hash with Strings as key value pairs,
	 * parsing the values in the map and casting them to the proper
	 * types
	 * 
	 * @param mark the Hash
	 * @return a PathMark object
	 */
	public static HighlightMark createFromMap(Map<String, String> markMap) {
		HighlightMark pathobj = null;

		try {
			String pathData = markMap.get("path");
			int posx = Integer.parseInt(markMap.get("posx"));
			int posy = Integer.parseInt(markMap.get("posy"));
			int endx = Integer.parseInt(pathData.split(",")[0]);
			int endy = Integer.parseInt(pathData.split(",")[1]);
			logger.fine("Adding from map. pos:"+markMap.get("posx")+","+markMap.get("posy")+" end:"+ endx + "," + endy);
			// Singleline
			if(posy == endy && posx > endx) {
				logger.fine("Single line, swapping.");
				int tmp = posx;
				posx = endx;
				endx = tmp;
			}
			Point start = new Point(posx, 0);
			Point end = new Point(endx, endy);

			logger.fine("Adding from map: "+markMap.get("posx")+","+markMap.get("posy")+" start:"+start+" end:"+ end);
			pathobj = new HighlightMark(
					Integer.parseInt(markMap.get("id")), 
					posx, 
					posy, 
					Integer.parseInt(markMap.get("pageno")),
					Integer.parseInt(markMap.get("markerid")),
					Integer.parseInt(markMap.get("width")), 
					Integer.parseInt(markMap.get("height")),
					markMap.get("rawtext"),
					markMap.get("path"),
					Long.parseLong(markMap.get("timecreated")), 
					Integer.parseInt(markMap.get("criterionid")),
					markMap.get("markername")
					);
			pathobj.setEnd(end);
			pathobj.setMarkHTML();
		} catch (Exception e) {
			e.printStackTrace();
			logger.severe(e.getMessage());
		}

		return pathobj;
	}

	public static Path createPath(Point start, int criterionid) {
		String color = "yellow";
		if(criterionid > 0) {
			color = Color.getCSSHueColor(criterionid);
		}

		Path currentPath = new Path(start.getX(), start.getY());
		currentPath.setFillOpacity(0);
		currentPath.setFillColor(color);
		currentPath.setStrokeWidth(size);
		currentPath.setStrokeOpacity(0.25);
		currentPath.setStrokeColor("yellow");
		
		return currentPath;
	}

	@Override
	public void setMarkHTML() {
		
		int width = EMarkingWeb.markingInterface.getMarkingPagesInterface().getOffsetWidth();
		Point newEnd = new Point(Math.abs(this.posx - end.getX()), Math.abs(this.posy - end.getY()));
		List<Point> points = calculatePath(new Point(0, 0), newEnd, width, false);
		Path path = null;
		for(int i=0; i<points.size(); i++) {
			int x = points.get(i).getX();
			int y = points.get(i).getY();
			if(i%2==0) {
				if(i==0) {
					path = new Path(x, y);
				} else {
					path.moveTo(x, y);
				}
			} else {
				path.lineTo(x, y);
			}
		}

		String color = "yellow";
		if(this.criterionid > 0) {
			color = Color.getCSSHueColor(criterionid);
		}

		
		String html = 
				"<svg width=\"1\" height=\"" + size+ "\" style=\"overflow:visible;opacity:0.25;\"><path title=\"" + this.rawtext + "" + this.markername + "\" style=\"stroke:" + color 
				+ "\" stroke-width=\"18\" fill=\"none\" d=\"" + path.getElement().getAttribute("d") + "\"></path></svg>";

		this.setHTML(html);
	}
	
	@Override
	protected String getMarkPopupHTML() {
		String html = super.getMarkPopupHTML();
		if(EMarkingConfiguration.isDebugging()) {
			html += "<br/>" + this.posx + "," + this.posy + " -> " + this.end;
		}
		return html;
	}
	
	public static List<Point> calculatePath(Point start, Point end, int width, boolean fullpage) {
		List<Point> points = new ArrayList<Point>();
		int deltaY = start.getY() - end.getY();
		int steps = (int) Math.abs(((float) deltaY / (float) size)) + 1;
		if(steps <= 1) {
			points.add(new Point(start.getX(), start.getY()));
			points.add(new Point(end.getX(), end.getY()));
		} else if(deltaY > 0){
			points.add(new Point(start.getX(), start.getY()));
			points.add(new Point(fullpage ? 0 : end.getX(), start.getY()));
		} else {
			points.add(new Point(start.getX(), start.getY()));
			points.add(new Point(fullpage ? width : end.getX(), start.getY()));
		}
		if(deltaY % size != 0) {
			steps++;
		}
		for(int i=2; i<steps; i++) {
			points.add(new Point(fullpage ? 0 : start.getX(), start.getY() - (deltaY / Math.abs(deltaY)) * size * (i-1)));
			points.add(new Point(fullpage ? width : end.getX(), start.getY() - (deltaY / Math.abs(deltaY)) * size * (i-1)));
		}
		if(steps > 1) {
			if(deltaY <= 0) {
				points.add(new Point(end.getX(), end.getY()));
				points.add(new Point(fullpage ? 0 : end.getX(), end.getY()));				
			} else {
				points.add(new Point(end.getX(), end.getY()));
				points.add(new Point(fullpage ? width : end.getX(), end.getY()));				
			}
		}
		return points;
	}

}
