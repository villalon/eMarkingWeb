/**
 * 
 */
package cl.uai.webcursos.emarking.desktop;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
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
		// Obtain properties for log4j
		PropertyConfigurator.configure("log4j.properties");
		// Logging the start
		logger.info("Starting EMarking CLI");

		Option help = new Option( "help", "print this message" );
		Option debug = new Option( "debug", "saves QR extracted images for debugging" );
		Option doubleside = new Option( "doubleside", "PDF contains pages with both sides digitized" );
		Option pdf = Option
				.builder()
				.argName("file")
				.longOpt("pdf")
                .hasArg()
                .desc("digitized answers PDF file")
                .build();
		Option url = Option
				.builder()
				.argName("moodle url")
				.longOpt("url")
                .hasArg()
                .desc("full moodle url, e.g http://www.moodle.com/")
                .build();
		Option username = Option
				.builder()
				.argName("username")
				.longOpt("user")
                .hasArg()
                .desc("moodle user for reading enrollment information")
                .build();
		Option password = Option
				.builder()
				.argName("password")
				.longOpt("pwd")
                .hasArg()
                .desc("moodle user password")
                .build();
		Option tmpdir = Option
				.builder()
				.argName("path")
				.longOpt("tmp")
                .hasArg()
                .desc("directory for temporary files")
                .build();
		
		Options options = new Options();
		options.addOption(pdf);
		options.addOption(url);
		options.addOption(username);
		options.addOption(password);
		options.addOption(tmpdir);
		options.addOption(debug);
		options.addOption(doubleside);
		options.addOption(help);
		
        CommandLine line = null;
	    // create the parser
	    CommandLineParser parser = new DefaultParser();
	    try {
	        // parse the command line arguments
	        line = parser.parse( options, args );
	    }
	    catch( ParseException exp ) {
	        // oops, something went wrong
	        System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
	        System.exit(1);
	    }
	    if(line.hasOption("help")) {
	    	HelpFormatter formatter = new HelpFormatter();
	    	formatter.printHelp("java -jar emarking.jar", options);
	    	System.exit(0);
	    }
		// Obtain current locale for language settings
		Locale locale = Locale.getDefault();
		// Set language settings
		EmarkingDesktop.lang = ResourceBundle.getBundle("cl.uai.webcursos.emarking.desktop.lang", locale);

		if (!line.hasOption("pdf") || !line.hasOption("url") || !line.hasOption("user") || !line.hasOption("pwd")) {
	    	HelpFormatter formatter = new HelpFormatter();
	    	formatter.printHelp("java -jar emarking.jar", options);			
			System.exit(1);
		}

		eMarkingCli cli = null;
		try {
			cli = new eMarkingCli(line);
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
	public eMarkingCli(CommandLine line) throws Exception {

		UrlValidator validator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
		if (!validator.isValid(line.getOptionValue("url")) && !line.getOptionValue("url").contains(".online")) {
			throw new Exception("Invalid Moodle URL " + line.getOptionValue("url"));
		}

		File pdffile = new File(line.getOptionValue("pdf"));

		if (!pdffile.exists()) {
			throw new Exception("Invalid parameters. File does not exist:" + line.getOptionValue("pdf"));
		}

		File tmpdir = new File(line.getOptionValue("tmp"));

		if (!tmpdir.exists() || !tmpdir.isDirectory()) {
			throw new Exception("Invalid parameters. Temp dir does not exist:" + line.getOptionValue("tmp"));
		}

		moodle = new Moodle();
		moodle.loadProperties();

		moodle.setUrl(line.getOptionValue("url"));
		moodle.setUsername(line.getOptionValue("user"));
		moodle.setPassword(line.getOptionValue("pwd"));

		moodle.setDebugCorners(line.hasOption("debug"));		
		moodle.getQrExtractor().setDoubleside(line.hasOption("doubleside"));

		if (!moodle.connect()) {
			throw new Exception("Invalid parameters. Could not login to Moodle.");
		}

		PDFDocument document = new PDFDocument();
		document.load(new File(line.getOptionValue("pdf")));
		int totalpages = document.getPageCount();

		if (totalpages == 0) {
			throw new Exception("PDF contains no pages.");
		}

		File tempdir = new File(line.getOptionValue("tmp"));
		
		qr = moodle.getQrExtractor();
		qr.setPdffile(line.getOptionValue("pdf"));
		qr.setFileType(FileType.PDF);
		qr.setTempdir(tempdir);
		qr.setTotalpages(totalpages);
	}

	public void run() {
		qr.run();
	}
}
