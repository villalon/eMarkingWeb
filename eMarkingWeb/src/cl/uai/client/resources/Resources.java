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
 * Strings for component 'block_news_items', language 'en', branch 'MOODLE_20_STABLE' 
*
* @package   block_news_items
* @copyright 2011 onwards Jorge Villalon {@link http://villalon.cl}
* @license   http://www.gnu.org/copyleft/gpl.html GNU GPL v3 or later
*/
package cl.uai.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author Jorge Villalon
 *
 */
public interface Resources extends ClientBundle {
	public static final Resources INSTANCE =  GWT.create(Resources.class);

	@Source("larrow.png")
	ImageResource leftarrow();
	
	@Source("rarrow.png")
	ImageResource rightarrow();	
	
	@Source("marker0.png")
	ImageResource marker0();	
	
	@Source("marker25.png")
	ImageResource marker25();	
							
	@Source("marker50.png")
	ImageResource marker50();	
									
	@Source("marker75.png")
	ImageResource marker75();	
	
	@Source("marker100.png")
	ImageResource marker100();
	
	@Source("othermarker0.png")
	ImageResource othermarker0();	
	
	@Source("othermarker25.png")
	ImageResource othermarker25();	
	
	@Source("othermarker50.png")
	ImageResource othermarker50();	
	
	@Source("othermarker75.png")
	ImageResource othermarker75();	
	
	@Source("othermarker100.png")
	ImageResource othermarker100();
	
	@Source("EMarkingWeb.css")
	EmarkingCss css();
}
