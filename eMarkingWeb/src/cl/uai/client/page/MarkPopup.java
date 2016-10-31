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
package cl.uai.client.page;

import java.util.logging.Logger;

import cl.uai.client.resources.Resources;

import com.google.gwt.user.client.ui.HTML;

/**
 * A MarkPopup is  a popup which is shown when the user moves the mouse
 * over a Mark. It indicates details such as the Mark's inner comment, author
 * and score.
 * 
 * @author Jorge Villalón <villalon@gmail.com>
 *
 */
public class MarkPopup extends HTML {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(MarkPopup.class.getName());
	
	/**
	 * Constructor setting css style and empty HTML
	 */
	public MarkPopup() {
		super("&nbsp;");
		this.addStyleName(Resources.INSTANCE.css().markpopup());
	}


	@Override
	protected void onLoad() {
		super.onLoad();
	}
}
