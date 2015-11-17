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
package cl.uai.client.marks;

import java.util.Map;
import java.util.logging.Logger;

import cl.uai.client.EMarkingConfiguration;
import cl.uai.client.EMarkingWeb;
import cl.uai.client.MarkingInterface;
import cl.uai.client.data.AjaxData;
import cl.uai.client.data.AjaxRequest;
import cl.uai.client.page.EditIcon;
import cl.uai.client.page.EditMarkDialog;
import cl.uai.client.page.EditMarkMenu;
import cl.uai.client.page.LoadingIcon;
import cl.uai.client.page.MarkingPage;
import cl.uai.client.page.MinimizeIcon;
import cl.uai.client.page.RegradeIcon;
import cl.uai.client.page.TrashIcon;
import cl.uai.client.resources.Resources;
import cl.uai.client.utils.Color;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;


/**
 * The most basic mark a page can include
 * 
 * @author Jorge Villalón <villalon@gmail.com>
 *
 */
public abstract class Mark extends HTML implements ContextMenuHandler, ClickHandler {

	/** For logging purposes **/
	protected static Logger logger = Logger.getLogger(Mark.class.getName());

	/** The delete icon **/
	protected static TrashIcon deleteIcon = null;

	/** Criterion to which this mark is associated (if any) **/
	protected int criterionid = 0;
	
	static {
		deleteIcon = new TrashIcon();
	}

	public int getCriterionid() {
		return criterionid;
	}

	/** The regrade icon **/
	protected static RegradeIcon regradeIcon = null;

	protected boolean iconOnly = false;
	
	protected IconType iconType = null;
	
	static {
		regradeIcon = new RegradeIcon();
	}

	/** The edit icon **/
	protected static EditIcon editIcon = null;

	static {
		editIcon = new EditIcon();
	}
	
	/** The minimize icon **/
	protected static MinimizeIcon minimizeIcon = null;

	static {
		minimizeIcon = new MinimizeIcon();
	}
	
	public static LoadingIcon loadingIcon = null;
	static {
		loadingIcon = new LoadingIcon();
	}

	/**
	 * Hides all icons
	 */
	public static void hideIcons() {
		deleteIcon.setVisible(false);
		editIcon.setVisible(false);
		regradeIcon.setVisible(false);
		minimizeIcon.setVisible(false);
	}
	
	protected static EditMarkMenu editMenu = null;

	/** A mark id, corresponding to emarking_comment table in Moodle **/
	protected int id = -1;

	/** The mark position in the page **/
	protected int posx = -1;
	protected int posy = -1;
	
	protected int width = 140;
	protected int height = 100;
	/** The mark inner comment **/
	protected String rawtext = "";
	/** The mark page number **/
	protected int pageno = 0;
	/** The marker id and name **/
	protected int markerid = 0;
	protected String markername = "";
	/** The mark format, as there is no OO in the DB. 1 is Mark **/
	protected int format = 1;
	
	protected String previousText="";

	private long timecreated;

	/**
	 * @return the pageno
	 */
	public int getPageno() {
		return pageno;
	}
	/**
	 * Creates a Mark object at a specific position in a page
	 * @param posx X coordinate in the page
	 * @param posy Y coordinate in the page
	 * @param pageno the page number (1 to N)
	 */
	public Mark(
			int id,
			int posx,
			int posy,
			int pageno,
			int markerId,
			long timecreated,
			int criterionid,
			String markername,
			String rawtext
			) {
        this.markerid = markerId;
		this.posx = posx;
		this.posy = posy;        
		this.pageno = pageno;
		this.timecreated = timecreated;
		this.criterionid = criterionid;
		this.markername = markername;
		this.rawtext = rawtext;
		this.id = id;
		
		this.addStyleName(Resources.INSTANCE.css().mark());

		this.addHandlers();
	}
	
	protected void addHandlers(){
		this.addMouseOverHandler(new MarkOnMouseOverHandler());
		this.addMouseOutHandler(new MarkOnMouseOutHandler());
		this.addDomHandler(this, ContextMenuEvent.getType());
		this.addDomHandler(this, ClickEvent.getType());
	}
	
