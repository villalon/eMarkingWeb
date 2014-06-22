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
package cl.uai.client.toolbar.sortpages;

import cl.uai.client.MarkingInterface;
import cl.uai.client.resources.Resources;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;

/**
 * Table to demonstrate draggable rows and columns.
 */
public final class SortPagesFlexTable extends FlexTable {

  /**
   * Creates a FlexTable with the desired number of rows and columns, making each row draggable via
   * the provided drag controller.
   * 
   * @param rows desired number of table rows
   * @param cols desired number of table columns
   * @param tableRowDragController the drag controller to enable dragging of table rows
   */
  public SortPagesFlexTable(int rows, int cols, FlexTableRowDragController tableRowDragController) {
    addStyleName(Resources.INSTANCE.css().sortpagesflextable());
    for (int row = 0; row < rows; row++) {
      HTML handle = new HTML(MarkingInterface.messages.PageNumber(row+1));
      handle.addStyleName(Resources.INSTANCE.css().sortpagesdraghandle());
      setWidget(row, 0, handle);
      tableRowDragController.makeDraggable(handle);
    }
  }
  
  public String[] getSortOrder() {
	  String[] output = new String[this.getRowCount()];
	  for(int i=0;i<output.length;i++) {
		  String html = this.getHTML(i, 0);
		  int start = html.indexOf(MarkingInterface.messages.Page());
		  start = html.indexOf(" ", start);
		  int end = html.indexOf("</div>", start);
		  html = html.substring(start, end);
		  int val = Integer.parseInt(html.trim());
		  output[i] = (val - 1)+"";
	  }
	  return output;
  }
}