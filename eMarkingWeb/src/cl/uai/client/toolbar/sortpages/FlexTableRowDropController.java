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

import cl.uai.client.resources.Resources;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractPositioningDropController;
import com.allen_sauer.gwt.dnd.client.util.CoordinateLocation;
import com.allen_sauer.gwt.dnd.client.util.DOMUtil;
import com.allen_sauer.gwt.dnd.client.util.Location;
import com.allen_sauer.gwt.dnd.client.util.LocationWidgetComparator;
import com.allen_sauer.gwt.dnd.client.util.WidgetLocation;

/**
 * Allows one or more table rows to be dropped into an existing table.
 */
public final class FlexTableRowDropController extends AbstractPositioningDropController {

  private FlexTable flexTable;

  private InsertPanel flexTableRowsAsIndexPanel = new InsertPanel() {

    @Override
    public void add(Widget w) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Widget getWidget(int index) {
      return flexTable.getWidget(index, 0);
    }

    @Override
    public int getWidgetCount() {
      return flexTable.getRowCount();
    }

    @Override
    public int getWidgetIndex(Widget child) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void insert(Widget w, int beforeIndex) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(int index) {
      throw new UnsupportedOperationException();
    }
  };

  private Widget positioner = null;

  private int targetRow;

  public FlexTableRowDropController(FlexTable flexTable) {
    super(flexTable);
    this.flexTable = flexTable;
  }

  @Override
  public void onDrop(DragContext context) {
    FlexTableRowDragController trDragController = (FlexTableRowDragController) context.dragController;
    FlexTableUtil.moveRow(trDragController.getDraggableTable(), flexTable,
        trDragController.getDragRow(), targetRow + 1);
    super.onDrop(context);
  }

  @Override
  public void onEnter(DragContext context) {
    super.onEnter(context);
    positioner = newPositioner(context);
  }

  @Override
  public void onLeave(DragContext context) {
    positioner.removeFromParent();
    positioner = null;
    super.onLeave(context);
  }

  @Override
  public void onMove(DragContext context) {
    super.onMove(context);
    targetRow = DOMUtil.findIntersect(flexTableRowsAsIndexPanel, new CoordinateLocation(
        context.mouseX, context.mouseY), LocationWidgetComparator.BOTTOM_HALF_COMPARATOR) - 1;

    if (flexTable.getRowCount() > 0) {
      Widget w = flexTable.getWidget(targetRow == -1 ? 0 : targetRow, 0);
      Location widgetLocation = new WidgetLocation(w, context.boundaryPanel);
      Location tableLocation = new WidgetLocation(flexTable, context.boundaryPanel);
      context.boundaryPanel.add(positioner, tableLocation.getLeft(), widgetLocation.getTop()
          + (targetRow == -1 ? 0 : w.getOffsetHeight()));
    }
  }

  Widget newPositioner(DragContext context) {
    Widget p = new SimplePanel();
    p.addStyleName(Resources.INSTANCE.css().sortpagespositioner());
    p.setPixelSize(flexTable.getOffsetWidth(), 1);
    return p;
  }
}