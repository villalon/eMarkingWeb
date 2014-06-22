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
* Strings for eMarking interface 
*
* @package   emarking
* @copyright 2013 onwards Jorge Villalon <villalon@gmail.com>
* @license   http://www.gnu.org/copyleft/gpl.html GNU GPL v3 or later
*/
package cl.uai.client.resources;

import com.google.gwt.i18n.client.Messages;

public interface EmarkingMessages extends Messages {
	public String AddEditComment();
	public String AddEditMark();
	public String AnonymousCourse();
	public String Anonymous();
	public String Cancel();
	public String CantReachServerRetrying(int seconds);
	public String CheckTitle();
	public String ClosingConfirm();
	public String Comment();
	public String CommentForMarker();
	public String CommentTitle();
	public String CrossTitle();
	public String ErrorAddingMark();
	public String ErrorFinishingEmarking();
	public String ErrorInvalidLevelId();
	public String ErrorLoadingSubmission();
	public String FinishMarking();
	public String GeneralFeedback();
	public String GeneralFeedbackInstructions();
	public String InvalidSubmissionData();
	public String LastSaved(String when);
	public String Level();
	public String Loading();
	public String MarkerDetails(String name);
	public String Never();
	public String PageNumber(int pagenumber);
	public String PageLoadingTryAgain();
	public String PenTitle();
	public String PreviousComments();
	public String RubricTitle();
	public String Save();
	public String SaveChanges();
	public String SaveChangesClose();
	public String SetBonus();
	public String SetLevel();
	public String ShowMarked();
	public String StudentN(String n);
	public String TextTitle();
	public String FinishingMarkingSuccessfull();
	public String FinishingMarking();
	public String SortPages();
	public String Page();
	public String PleaseWait();
	public String SortPagesInstructions();
	public String SortPagesChange();
	public String RegradeComment();
	public String Regrade();
	public String RegradeReply();
	public String MyComments();
	public String Recent();
	public String MostUsed();
	public String All();
	public String StatusAccepted();
	public String StatusGrading();
	public String StatusMissing();
	public String StatusRegrading();
	public String StatusResponded();
	public String StatusSubmitted();
	public String StatusError();
	public String Saving();
	public String Select();
	public String MissassignedScore();
	public String UnclearFeedback();
	public String StatementProblem();
	public String Other();
	public String Motive();
	public String RequestRegrade();
	public String RequestMaximumLength(int length);
	public String MotiveIsMandatory();
	public String RotatePage(int page);
	public String RotatePages();
	public String RotatePagesInstructions();
	public String NoMoreSubmissions();
	public String JumpToNextStudent();
	public String ShowRubric();
	public String ShowMarkingPending();
	public String ShowRegradePending();
	public String HideRubric();
	public String MarksSummary();
	public String Score();
	public String Edit();
	public String Delete();
}