	/**
	 * @return the format
	 */
	public int getFormat() {
		return format;
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @return the markername
	 */
	public String getMarkername() {
		return markername;
	}

	/**
	 * @return the posx
	 */
	public int getPosx() {
		return posx;
	}

	/**
	 * @return the posy
	 */
	public int getPosy() {
		return posy;
	}

	/**
	 * @return the rawtext of the mark
	 */
	public String getRawtext() {
		return rawtext;
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		this.setMarkHTML();
	}

	/**
	 * Sets the inner HTML according to an icon
	 */
	public void setMarkHTML() {
		
		// Starts with an empty HTML
		String html = "";		
		String iconhtml = "";

		// Create the icon string if any
		if(this.iconType != null) {
			Icon icon = new Icon(this.iconType);
			iconhtml = icon.toString();
		} else if(this.iconOnly && this instanceof CustomMark) {
			CustomMark cmark = (CustomMark) this;
			iconhtml = cmark.getTitle();
		}

		String markername = EMarkingConfiguration.isMarkerAnonymous() ? MarkingInterface.messages.MarkerDetails(MarkingInterface.messages.Anonymous())
				: MarkingInterface.messages.MarkerDetails(this.getMarkername());
		String styleColor = "";

		// If the mark has a color, we use the background to color it
		if(this.criterionid > 0 && this.iconOnly) {
			styleColor = "style=\"color:" + Color.getCSSHueColor(criterionid) + "\"";
		}
		
		html += "<div class=\"" + Resources.INSTANCE.css().markicon() + "\" title=\""+ markername +"\" " + styleColor + ">" + iconhtml + "</div>";
		// If the mark is an icon
		if(!this.iconOnly && this.getRawtext().trim().length() > 0) {
			html += "<div class=\""+Resources.INSTANCE.css().markrawtext()+"\">"+ this.getRawtext() + "</div>";
			// Show the marker's name if the marking process is not anonymous
			if(!EMarkingConfiguration.isMarkerAnonymous()) {
				html += "<div class=\""+Resources.INSTANCE.css().markmarkername()+"\">"+ markername + "</div>";
			}
		}

		this.setHTML(html);		
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Changes its inner HTML while it is loading
	 */
	protected void setLoading() {
		Icon icon = new Icon(IconType.COG);
		icon.addStyleName("icon-spin");
		icon.addStyleName(Resources.INSTANCE.css().iconupdating());
		this.setHTML("<div class=\""+Resources.INSTANCE.css().innercomment()+"\">"+icon.toString()+"</div>");
		this.addStyleName(Resources.INSTANCE.css().updating());		
	}

	/**
	 * @param markerid the markerid to set
	 */
	public void setMarkerid(int markerid) {
		this.markerid = markerid;
	}

	/**
	 * @param markername the markername to set
	 */
	public void setMarkername(String markername) {
		this.markername = markername;
	}

	/**
	 * @param rawtext the rawtext to set
	 */
	public void setRawtext(String rawtext) {
		this.rawtext = rawtext;
	}

	/**
	 * Updates a mark's comment, position or bonus
	 * 
	 * @param newcomment the new comment
	 * @param newposx the new X coordinate in the page
	 * @param newposy the new Y coordinate in the page
	 * @param newbonus the new bonus
	 */
	public void update(final String newcomment, int newposx, int newposy, final int newlevel, final float newbonus, final String newregrademarkercomment, final int newregradeaccepted, int widthPage, int heightPage) {

		final Mark mark = this;

		int regradeid = 0;
		if(mark instanceof RubricMark)
			regradeid = ((RubricMark) mark).getRegradeid();
		
		// This shouldn't happen so log it for debugging
		if(this.id == 0) {
			logger.severe("Fatal error! A comment with id 0!");
			return;
		}

		EMarkingWeb.markingInterface.addLoading(true);
		
		this.setLoading();

		// Call the ajax request to update the data
		AjaxRequest.ajaxRequest("action=updcomment&cid=" + this.id + 
				"&posx=" + newposx + 
				"&posy=" + newposy +
				"&bonus=" + newbonus +
				"&format=" + this.format +
				"&levelid=" + newlevel +
				"&regradeid=" + regradeid +
				"&regradeaccepted=" + newregradeaccepted +
				"&regrademarkercomment=" + newregrademarkercomment +
				"&width=" + this.width +
				"&height=" + this.height +
				"&comment=" + URL.encode(newcomment) +
				"&windowswidth=" + widthPage +
				"&windowsheight=" + heightPage, new AsyncCallback<AjaxData>() {

			@Override
			public void onFailure(Throwable caught) {
				logger.severe("Error updating mark to Moodle!");
				logger.severe(caught.getMessage());
				Window.alert(caught.getMessage());
				EMarkingWeb.markingInterface.finishLoading();
			}

			@Override
			public void onSuccess(AjaxData result) {
				Map<String, String> value = AjaxRequest.getValueFromResult(result);

				// Parse json values from Moodle
				long timemodified = Long.parseLong(value.get("timemodified"));
				float newgrade = Float.parseFloat(value.get("newgrade"));
				mark.setPreviousText(mark.getRawtext());
				mark.setRawtext(newcomment);
				
				if(mark instanceof RubricMark) {
					RubricMark rmark = (RubricMark)mark;
					// Update submission data
					rmark.setLevelId(newlevel);
					rmark.setBonus(newbonus);
					rmark.setRegradeaccepted(newregradeaccepted);
					rmark.setRegrademarkercomment(newregrademarkercomment);
					EMarkingWeb.markingInterface.getRubricInterface().getRubricPanel().addMarkToRubric(rmark);
					EMarkingWeb.markingInterface.getRubricInterface().getRubricPanel().finishloadingRubricCriterion(newlevel);
				}
				
				// Update the marking interface with the final grade and time
				EMarkingWeb.markingInterface.setFinalgrade(newgrade, timemodified);
				setMarkHTML();
				
				EMarkingWeb.markingInterface.getRubricInterface().getToolsPanel().getPreviousComments().addMarkAsCommentToInterface(mark);

				removeStyleName(Resources.INSTANCE.css().updating());
				
				EMarkingWeb.markingInterface.finishLoading();
			}
		});		
	}
	
	public void updateMark(int posx, int posy) {
		// Edit works for both comment and rubric marks only
		if(this instanceof RubricMark || this instanceof CommentMark) {
			final Mark mark = (Mark) this;
			
			// If the mark is a rubric get its level
			int level = 0;
			int regradeid = 0;
			if(mark instanceof RubricMark) {
				regradeid = ((RubricMark) mark).getRegradeid();
				level = ((RubricMark) mark).getLevelId();
			}
			
			// Create the comment dialog with the corresponding rubric level
			EditMarkDialog dialog = new EditMarkDialog(posx, posy, level, regradeid);
			
			if(mark instanceof RubricMark && regradeid > 0) {
				dialog.getTxtRegradeComment().setText(((RubricMark) mark).getRegrademarkercomment());
			}
			
			// Set dialog's current values to the mark's
			dialog.setTxtComment(mark.getRawtext());
			
			// Update when closing
			dialog.addCloseHandler(new CloseHandler<PopupPanel>() {
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					EditMarkDialog dialog = (EditMarkDialog) event.getSource();
					
					MarkingPage page = EMarkingWeb.markingInterface.getMarkingPagesInterface().getPageByIndex(pageno-1);
					int widthPage = page.getWidth();
					int heightPage = page.getHeight();
					// If the dialog was not cancelled update the mark with the dialog values
					if(!dialog.isCancelled()) {
							mark.update(dialog.getTxtComment(),
								mark.getPosx(),
								mark.getPosy(),
								dialog.getLevelId(),
								dialog.getBonus(), 
								dialog.getRegradeComment(), 
								dialog.getRegradeAccepted(),
								widthPage,
								heightPage);
					}
				}
			});
			
			// Show the dialog
			dialog.show();
		}		
	}
	
	protected void setPreviousText(String text) {
		this.previousText = text;
		
	}
	public int getWidth() {
		return this.width;
	}
	public int getHeight() {
		return this.height;
	}
	public String getPreviousText() {
		return this.previousText;
	}
	
	public int getMarkerId() {
		return markerid;
	}
	
	public long getTimeCreated() {
		return timecreated;
	}
	
	public void setTimeCreated(long timecreated) {
		this.timecreated = timecreated;		
	}
	
	@Override
	public void onContextMenu(ContextMenuEvent event) {
		event.getNativeEvent().stopPropagation();
		event.getNativeEvent().preventDefault();

		editMenu = new EditMarkMenu(this, 
				event.getNativeEvent().getClientX(), 
				event.getNativeEvent().getClientY());
		editMenu.show();
	}
	
	@Override
	public void onClick(ClickEvent event) {
		event.stopPropagation();
	}
}
