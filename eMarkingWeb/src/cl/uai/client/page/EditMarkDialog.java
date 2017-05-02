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

import java.util.ArrayList;
import java.util.logging.Logger;

import cl.uai.client.EMarkingConfiguration;
import cl.uai.client.EMarkingWeb;
import cl.uai.client.MarkingInterface;
import cl.uai.client.data.Level;
import cl.uai.client.feedback.FeedbackInterface;
import cl.uai.client.feedback.FeedbackObject;
import cl.uai.client.marks.RubricMark;
import cl.uai.client.resources.Resources;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A comment dialog shown to ask for an inner comment in Comment Marks
 * and also provides a select for bonus values for Rubric Marks 
 * 
 * @author Jorge Villalón <villalon@gmail.com>
 *
 */
public class EditMarkDialog extends DialogBox {

	private static Logger logger = Logger.getLogger(EditMarkDialog.class.getName());

	/**
	 * If the dialog was cancelled
	 * @return
	 */
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * Gets the comment text in the dialog
	 * @return the text the user entered
	 */
	public String getTxtComment() {
		return txtComment.getValue();
	}
	
	public String getRegradeComment() {
		return txtRegradeComment.getValue();
	}
	
	public int getRegradeAccepted() {
		return 1;
	}

	/** Dialog's main panel **/
	private VerticalPanel mainPanel;
	
	/** Super panel main + feedback**/
	private HorizontalPanel superPanel;
	
	/** The text box for the comment **/
	private SuggestBox txtComment;

	/** The text box for the regrade comment **/
	private SuggestBox txtRegradeComment;

	public SuggestBox getTxtRegradeComment() {
		return txtRegradeComment;
	}

	/** An id for regrading **/
	private int regradeId = 0;
	
	/** An associated rubric level to offer bonuses **/
	private int levelId = 0;
	/**
	 * @return the rubricLevel
	 */
	public int getLevelId() {
		return levelId;
	}

	/** The bonus **/
	private TextBox bonusTxt = null;

	/** The list box with valid level values **/
	private ListBox levelsList = null;

	/** Indicates if the dialog was cancelled **/
	private boolean cancelled = false;
	
	/** Variables for implementation enhanced feedback, hans's thesis **/
	private ArrayList<FeedbackObject> feedbackArray = null;	
	private FeedbackInterface feedbackPanel = null;
	private String keyWords = null;
	private boolean simplePanel = false;
	private ScrollPanel feedbackSummary;
	private VerticalPanel feedbackForStudent;
	private int count = 1;
	
