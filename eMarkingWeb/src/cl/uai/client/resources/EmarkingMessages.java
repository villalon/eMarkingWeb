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
	public String All();
	public String Anonymous();
	public String AnonymousCourse();
	public String AnswerKey();
	public String Cancel();
	public String CantReachServerRetrying(int seconds);
	public String CheckTitle();
	public String ClosingConfirm();
	public String Comment();
	public String CommentForMarker();
	public String CommentTitle();
	public String CrossTitle();
	public String Delete();
	public String Edit();
	public String ErrorAddingMark();
	public String ErrorFinishingEmarking();
	public String ErrorInvalidLevelId();
	public String ErrorLoadingSubmission();
	public String FinishingMarking();
	public String FinishingMarkingSuccessfull();
	public String FinishMarking();
	public String GeneralFeedback();
	public String GeneralFeedbackInstructions();
	public String HideRubric();
	public String InvalidBonusValue();
	public String InvalidSubmissionData();
	public String JumpToNextStudent();
	public String LastSaved(String when);
	public String Level();
	public String Loading();
	public String Marker();
	public String MarkerDetails(String name);
	public String MarksSummary();
	public String MissassignedScore();
	public String MostUsed();
	public String Motive();
	public String MotiveIsMandatory();
	public String MyComments();
	public String Never();
	public String NoMoreSubmissions();
	public String Other();
	public String Page();
	public String PageLoadingTryAgain();
	public String PageNumber(int pagenumber);
	public String PenTitle();
	public String PleaseWait();
	public String PreviousComments();
	public String QuestionTitle();
	public String Recent();
	public String Regrade();
	public String RegradeComment();
	public String RegradeReply();
	public String RequestMaximumLength(int length);
	public String RequestRegrade();
	public String RotatePage(int page);
	public String RotatePages();
	public String RotatePagesInstructions();
	public String RubricTitle();
	public String Save();
	public String SaveChanges();
	public String SaveChangesClose();
	public String Saving();
	public String Score();
	public String Select();
	public String SetBonus();
	public String SetLevel();
	public String ShowMarked();
	public String ShowMarkingPending();
	public String ShowRegradePending();
	public String ShowRubric();
	public String SortPages();
	public String SortPagesChange();
	public String SortPagesInstructions();
	public String StatementProblem();
	public String StatusAccepted();
	public String StatusError();
	public String StatusGrading();
	public String StatusMissing();
	public String StatusRegrading();
	public String StatusResponded();
	public String StatusSubmitted();
	public String StudentN(String n);
	public String TextTitle();
	public String UnclearFeedback();
	public String Published();
	public String AgreeStatus();
	public String DeleteMarkConfirm();
	public String Requested();
	public String Replied();
	public String NoCriterion();
	public String Close();
	public String JustNow();
	public String MinuteAgo();
	public String MinutesAgo();
	public String HoursAgo();
	public String HourAgo();
	public String DayAgo();
	public String DaysAgo();
	public String Mark();
	public String Exam();
	public String View();
	public String ShowWall();
	public String ShowChat();
	public String ShowHelp();
	public String Chat();
	public String NoChatAvailable(String url);
	public String NoChatAvailableForMarker();
	public String SOS();
	public String Wall();
	public String Collaboration();
	public String ShowColors();
	public String SendSOS();
	public String SendSOSTitle();
	public String Send();
	public String Priority();
	public String Help();
	public String AboutEmarking();
	public String AboutEmarkingDetail(String version);
	public String Tutorials();
	public String HowToWhatIsEMarking();
	public String HowToPrintAnExam();	
	public String HowToCreateRubric();
	public String HowToAnonymousMarking();	
	public String HowToMarkingReports();
	public String ChatServerError(String adminemail);
	public String MinimizeAllRubricMarks();
	public String MarkerTitle();
}
