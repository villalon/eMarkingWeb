/**
 * 
 */
package cl.uai.webcursos.emarking.desktop;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import cl.uai.webcursos.emarking.desktop.data.Moodle;

import com.albertoborsetta.formscanner.api.FormField;
import com.albertoborsetta.formscanner.api.FormTemplate;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

/**
 * @author jorgevillalon
 *
 */
public class ImageDecoder implements Runnable {

	private static Logger logger = Logger.getLogger(ImageDecoder.class);

	private int filenumber = 0;

	private File tempdir;

	private boolean doubleside = false;

	private QrDecodingResult qrResult;

	public QrDecodingResult getQrResult() {
		return qrResult;
	}

	public boolean isDoubleside() {
		return doubleside;
	}

	private QRCodeReader reader = null;

	private BufferedImage image = null;
	private BufferedImage backimage = null;

	public BufferedImage getBackimage() {
		return backimage;
	}

	private BufferedImage anonymous = null;
	private BufferedImage backanonymous = null;

	public BufferedImage getBackanonymous() {
		return backanonymous;
	}

	private boolean rotated = false;

	private boolean success = false;

	private BufferedImage qr;
	
	private Moodle moodle;

	public ImageDecoder(BufferedImage _img, BufferedImage _back, int _filenumber, File _tmpdir, Moodle _moodle) {
		this.image = _img;
		this.backimage = _back;
		this.reader = new QRCodeReader();
		this.filenumber = _filenumber;
		this.tempdir = _tmpdir;
		this.moodle = _moodle;

		if(this.backimage != null) {
			this.doubleside = true;
		}
	}

	private BufferedImage createAnonymousVersion(BufferedImage image) {
		int cropHeight = (int) Math.max(((float) image.getHeight() / 10.0f),130);
		BufferedImage anonymousimage = new BufferedImage(
				image.getWidth(), 
				image.getHeight(), 
				BufferedImage.TYPE_INT_ARGB_PRE);
		Graphics g = anonymousimage.getGraphics();
		g.drawImage(image, 
				0, cropHeight, 
				anonymousimage.getWidth(), anonymousimage.getHeight(), 
				0, cropHeight, 
				image.getWidth(), image.getHeight(), null);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, anonymousimage.getWidth(), cropHeight);
		g.dispose();

