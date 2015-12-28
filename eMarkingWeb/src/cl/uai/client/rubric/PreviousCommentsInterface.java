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
package cl.uai.client.rubric;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import cl.uai.client.EMarkingComposite;
import cl.uai.client.EMarkingConfiguration;
import cl.uai.client.EMarkingWeb;
import cl.uai.client.MarkingInterface;
import cl.uai.client.data.AjaxData;
import cl.uai.client.data.AjaxRequest;
import cl.uai.client.data.Criterion;
import cl.uai.client.marks.Mark;
import cl.uai.client.resources.Resources;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author Jorge Villalon <villalon@gmail.com>
 *
 */
public class PreviousCommentsInterface extends EMarkingComposite {

	Logger logger = Logger.getLogger(PreviousCommentsInterface.class.getName());
	
	/** Main panel for previous comments **/
	private VerticalPanel mainPanel = new VerticalPanel();

	/** The panels containing the comments **/
	private FlowPanel previousCommentsAll = null;
	private FlowPanel previousCommentsMine = null;
	private FlowPanel previousCommentsRecent = null;
	private FlowPanel previousCommentsMostUsed = null;
	private Map<Integer, FlowPanel> previousCommentsCriteria = null;
	private Map<Integer, FlowPanel> previousCommentsDrafts = null;
	
	private StackPanel commentsTabs = null;
	
	/*
	 * Comment lists
	 */
	private List<Comment> previousComments;

	
	
	/**
	 * Creates the interface
	 */
	public PreviousCommentsInterface() {
		
		previousComments = new LinkedList<Comment>();
		
		// Initialize interface and add CSS style
		mainPanel = new VerticalPanel();
		mainPanel.addStyleName(Resources.INSTANCE.css().previouscomments());
		
		commentsTabs = new StackPanel();
		commentsTabs.addStyleName(Resources.INSTANCE.css().previouscomments());
		
		// Add comments table
		previousCommentsAll = new FlowPanel();
		previousCommentsMine = new FlowPanel();
		previousCommentsRecent = new FlowPanel();
		previousCommentsMostUsed = new FlowPanel();
		previousCommentsCriteria = new HashMap<Integer, FlowPanel>();

		commentsTabs.add(previousCommentsMine, MarkingInterface.messages.MyComments());
		
		for(Criterion criterion : MarkingInterface.submissionData.getRubricfillings().values()) {
			FlowPanel criterionPanel = new FlowPanel();
			previousCommentsCriteria.put(criterion.getId(), criterionPanel);
			commentsTabs.add(criterionPanel, criterion.getDescription());
		}

		for(Criterion criterion : MarkingInterface.submissionData.getRubricfillings().values()) {
			FlowPanel criterionPanel = new FlowPanel();
			previousCommentsCriteria.put(criterion.getId(), criterionPanel);
			commentsTabs.add(criterionPanel, criterion.getDescription());
		}

		commentsTabs.add(previousCommentsRecent, MarkingInterface.messages.Recent());
		commentsTabs.add(previousCommentsMostUsed, MarkingInterface.messages.MostUsed());
		commentsTabs.add(previousCommentsAll, MarkingInterface.messages.All());
		
		mainPanel.add(commentsTabs);
		
		this.initWidget(mainPanel);
	}

	@Override
	protected void onLoad() {
		super.onLoad();

		Widget parent = this.getParent().getParent().getParent();
		int height = parent.getOffsetHeight() - 8;
		this.setHeight(height+"px");
		
		AjaxRequest.ajaxRequest("action=prevcomments", new AsyncCallback<AjaxData>() {			
			@Override
			public void onSuccess(AjaxData result) {
				
				previousComments.clear();
				List<Map<String, String>> comments = AjaxRequest.getValuesFromResult(result);
				for(Map<String, String> comment : comments) {
					Comment newcomment = Comment.createFromMap(comment);
					previousComments.add(newcomment);
					EMarkingWeb.markingInterface.previousCommentsOracle.add(newcomment.getText());
				}
				
				updateAllCommentsInInterfaces();
			}

			@Override
			public void onFailure(Throwable caught) {
				logger.severe("Error getting previous comments from Moodle!");
				logger.severe(caught.getMessage());
				Window.alert(caught.getMessage());
			}
		});
	}

