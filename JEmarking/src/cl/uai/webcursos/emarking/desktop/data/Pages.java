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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import cl.uai.webcursos.emarking.desktop.EmarkingDesktop;
import cl.uai.webcursos.emarking.desktop.FixRowDialog;

/**
 * Class representing a list of pages with its corresponding
 * data for each page.
 * 
 * @author jorgevillalon
 *
 */
public class Pages extends Hashtable<Integer, Page> {

	private static final long serialVersionUID = -8592122074506658642L;
	private static Logger logger = Logger.getLogger(Pages.class);
	private Moodle moodle;
	private int max = -1;
	private int min = Integer.MAX_VALUE;
	private List<Integer> studentIds = new ArrayList<Integer>();
	private Statistics stats;

	public Pages(Moodle _moodle) {
		this.moodle = _moodle;
	}

	public Statistics getStats() {
		return this.stats;
	}
	
	public int getMaxPageNumber() {
		return max;
	}
	/**
	 * Fixes data for a particular page using a dialog for input
	 * 
	 * @param row
	 * @return true if successfull
	 * @throws Exception 
	 */
	public boolean fixPageData(int row, JFrame frame) throws Exception {

		// Validates that only even rows are selected when using doubleside
		if(this.moodle.getQrExtractor().isDoubleside() && row % 2 != 0) {
			JOptionPane.showMessageDialog(null, EmarkingDesktop.lang.getString("onlyevenrowsdoubleside"));
			return false;
		}

		// Gets the page and shows the dialog with its data
		Page current = this.get(row);

		FixRowDialog dialog = new FixRowDialog(moodle);
		dialog.getLblPageNumber().setText(Integer.toString(row+1));

		if(current.getCourse() != null) {
			dialog.getCoursesCombo().setSelectedItem(current.getCourse());
		}
		if(current.getStudent() != null) {
			dialog.getStudentsCombo().setSelectedItem(current.getStudent());
		}
		if(current.getPagenumber() > 0) {
			dialog.getComboPageNumber().setSelectedIndex(current.getPagenumber());
		}

		dialog.setModal(true);
		dialog.setLocationRelativeTo(frame);
		dialog.setVisible(true);

		if(dialog.isCancelled())
			return false;

		Page newpage = new Page(this.moodle);
		newpage.setStudent((Student)dialog.getStudentsCombo().getSelectedItem());
		newpage.setCourse((Course)dialog.getCoursesCombo().getSelectedItem());
		newpage.setPagenumber(dialog.getComboPageNumber().getSelectedIndex());

		renamePage(current, newpage, newpage.getPagenumber());

		return true;
	}

	public void fixFromPrevious(int row) throws Exception {
		if(this.moodle.getQrExtractor().isDoubleside() && row % 2 != 0) {
			throw new Exception("Invalid row number for fixing row in doubleside");
		}

		int minimum = this.moodle.getQrExtractor().isDoubleside() ? 2 : 1;

		if(row < minimum) {
			throw new Exception("There is no previous row to copy from");
		}

		Page previous = this.get(row-1);
		Page current = this.get(row);

		if(previous == null || current == null) {
			throw new Exception("There is no information for previous or current pages. This is a fatal error, please notify the author.");
		}

		if(previous.getStudent() == null || previous.getCourse() == null) {
			throw new Exception("There is no student or course information in the page you want to copy from");
		}

		renamePage(current, previous, previous.getPagenumber()+1);
	}

	public void fixFromFollowing(int row) throws Exception {
		if(this.moodle.getQrExtractor().isDoubleside() && row % 2 != 0) {
			throw new Exception("Invalid row number for fixing row in doubleside");
		}

		if(row > this.size() - 2){
			throw new Exception("There is no following row to copy from");
		}

		int gap = this.moodle.getQrExtractor().isDoubleside() ? 2 : 1;

		Page following = this.get(row + gap);
		Page current = this.get(row);

		if(following == null || current == null){
			throw new Exception("There is no information for previous or current pages. This is a fatal error, please notify the author.");
		}

		if(following.getStudent() == null || following.getCourse() == null) {
			throw new Exception("There is no student or course information in the page you want to copy from");
		}

		if(following.getPagenumber() <= 1) {
			throw new Exception("The page you want to precede is the first one");
		}

		renamePage(current, following, following.getPagenumber()-1);
	}

