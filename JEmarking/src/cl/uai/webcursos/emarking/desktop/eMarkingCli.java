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
import org.apache.commons.io.FileUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.ghost4j.document.PDFDocument;

import cl.uai.webcursos.emarking.desktop.QRextractor.FileType;
import cl.uai.webcursos.emarking.desktop.data.Moodle;
import cl.uai.webcursos.emarking.desktop.utils.GhostscriptExtract;

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
		Option help = new Option( "help", "print this message" );
		Option debug = new Option( "debug", "saves QR extracted images for debugging" );
		Option doubleside = new Option( "doubleside", "PDF contains pages with both sides digitized" );
		Option extractonly = new Option( "extractonly", "Extracts the images from the PDF but with no QR identification" );
		Option userid = Option
				.builder()
				.argName("uid")
				.longOpt("userid")
                .hasArg()
                .desc("id of the user for extract only option")
                .build();
		Option courseid = Option
				.builder()
				.argName("cid")
				.longOpt("courseid")
                .hasArg()
                .desc("id of the user for extract only option")
                .build();
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
		Option log4j = Option
				.builder()
				.argName("path")
				.longOpt("log4j")
                .hasArg()
                .desc("log4j properties file")
                .build();
		Option resolution = Option
				.builder()
				.argName("resolution")
				.longOpt("res")
                .hasArg()
                .desc("Resolution in pp (e.g 300pp)")
                .build();
				
				
		Options options = new Options();
		options.addOption(extractonly);
		options.addOption(userid);
		options.addOption(courseid);
		options.addOption(resolution);
		options.addOption(pdf);
		options.addOption(url);
		options.addOption(username);
		options.addOption(password);
		options.addOption(tmpdir);
		options.addOption(log4j);
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

	    File log4jproperties = new File(line.getOptionValue("log4j"));
		if(!log4jproperties.exists()) {
			System.err.println("Fatal error, could not load log4j properties");
			System.exit(1);
		}
		// Obtain properties for log4j
		PropertyConfigurator.configure(log4jproperties.getAbsolutePath());

		// Obtain current locale for language settings
		Locale locale = Locale.getDefault();
		// Set language settings
		EmarkingDesktop.lang = ResourceBundle.getBundle("cl.uai.webcursos.emarking.desktop.lang", locale);

		if (line.hasOption("extractonly")) {
			if((!line.hasOption("userid") || !line.hasOption("courseid")
					|| !line.hasOption("pdf") || !line.hasOption("tmp")
					 || !line.hasOption("log4j"))) {
			System.out.println("Invalid parameters for extract only");
	    	HelpFormatter formatter = new HelpFormatter();
	    	formatter.printHelp("java -jar emarking.jar", options);			
			System.exit(1);
			}
			
			try {
				extractImagesFromPDF(line);
				System.exit(0);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(e.hashCode());
			}
			
		} else if (!line.hasOption("pdf") || !line.hasOption("url") || !line.hasOption("user") || !line.hasOption("pwd") || !line.hasOption("log4j")) {
			System.out.println("Invalid parameters for QR processing");
	    	HelpFormatter formatter = new HelpFormatter();
	    	formatter.printHelp("java -jar emarking.jar", options);			
			System.exit(1);
		}
		
		// Logging the start
		logger.info("Starting EMarking CLI");

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
		
		PDFDocument document = new PDFDocument();
		document.load(new File(line.getOptionValue("pdf")));
		int totalpages = document.getPageCount();

		if (totalpages == 0) {
			throw new Exception("PDF contains no pages.");
		}

		int resolution = 0;
		if(line.hasOption("resolution")) {
			resolution = Integer.parseInt(line.getOptionValue("resolution"));
		}
		moodle = new Moodle();
		moodle.loadProperties();

		moodle.setUrl(line.getOptionValue("url"));
		moodle.setUsername(line.getOptionValue("user"));
		moodle.setPassword(line.getOptionValue("pwd"));
		
		if(resolution > 0) {
			moodle.setResolution(resolution);
		}

		moodle.setDebugCorners(line.hasOption("debug"));		
		moodle.getQrExtractor().setDoubleside(line.hasOption("doubleside"));

		if (!moodle.connect()) {
			throw new Exception("Invalid parameters. Could not login to Moodle.");
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
	
	private static void extractImagesFromPDF(CommandLine line) throws Exception {
		logger.debug("Extracting images");
		
		File pdffile = new File(line.getOptionValue("pdf"));

		if (!pdffile.exists()) {
			throw new Exception("Invalid parameters. File does not exist:" + line.getOptionValue("pdf"));
		}
		
		logger.debug("File:" + pdffile.getAbsolutePath());

		File tmpdir = new File(line.getOptionValue("tmp"));

		if (!tmpdir.exists() || !tmpdir.isDirectory()) {
			throw new Exception("Invalid parameters. Temp dir does not exist:" + line.getOptionValue("tmp"));
		}
		
		if(tmpdir.listFiles().length != 0) {
			logger.debug("Temp folder is not empty, cleaning.");
			FileUtils.cleanDirectory(tmpdir);
		}

		logger.debug("Tmp dir:" + tmpdir.getAbsolutePath());

		PDFDocument document = new PDFDocument();
		document.load(new File(line.getOptionValue("pdf")));
		int totalpages = document.getPageCount();

		if (totalpages == 0) {
			throw new Exception("PDF contains no pages.");
		}

		logger.debug("PDF contains " + totalpages + " pages");

		int userid = Integer.parseInt(line.getOptionValue("userid"));
		int courseid = Integer.parseInt(line.getOptionValue("courseid"));
		
		if(userid < 1 || courseid < 1) {
			throw new Exception("Invalid user or course id");
		}
		
		logger.debug("Userid:" + userid + " Courseid:" + courseid);
		
		GhostscriptExtract.extractImagesFromPDF(1, totalpages, 300, tmpdir, pdffile.getAbsolutePath());
		
		int pagenumber = 1;
		for (File file : tmpdir.listFiles()) {
			if(!file.getName().endsWith(Moodle.imageExtension)) {
				continue;
			}
			String filename = tmpdir.getAbsolutePath() + "/" + userid + "-" + courseid + "-" + pagenumber + Moodle.imageExtension;
			logger.debug("Rename " + file.getAbsolutePath() + " to " + filename);
			file.renameTo(new File(filename));
			pagenumber++;
		}
	}
}
