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
//Import all needed packages
package cl.uai.webcursos.emarking.desktop.utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;

import cl.uai.webcursos.emarking.desktop.data.Moodle;
import cl.uai.webcursos.emarking.desktop.data.MoodleWorkerEvent;
import cl.uai.webcursos.emarking.desktop.data.MoodleWorkerListener;

public class ZipFile implements Runnable  {

	private static Logger logger = Logger.getLogger(ZipFile.class);
	int datalimit = 100 * 1024 * 1024;

	public int getDatalimit() {
		return datalimit / 1024 / 1024;
	}

	public void setDatalimit(int datalimit) {
		this.datalimit = datalimit * 1024 * 1024;
	}
	List<String> fileList;
	private static String SOURCE_FOLDER = "X:\\Reports"; //SourceFolder path
	private Moodle moodle;
	private List<File> zipfiles;
	private EventListenerList listenerList = null;

	public ZipFile(Moodle moodle)
	{
		this.moodle = moodle;
		fileList = new ArrayList<String>();
		SOURCE_FOLDER = this.moodle.getQr().getTempdirStringPath();
		this.listenerList = new EventListenerList();
		this.zipfiles = new ArrayList<File>();
		this.setDatalimit(moodle.getMaxZipSize());
	}

	/**
	 * Adds a listener for the uploading event
	 * @param l
	 */
	public void addProgressListener(MoodleWorkerListener l) {
		listenerList.add(MoodleWorkerListener.class, l);
	}

	public void removeProgressListener(MoodleWorkerListener l) {
		listenerList.remove(MoodleWorkerListener.class, l);
	}

	protected void fireProgressStarted(MoodleWorkerEvent e) {
		MoodleWorkerListener[] ls = listenerList.getListeners(MoodleWorkerListener.class);
		for (MoodleWorkerListener l : ls) {
			l.processStarted(e);
		}
	}
	protected void fireProgressFinished(MoodleWorkerEvent e) {
		MoodleWorkerListener[] ls = listenerList.getListeners(MoodleWorkerListener.class);
		for (MoodleWorkerListener l : ls) {
			l.processFinished(e);
		}
	}
	protected void fireFileAdded(MoodleWorkerEvent e) {
		MoodleWorkerListener[] ls = listenerList.getListeners(MoodleWorkerListener.class);
		for (MoodleWorkerListener l : ls) {
			l.stepPerformed(e);
		}
	}

	private List<File> zipIt(String zipFile)
	{
		byte[] buffer = new byte[1024];
		List<File> zips = new ArrayList<File>();

		try{
			int currentfile = 1;

			FileOutputStream fos = new FileOutputStream(zipFile + currentfile + ".zip");
			ZipOutputStream zos = new ZipOutputStream(fos);

			zips.add(new File(zipFile + currentfile + ".zip"));

			String laststudent = "";
			int studentdatasize = 0;
			int accumulateddata = 0;
			int currentimage = 0;
			for(String file : this.fileList){
				String zipfilename = zipFile + currentfile + ".zip";
				currentimage++;
				if(!file.equals("answers.txt")) {
					String[] parts = file.split("-");
					if(parts.length != 3) {
						logger.error("Invalid file in directory " + file);
					}
					if(!laststudent.equals(parts[0])) {
						laststudent = parts[0];
						accumulateddata += studentdatasize;
						studentdatasize = 0;

						if(accumulateddata > datalimit) {
							currentfile++;
							accumulateddata = 0;

							zos.closeEntry();
							zos.close();

							zipfilename = zipFile + currentfile + ".zip";
							logger.debug("New file created " + zipfilename);

							fos = new FileOutputStream(zipfilename);
							zos = new ZipOutputStream(fos);

							zips.add(new File(zipfilename));
						}
					}
				}

				ZipEntry ze= new ZipEntry(file);
				zos.putNextEntry(ze);

				FileInputStream in = 
						new FileInputStream(SOURCE_FOLDER + File.separator + file);

				int len;
				while ((len = in.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
					studentdatasize += len;
				}

				in.close();

				MoodleWorkerEvent e = new MoodleWorkerEvent(this, currentimage, this.fileList.size(), file);
				fireFileAdded(e);
			}

			zos.closeEntry();
			//remember close it
			zos.close();


		}catch(IOException ex){
			ex.printStackTrace();   
		}

		return zips;
	}

	private void generateFileList(File node){

		//add file only
		if(node.isFile())
		{
			fileList.add(generateZipEntry(node.toString()));

		}

		if(node.isDirectory())
		{
			String[] subNote = node.list();
			for(String filename : subNote){
				generateFileList(new File(node, filename));
			}
		}

	}

	public List<File> getZipFiles() {
		return this.zipfiles;
	}

	private String generateZipEntry(String file)
	{
		return file.substring(SOURCE_FOLDER.length()+1, file.length());
	}

	@Override
	public void run() {

		if(moodle.isAnswerSheets()) {
			Path path = Paths.get(moodle.getQr().getTempdirStringPath() + "/answers.txt");
			logger.info("Saving answers to " + path.toString());
			try {
				Files.write(path, moodle.getStudentOMRAnswers().getBytes());
			} catch (IOException e1) {
				logger.error("Error writing answers");
				e1.printStackTrace();
			}
		}

		this.generateFileList(new File(moodle.getQr().getTempdirStringPath()));
		logger.debug("Files to include in zip:" + this.fileList.size());

		MoodleWorkerEvent e = new MoodleWorkerEvent(this, 0, this.fileList.size(), "");
		fireProgressStarted(e);

		File zipfile = null;
		try {
			zipfile = File.createTempFile("emarking", ".zip");
			zipfiles = this.zipIt(zipfile.getAbsolutePath());
		} catch (IOException ex) {
			ex.printStackTrace();
			logger.error(ex.getMessage());
		}

		logger.debug("Zip process finished");
		e = new MoodleWorkerEvent(this, this.fileList.size(), this.fileList.size(), "");
		fireProgressFinished(e);			
	}

	/**
	 * Unzip it
	 * @param zipFile input zip file
	 * @param output zip file output folder
	 */
	public int unZipIt(String zipFile) {

		byte[] buffer = new byte[1024];
		int totalFiles = 0;
		try{

			//create output directory if not exists
			File folder = moodle.getQr().getTempdir();
			if(!folder.exists()){
				folder.mkdir();
			}

			//get the zip file content
			ZipInputStream zis = 
					new ZipInputStream(new FileInputStream(zipFile));
			//get the zipped file list entry
			ZipEntry ze = zis.getNextEntry();

			while(ze!=null){

				String fileName = ze.getName();
				File newFile = new File(moodle.getQr().getTempdir() + File.separator + fileName);

				//create all non exists folders
				//else you will hit FileNotFoundException for compressed folder
				new File(newFile.getParent()).mkdirs();

				FileOutputStream fos = new FileOutputStream(newFile);             

				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}

				fos.close();   
				ze = zis.getNextEntry();
				totalFiles++;
			}

			zis.closeEntry();
			zis.close();

			logger.info("Done");

			return totalFiles;

		} catch(IOException ex){
			ex.printStackTrace(); 
		}
		return totalFiles;
	}    
}
