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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.event.EventListenerList;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.ghost4j.Ghostscript;
import org.ghost4j.GhostscriptException;
import org.ghost4j.document.DocumentException;

/**
 * @author Jorge Villalón
 *
 */
public class QRextractor implements Runnable {

	/**
	 * @return the fileType
	 */
	public FileType getFileType() {
		return fileType;
	}

	/**
	 * @param fileType the fileType to set
	 */
	public void setFileType(FileType fileType) {
		this.fileType = fileType;
	}

	public enum FileType {
		ZIP,
		PDF
	}

	private FileType fileType;


	private static Logger logger = Logger.getLogger(QRextractor.class);

	private File tempdir = null;
	private int totalpages = 0;
	private int maxpages = Integer.MAX_VALUE;
	private int threads = 4;
	private int step = 32;
	private int resolution = 100;
	public int getResolution() {
		return resolution;
	}

	public void setResolution(int resolution) {
		this.resolution = resolution;
	}

	private boolean doubleside = false;

	private String pdffile = null;

	private TreeMap<Integer, Map<Integer, String>> decodedpages = null;

	private TreeMap<Integer, String> errorpages = null;

	private EventListenerList listenerList = null;

	public QRextractor() {
		listenerList = new EventListenerList();
		decodedpages = new TreeMap<Integer, Map<Integer, String>>();
		errorpages = new TreeMap<Integer, String>();
	}

	/**
	 * Adds a listener for the QR decoded event
	 * @param l
	 */
	void addPageProcessedListener(PageProcessedListener l) {
		listenerList.add(PageProcessedListener.class, l);
	}

	/**
	 * Removes a listener for the QR decoded event
	 * @param l
	 */
	void removePageProcessedListener(PageProcessedListener l) {
		listenerList.remove(PageProcessedListener.class, l);
	}

	protected void firePageProcessed(QRExtractorEvent e) {
		PageProcessedListener[] ls = listenerList.getListeners(PageProcessedListener.class);
		for (PageProcessedListener l : ls) {
			l.processed(e);
		}
	}

	protected void fireExtractionFinished(QRExtractorEvent e) {
		PageProcessedListener[] ls = listenerList.getListeners(PageProcessedListener.class);
		for (PageProcessedListener l : ls) {
			l.finished(e);
		}
	}

	protected void fireExtractionStarted(QRExtractorEvent e) {
		PageProcessedListener[] ls = listenerList.getListeners(PageProcessedListener.class);
		for (PageProcessedListener l : ls) {
			l.started(e);
		}
	}

	/**
	 * @return the decodedpages
	 */
	public TreeMap<Integer, Map<Integer, String>> getDecodedpages() {
		return decodedpages;
	}

	/**
	 * @return the errorpages
	 */
	public TreeMap<Integer, String> getErrorpages() {
		return errorpages;
	}

	/**
	 * @return the maxpages
	 */
	public int getMaxpages() {
		return maxpages;
	}

	public Object[][] getResultsAsData() {
		Object[][] data = new Object[totalpages][2];

		for(int i=0; i<totalpages; i++) {
			data[i][0] = i+1;
			data[i][1] = "N/A";
		}

		return data;
	}

	/**
	 * @return the step
	 */
	public int getStep() {
		return step;
	}

	/**
	 * @return the tempdir
	 */
	public File getTempdir() {
		return tempdir;
	}
	/**
	 * @return the totalpages
	 */
	public int getTotalpages() {
		return totalpages;
	}

	/**
	 * @return the doubleside
	 */
	public boolean isDoubleside() {
		return doubleside;
	}	

