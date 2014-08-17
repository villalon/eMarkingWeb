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
package cl.uai.webcursos.emarking.desktop;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import cl.uai.webcursos.emarking.desktop.data.Moodle;
import cl.uai.webcursos.emarking.desktop.data.Student;

public class StudentsTableCellRenderer extends DefaultTableCellRenderer {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3207417568718607073L;
	private Moodle moodle;
	
	public StudentsTableCellRenderer(Moodle _moodle) {
		this.moodle = _moodle;
	}
	
	@Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if(isSelected)
        	c.setBackground(new Color(Integer.parseInt("BE",16), Integer.parseInt("7B",16), Integer.parseInt("E8",16)));
        else {
            c.setBackground(Color.WHITE);
        	Student student = this.moodle.getStudentByRowNumber(row);
        	if(student != null) {
        		if(student.getPages() == 0) {
        			c.setBackground(new Color(Integer.parseInt("FF",16), Integer.parseInt("38",16), Integer.parseInt("10",16)));        			
        		} else if(this.moodle.getPages().isNumberOutlier(student.getPages())) {
        			c.setBackground(new Color(Integer.parseInt("E8",16), Integer.parseInt("C0",16), Integer.parseInt("43",16)));
        		}
        	}
        }
        return c;
    }
}
