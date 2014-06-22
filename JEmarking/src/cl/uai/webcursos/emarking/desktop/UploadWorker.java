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
package cl.uai.webcursos.emarking.desktop;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.swing.JOptionPane;
import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.ghost4j.document.DocumentException;

import cl.uai.webcursos.emarking.desktop.data.Activity;
import cl.uai.webcursos.emarking.desktop.data.Moodle;
import cl.uai.webcursos.emarking.desktop.data.MoodleWorkerEvent;
import cl.uai.webcursos.emarking.desktop.data.MoodleWorkerListener;

/**
 * @author Jorge Villalón
 *
 */
public class UploadWorker implements Runnable {

	private static Logger logger = Logger.getLogger(UploadWorker.class);

	private Moodle moodle = null;
	
	private int courseId = 0;

	private EventListenerList listenerList = null;

	/**
	 * Adds a listener for the uploading event
	 * @param l
	 */
	void addProcessingListener(MoodleWorkerListener l) {
		listenerList.add(MoodleWorkerListener.class, l);
	}

	protected void fireUploading(MoodleWorkerEvent e) {
		MoodleWorkerListener[] ls = listenerList.getListeners(MoodleWorkerListener.class);
		for (MoodleWorkerListener l : ls) {
			l.stepPerformed(e);
		}
	}

	protected void fireUploadFinished(MoodleWorkerEvent e) {
		MoodleWorkerListener[] ls = listenerList.getListeners(MoodleWorkerListener.class);
		for (MoodleWorkerListener l : ls) {
			l.processFinished(e);
		}
	}

	protected void fireUploadStarted(MoodleWorkerEvent e) {
		MoodleWorkerListener[] ls = listenerList.getListeners(MoodleWorkerListener.class);
		for (MoodleWorkerListener l : ls) {
			l.processStarted(e);
		}
	}

	void removeProcessingListener(MoodleWorkerListener l) {
		listenerList.remove(MoodleWorkerListener.class, l);
	}

	private Activity activity;
	private boolean merge;
	private String newactivityname;
	private List<File> filesToUpload;


	/**
	 * @param moodle
	 * @param activity
	 * @param merge
	 * @param newactivityname
	 * @param fileToUpload
	 */
	public UploadWorker(Moodle moodle, Activity activity, boolean merge,
			String newactivityname, List<File> filesToUpload, int courseId) {
		listenerList = new EventListenerList();
		this.moodle = moodle;
		this.activity = activity;
		this.merge = merge;
		this.newactivityname = newactivityname;
		this.filesToUpload = filesToUpload;
		this.courseId = courseId;
	}

	/**
	 * 
	 * @param pdfdoc
	 * @param totalpages
	 * @param tempdir
	 * @throws DocumentException 
	 */
	@Override
	public void run() {

		logger.debug("Starting upload worker");
		logger.debug("Files to upload: " + this.filesToUpload.size());

		int totalBytes = 0;

		for(File fileToUpload : this.filesToUpload) {
			if(this.activity != null) {
				logger.debug("Activity: " + this.activity.getId() + " " + this.activity.getName());
			}
			logger.debug("New activity name:" + this.newactivityname);
			logger.debug("Merge:" + this.merge);

			String CrLf = "\r\n";

			String mergestring = merge ? "1" : "0";

			String uploadUrl = moodle.getUrl() + Moodle.EMARKING_MODULE_URL + 
					"?action=upload" + 
					"&course=" + this.courseId + 
					"&merge=" + mergestring +
					"&username="+this.moodle.getUsername() + 
					"&password="+this.moodle.getPassword();

			String cleanNewActivityName;
			try {
				cleanNewActivityName = URLEncoder.encode(newactivityname, "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, "Fatal error, could not encode using UTF-8. Check your java installation.");
				return;
			}

			if(activity != null) {
				uploadUrl += "&nmid=" + activity.getId();
			} else {
				uploadUrl += "&nmid=-666&name=" + cleanNewActivityName;			
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

				totalBytes += imgData.length;

				// SEND THE ZIP FILE
				int index = 0;
				int size = 1024;
				do {
					if ((index + size) > imgData.length) {
						size = imgData.length - index;
					}
					os.write(imgData, index, size);
					os.flush();
					index += size;

					MoodleWorkerEvent e = new MoodleWorkerEvent(this, imgData.length, index, null);
					fireUploading(e);
				} while (index < imgData.length && !Thread.currentThread().isInterrupted());

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
				return;
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

			JsonArray jarr;
			try {
				jarr = this.moodle.parseMoodleResponse(response.toString());
			} catch (Exception e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, "Fatal error processing server response after uploading. Check the logs and notify administrator.");
				return;
			}
			JsonObject jobj = jarr.getJsonObject(0);
			int id = Integer.parseInt(jobj.getString("id"));
			String name = jobj.getString("name");
			this.activity = new Activity();
			this.activity.setId(id);
			this.activity.setName(name);

			logger.debug("Just added emarking id:" + id + " name:" + name);
		}

		MoodleWorkerEvent e = new MoodleWorkerEvent(this, totalBytes, totalBytes, null);
		fireUploadFinished(e);
	}
}