	/**
	 * Creates a comment dialog at a specific position
	 * 
	 * @param posx Top position for the dialog
	 * @param posy Left position for the dialog
	 * @param level An optional rubric level in case we are editing one
	 */
	public EditMarkDialog(int posx, int posy, int level, int regradeid) {
		super(true, false);
		
		this.regradeId = regradeid;

		this.levelId = level;
		Level lvl = MarkingInterface.submissionData.getLevelById(levelId);

		logger.severe("keywords!"+EMarkingConfiguration.getKeywords());
		if(!EMarkingConfiguration.getKeywords().equals("") ){
			keyWords = EMarkingConfiguration.getKeywords();
			feedbackArray = new ArrayList<FeedbackObject>();
			feedbackPanel = new FeedbackInterface(keyWords);
			feedbackPanel.setParent(this);
		}else{
			simplePanel = true;
		}
		
		superPanel = new HorizontalPanel();
		superPanel.addStyleName(Resources.INSTANCE.css().feedbackdialog());

		feedbackForStudent = new VerticalPanel();
		feedbackForStudent.addStyleName(Resources.INSTANCE.css().feedbackforstudent());
		
		feedbackSummary = new ScrollPanel(feedbackForStudent);
		feedbackSummary.addStyleName(Resources.INSTANCE.css().feedbacksummary());
		
		mainPanel = new VerticalPanel();
		mainPanel.addStyleName(Resources.INSTANCE.css().editmarkdialog());

		// Adds the CSS style and other settings
		this.addStyleName(Resources.INSTANCE.css().commentdialog());
		this.setAnimationEnabled(true);
		this.setGlassEnabled(true);

		bonusTxt = new TextBox();
		bonusTxt.addStyleName(Resources.INSTANCE.css().bonuslist());

		this.levelsList = new ListBox();
		this.levelsList.addStyleName(Resources.INSTANCE.css().levelslist());
		this.levelsList.addChangeHandler(new ChangeHandler() {			
			@Override
			public void onChange(ChangeEvent event) {
				int levelid = Integer.parseInt(levelsList.getValue(levelsList.getSelectedIndex()));
				levelId = levelid;
				Level lvl = MarkingInterface.submissionData.getLevelById(levelId);
				setBonus(lvl.getBonus());
			}
		});

		// If there's a rubric level we should edit a Mark
		// otherwise we are just editing its comment
		if(this.levelId == 0) {
			this.setHTML(MarkingInterface.messages.AddEditComment());
		} else {			
			this.setHTML(MarkingInterface.messages.AddEditMark() + "<br/>" + lvl.getCriterion().getDescription());
		}

		// Position the dialog
		if (simplePanel) {
			this.setPopupPosition(posx, posy);
		} else {
			// The Dialog is more big, so we need to fix the position
			this.setPopupPosition( (int)(Window.getClientWidth() * 0.08), (int) (Window.getClientHeight() * 0.15));
		}
		
		if(this.levelId > 0) {

			loadLevelsList();

			HorizontalPanel hpanelLevels = new HorizontalPanel();
			hpanelLevels.setWidth("100%");
			Label messages = new Label(MarkingInterface.messages.Level());
			hpanelLevels.add(messages);
			hpanelLevels.add(levelsList);
			hpanelLevels.setCellHorizontalAlignment(levelsList, HasHorizontalAlignment.ALIGN_RIGHT);
			mainPanel.add(hpanelLevels);
			mainPanel.setCellHorizontalAlignment(hpanelLevels, HasHorizontalAlignment.ALIGN_RIGHT);
		}

		// Save button
		Button btnSave = new Button(MarkingInterface.messages.Save());
		btnSave.addStyleName(Resources.INSTANCE.css().btnsave());
		btnSave.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(levelId > 0 && !bonusIsValid()) {
					Window.alert(MarkingInterface.messages.InvalidBonusValue());
					return;
				}
				cancelled = false;
				hide();
			}
		});

		// Cancel button
		Button btnCancel = new Button(MarkingInterface.messages.Cancel());
		btnSave.addStyleName(Resources.INSTANCE.css().btncancel());
		btnCancel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				cancelled = true;
				hide();
			}
		});

		// The comment text box
		TextArea txt = new TextArea();
		txt.setVisibleLines(5);
		txt.getElement().getStyle().setMarginBottom(5, Unit.PT);
		txtComment = new SuggestBox(EMarkingWeb.markingInterface.previousCommentsOracle,
				txt);
		txtComment.setAutoSelectEnabled(false);
		txtComment.addStyleName(Resources.INSTANCE.css().editmarksuggestbox());

		HorizontalPanel hpanelComment = new HorizontalPanel();
		hpanelComment.setWidth("100%");
		hpanelComment.add(new Label(MarkingInterface.messages.Comment()));
		hpanelComment.add(txtComment);
		hpanelComment.setCellHorizontalAlignment(txtComment, HasHorizontalAlignment.ALIGN_RIGHT);
		mainPanel.add(hpanelComment);
		mainPanel.setCellHorizontalAlignment(hpanelComment, HasHorizontalAlignment.ALIGN_RIGHT);

		// If the rubric level is not null then create the bonus list and add it to the dialog 
		if(this.levelId > 0) {
			setBonus(lvl.getBonus());

			HorizontalPanel hpanelBonus = new HorizontalPanel();
			hpanelBonus.setWidth("100%");
			hpanelBonus.add(new Label(MarkingInterface.messages.SetBonus()));
			hpanelBonus.add(bonusTxt);
			hpanelBonus.setCellHorizontalAlignment(bonusTxt, HasHorizontalAlignment.ALIGN_RIGHT);
			if(EMarkingConfiguration.isFormativeFeedbackOnly()) {
				hpanelBonus.setVisible(false);
			}
			mainPanel.add(hpanelBonus);
			mainPanel.setCellHorizontalAlignment(hpanelBonus, HasHorizontalAlignment.ALIGN_RIGHT);
		}

		// The regrade comment text box
		txt = new TextArea();
		txt.setVisibleLines(5);
		txt.getElement().getStyle().setMarginBottom(5, Unit.PT);
		txtRegradeComment = new SuggestBox(EMarkingWeb.markingInterface.previousCommentsOracle,
				txt);

		if(this.regradeId > 0) {
			
			mainPanel.add(new HTML("<hr>"));
			mainPanel.add(new Label(MarkingInterface.messages.Regrade()));			

			// Add the textbox
			HorizontalPanel hpanelRegradeComment = new HorizontalPanel();
			hpanelRegradeComment.setWidth("100%");
			hpanelRegradeComment.add(new Label(MarkingInterface.messages.RegradeComment()));
			hpanelRegradeComment.add(txtRegradeComment);
			hpanelRegradeComment.setCellHorizontalAlignment(txtRegradeComment, HasHorizontalAlignment.ALIGN_RIGHT);
			mainPanel.add(hpanelRegradeComment);
			mainPanel.setCellHorizontalAlignment(hpanelRegradeComment, HasHorizontalAlignment.ALIGN_RIGHT);
		}
		
		// Add buttons
		HorizontalPanel hpanel = new HorizontalPanel();
		hpanel.setSpacing(2);
		hpanel.setWidth("100%");
		hpanel.add(btnSave);
		hpanel.add(btnCancel);
		hpanel.setCellWidth(btnSave, "100%");
		hpanel.setCellWidth(btnCancel, "0px");
		hpanel.setCellHorizontalAlignment(btnCancel, HasHorizontalAlignment.ALIGN_RIGHT);
		hpanel.setCellHorizontalAlignment(btnSave, HasHorizontalAlignment.ALIGN_RIGHT);
		mainPanel.add(hpanel);
		mainPanel.setCellHorizontalAlignment(hpanel, HasHorizontalAlignment.ALIGN_RIGHT);

		if(simplePanel){
			// No feedback
			this.setWidget(mainPanel);
		}else{
			// Remove CSS Style
			mainPanel.removeStyleName(Resources.INSTANCE.css().editmarkdialog());
			mainPanel.addStyleName(Resources.INSTANCE.css().editmarkdialogWithFeedback());
			
			bonusTxt.removeStyleName(Resources.INSTANCE.css().bonuslist());
			bonusTxt.addStyleName(Resources.INSTANCE.css().bonuslistWithFeedback());
			
			this.levelsList.removeStyleName(Resources.INSTANCE.css().levelslist());
			this.levelsList.addStyleName(Resources.INSTANCE.css().levelslistWithFeedback());
			
			txtComment.removeStyleName(Resources.INSTANCE.css().editmarksuggestbox());
			txtComment.addStyleName(Resources.INSTANCE.css().editmarksuggestboxWithFeedback());
			
			// Add feedback panel
			mainPanel.add(new HTML("<h4>Feedback</h4>"));
			mainPanel.add(feedbackSummary);
		
			superPanel.add(mainPanel);
			superPanel.add(feedbackPanel);
			this.setWidget(superPanel);
		}
	}
	
	@Override
	public void show() {
		super.show();
		logger.fine("Shown!");
		this.txtComment.getValueBox().setFocus(true);
	}

	/**
	 * Sets the text in the text box
	 * @param txt
	 */
	public void setTxtComment(String txt) {
		this.txtComment.setText(txt);
	}
	
	public void setRegradeTxtComment(String txt) {
		this.txtRegradeComment.setText(txt);
	}

	/**
	 * The selected bonus in the list box
	 * @return the bonus (e.g: -0.38)
	 */
	public float getBonus() {
		if(this.bonusTxt.getText() == null || this.bonusTxt.getText().trim().length() == 0 || this.levelId <= 0)
			return 0;

		String text = bonusTxt.getText();
		if (LocaleInfo.getCurrentLocale().getNumberConstants().decimalSeparator().equals(".")) {
			text = text.replace(',','.');					
		} else {
			text = text.replace('.',',');					
		}
		bonusTxt.setText(text);

		float currentbonus = 0;
		String bonustxt = this.bonusTxt.getText();
		if(!bonusTxt.getText().startsWith("+") && !bonusTxt.getText().startsWith("-")) {
			bonustxt = '+' + bonustxt;
		}
		currentbonus = (float) RubricMark.getNumberFormat(true).parse(bonustxt);

		return currentbonus;
	}

	/**
	 * Creates a list of possible levels that this mark could be switched to
	 * according to its criterion
	 */
	private void loadLevelsList() {
		if(this.levelId == 0) {
			logger.warning("Level id is 0!");
			return;
		}

		this.levelsList.clear();
		Level lvl = MarkingInterface.submissionData.getLevelById(levelId);

		int index=0;
		for(Level level : lvl.getCriterion().getLevels().values()) {

			this.levelsList.addItem(
					level.getDescription() + " (" + RubricMark.scoreFormat(level.getScore(), false) + " pts)",
					Integer.toString(level.getId()));

			if(this.levelId == level.getId()) {
				this.levelsList.setSelectedIndex(index);
			}

			index++;
		}

	}


	/**
	 * Sets the selected bonus
	 * 
	 * @param bonus the bonus
	 */
	public void setBonus(float bonus) {
		
		if(this.levelId <= 0) {
			logger.warning("Edit dialog with no level associated. This shouldn't happen. Level id:" + this.levelId);
			return;
		}

		this.bonusTxt.setText(RubricMark.getNumberFormat(true).format(bonus));
	}

	@Override
	public boolean onKeyDownPreview(char key, int modifiers) {
		switch (key) {
		case KeyCodes.KEY_ESCAPE:
			cancelled = true;
			hide();
			break;
		case KeyCodes.KEY_ENTER:
			if(levelId > 0 && !bonusIsValid()) {
				Window.alert(MarkingInterface.messages.InvalidBonusValue());
				break;
			}			
			cancelled = false;
			hide();
			break;
		}
		return true;
	}
	
	private boolean bonusIsValid() {
		float bonus = getBonus();
		Level lvl = MarkingInterface.submissionData.getLevelById(levelId);
		float maxscore = lvl.getCriterion().getMaxscore();
		if(lvl.getScore() + bonus < 0 || lvl.getScore() + bonus > maxscore) {
			return false;
		}		
		return true;
	}

	public String getFeedback(){
		JSONObject outputFeedback = new JSONObject();
		for(int iterator = 0; iterator < feedbackArray.size() ; iterator ++){
            JSONArray array = new JSONArray();
            array.set(0, new JSONString(feedbackArray.get(iterator).getNameOER().replaceAll("\\<.*?>","")));
            array.set(1, new JSONString(feedbackArray.get(iterator).getName().replaceAll("\\<.*?>","")));
            array.set(2, new JSONString(feedbackArray.get(iterator).getLink()));
            
            outputFeedback.put(Integer.toString(iterator),  array);
		}
		return outputFeedback.toString();
	}

	public boolean haveFeedback() {
		if(!EMarkingConfiguration.getKeywords().equals("")){
			if(feedbackArray.size() > 0){
				return true;
			}
		}
		return false;
	}
	
	public void removeFeedback(String name) {
		int iterator = 0;
		while(iterator < feedbackArray.size()){
			if(feedbackArray.get(iterator).getName() == name){
				feedbackArray.remove(iterator);
			}
			iterator += 1;
		}	
	}
	
	public void loadFeedback() {
		feedbackForStudent.clear();
		count = 1;
		int iterator = 0;
		while(iterator < feedbackArray.size()){		
			final Anchor link = new Anchor(feedbackArray.get(iterator).getLink(), false, feedbackArray.get(iterator).getLink(), "_blank");
			link.addStyleName(Resources.INSTANCE.css().resourcelink());
			addFeedback(
					feedbackArray.get(iterator).getName(),
					link.toString(),
					feedbackArray.get(iterator).getNameOER(),
					feedbackArray.get(iterator).getLink(),
					-1);
			iterator += 1;
		}	
	}
	
	public void setFeedbackArray(ArrayList<FeedbackObject> feedbackArray, int id) {
		int iterator = 0;
		while(iterator < feedbackArray.size()){		
			final Anchor link = new Anchor(feedbackArray.get(iterator).getLink(), false, feedbackArray.get(iterator).getLink(), "_blank");
			link.addStyleName(Resources.INSTANCE.css().resourcelink());
			addFeedback(
					feedbackArray.get(iterator).getName(),
					link.toString(),
					feedbackArray.get(iterator).getNameOER(),
					feedbackArray.get(iterator).getLink(),
					id);
			iterator += 1;
		}

	}
	
	public void addFeedback(final String name, String auxLink, String source, String rawLink, int id) {

		HorizontalPanel rowFeedback = new HorizontalPanel();
		HTML feedback = new HTML();
		Icon iconRemove = new Icon(IconType.TRASH);
		String sourceName = "";
		
		HTML lessIcon = new HTML(iconRemove.toString());
		lessIcon.addStyleName(Resources.INSTANCE.css().plusicon());
		
		if(source == "ocwmit"){
			sourceName = "OCW MIT";
		}else if(source == "merlot"){
			sourceName = "Merlot";
		}else{
			sourceName = "Webcursos";
		}
		String html = "<p>" + count +". " + sourceName + " - " + name + "</p><p>Link " + auxLink + "</p><hr>" ;
		count += 1;
		feedback.setHTML(html);
		rowFeedback.add(lessIcon);
		rowFeedback.add(feedback);
		feedbackForStudent.add(rowFeedback);
		
		if(id > 0){
			feedbackArray.add(new FeedbackObject(id, name, rawLink, source));
		}else if(id == 0){
			feedbackArray.add(new FeedbackObject(name, rawLink, source));
		}
		lessIcon.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				removeFeedback(name);
				loadFeedback();
			}						
		});
	
	}
	
	public ArrayList<FeedbackObject> getFeedbackArray() {
		return feedbackArray;
	}
}
