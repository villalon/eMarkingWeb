/**
 * 
 */
package cl.uai.webcursos.emarking.desktop;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.albertoborsetta.formscanner.api.FormField;
import com.albertoborsetta.formscanner.api.FormPoint;
import com.albertoborsetta.formscanner.api.FormTemplate;

/**
 * @author Jorge
 *
 */
public class FormScannerTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("Starting form scanner");

		// Create a template with the points of correct responses from the xml file
		File template = new File("omr/4-4-1_a.xtmpl");
		FormTemplate formTemplate = new FormTemplate(template);

		int threshold = 127;
		int density = 40;
		int shapeSize = 8;

		File dir = new File("omr");
		for(File imageFile : dir.listFiles()) {
			if(!imageFile.getAbsolutePath().endsWith("_a.png"))
				continue;
			System.out.println(imageFile.getPath());
			// Analyze image for search correct answers using the formTemplate
			BufferedImage image = ImageIO.read(imageFile);
			FormTemplate filledForm = new FormTemplate(imageFile.getName(), formTemplate);
			filledForm.findCorners(image, threshold, density);
			filledForm.findPoints(image, threshold, density, shapeSize);

			for(String key : filledForm.getFields().keySet()) {
				FormField ff = filledForm.getField(key);
				System.out.println("Key:" + key + " Values:"  + ff.getValues());
				for(String pointkey : ff.getPoints().keySet()) {
					FormPoint fpp = ff.getPoint(pointkey);
					if(pointkey == null || fpp == null)
						continue;
					System.out.println("pointkey:" + pointkey);
					System.out.println(fpp.toString());
				}
			}
		}
		System.out.println("Finished form scanner");
		
		System.exit(0);
	}

}