	private void renamePage(Page current, Page copyfrom, int newpagenumber) throws Exception {
		int row = current.getRow();

		if(this.max > 0 && newpagenumber > this.max) {
			logger.warn("Invalid page number, exceeds maximum");
		}

		String oldfilename = current.getFilename();
		File oldfile = new File(this.moodle.getQrExtractor().getTempdirStringPath() + "/" + oldfilename + Moodle.imageExtension);
		String newfilename = copyfrom.getStudent().getId() + "-" + copyfrom.getCourse().getId() + "-" + newpagenumber;
		File newfile = new File(this.moodle.getQrExtractor().getTempdirStringPath() + "/" + newfilename + Moodle.imageExtension);
		if(newfile.exists()) {
			throw new Exception("Invalid fix, page already exists!");
		}
		oldfile.renameTo(newfile);

		// Change the anonymous version too
		oldfile = new File(this.moodle.getQrExtractor().getTempdirStringPath() + "/" + oldfilename + "_a" + Moodle.imageExtension);
		oldfile.renameTo(new File(this.moodle.getQrExtractor().getTempdirStringPath() + "/" + newfilename + "_a" + Moodle.imageExtension));

		if(current.getStudent() != null) {
			current.getStudent().removePage(current);
		}
		copyfrom.getStudent().addPage(current);
		
		current.setStudent(copyfrom.getStudent());
		current.setCourse(copyfrom.getCourse());
		current.setPagenumber(newpagenumber);
		current.setFilename(newfilename);

		logger.debug("Changing " + oldfilename + " to " + newfilename);

		updateStats(current);

		if(this.moodle.getQrExtractor().isDoubleside()) {
			Page next = this.get(row+1);

			oldfilename = next.getFilename();
			oldfile = new File(this.moodle.getQrExtractor().getTempdirStringPath() + "/" + oldfilename + Moodle.imageExtension);
			newfilename += "b";
			newfile = new File(this.moodle.getQrExtractor().getTempdirStringPath() + "/" + newfilename + Moodle.imageExtension);
			if(newfile.exists()) {
				throw new Exception("Invalid fix, page already exists!");
			}
			oldfile.renameTo(newfile);

			// Change the anonymous version too
			oldfile = new File(this.moodle.getQrExtractor().getTempdirStringPath() + "/" + oldfilename + "_a" + Moodle.imageExtension);
			oldfile.renameTo(new File(this.moodle.getQrExtractor().getTempdirStringPath() + "/" + newfilename + "_a" + Moodle.imageExtension));

			if(next.getStudent() != null) {
				next.getStudent().removePage(next);
			}
			copyfrom.getStudent().addPage(next);
			
			next.setStudent(copyfrom.getStudent());
			next.setCourse(copyfrom.getCourse());
			next.setPagenumber(newpagenumber);
			next.setFilename(newfilename);

			logger.debug("Changing " + oldfilename + " to " + newfilename);			
			updateStats(next);
		}		
	}
	
	public boolean isNumberOutlier(int pagenumber) {
		
		if(this.stats == null)
			return false;
		
		int mean = (int) Math.rint(this.stats.getMean());
		int stdev = (int) Math.rint(this.stats.getStdDev());
		int minstat = mean - stdev;
		int maxstat = mean + stdev;
		
		return (pagenumber > maxstat || pagenumber < minstat);
	}

	public Object[] getRowData(int row) {
		Page p = this.get(row);
		if(p==null)
			return null;
		Object[] rowData = new Object[4];
		rowData[0] = p.getRow()+1;
		rowData[1] = p.getStudent() == null ? EmarkingDesktop.lang.getString("nn") : p.getStudent().getFullname();
		rowData[2] = p.getCourse() == null ? EmarkingDesktop.lang.getString("notfound") : p.getCourse().getFullname();
		rowData[3] = p.getPagenumber();
		return rowData;
	}

	public String getSummary() {
		String output = "";
		output += " " + EmarkingDesktop.lang.getString("pages") + ": " + this.keySet().size();
		output += " " + EmarkingDesktop.lang.getString("students") + ": " + studentIds.size() + "/" + this.moodle.getStudents().size();
		output += " " + EmarkingDesktop.lang.getString("pagesperstudent") + ":";
		DecimalFormat df = new DecimalFormat("#.##"); 
		if(this.stats != null) {
			output += " " + df.format(this.stats.getMean());
			output += " +- " + df.format(this.stats.getStdDev());
		}
		output += " [" + min + "-" + max + "]";
		return output;
	}
	
	@Override
	public synchronized Page put(Integer key, Page value) {
		
		updateStats(value);

		return super.put(key, value);	
	}
	
	private void updateStats(Page p) {
		if(p.getStudent() != null) {
			if(p.getStudent().getPages() > max) {
				max = p.getStudent().getPages();
			}
			if(p.getStudent().getPages() < min) {
				min = p.getStudent().getPages();
			}
			if(!studentIds.contains(p.getStudent().getId())) {
				studentIds.add(p.getStudent().getId());
			}
			double[] data = new double[studentIds.size()];
			int current = 0;
			for(int studentid : studentIds) {
				data[current] = this.moodle.getStudents().get(studentid).getPages();
				current++;
			}
			stats = new Statistics(data);
		}		
	}
}
