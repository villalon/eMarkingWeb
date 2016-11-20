/**
 * 
 */
package cl.uai.webcursos.emarking.desktop.data;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;

import cl.uai.webcursos.emarking.desktop.EmarkingDesktop;

/**
 * @author jorgevillalon
 *
 */
public class MoodleWorker implements Runnable {

	private static Logger logger = Logger.getLogger(MoodleWorker.class);

	public static enum Action {
		FIX_FROM_PREVIOUS,
		FIX_FROM_FOLLOWING,
		ROTATE180,
		ROTATE180ANDFIX,
		SWAPFRONTBACK, 
		ROTATE180ANDFIXFROMFOLLOWING
	}

	private Moodle moodle;
	private int[] rows;
	private Action action;

	public MoodleWorker(Moodle moodle, int[] rows, Action action) {
		this.listenerList = new EventListenerList();
		this.moodle = moodle;
		this.rows = rows;
		this.action = action;
	}

	private EventListenerList listenerList = null;

	/**
	 * Adds a listener for the uploading event
	 * @param l
	 */
	public void addRowProcessedListener(MoodleWorkerListener l) {
		listenerList.add(MoodleWorkerListener.class, l);
	}

	public void removeRowProcessedListener(MoodleWorkerListener l) {
		listenerList.remove(MoodleWorkerListener.class, l);
	}

	protected void fireRowProcessingStarted(MoodleWorkerEvent e) {
		MoodleWorkerListener[] ls = listenerList.getListeners(MoodleWorkerListener.class);
		for (MoodleWorkerListener l : ls) {
			l.processStarted(e);
		}
	}

	protected void fireRowProcessed(MoodleWorkerEvent e) {
		MoodleWorkerListener[] ls = listenerList.getListeners(MoodleWorkerListener.class);
		for (MoodleWorkerListener l : ls) {
			l.stepPerformed(e);
		}
	}

	protected void fireRowProcessingFinished(MoodleWorkerEvent e) {
		MoodleWorkerListener[] ls = listenerList.getListeners(MoodleWorkerListener.class);
		for (MoodleWorkerListener l : ls) {
			l.processFinished(e);
		}
	}


