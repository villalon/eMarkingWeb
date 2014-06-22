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
package cl.uai.client;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.VetoDragException;

/**
 * Handler that manages Drag and Drop on the whole marking interface
 * 
 * @author Jorge Villalón <villalon@gmail.com>
 *
 */
public class MarkingInterfaceDragHandler implements DragHandler {

	/**
	 * 
	 */
	public MarkingInterfaceDragHandler() {
	}

	/* (non-Javadoc)
	 * @see com.allen_sauer.gwt.dnd.client.DragHandler#onDragEnd(com.allen_sauer.gwt.dnd.client.DragEndEvent)
	 */
	@Override
	public void onDragEnd(DragEndEvent event) {
	}

	/* (non-Javadoc)
	 * @see com.allen_sauer.gwt.dnd.client.DragHandler#onDragStart(com.allen_sauer.gwt.dnd.client.DragStartEvent)
	 */
	@Override
	public void onDragStart(DragStartEvent event) {
	}

	/* (non-Javadoc)
	 * @see com.allen_sauer.gwt.dnd.client.DragHandler#onPreviewDragEnd(com.allen_sauer.gwt.dnd.client.DragEndEvent)
	 */
	@Override
	public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
	}

	/* (non-Javadoc)
	 * @see com.allen_sauer.gwt.dnd.client.DragHandler#onPreviewDragStart(com.allen_sauer.gwt.dnd.client.DragStartEvent)
	 */
	@Override
	public void onPreviewDragStart(DragStartEvent event)
			throws VetoDragException {
	}
}
