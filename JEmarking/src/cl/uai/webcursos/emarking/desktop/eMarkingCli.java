/**
 * 
 */
package cl.uai.webcursos.emarking.desktop;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.validator.routines.UrlValidator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.ghost4j.document.PDFDocument;

import cl.uai.webcursos.emarking.desktop.QRextractor.FileType;
import cl.uai.webcursos.emarking.desktop.data.Moodle;

/**
 * @author villalon
 *
 */
public class eMarkingCli {

	private static Logger logger = Logger.getLogger(eMarkingCli.class);
	private Moodle moodle = null;
	private QRextractor qr = null;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) {

		// Obtain current locale for language settings
		Locale locale = Locale.getDefault();
		// Set language settings
		EmarkingDesktop.lang = ResourceBundle.getBundle("cl.uai.webcursos.emarking.desktop.lang", locale);

		if (args.length < 6) {
			System.err.println(
					"Invalid arguments. Usage: java -jar emarking.jar http://moodle_url/ username password pdffile tempdir log4j.properties");
			System.exit(1);
		}

		try {
			File log4jfile = new File(args[5]);

			if (!log4jfile.exists()) {
				throw new Exception("Log4j file does not exist.");
			}
		} catch (Exception e) {
			System.err.println("Invalid parameters. Log 4j file does not exist:" + args[5]);
			System.exit(2);
		}
		// Obtain properties for log4j
		PropertyConfigurator.configure(args[5]);
		// Logging the start
		logger.info("Starting EMarking CLI");

		eMarkingCli cli = null;
		try {
			cli = new eMarkingCli(args);
			cli.run();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Fatal error executing extraction.");
		}

		logger.info("Finished EMarking CLI");
	}

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public eMarkingCli(String[] args) throws Exception {

		UrlValidator validator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
		if (!validator.isValid(args[0])) {
			throw new Exception("Invalid Moodle URL " + args[0]);
		}

		if (args[1].length() < 3 || args[2].length() < 3) {
			throw new Exception("Invalid username and/or password ");
		}

		File pdffile = new File(args[3]);

		if (!pdffile.exists()) {
			throw new Exception("Invalid parameters. File does not exist:" + args[3]);
		}

		File tmpdir = new File(args[4]);

		if (!tmpdir.exists() || !tmpdir.isDirectory()) {
			throw new Exception("Invalid parameters. Temp dir does not exist:" + args[4]);
		}

		moodle = new Moodle();
		moodle.loadProperties();

		moodle.setUrl(args[0]);
		moodle.setUsername(args[1]);
		moodle.setPassword(args[2]);

		if (!moodle.connect()) {
			throw new Exception("Invalid parameters. Could not login to Moodle.");
		}

		PDFDocument document = new PDFDocument();
		document.load(new File(args[3]));
		int totalpages = document.getPageCount();

		if (totalpages == 0) {
			throw new Exception("PDF contains no pages.");
		}

		qr = new QRextractor(moodle);
		qr.setPdffile(args[3]);
		qr.setFileType(FileType.PDF);
		qr.setTempdir(args[4]);
		qr.setTotalpages(totalpages);
	}

	public void run() {
		qr.run();
	}
}
