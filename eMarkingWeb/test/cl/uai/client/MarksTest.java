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

import java.util.Map;

import cl.uai.client.data.AjaxData;
import cl.uai.client.data.AjaxRequest;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Jorge Villalón <villalon@gmail.com>
 *
 */
public class MarksTest extends GWTTestCase {

	private static String action = "username=admin&password=pepito.P0&testing=true";

	/* (non-Javadoc)
	 * @see com.google.gwt.junit.client.GWTTestCase#getModuleName()
	 */
	@Override
	public String getModuleName() {
		return "cl.uai.EMarkingWeb";
	}

	@Override
	protected void gwtSetUp() throws Exception {
		super.gwtSetUp();

		delayTestFinish(500);
		
		MarkingInterface.setDraftId(1);
		EMarkingConfiguration.setMoodleUrl("http://localhost/wc/mod/emarking/ajax.php");
	}
	
	public MarksTest() {

	}
	
	public void testPing() {
		
		AjaxRequest.ajaxRequest(action + "&action=ping", new AsyncCallback<AjaxData>() {
			
			@Override
			public void onSuccess(AjaxData result) {
				Map<String, String> output = AjaxRequest.getValueFromResult(result);
				int user = 0;
				int student = 0;
				String username = null;
				String sesskey = null;
				try {
					user = Integer.parseInt(output.get("user"));
					student = Integer.parseInt(output.get("student"));
					username = output.get("username");
					sesskey = output.get("sesskey");
					Boolean.parseBoolean(output.get("anonymous"));
					Boolean.parseBoolean(output.get("hascapability"));
				} catch (Exception e) {
					e.printStackTrace();
					fail();
				}
				assertTrue(user > 0);
				assertTrue(student > 0);
				assertNotNull(username);
				assertNotNull(sesskey);
				finishTest();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				fail();
			}
		});
	}
	
	public void testAddMark() {			
		
		AjaxRequest.ajaxRequest(action + "&action=addmark&level=85&posx=416&posy=94&pageno=1&bonus=0.0&comment=", new AsyncCallback<AjaxData>() {
			
			@Override
			public void onSuccess(AjaxData result) {
				Map<String, String> output = AjaxRequest.getValueFromResult(result);
				float score = 0;
				float maxscore = 0;
				float grade = 0;
				int markerid = 0;
				int lvlid = 0;
				int criterionid = 0;
				int id = 0;
				try {
					id = Integer.parseInt(output.get("id"));
					criterionid = Integer.parseInt(output.get("criterionid"));
					lvlid = Integer.parseInt(output.get("lvlid"));
					markerid = Integer.parseInt(output.get("markerid"));
					score = Float.parseFloat(output.get("score"));
					maxscore = Float.parseFloat(output.get("maxscore"));
					grade = Float.parseFloat(output.get("grade"));
				} catch (Exception e) {
					e.printStackTrace();
					fail();
				}
				assertTrue(id > 0);
				assertTrue(criterionid > 0);
				assertTrue(lvlid > 0);
				assertTrue(markerid > 0);
				assertEquals(0, score, 0.0001);
				assertEquals(2, maxscore, 0.0001);
				assertTrue(grade >= 0);
				finishTest();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				fail();
			}
		});
	}
	