	private void updateAllCommentsInInterfaces() {
		removeAllCommentsFromInterfaces();
		
		previousCommentsAll.clear();
		previousCommentsMine.clear();
		previousCommentsRecent.clear();
		previousCommentsMostUsed.clear();
		for(FlowPanel f : previousCommentsCriteria.values()) {
			f.clear();
		}

		Collections.sort(previousComments, Comment.CommentTextComparator);
		for(Comment c : previousComments) {
			if(c.isOwnComment()) {
				addCommentLabelToInterface(c, previousCommentsMine);
			}
			addCommentLabelToInterface(c, previousCommentsAll);
			for(int cid : c.getCriteriaIds()) {
				if(previousCommentsCriteria.get(cid) != null) {
					FlowPanel f = previousCommentsCriteria.get(cid);
					addCommentLabelToInterface(c, f);
				}
			}
		}
		Collections.sort(previousComments, Comment.CommentTimesUsedComparator);
		for(Comment c : previousComments) {
			addCommentLabelToInterface(c, previousCommentsMostUsed);
		}
		Collections.sort(previousComments, Comment.CommentLastUsedComparator);
		for(Comment c : previousComments) {
			addCommentLabelToInterface(c, previousCommentsRecent);
		}		
	}

	private void addCommentLabelToInterface(Comment comment, FlowPanel commentInterface) {
		PreviousCommentLabel commentLabel = new PreviousCommentLabel();
		commentLabel.setText(comment.getText());
		commentLabel.addStyleName(Resources.INSTANCE.css().previousComment());
		EMarkingWeb.markingInterface.dragController.makeDraggable(commentLabel);
		commentInterface.add(commentLabel);			
	}
	
	private void removeAllCommentsFromInterfaces() {
		for(int i=0; i<previousCommentsAll.getWidgetCount(); i++) {
			PreviousCommentLabel lbl = (PreviousCommentLabel) previousCommentsAll.getWidget(i);
			EMarkingWeb.markingInterface.dragController.makeNotDraggable(lbl);
		}
		for(int i=0; i<previousCommentsMine.getWidgetCount(); i++) {
			PreviousCommentLabel lbl = (PreviousCommentLabel) previousCommentsMine.getWidget(i);
			EMarkingWeb.markingInterface.dragController.makeNotDraggable(lbl);
		}
		for(int i=0; i<previousCommentsRecent.getWidgetCount(); i++) {
			PreviousCommentLabel lbl = (PreviousCommentLabel) previousCommentsRecent.getWidget(i);
			EMarkingWeb.markingInterface.dragController.makeNotDraggable(lbl);
		}
	}
	
	/**
	 * Adds a string comment
	 * @param comment
	 */
	public void addMarkAsCommentToInterface(Mark mark) {
		if(mark.getRawtext().trim().length() == 0) {
			return;
		}
		
		Comment prevComment = findPreviousComment(mark.getRawtext());
		
		// First check if the comment hasn't been added before
		if(prevComment != null) {
				long unixTime = System.currentTimeMillis() / 1000L;
				prevComment.setLastUsed(unixTime);
				prevComment.setTimesUsed(prevComment.getTimesUsed()+1);
				prevComment.setMarkerId(EMarkingConfiguration.getMarkerId());
				prevComment.setPages(mark.getPageno());
				prevComment.setOwnComment(true);
		} else {
			List<Integer> markers = new ArrayList<Integer>();
			markers.add(mark.getMarkerId());
			List<Integer> pages = new ArrayList<Integer>();
			pages.add(mark.getPageno());
			List<Integer> criteria = new ArrayList<Integer>();
			criteria.add(mark.getCriterionid());
			List<Integer> drafts = new ArrayList<Integer>();
			drafts.add(MarkingInterface.getDraftId());
			Comment newComment = new Comment(
					mark.getId(), 
					mark.getRawtext(), 
					mark.getFormat(), 
					markers, 
					1, 
					mark.getTimeCreated(),
					pages,
					true,
					criteria,
					drafts);
			
			previousComments.add(newComment);
			
			EMarkingWeb.markingInterface.previousCommentsOracle.add(newComment.getText());
		}
		
		updateAllCommentsInInterfaces();
	}

	private Comment findPreviousComment(String text) {
		Comment previousComment = null;
		
		// First check if the comment hasn't been added before
		for(Comment prevComment : previousComments) {
			if(prevComment.getText().trim().equals(text)) {
				previousComment = prevComment;
				break;
			}
		}
		
		return previousComment;
	}
	/**
	 * Deletes a string comment
	 * @param comment
	 */
	public void deletePreviousComment(String comment) {
		if(comment.trim().length() == 0)
			return;

		Comment previousComment = findPreviousComment(comment);
		
		if(previousComment != null) {
			if(previousComment.getTimesUsed() == 1) {
				previousComments.remove(previousComment);
			} else {
				long unixtime = System.currentTimeMillis() / 1000L;
				previousComment.setTimesUsed(previousComment.getTimesUsed()-1);
				previousComment.setLastUsed(unixtime);
			}
		}
		
		updateAllCommentsInInterfaces();
	}	
}