		return anonymousimage;
	}

	public BufferedImage getAnonymous() {
		return anonymous;
	}
	public int getFilenumber() {
		return filenumber;
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getQr() {
		return qr;
	}

	public boolean isRotated() {
		return rotated;
	}

	public boolean isSuccess() {
		return success;
	}

	private BufferedImage rotateImage180(BufferedImage image) {
		// Flip the image vertically and horizontally; equivalent to rotating the image 180 degrees
		AffineTransform tx = AffineTransform.getScaleInstance(-1, -1);
		tx.translate(-image.getWidth(null), -image.getHeight(null));
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		image = op.filter(image, null);
		return image;
	}

	private BinaryBitmap extractTopRightCornerForQR(BufferedImage image) {
		BufferedImage subimage = image.getSubimage(
				image.getWidth() - image.getWidth() / 4, 0,
				image.getWidth() / 4, image.getHeight() / 4);
		LuminanceSource source = new BufferedImageLuminanceSource(
				subimage);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(
				source));

		return bitmap;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		// First decode the frontpage
		this.qrResult = decodeQR(image, filenumber);

		// If we couldn't find a code, but we have a backpage, we try processing it
		if(!qrResult.isSuccess() && this.doubleside) {
			QrDecodingResult qrresultback = decodeQR(backimage, filenumber);

			// If the backpage contains a valid QR then it was flipped, we flip the front and backpages
			if(qrresultback.isSuccess()) {
				// Flip front and backpages
				BufferedImage tmp = this.image;
				this.image = this.backimage;
				this.backimage = tmp;

				// Replace decoding info with the backpage
				qrResult = qrresultback;
			}
		}

		// If images were rotated, both are rotated
		if(qrResult.isSuccess() && qrResult.isRotated()) {
			this.image = rotateImage180(this.image);
			if(this.doubleside) {
				this.backimage = rotateImage180(this.backimage);
			}
		}

		// All numbers are ok, now create the anonymous version of the page
		this.anonymous = createAnonymousVersion(image);
		if(this.doubleside) {
			this.backanonymous = createAnonymousVersion(this.backimage);
		}

		this.success = qrResult.isSuccess();
		this.rotated = qrResult.isRotated();
		
		if(this.success && qrResult.isAnswersheet() && this.moodle.getOMRTemplate() != null) {
			TreeMap<String, String> answers = new TreeMap<String, String>();
			FormTemplate formTemplate = null;
			try {
				File omrtemplatefile = new File(this.moodle.getOMRTemplate());
				formTemplate = new FormTemplate(omrtemplatefile);
				FormTemplate filledForm = new FormTemplate(qrResult.getFilename(), formTemplate);
				filledForm.findCorners(anonymous, this.moodle.getOMRthreshold(), this.moodle.getOMRdensity());
				filledForm.findPoints(anonymous, this.moodle.getOMRthreshold(), this.moodle.getOMRdensity(), this.moodle.getOMRshapeSize());

				for(String key : filledForm.getFields().keySet()) {
					FormField ff = filledForm.getField(key);
					answers.put(key, ff.getValues());
				}
				
				qrResult.setAnswers(answers);
			} catch (Exception e) {
				logger.error("Problem with the OMR template");
				e.printStackTrace();
			}
		}
		
		// Now write images as files
		try {
			ImageIO.write((RenderedImage) image, "png", 
					new File(tempdir.getAbsolutePath() + "/" + qrResult.getFilename() + ".png"));
			ImageIO.write((RenderedImage) anonymous, "png",			
					new File(tempdir.getAbsolutePath() + "/" + qrResult.getFilename() + "_a.png"));
			if(doubleside) {
				ImageIO.write((RenderedImage) backimage, "png", 
						new File(tempdir.getAbsolutePath() + "/" + qrResult.getBackfilename() + ".png"));
				ImageIO.write((RenderedImage) backanonymous, "png", 
						new File(tempdir.getAbsolutePath() + "/" + qrResult.getBackfilename() + "_a.png"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private QrDecodingResult decodeQR(BufferedImage image, int filenumber) {
		// Create qr image from original for decoding
		BinaryBitmap bitmap = extractTopRightCornerForQR(image);
		QrDecodingResult decodingresult = new QrDecodingResult();

		try {
			// Decode QR
			Result result = reader.decode(bitmap);

			// Clean the output from the QR
			decodingresult.setOutput(result.getText().replace(" ", "").trim());

			// The image filename will be the output
			decodingresult.setFilename(decodingresult.getOutput());

			// Consistency checking
			if(decodingresult.getFilename().length() == 0) {
				decodingresult.setFilename("ERROR-EMPTYQR-" + (filenumber + 1));
			} else {
				String[] parts = decodingresult.getFilename().split("-");

				// Now check if the QR string has five parts (which indicates it is an answer sheet)
				if(parts.length == 5 && parts[4].trim().contains("BB")) {
					decodingresult.setAttemptId(Integer.parseInt(parts[3]));
					decodingresult.setAnswersheet(true);
				}
				
				// Now check if the QR string has a fourth component (image is rotated)
				if(parts.length == 4 && parts[3].trim().contains("R")) {
					decodingresult.setRotated(true);					
				}
				
				// If everything looks well, parse the numbers from the decoded QR info
				if(parts.length >= 3) {

					// Parse the parts (any exception will be caught as an error)
					decodingresult.setUserid(Integer.parseInt(parts[0]));
					decodingresult.setCourseid(Integer.parseInt(parts[1]));
					decodingresult.setExampage(Integer.parseInt(parts[2]));
					
					// Set filename with the corresponding IDs
					decodingresult.setFilename(decodingresult.getUserid() + "-" + decodingresult.getCourseid() + "-" + decodingresult.getExampage());
					
					// Processing was a success
					decodingresult.setSuccess(true);
				} else {
					logger.error("QR contains invalid information");
					decodingresult.setFilename("ERROR-INVALIDPARTSQR-" + (filenumber + 1));										
				}
			}
		} catch (NotFoundException e) {
			decodingresult.setFilename("ERROR-NOTFOUND-" + (filenumber + 1));
		} catch (ChecksumException e) {
			decodingresult.setFilename("ERROR-CHECKSUM-" + (filenumber + 1));
		} catch (FormatException e) {
			decodingresult.setFilename("ERROR-CHECKSUM-" + (filenumber + 1));
		} catch(Exception e) {
			decodingresult.setFilename("ERROR-NULL-" + (filenumber + 1));
		}

		// Regardless of the result, the back-file name is the sames as the file with a b
		decodingresult.setBackfilename(decodingresult.getFilename() + "b");

		return decodingresult;
	}
}