	@Override
	public void run() {
		Map<Integer, Boolean> rowsprocessed = new Hashtable<Integer, Boolean>();

		MoodleWorkerEvent e = new MoodleWorkerEvent(this, 0, this.rows.length, null);
		fireRowProcessingStarted(e);

		int current = 0;
		for(int row : this.rows) {

			if(Thread.currentThread().isInterrupted())
				break;

			if(rowsprocessed.containsKey(row)) {
				logger.error("This shouldn't happen");
				continue;
			}

			switch(this.action) {
			case FIX_FROM_FOLLOWING:
				try {
					moodle.getPages().fixFromFollowing(row);
					rowsprocessed.put(row, true);
					e = new MoodleWorkerEvent(this, current, this.rows.length, row);
					fireRowProcessed(e);
				} catch (Exception ex) {
					ex.printStackTrace();
					logger.error("Something went wrong! Row:" + row);
				}
				break;
			case FIX_FROM_PREVIOUS:
				try {
					moodle.getPages().fixFromPrevious(row);
					rowsprocessed.put(row, true);
					e = new MoodleWorkerEvent(this, current, this.rows.length, row);
					fireRowProcessed(e);
				} catch (Exception ex) {
					ex.printStackTrace();
					logger.error("Something went wrong! Row:" + row);
				}
				break;
			case ROTATE180:
				try {
					rotatePageAndSave(row);
					rowsprocessed.put(row, true);
					if(moodle.getQrExtractor().isDoubleside() && row % 2 == 0) {
						rotatePageAndSave(row+1);
						rowsprocessed.put(row+1, true);
					}
					e = new MoodleWorkerEvent(this, current, this.rows.length, row);
					fireRowProcessed(e);
				} catch (Exception ex) {
					ex.printStackTrace();
					logger.error("Something went wrong! Row:" + row);
				}
				break;
			case ROTATE180ANDFIX:
				try {
					rotatePageAndSave(row);
					rowsprocessed.put(row, true);
					if(moodle.getQrExtractor().isDoubleside() && row % 2 == 0) {
						rotatePageAndSave(row+1);
						rowsprocessed.put(row+1, true);
					}
					moodle.getPages().fixFromPrevious(row);
					e = new MoodleWorkerEvent(this, current, this.rows.length, row);
					fireRowProcessed(e);
				} catch (Exception e1) {
					e1.printStackTrace();
					logger.error("Something went wrong! Row:" + row);
				}
				break;
			case SWAPFRONTBACK:
				try {
					if(row % 2 == 0) {
						swapFrontBackPages(row);
						e = new MoodleWorkerEvent(this, current, this.rows.length, row);
						fireRowProcessed(e);
					} else {
						logger.error("Invalid page for swapping " + row);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					logger.error("Something went wrong! Row:" + row);
				}
				break;
			case ROTATE180ANDFIXFROMFOLLOWING:
				try {
					rotatePageAndSave(row);
					rowsprocessed.put(row, true);
					if(moodle.getQrExtractor().isDoubleside() && row % 2 == 0) {
						rotatePageAndSave(row+1);
						rowsprocessed.put(row+1, true);
					}
					moodle.getPages().fixFromFollowing(row);
					e = new MoodleWorkerEvent(this, current, this.rows.length, row);
					fireRowProcessed(e);
				} catch (Exception e1) {
					e1.printStackTrace();
					logger.error("Something went wrong! Row:" + row);
				}
				break;
			default:
				break;
			}
			current++;
		}

		e = new MoodleWorkerEvent(this, this.rows.length, this.rows.length, null);
		fireRowProcessingFinished(e);
	}

	/**
	 * Swaps the front and back page when it can be done
	 * @throws Exception
	 */
	private void swapFrontBackPages(int row) throws Exception {
		if(!moodle.getQrExtractor().isDoubleside())
			throw new Exception("This can not be done in single side scanning");

		if(moodle.getQrExtractor().isDoubleside() && row % 2 != 0)
			throw new Exception(EmarkingDesktop.lang.getString("onlyevenrowsdoubleside"));

		Page current = moodle.getPages().get(row);
		Page next = moodle.getPages().get(row+1);

		if(current == null || next == null)
			throw new Exception("Invalid pages in swap operation");

		File currentFile = new File(moodle.getQrExtractor().getTempdirStringPath() + "/" + current.getFilename() + Moodle.imageExtension);
		File nextFile = new File(moodle.getQrExtractor().getTempdirStringPath() + "/" + next.getFilename() + Moodle.imageExtension);
		File tempFile = File.createTempFile("emarking", Moodle.imageExtension);

		File currentFileAnonymous = new File(moodle.getQrExtractor().getTempdirStringPath() + "/" + current.getFilename() + "_a" + Moodle.imageExtension);
		File nextFileAnonymous = new File(moodle.getQrExtractor().getTempdirStringPath() + "/" + next.getFilename() + "_a" + Moodle.imageExtension);
		File tempFileAnonymous = File.createTempFile("emarking_a", Moodle.imageExtension);

		if(!currentFile.exists() || !nextFile.exists() || !currentFile.exists() || !nextFile.exists())
			throw new Exception("Invalid files for swap operation");

		boolean result = true;

		result &= nextFile.renameTo(tempFile);
		result &= currentFile.renameTo(nextFile);
		result &= tempFile.renameTo(currentFile);
		logger.debug("Exchanged " + nextFile + " and " + currentFile);

		result &= nextFileAnonymous.renameTo(tempFileAnonymous);
		result &= currentFileAnonymous.renameTo(nextFileAnonymous);
		result &= tempFileAnonymous.renameTo(currentFileAnonymous);
		logger.debug("Exchanged " + nextFileAnonymous + " and " + currentFileAnonymous);

		if(!result) {
			throw new Exception("Fatal error renaming files in swap operation");
		}
	}

	/**
	 * @param row 
	 * @throws HeadlessException
	 */
	private void rotatePageAndSave(int row) throws Exception {
		// Retrieve the page from the row number
		Page p = moodle.getPages().get(row);

		// Read the file as image
		File file = p.getFile();
		BufferedImage image;
		image = ImageIO.read(file);

		// Rotate the image
		image = rotateImage180static(image);

		// Save the file
		ImageIO.write(image, Moodle.imageType, file);

		// Produce a new anonymous version
		File fileAnonymous = new File(moodle.getQrExtractor().getTempdirStringPath() + "/" + p.getFilename() + "_a" + Moodle.imageExtension);
		BufferedImage anonymousImage = createAnonymousVersionStatic(image);
		ImageIO.write(anonymousImage, Moodle.imageType, fileAnonymous);
	}

	private BufferedImage rotateImage180static(BufferedImage image) {
		// Flip the image vertically and horizontally; equivalent to rotating the image 180 degrees
		AffineTransform tx = AffineTransform.getScaleInstance(-1, -1);
		tx.translate(-image.getWidth(null), -image.getHeight(null));
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		image = op.filter(image, null);
		return image;
	}

	private BufferedImage createAnonymousVersionStatic(BufferedImage image) {
		int cropHeight = (int) ((float) image.getHeight() / 10.0f);
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

	public int getTotalRows() {
		return this.rows.length;
	}
}