	void removeQRdecodeListener(PageProcessedListener l) {
		listenerList.remove(PageProcessedListener.class, l);
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

		logger.debug("Starting Ghost4j");

		logger.debug("Document: " + this.pdffile);
		logger.debug("Total pages: " + totalpages);
		logger.debug("Tempdir: " + this.tempdir);
		logger.debug("Resolution: " + this.resolution + "ppp");
		logger.debug("Maxpages: " + this.maxpages);
		logger.debug("Threads: " + this.threads);
		logger.debug("Doubleside: " + this.doubleside);
		logger.debug("Step: " + this.step);
		logger.debug("File type: " + this.fileType);

		long time = System.currentTimeMillis();
		int currentpage = 0;

		// Checking if number of pages is even for double sided scanning jobs
		if(doubleside && step % 2 != 0)
			step++;

		// Checking that maxpages is also even for double sided scanning jobs
		if(doubleside && maxpages % 2 != 0 && maxpages < totalpages)
			maxpages++;

		if(totalpages > maxpages)
			totalpages = maxpages;

		while(totalpages > currentpage && currentpage < maxpages) {

			if(Thread.currentThread().isInterrupted())
				break;

			// Measuring time per step
			long timeperstep = System.currentTimeMillis();

			// Calculate last page index, either the current page plus a step or the total pages 
			int lastpage = Math.min(currentpage+step-1, totalpages - 1);
			logger.debug("Extracting images from " + (currentpage + 1) + " to " + (lastpage + 1));

			// We start with an empty array for images
			List<BufferedImage> images = null;
			// Images are exracted using the SimpleRenderer from Ghost4j
			try {
				images = getImages(currentpage+1, lastpage+1, pdffile);
				long timeperstepextract = System.currentTimeMillis() - timeperstep;
				logger.debug("Extraction of " + images.size() + " pages finished in " + (timeperstepextract / 1000) + " seconds");
			} catch (Exception e3) {
				e3.printStackTrace();
				break;
			}

			// As we don't really know how many pages will Ghost4j extract
			lastpage = currentpage + images.size();


			// Iterate through extracted images
			for (int i = 0; i < images.size(); i+=threads) {

				// Checks if a cancel button was pressed while processing, in which case we stop
				if(Thread.currentThread().isInterrupted())
					break;

				ImageDecoder[] decoders = new ImageDecoder[threads];
				Thread[] decoderthreads = new Thread[threads];

				for(int j=0; j<threads; j++) {
					// This is the current page
					int filenumber = currentpage;

					// Checks if a cancel button was pressed while processing, in which case we stop
					if(Thread.currentThread().isInterrupted())
						break;

					if(i+j >= images.size())
						break;

					BufferedImage currentpageimage = (BufferedImage) images.get(i+j);
					BufferedImage nextpageimage = null;
					if(doubleside) {
						nextpageimage = currentpage <= lastpage && images.size() > i + j + 1 ? 
								(BufferedImage) images.get(i+j+1) :
									null;
					}

					decoders[j] = new ImageDecoder(
							currentpageimage, 
							nextpageimage, 
							filenumber,
							tempdir);

					decoderthreads[j] = new Thread(decoders[j]);
					decoderthreads[j].start();

					currentpage++;

					if(doubleside) {
						currentpage++;
						i++;
					}
				}

				for(int j=0; j<threads; j++) {
					try {
						if(decoderthreads[j] != null)
							decoderthreads[j].join();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}

				for(int j=0; j<threads; j++) {

					// Checks if a cancel button was pressed while processing, in which case we stop
					if(Thread.currentThread().isInterrupted())
						break;

					ImageDecoder decoder = decoders[j];
					if(decoder == null)
						continue;

					QrDecodingResult result = decoder.getQrResult();
					if(decoder.isSuccess()) {
						Map<Integer, String> pages = null;
						if(decodedpages.containsKey(result.getUserid())) {
							pages = decodedpages.get(result.getUserid());
						} else {
							pages = new Hashtable<Integer, String>();
							decodedpages.put(result.getUserid(), pages);
						}
						pages.put(result.getExampage(), result.getFilename());
						if(doubleside) {
							pages.put(result.getExampage(), result.getBackfilename());
						}
					} else {
						errorpages.put(decoder.getFilenumber(), result.getFilename());
						if(doubleside)
							errorpages.put(decoder.getFilenumber(), result.getBackfilename());
					}

					QRExtractorEvent e = new QRExtractorEvent(this, decoder.getQrResult(), false);
					firePageProcessed(e);

					if(doubleside) {
						QRExtractorEvent e2 = new QRExtractorEvent(this, decoder.getQrResult(), true);
						firePageProcessed(e2);
					}
				}
			}

			timeperstep = System.currentTimeMillis() - timeperstep;
			logger.debug("Extraction + Processing finished in " + (timeperstep / 1000) + " seconds");
		}

		time = System.currentTimeMillis() - time;
		logger.debug("Total Extraction finished in " + (time / 1000) + " seconds");

		logger.debug("Decoded pages:" + decodedpages.size());
		logger.debug("Error pages:" + errorpages.size());

		QRExtractorEvent e = new QRExtractorEvent(this, null, false);
		fireExtractionFinished(e);
	}

	/**
	 * @param doubleside the doubleside to set
	 */
	public void setDoubleside(boolean doubleside) {
		this.doubleside = doubleside;
		this.setStep();
	}

	/**
	 * @param maxpages the maxpages to set
	 */
	public void setMaxpages(int maxpages) {
		this.maxpages = maxpages;
	}

	/**
	 * @param tempdir the tempdir to set
	 */
	public void setTempdir() {	
		try {
			this.tempdir = Files.createTempDirectory("emarking").toFile();
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public String getTempdirStringPath() {
		return this.tempdir.getAbsolutePath();
	}

	/**
	 * @param totalpages the totalpages to set
	 */
	public void setTotalpages(int totalpages) {
		this.totalpages = totalpages;
	}

	private List<BufferedImage> getImages(int first, int last, String pdffile) throws Exception {

		// If we are processing a PDF file we use ghostscript to read it
		if(this.fileType == FileType.PDF) {
			File inputfile = new File(pdffile);
			File tmpfile = new File("input.pdf");
			if(tmpfile.exists())
				tmpfile.delete();

			try {
				FileUtils.copyFile(inputfile, tmpfile);
			} catch (IOException e1) {
				e1.printStackTrace();
				throw new Exception("Impossible to copy file");
			}

			Ghostscript gs = Ghostscript.getInstance();

			//prepare Ghostscript interpreter parameters
			//refer to Ghostscript documentation for parameter usage
			String[] gsArgs = new String[9];
			gsArgs[0] = "-dSAFER";
			gsArgs[1] = "-dBATCH";
			gsArgs[2] = "-dNOPAUSE";
			gsArgs[3] = "-sDEVICE=pnggray";
			gsArgs[4] = "-r" + resolution;
			gsArgs[5] = "-dFirstPage=" + first;
			gsArgs[6] = "-dLastPage=" + last;
			gsArgs[7] = "-sOutputFile=tmpfigure%d.png";
			gsArgs[8] = pdffile;

			//execute and exit interpreter
			try {
				gs.initialize(gsArgs);
				gs.exit();
			} catch (GhostscriptException e) {
				logger.error("ERROR: " + e.getMessage());
				throw new Exception("Impossible to extract images");
			}

			List<BufferedImage> images = new ArrayList<BufferedImage>();

			for(int i=1;i<=last-first+1;i++) {
				try {
					File f = new File("tmpfigure"+ i +".png");
					images.add(ImageIO.read(f));
					f.delete();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return images;
		} else if(this.fileType == FileType.ZIP) {

			List<BufferedImage> images = new ArrayList<BufferedImage>();

			for(int i=first;i<=last;i++) {
				String filename = this.tempdir.getAbsolutePath() + "/Prueba_"+ i +".png";
				try {
					File f = new File(filename);
					images.add(ImageIO.read(f));
					f.delete();
				} catch (IOException e) {
					e.printStackTrace();
					logger.error("Could not read file " + filename);
				}
			}

			return images;
		} else {
			throw new Exception("Invalid file type");
		}
	}

	public String getPdffile() {
		return pdffile;
	}

	public void setPdffile(String pdffile) {
		this.pdffile = pdffile;
	}

	public int getMaxThreads() {
		return this.threads;
	}	

	public void setMaxThreads(int _threads) {
		this.threads = _threads;
		this.setStep();
	}

	private void setStep() {
		this.step = this.doubleside ? this.threads * 4 : this.threads * 2;		
	}
}
