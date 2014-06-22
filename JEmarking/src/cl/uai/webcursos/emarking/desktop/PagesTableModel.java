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
/**
 * 
 */
package cl.uai.webcursos.emarking.desktop;

import javax.swing.table.DefaultTableModel;

/**
 * @author Jorge Villalon
 *
 */
public class PagesTableModel extends DefaultTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7215867738204049050L;

	public PagesTableModel(Object[][] data, String[] headers) {
		super(data, headers);
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
}
