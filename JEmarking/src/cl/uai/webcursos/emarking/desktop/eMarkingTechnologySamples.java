/**
 * 
 */
package cl.uai.webcursos.emarking.desktop;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * @author Jorge
 *
 */
public class eMarkingTechnologySamples {

	private static Logger logger = Logger.getLogger(eMarkingTechnologySamples.class);
	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		PropertyConfigurator.configure("log4j.properties");
		
		logger.info("Executing Image decoder");

		File samplesdir = new File("samples");
		
		for(File f : samplesdir.listFiles()) {
			if(f.isDirectory() || !f.getAbsolutePath().endsWith(".png"))
				continue;
			
			logger.info(f.getAbsolutePath());
			BufferedImage img = null;
			img = ImageIO.read(f);

			BufferedImage imgback = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
			
			ImageDecoder decoder = new ImageDecoder(img, imgback, 1, Files.createTempDirectory("emarking").toFile(), null);

			decoder.run();

			logger.info("Success:" + decoder.isSuccess());
			logger.info("Courseid:" + decoder.getQrResult().getCourseid());
			logger.info("Userid:" + decoder.getQrResult().getUserid());
			logger.info("Page:" + decoder.getQrResult().getExampage());
		}
		logger.info("Done");
	}
}
