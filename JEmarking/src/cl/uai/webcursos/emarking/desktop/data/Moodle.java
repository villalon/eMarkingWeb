/*******************************************************************************
 * This file is part of Moodle - http://moodle.org/
 * 
 * Moodle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Moodle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Moodle.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @package cl.uai.webcursos.emarking
 * @copyright 2014 Jorge Villalón {@link http://www.villalon.cl}
 * @license   http://www.gnu.org/copyleft/gpl.html GNU GPL v3 or later
 ******************************************************************************/
/**
 * 
 */
package cl.uai.webcursos.emarking.desktop.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Properties;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import cl.uai.webcursos.emarking.desktop.EmarkingDesktop;
import cl.uai.webcursos.emarking.desktop.QRextractor;

/**
 * This class contains all the interactions with the information to be processed
 * in eMarking.
 * 
 * It deals with the Moodle connection, the QRextractor for identifying QR codes and
 * manages the extracted images.
 * 
 * @author Jorge Villalón
 *
 */
public class Moodle {
	
	/** For logging **/
	private static Logger logger = Logger.getLogger(Moodle.class);

	/** User agent to use when connecting to Moodle **/
	public static final String USER_AGENT = "Mozilla/5.0";
	/** Moodle installation URL **/
	private String moodleUrl;
	/** The Ajax url within Moodle **/
	private String moodleAjaxUrl;
	/** Moodle username **/
	private String moodleUsername;
	/** Moodle password **/
	private String moodlePassword;
	/** Scanning was made for both sides of pages **/
	private boolean doubleSide;
	public int getMaxthreads() {
		return qrExtractor.getMaxThreads();
	}

	public String getMaxzipsize() {
		return maxzipsize;
	}

	/** Max zip size before generating multiple zips **/
	private String maxzipsize = "64Mb";
	/** The scanned and processed pages **/
	private Pages studentPages;
	/** The QR extractor **/
	private QRextractor qrExtractor;
	/** Stores the last file processed by user **/
	private String lastfile;

	/** OMR template for parsing bubbles **/
	private String omrTemplate;
	private int threshold = 127;
	private int density = 40;
	private int shapesize = 8;
	
	/**
	 * @return the QR extractor
	 */
	public QRextractor getQr() {
		return qrExtractor;
	}

	/**
	 * @return the pages
	 */
	public Pages getPages() {
		return studentPages;
	}
	
	public Moodle() {
		this.qrExtractor = new QRextractor(this);
		this.clearPages();
	}

	public void clearPages() {
		this.qrExtractor.setTempdir();
		this.studentPages = new Pages(this);
		this.students = new Hashtable<Integer, Student>();
		this.courses = new Hashtable<Integer, Course>();
	}

	public QRextractor getQrExtractor() {
		return this.qrExtractor;
	}

	/**
	 * @return the doubleside
	 */
	public boolean isDoubleside() {
		return doubleSide;
	}

	/**
	 * @param doubleside the doubleside to set
	 */
	public void setDoubleside(boolean doubleside) {
		this.doubleSide = doubleside;
	}

	private Hashtable<Integer, Student> students = new Hashtable<Integer, Student>();
	private Hashtable<Integer, Course> courses = new Hashtable<Integer, Course>();
	private Hashtable<Integer, Course> usercourses = new Hashtable<Integer, Course>();