	public void testGrades() {			
		
		AjaxRequest.ajaxRequest(action + "&action=addmark&level=85&posx=416&posy=94&pageno=1&bonus=0.0&comment=", new AsyncCallback<AjaxData>() {
			
			@Override
			public void onSuccess(AjaxData result) {
				Map<String, String> output = AjaxRequest.getValueFromResult(result);
				int id = 0;
				try {
					id = Integer.parseInt(output.get("id"));
				} catch (Exception e) {
					e.printStackTrace();
					fail();
				}
				assertTrue(id > 0);
				AjaxRequest.ajaxRequest(action + "&action=addmark&level=88&posx=416&posy=204&pageno=1&bonus=0.0&comment=", new AsyncCallback<AjaxData>() {
					@Override
					public void onFailure(Throwable caught) {
						fail();
					}

					@Override
					public void onSuccess(AjaxData result) {
						Map<String, String> output = AjaxRequest.getValueFromResult(result);
						float score = 0;
						float maxscore = 0;
						float grade = 0;
						int markerid = 0;
						int lvlid = 0;
						int criterionid = 0;
						int id = 0;
						try {
							id = Integer.parseInt(output.get("id"));
							criterionid = Integer.parseInt(output.get("criterionid"));
							lvlid = Integer.parseInt(output.get("lvlid"));
							markerid = Integer.parseInt(output.get("markerid"));
							score = Float.parseFloat(output.get("score"));
							maxscore = Float.parseFloat(output.get("maxscore"));
							grade = Float.parseFloat(output.get("grade"));
						} catch (Exception e) {
							e.printStackTrace();
							fail();
						}
						assertTrue(id > 0);
						assertTrue(criterionid > 0);
						assertTrue(lvlid > 0);
						assertTrue(markerid > 0);
						assertEquals(0, score, 0.0001);
						assertEquals(2, maxscore, 0.0001);
						assertEquals(1, grade, 0.0001);
						subtestUpdateMarkLevel(id, 89, 2.5f);
					}
				});
			}
			
			@Override
			public void onFailure(Throwable caught) {
				fail();
			}
		});
	}
	
	private void subtestUpdateMarkLevel(int commentid, int levelid, final float expected) {
		// http://localhost/wc/mod/emarking/ajax.php?ids=1&action=updcomment&cid=105&posx=531&posy=264&bonus=0.0&format=2&levelid=89&regradeid=0&regradeaccepted=0&regrademarkercomment=&width=140&height=100&comment=
		System.out.println(action + "&action=updcomment&cid="+ commentid +"&levelid="+levelid+"&posx=416&posy=204&pageno=1&bonus=0.0&format=2&comment=");
		AjaxRequest.ajaxRequest(action + "&action=updcomment&cid="+ commentid +"&levelid="+levelid+"&posx=416&posy=204&pageno=1&bonus=0.0&format=2&comment=", new AsyncCallback<AjaxData>() {
			@Override
			public void onFailure(Throwable caught) {
				fail();
			}

			@Override
			public void onSuccess(AjaxData result) {
				Map<String, String> output = AjaxRequest.getValueFromResult(result);
				float grade = 0;
				long timemodified = 0;
				String message = null;
				try {
					timemodified = Long.parseLong(output.get("timemodified"));
					grade = Float.parseFloat(output.get("newgrade"));
					message = output.get("message");
				} catch (Exception e) {
					e.printStackTrace();
					fail();
				}
				assertTrue(timemodified > 0);
				assertNotNull(message);
				assertEquals(expected, grade, 0.0001);
				finishTest();
			}
		});
	}
	
	public void testAddComment() {			
		
		AjaxRequest.ajaxRequest(action + "&action=addcomment&posx=416&posy=124&pageno=1&format=1&comment=JUNIT", new AsyncCallback<AjaxData>() {
			
			@Override
			public void onSuccess(AjaxData result) {
				Map<String, String> output = AjaxRequest.getValueFromResult(result);
				int markerid = 0;
				String markername = null;
				long timemodified = 0;
				int id = 0;
				try {
					id = Integer.parseInt(output.get("id"));
					markerid = Integer.parseInt(output.get("markerid"));
					markername = output.get("markername");
					timemodified = Long.parseLong(output.get("timemodified"));
				} catch (Exception e) {
					e.printStackTrace();
					fail();
				}
				assertTrue(id > 0);
				assertTrue(markerid > 0);
				assertNotNull(markername);
				assertTrue(timemodified > 0);
				subtestDeleteComment(id);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				fail();
			}
		});
	}
		
	private void subtestDeleteComment(int id) {
		
		AjaxRequest.ajaxRequest(action + "&action=deletecomment&id="+id, new AsyncCallback<AjaxData>() {
			
			@Override
			public void onSuccess(AjaxData result) {
				Map<String, String> output = AjaxRequest.getValueFromResult(result);
				long timemodified = 0;
				int id = 0;
				try {
					id = Integer.parseInt(output.get("id"));
					timemodified = Long.parseLong(output.get("timemodified"));
				} catch (Exception e) {
					e.printStackTrace();
					fail();
				}
				assertTrue(id > 0);
				assertTrue(timemodified > 0);
				finishTest();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				fail();
			}
		});
	}
}
