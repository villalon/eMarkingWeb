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

import java.io.File;

public class Page {
	private int row;
	private Student student = null;
	private Course course = null;
	private String problem = null;
	private String filename = null;
	private int pagenumber = -1;
	private Moodle moodle = null;
	private boolean rotated = false;

	public boolean isRotated() {
		return rotated;
	}
	public void setRotated(boolean rotated) {
		this.rotated = rotated;
	}
	public Page(Moodle _moodle){
		this.moodle = _moodle;
	}
	/**
	 * @return the pagenumber
	 */
	public int getPagenumber() {
		return pagenumber;
	}
	/**
	 * @param pagenumber the pagenumber to set
	 */
	public void setPagenumber(int pagenumber) {
		this.pagenumber = pagenumber;
	}
	/**
	 * @return the problem
	 */
	public String getProblem() {
		return problem;
	}
	/**
	 * @param problem the problem to set
	 */
	public void setProblem(String problem) {
		this.problem = problem;
	}
	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}
	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	/**
	 * @return the row
	 */
	public int getRow() {
		return row;
	}
	/**
	 * @param row the row to set
	 */
	public void setRow(int row) {
		this.row = row;
	}
	/**
	 * @return the student
	 */
	public Student getStudent() {
		return student;
	}
	/**
	 * @param student the student to set
	 */
	public void setStudent(Student student) {
		this.student = student;
	}
	/**
	 * @return the course
	 */
	public Course getCourse() {
		return course;
	}
	/**
	 * @param course the course to set
	 */
	public void setCourse(Course course) {
		this.course = course;
	}
	
	@Override
	public String toString() {
		return "Row:"+row+" Student:"+student+" Page number:"+pagenumber+" Course:"+course+" Filename:"+filename+" Output:"+problem;
	}
	
	/**
	 * Returns the file associated to this page
	 * @return
	 */
	public File getFile() {
		File file = new File(moodle.getQrExtractor().getTempdirStringPath() + "/" + this.filename + Moodle.imageExtension);
		return file;
	}
	
	public boolean isProblematic() {
		return this.course == null || this.student == null || this.pagenumber <= 0 || this.filename == null;
	}
}
