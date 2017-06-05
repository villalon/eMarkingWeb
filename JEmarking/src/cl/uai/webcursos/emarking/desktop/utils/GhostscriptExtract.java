package cl.uai.webcursos.emarking.desktop.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.ghost4j.Ghostscript;
import org.ghost4j.GhostscriptException;
import org.ghost4j.GhostscriptLoggerOutputStream;

import cl.uai.webcursos.emarking.desktop.data.Moodle;

public class GhostscriptExtract {

	private static Logger logger = Logger.getLogger(GhostscriptExtract.class);

	/**
	 * Extracts JPG images for a set of pages from a PDF file
	 * @param first page number of the first page to extract
	 * @param last page number of the last page to extract
	 * @param resolution resolution (e.g. 300 for 300pp)
	 * @param tempdir temp directory
	 * @param pdffile the PDF file that will be processed
	 * @throws Exception
	 */
	public static void extractImagesFromPDF(int first, int last, int resolution, File tempdir, String pdffile) throws Exception {
		Path dir = tempdir.toPath();
		File inputfile = new File(pdffile);
		File tmpfile = new File(dir.toAbsolutePath() + "/input.pdf");

		logger.debug("PDF file to process: " + pdffile + " Temp file: " + tmpfile.getAbsolutePath());

		if(!tmpfile.exists() || tmpfile.getTotalSpace() == 0 || tmpfile.getTotalSpace() != inputfile.getTotalSpace()) {

			if(tmpfile.exists())
				tmpfile.delete();

			try {
				logger.debug("PDF copied to temp");
				FileUtils.copyFile(inputfile, tmpfile);
			} catch (IOException e1) {
				e1.printStackTrace();
				throw new Exception("Impossible to copy file");
			}
		}

		GhostscriptLoggerOutputStream gsloggerOutStream = new GhostscriptLoggerOutputStream(Level.OFF);
		Ghostscript gs = Ghostscript.getInstance();

		try {
			synchronized(gs) {
				gs.setStdOut(gsloggerOutStream);
				gs.setStdOut(gsloggerOutStream);

				//prepare Ghostscript interpreter parameters
				//refer to Ghostscript documentation for parameter usage
				String[] gsArgs = new String[10];
				gsArgs[0] = "-dSAFER";
				gsArgs[1] = "-dBATCH";
				gsArgs[2] = "-dNOPAUSE";
				gsArgs[3] = "-sDEVICE=jpeggray";
				gsArgs[4] = "-dJPEGQ=100";
				gsArgs[5] = "-r" + resolution;
				gsArgs[6] = "-dFirstPage=" + first;
				gsArgs[7] = "-dLastPage=" + last;
				gsArgs[8] = "-sOutputFile=" + dir.toAbsolutePath() + "/page%06d" + Moodle.imageExtension;
				gsArgs[9] = dir.toAbsolutePath() + "/input.pdf";

				//execute and exit interpreter
				gs.initialize(gsArgs);
				gs.exit();
				Ghostscript.deleteInstance();
			}
		} catch (GhostscriptException e) {
			logger.error("ERROR: " + e.getMessage());
			try {
				Ghostscript.deleteInstance();
			} catch (Exception ex) {
				logger.error("ERROR DELETING INSTANCE: " + ex.getMessage());				
			}
			throw new Exception("Impossible to extract images");
		}
	}
}
