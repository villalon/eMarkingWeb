/*******************************************************************************
 * This file is part of Moodle - http://moodle.org/
 * 
 * Moodle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Moodle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Moodle.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @package cl.uai.webcursos.emarking
 * @copyright 2014 Jorge Villalón {@link http://www.villalon.cl}
 * @license   http://www.gnu.org/copyleft/gpl.html GNU GPL v3 or later
 ******************************************************************************/
package cl.uai.webcursos.emarking.desktop.data;

import java.util.Map;
import java.util.TreeMap;

public class Student {

	private int id;
	private String fullname;
	private String idnumber;
	private int rownumber;
	private int pages = 0;
	private TreeMap<String, String> answers;
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the fullname
	 */
	public String getFullname() {
		return fullname;
	}
	/**
	 * @param fullname the fullname to set
	 */
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	/**
	 * @return the idnumber
	 */
	public String getIdnumber() {
		return idnumber;
	}
	/**
	 * @param idnumber the idnumber to set
	 */
	public void setIdnumber(String idnumber) {
		this.idnumber = idnumber;
	}
	
	@Override
	public String toString() {
		return getFullname();
	}
	/**
	 * @return the rownumber
	 */
	public int getRownumber() {
		return rownumber;
	}
	/**
	 * @param rownumber the rownumber to set
	 */
	public void setRownumber(int rownumber) {
		this.rownumber = rownumber;
	}
	public void addPage(Page p) {
		this.setPages(this.getPages() + 1);
	}
	/**
	 * @return the pages
	 */
	public int getPages() {
		return pages;
	}
	/**
	 * @param pages the pages to set
	 */
	public void setPages(int pages) {
		this.pages = pages;
	}
	public void removePage(Page current) {
		this.setPages(this.getPages() - 1);
	}
	/**
	 * @return the answers
	 */
	public TreeMap<String, String> getAnswers() {
		return answers;
	}
	/**
	 * @param answers the answers to set
	 */
	public void setAnswers(TreeMap<String, String> answers) {
		this.answers = answers;
	}
	public String getAnswersValues() {
		String output = "";
		if(this.answers == null)
			return output;
		
		for(Map.Entry<String, String> entry : this.answers.entrySet()) {
			output += entry.getKey() + ":" + entry.getValue() + ";";
		}
		return output;
	}
}
