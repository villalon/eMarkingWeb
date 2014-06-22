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

import java.util.Map;
import java.util.SortedMap;

/**
 * @author Jorge Villalón <villalon@gmail.com>
 *
 */
public class Criterion {

	private int id;
	private String description;
	private SortedMap<Integer, Level> levels;
	private int selectedLevel = 0;
	private float maxscore;
	private int regradeid = 0;
	private boolean markerIsAssigned = true;
	public boolean isMarkerIsAssigned() {
		return markerIsAssigned;
	}
	
	private String regradeComment;
	public String getRegradeComment() {
		return regradeComment;
	}

	public String getRegradeMarkerComment() {
		return regradeMarkerComment;
	}

	private String regradeMarkerComment;

	public void setMarkerIsAssigned(boolean markerIsAssigned) {
		this.markerIsAssigned = markerIsAssigned;
	}

	public int getRegradeid() {
		return regradeid;
	}

	public void setRegradeid(int regradeid) {
		this.regradeid = regradeid;
	}

	public int getRegradeaccepted() {
		return regradeaccepted;
	}

	public void setRegradeaccepted(int regradeaccepted) {
		this.regradeaccepted = regradeaccepted;
	}

	private int regradeaccepted = 0;
	
	/**
	 * @return the maxscore
	 */
	public float getMaxscore() {
		return maxscore;
	}

	/**
	 * @return the selectedLevel
	 */
	public Level getSelectedLevel() {
		if(levels.containsKey(selectedLevel))
			return levels.get(selectedLevel);
		else
			return null;
	}
	
	/**
	 * Calculates a Hue value for HSL colors depending on the score
	 * of the level and the maximum possible (it also includes the bonus)
	 * 
	 * @return a hue value between 0 and 120
	 */
	public int getHue() {
		Level lvl = getSelectedLevel();
		return this.getPercentForLevel(lvl);
	}


	/**
	 * @param selectedLevel the selectedLevel to set
	 */
	public void setSelectedLevel(int levelid) {
		this.selectedLevel = 0;
		for(Level lvl : levels.values()) {
			if(lvl.getId() == levelid) {
				this.selectedLevel = levelid;				
			}
		}
	}
	
	public int getPercentForLevel(Level lvl){
		if(lvl == null)
			return 0;
		
		float percent = (lvl.getScore() + lvl.getBonus()) / maxscore;
		float hue = percent * 120;
		
		return (int) hue;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param id
	 * @param description
	 */
	public Criterion(int id, String description, float maxscore, int regradeid, int regradeaccepted, SortedMap<Integer, Level> lvls) {
		super();
		this.id = id;
		this.description = description;
		this.levels = lvls;
		this.maxscore = maxscore;
		this.regradeid = regradeid;
		this.regradeaccepted = regradeaccepted;
	}

	/**
	 * @return the levels
	 */
	public Map<Integer, Level> getLevels() {
		return levels;
	}
	
	/**
	 * Get the bonus from the selected level
	 * 
	 * @return
	 */
	public float getBonus() {
		Level lvl = getSelectedLevel();
		if(lvl == null)
			return 0;
		return lvl.getBonus();
	}
	
	/**
	 * Sets the bonus for the selected level and sets 0 to all siblings
	 * @param bonus
	 */
	public void setBonus(float bonus) {
		for(Level lvl : levels.values()) {
			if(lvl.getId() == selectedLevel) {
				lvl.setBonus(bonus);
			} else {
				lvl.setBonus(0);
			}
		}
	}

	public void setRegradeComment(String regradecomment) {
		this.regradeComment = regradecomment;
	}

	public void setRegradeMarkerComment(String regrademarkercomment) {
		this.regradeMarkerComment = regrademarkercomment;
	}
}