	public boolean connect() {
		try {
			retrieveCourses();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void retrieveStudents(int courseId) throws Exception {

		String response = makeMoodleRequest(getMoodleAjaxUrl() + "?action=students&course="+courseId+"&username="+moodleUsername+"&password="+moodlePassword);

		JsonArray jarr = parseMoodleResponse(response);

		if(students == null)
			students = new Hashtable<Integer, Student>();

			for(int i=0;i<jarr.size();i++) {
				try {
					JsonObject job = jarr.getJsonObject(i);
					int id = Integer.parseInt(job.getString("id"));			
					String idnumber = job.getString("idnumber");			
					String studentname = job.getString("lastname") + ", " + job.getString("firstname");

					Student st = new Student();
					st.setId(id);
					st.setIdnumber(idnumber);
					st.setFullname(studentname);

					if(!students.containsKey(id)) {
						st.setRownumber(students.keySet().size());
						students.put(id, st);
					}

					logger.debug("id:" + id + " student:" + studentname + " idnumber:" + idnumber);
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
	}

	private void retrieveCourses() throws Exception {

		String response = makeMoodleRequest(getMoodleAjaxUrl() + "?action=courses&username="+moodleUsername+"&password="+moodlePassword);

		JsonArray jarr = parseMoodleResponse(response);

		usercourses = new Hashtable<Integer, Course>();

		for(int i=0;i<jarr.size();i++) {
			try {
				JsonObject job = jarr.getJsonObject(i);
				int id = Integer.parseInt(job.getString("id"));			
				String shortname = job.getString("shortname");			
				String fullname = job.getString("fullname");

				Course st = new Course();
				st.setId(id);
				st.setShortname(shortname);
				st.setFullname(fullname);

				usercourses.put(id, st);

				logger.debug("id:" + id + " shortname:" + shortname + " fullname:" + fullname);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	public Course[] searchCourses(String q) throws Exception {

		String response = makeMoodleRequest(getMoodleAjaxUrl() + "?action=coursesearch&username="+moodleUsername+"&password="+moodlePassword+"&q="+q);

		JsonArray jarr = parseMoodleResponse(response);

		Course[] courses = new Course[jarr.size()];

		for(int i=0;i<jarr.size();i++) {
			try {
				JsonObject job = jarr.getJsonObject(i);
				int id = Integer.parseInt(job.getString("id"));			
				String shortname = job.getString("shortname");			
				String fullname = job.getString("fullname");

				Course st = new Course();
				st.setId(id);
				st.setShortname(shortname);
				st.setFullname(fullname);

				courses[i] = st;

				logger.debug("id:" + id + " shortname:" + shortname + " fullname:" + fullname);
			} catch (Exception e) {
				logger.error(e.getMessage());
				return null;
			}
		}

		return courses;
	}

	public void retrieveCourseFromId(int courseid) throws Exception {

		String response = makeMoodleRequest(getMoodleAjaxUrl() + "?action=courseinfo&course="+courseid+"&username="+moodleUsername+"&password="+moodlePassword);

		JsonArray jarr = parseMoodleResponse(response);
		JsonObject job = jarr.getJsonObject(0);

		int id = Integer.parseInt(job.getString("id"));			
		String shortname = job.getString("shortname");			
		String fullname = job.getString("fullname");

		Course st = new Course();
		st.setId(id);
		st.setShortname(shortname);
		st.setFullname(fullname);

		courses.put(id, st);
	}

	private Hashtable<Integer, Activity> retrieveEmarkingActivities(int courseid) throws Exception {

		String response = makeMoodleRequest(getMoodleAjaxUrl() + "?action=activities&course="+courseid+"&username="+moodleUsername+"&password="+moodlePassword);

		Hashtable<Integer, Activity> activities = new Hashtable<Integer, Activity>();
		JsonArray jarr = parseMoodleResponse(response);
		for(int i=0;i<jarr.size();i++) {
			try {
				JsonObject job = jarr.getJsonObject(i);
				int id = Integer.parseInt(job.getString("id"));			
				String name = job.getString("name");

				Activity st = new Activity();
				st.setId(id);
				st.setName(name);

				activities.put(id, st);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return activities;
	}

	/**
	 * @return the courses
	 */
	public Hashtable<Integer, Course> getCourses() {
		return courses;
	}

	/**
	 * @return the students
	 */
	public Hashtable<Integer, Student> getStudents() {
		return students;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return moodleUrl;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.moodleUrl = url;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return moodleUsername;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.moodleUsername = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return moodlePassword;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.moodlePassword = password;
	}

	private String makeMoodleRequest(String urlpostfix) throws Exception {
		URL obj = new URL(moodleUrl + urlpostfix);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		logger.debug("Sending 'GET' request to URL : " + moodleUrl + urlpostfix.replaceAll("password=.*", "password=xxxx&"));
		logger.debug("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		return response.toString();
	}

	public boolean uploadFile(File fileToUpload, Activity activity, String newactivityname, boolean merge, int courseId) throws Exception {
		String CrLf = "\r\n";

		String mergestring = merge ? "1" : "0";

		String uploadUrl = moodleUrl + getMoodleAjaxUrl() + 
				"?action=upload" + 
				"&course=" + courseId + 
				"&merge=" + mergestring +
				"&username="+this.moodleUsername + 
				"&password="+this.moodlePassword;

		if(activity != null) {
			uploadUrl += "&nmid=" + activity.getId();
		} else {
			uploadUrl += "&nmid=-666&name=" + URLEncoder.encode(newactivityname, "UTF-8");			
		}

		URLConnection conn = null;
		OutputStream os = null;
		InputStream is = null;

		StringBuffer response = new StringBuffer();

		try {
			URL url = new URL(uploadUrl);
			logger.debug("url:" + url);
			conn = url.openConnection();
			conn.setDoOutput(true);

			InputStream imgIs = new FileInputStream(fileToUpload);
			byte[] imgData = new byte[imgIs.available()];
			imgIs.read(imgData);
			imgIs.close();

			String message1 = "";
			message1 += "-----------------------------4664151417711" + CrLf;
			message1 += "Content-Disposition: form-data; name=\"uploadedfile\"; filename=\""+ fileToUpload.getName() +"\""
					+ CrLf;
			message1 += "Content-Type: application/zip" + CrLf;
			message1 += CrLf;

			// the image is sent between the messages in the multipart message.

			String message2 = "";
			message2 += CrLf + "-----------------------------4664151417711--"
					+ CrLf;

			conn.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=---------------------------4664151417711");
			// might not need to specify the content-length when sending chunked
			// data.
			conn.setRequestProperty("Content-Length", String.valueOf((message1
					.length() + message2.length() + imgData.length)));

			os = conn.getOutputStream();

			os.write(message1.getBytes());

			// SEND THE IMAGE
			int index = 0;
			int size = 1024;
			do {
				if ((index + size) > imgData.length) {
					size = imgData.length - index;
				}
				os.write(imgData, index, size);
				index += size;
			} while (index < imgData.length);

			os.write(message2.getBytes());
			os.flush();

			is = conn.getInputStream();


			char buff = 512;
			int len;
			byte[] data = new byte[buff];
			do {
				len = is.read(data);

				if (len > 0) {
					response.append(new String(data, 0, len));
				}
			} while (len > 0);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				os.close();
			} catch (Exception e) {
			}
			try {
				is.close();
			} catch (Exception e) {
			}
			try {

			} catch (Exception e) {
			}
		}

		logger.debug(response.toString());

		JsonArray jarr = parseMoodleResponse(response.toString());
		JsonObject jobj = jarr.getJsonObject(0);
		int id = Integer.parseInt(jobj.getString("id"));
		String name = jobj.getString("name");

		logger.debug("Just added emarking id:" + id + " name:" + name);
		JOptionPane.showMessageDialog(null, EmarkingDesktop.lang.getString("uploadsuccessfull"));

		return true;
	}

	public JsonArray parseMoodleResponse(String response) throws Exception {
		JsonReader jsonreader = Json.createReader(new StringReader(response));
		JsonObject jobj = jsonreader.readObject();
		String error = jobj.getString("error");
		logger.debug("Error code:"+error);

		if(error.trim().equals("")) {
			jsonreader = Json.createReader(new StringReader(jobj.get("values").toString()));
			JsonArray jarr = jsonreader.readArray();
			return jarr;
		} else {
			logger.error(error);
			throw new Exception(error);
		}
	}

	public void loadProperties() {
		Properties p = new Properties();
		File f = new File("moodle.properties");
		if(f.exists()) {
			try {
				p.load(new FileInputStream(f));
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
		if(p.containsKey("moodleurl")) {
			setUrl(p.getProperty("moodleurl"));
		}
		if(p.containsKey("username")) {
			setUsername(p.getProperty("username"));
		}
		if(p.containsKey("filename")) {
			setLastfile(p.getProperty("filename"));
		}
		if(p.containsKey("doubleside")) {
			setDoubleside(p.getProperty("doubleside").equals("true"));
		}
		if(p.containsKey("maxthreads")) {
			setMaxthreads(Integer.parseInt(p.getProperty("maxthreads")));
		}
		if(p.containsKey("resolution")) {
			setResolution(Integer.parseInt(p.getProperty("resolution")));
		}
		if(p.containsKey("maxzipsize")) {
			setMaxzipsize(p.getProperty("maxzipsize"));
		}
		if(p.containsKey("ajaxurl")) {
			setMoodleAjaxUrl(p.getProperty("ajaxurl"));
		}
		if(p.containsKey("omrtemplate")) {
			setOMRTemplate(p.getProperty("omrtemplate"));
		}
		if(p.containsKey("threshold")) {
			setThreshold(Integer.parseInt(p.getProperty("threshold")));
		}
		if(p.containsKey("density")) {
			setDensity(Integer.parseInt(p.getProperty("density")));
		}
		if(p.containsKey("shapesize")) {
			setShapeSize(Integer.parseInt(p.getProperty("shapesize")));
		}
	}

	public void setResolution(int resolution) {
		this.qrExtractor.setResolution(resolution);
	}

	public void setMaxthreads(int maxthreads) {
		this.qrExtractor.setMaxThreads(maxthreads);
	}

	public void setMaxzipsize(String maxzipsize) {
		this.maxzipsize = maxzipsize;
	}

	/**
	 * @return the lastfile
	 */
	public String getLastfile() {
		return lastfile;
	}

	/**
	 * @param lastfile the lastfile to set
	 */
	public void setLastfile(String lastfile) {
		this.lastfile = lastfile;
	}

	public void saveProperties() {
		Properties p = new Properties();
		File f = new File("moodle.properties");
		p.setProperty("moodleurl", this.moodleUrl);
		p.setProperty("username", this.moodleUsername);
		p.setProperty("filename", this.lastfile);
		p.setProperty("doubleside", this.doubleSide ? "true" : "false");
		p.setProperty("maxthreads", Integer.toString(this.qrExtractor.getMaxThreads()));
		p.setProperty("resolution", Integer.toString(this.qrExtractor.getResolution()));
		p.setProperty("maxzipsize", this.maxzipsize);
		p.setProperty("ajaxurl", this.moodleAjaxUrl);
		p.setProperty("omrtemplate", this.omrTemplate);
		p.setProperty("threshold", Integer.toString(this.threshold));
		p.setProperty("density", Integer.toString(this.density));
		p.setProperty("shapesize", Integer.toString(this.shapesize));
		try {
			p.store(new FileOutputStream(f), "eMarking for Moodle");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getMaxZipSize() {
		int datasize = 0;
		if(this.maxzipsize.equals(EmarkingDesktop.lang.getString("nosplit"))) {
			datasize = Integer.MAX_VALUE;
		} else {
			datasize = Integer.parseInt(this.maxzipsize.toLowerCase().replaceAll("mb", ""));
		}
		return datasize;
	}

	public String getMaxZipSizeString() {
		return this.maxzipsize;
	}

	public int getMaxThreads() {
		return this.qrExtractor.getMaxThreads();
	}

	public Hashtable<Integer, Activity> retrieveEmarkingActivities(
			Hashtable<Integer, Course> courses) {
		Hashtable<Integer, Activity> output = new Hashtable<Integer, Activity>();
		for(int courseid : courses.keySet()) {
			try {
				Hashtable<Integer, Activity> outputCourse = retrieveEmarkingActivities(courseid);
				output.putAll(outputCourse);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return output;
	}

	public Student getStudentByRowNumber(int row) {
		for(Student st : this.students.values()) {
			if(st.getRownumber() == row)
				return st;
		}
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public String getMoodleAjaxUrl() {
		return moodleAjaxUrl;
	}

	/**
	 * 
	 * @param moodleAjaxUrl
	 */
	public void setMoodleAjaxUrl(String moodleAjaxUrl) {
		this.moodleAjaxUrl = moodleAjaxUrl;
	}

	public void setOMRTemplate(String text) {
		this.omrTemplate = text;
	}
	
	public String getOMRTemplate() {
		return this.omrTemplate;
	}

	public int getOMRdensity() {
		return this.density;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	public void setDensity(int density) {
		this.density = density;
	}

	public void setShapeSize(int shapeSize) {
		this.shapesize = shapeSize;
	}

	public int getOMRthreshold() {
		return this.threshold;
	}

	public int getOMRshapeSize() {
		return this.shapesize;
	}
}
