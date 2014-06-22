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
package cl.uai.webcursos.emarking.desktop;

import java.awt.Dimension;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.ghost4j.document.DocumentException;
import org.ghost4j.document.PDFDocument;

import cl.uai.webcursos.emarking.desktop.data.Activity;
import cl.uai.webcursos.emarking.desktop.data.Course;
import cl.uai.webcursos.emarking.desktop.data.Moodle;
import cl.uai.webcursos.emarking.desktop.data.MoodleWorker;
import cl.uai.webcursos.emarking.desktop.data.MoodleWorkerEvent;
import cl.uai.webcursos.emarking.desktop.data.MoodleWorkerListener;
import cl.uai.webcursos.emarking.desktop.data.MoodleWorker.Action;
import cl.uai.webcursos.emarking.desktop.data.Page;
import cl.uai.webcursos.emarking.desktop.utils.ZipFile;

import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.JSplitPane;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;

import java.awt.event.InputEvent;

import javax.swing.JSeparator;

/**
 * Main class for the program's execution. Contains the main
 * interface and all its parts
 * 
 * @author jorgevillalon
 *
 */
public class EmarkingDesktop {

	private static Logger logger = Logger.getLogger(EmarkingDesktop.class);
	private JFrame frame;
	private PagesTable table;
	private JPopupMenu contextMenu;
	private Moodle moodle;
	private EmarkingProgress progress;
	private UploadProgress uploadProgress;
	private UploadWorker worker;
	public static ResourceBundle lang;
	private JToolBar toolBar;
	private JButton btnLoadPdf;
	private JButton btnSave;
	private JButton btnUpload;
	private JSplitPane splitPane;
	private JPanel imagePanel;
	private JScrollPane scrollPaneTable;
	private JButton btnNextProblem;
	private List<File> zipFiles;
	private JButton btnSelectAllProblems;
	public static boolean IS_MAC = false;
	private JMenuBar menuBar;
	private JMenuItem menuFileOpen;
	private JMenu menuFile;
	private JMenu menuEdit;
	private JMenuItem menuFix;
	private JMenuItem menuSave;
	private JMenuItem menuUpload;
	private JMenu menuNavigate;
	private JMenuItem menuNextProblem;
	private JMenuItem menuSelectAll;
	private JMenuItem menuFixPrevious;
	private JMenuItem menuFixFollowing;
	private JMenuItem menuRotate;
	private JMenuItem menuRotateAndFixPrevious;
	private JMenuItem menuSwap;
	private JSeparator separator;
	private JSeparator separator_1;
	private JSeparator separator_2;
	private JMenuItem menuRotateAndFixFollowing;

	/**
	 * Create the application.
	 */
	public EmarkingDesktop() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		moodle = new Moodle();

		moodle.getQr().addPageProcessedListener(new PageProcessedListener() {			
			@Override
			public void processed(QRExtractorEvent e) {
				
				QrDecodingResult qrResult = e.getQrresult();
				
				// Update progress bar
				int arg0 = progress.getProgressBar().getValue();
				arg0++;
				progress.getProgressBar().setValue(arg0);

				logger.debug("IDENTIFIED - Student:" + qrResult.getUserid() + " Course:"+
						qrResult.getCourseid()+" Page:" + qrResult.getExampage());

				// Validate data				
				if(!moodle.getCourses().containsKey(qrResult.getCourseid()) && qrResult.getCourseid() > 0) {
					logger.debug("Course " + qrResult.getCourseid() + " not found!");
					try {
						moodle.retrieveCourseFromId(qrResult.getCourseid());
						moodle.retrieveStudents(qrResult.getCourseid());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}

				if(!moodle.getStudents().containsKey(qrResult.getUserid()) && qrResult.getUserid() > 0 && qrResult.getCourseid() > 0) {
					logger.debug("Student " + qrResult.getUserid() + " not found");
					try {
						moodle.retrieveStudents(qrResult.getCourseid());
					} catch (Exception e1) {
						e1.printStackTrace();
					}					
				}

				Page p = new Page(moodle);
				p.setFilename(e.isBackPage() ? qrResult.getBackfilename() : qrResult.getFilename());
				p.setRow(table.getModel().getRowCount());
				p.setProblem(e.getQrresult().getOutput());
				p.setCourse(moodle.getCourses().containsKey(qrResult.getCourseid()) ? moodle.getCourses().get(qrResult.getCourseid()) : null);
				p.setStudent(moodle.getStudents().containsKey(qrResult.getUserid()) ? moodle.getStudents().get(qrResult.getUserid()) : null);
				p.setPagenumber(qrResult.getExampage());
				p.setRotated(qrResult.isRotated());
				if(qrResult.getExampage() > moodle.getMaxExamPage())
					moodle.setMaxExamPage(qrResult.getExampage());
				moodle.getPages().put(p.getRow(), p);
				table.getPagesTableModel().addRow((Object[][]) null);
				table.updateData(
						moodle.getPages().getRowData(p.getRow()),
						p.getRow(),
						false);
				if(table.getRowCount()==1)
					table.selectAll();
				progress.getLblProgress().setText(lang.getString("processingpage") + " " + p.getRow());
			}
			@Override
			public void finished(QRExtractorEvent e) {
				progress.setVisible(false);
				if(table.getRowCount() > 0) {
					btnSave.setEnabled(true);
					btnUpload.setEnabled(true);
					btnNextProblem.setEnabled(true);
					btnSelectAllProblems.setEnabled(true);
					menuNavigate.setEnabled(true);
					menuUpload.setEnabled(true);
					menuSave.setEnabled(true);
				}
				logger.debug("QR extraction finished!");
			}
			@Override
			public void started(QRExtractorEvent e) {
			}
		});

		frame = new JFrame();
		frame.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				super.keyTyped(e);
				logger.debug("Keychar:" + e.getKeyChar());
			}
			@Override
			public void keyPressed(KeyEvent e) {
				super.keyPressed(e);
				logger.debug("Keychar:" + e.getKeyChar());
			}
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				logger.debug("Keychar:" + e.getKeyChar());
			}
		});
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(EmarkingDesktop.class.getResource("/cl/uai/webcursos/emarking/desktop/resources/qrcode.png")));
		frame.setBounds(10, 10, 1024, 600);
		frame.setTitle("eMarking");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		contextMenu = new JPopupMenu("Right click!");
		JMenuItem popupMenuItem3 = new JMenuItem(lang.getString("fix"));
		popupMenuItem3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionFix();
			}
		});
		contextMenu.add(popupMenuItem3);
		JMenuItem popupMenuItem = new JMenuItem(lang.getString("fixfromprevious"));
		popupMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionFixFromPrevious();
			}
		});
		contextMenu.add(popupMenuItem);
		JMenuItem popupMenuItem2 = new JMenuItem(lang.getString("fixfromfollowing"));
		popupMenuItem2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionFixFromFollowing();
			}
		});
		contextMenu.add(popupMenuItem2);
		JMenuItem popupMenuItem4 = new JMenuItem(lang.getString("rotateimage180"));
		popupMenuItem4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionRotateImage180();
			}

		});
		contextMenu.add(popupMenuItem4);
		JMenuItem popupMenuItem5 = new JMenuItem(lang.getString("swap"));
		popupMenuItem5.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionSwap();
			}
		});
		if(moodle.isDoubleside()) {
			contextMenu.add(popupMenuItem5);
		}
		JMenuItem popupMenuItem6 = new JMenuItem(lang.getString("rotateandfixfromprevious"));
		popupMenuItem6.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionRotateAndFixFromPrevious();
			}
		});
		contextMenu.add(popupMenuItem6);
		JMenuItem popupMenuItem7 = new JMenuItem(lang.getString("rotateandfixfromfollowing"));
		popupMenuItem7.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionRotateAndFixFromFollowing();
			}
		});
		contextMenu.add(popupMenuItem7);
		// frame.getContentPane().add(contextMenu);

		progress = new EmarkingProgress();

		toolBar = new JToolBar();
		toolBar.setForeground(Color.LIGHT_GRAY);
		toolBar.setRollover(true);
		toolBar.setFloatable(false);
		frame.getContentPane().add(toolBar, BorderLayout.NORTH);

		btnLoadPdf = new JButton();
		btnLoadPdf.setToolTipText(lang.getString("loadpdf"));
		btnLoadPdf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionLoadPdf();
			}
		});
		btnLoadPdf.setIcon(new ImageIcon(EmarkingDesktop.class.getResource("/cl/uai/webcursos/emarking/desktop/resources/glyphicons_036_file.png")));
		toolBar.add(btnLoadPdf);

		btnSave = new JButton();
		btnSave.setToolTipText(lang.getString("save"));
		btnSave.setEnabled(false);
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionSave();
			}
		});
		btnSave.setIcon(new ImageIcon(EmarkingDesktop.class.getResource("/cl/uai/webcursos/emarking/desktop/resources/glyphicons_443_floppy_disk.png")));
		toolBar.add(btnSave);

		btnUpload = new JButton();
		btnUpload.setToolTipText(lang.getString("upload"));
		btnUpload.setEnabled(false);
		btnUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionUpload();
			}
		});
		btnUpload.setIcon(new ImageIcon(EmarkingDesktop.class.getResource("/cl/uai/webcursos/emarking/desktop/resources/glyphicons_201_upload.png")));
		toolBar.add(btnUpload);

		btnNextProblem = new JButton();
		btnNextProblem.setToolTipText(lang.getString("nextproblem"));
		btnNextProblem.setEnabled(false);
		btnNextProblem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionNextProblem();
			}
		});
		btnNextProblem.setIcon(new ImageIcon(EmarkingDesktop.class.getResource("/cl/uai/webcursos/emarking/desktop/resources/glyphicons_178_step_forward.png")));
		toolBar.add(btnNextProblem);
		
		btnSelectAllProblems = new JButton();
		btnSelectAllProblems.setToolTipText(lang.getString("selectallproblems"));
		btnSelectAllProblems.setEnabled(false);
		btnSelectAllProblems.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionSelectAllProblems();
			}
		});
		btnSelectAllProblems.setIcon(new ImageIcon(EmarkingDesktop.class.getResource("/cl/uai/webcursos/emarking/desktop/resources/glyphicons_177_fast_forward.png")));
		toolBar.add(btnSelectAllProblems);

		splitPane = new JSplitPane();
		splitPane.setOneTouchExpandable(true);
		splitPane.setContinuousLayout(true);
		splitPane.setDividerLocation(640);
		splitPane.setAutoscrolls(true);
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);

		imagePanel = new JPanel();

		scrollPaneTable = new JScrollPane();
		scrollPaneTable.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		initializeTable();
		scrollPaneTable.add(table);

		splitPane.setRightComponent(scrollPaneTable);
		splitPane.setLeftComponent(imagePanel);
		
		menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		menuFile = new JMenu(lang.getString("file"));
		menuBar.add(menuFile);
		
		menuFileOpen = new JMenuItem(lang.getString("loadpdf"));
		menuFileOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionLoadPdf();
			}
		});
		menuFileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK));
		menuFile.add(menuFileOpen);
		
		menuSave = new JMenuItem(lang.getString("save"));
		menuSave.setEnabled(false);
		menuSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionSave();
			}
		});
		
		separator_2 = new JSeparator();
		menuFile.add(separator_2);
		menuSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		menuFile.add(menuSave);
		
		menuUpload = new JMenuItem(lang.getString("upload"));
		menuUpload.setEnabled(false);
		menuUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionUpload();
			}
		});
		menuUpload.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK));
		menuFile.add(menuUpload);
		
		menuEdit = new JMenu(lang.getString("edit"));
		menuEdit.setEnabled(false);
		menuBar.add(menuEdit);
		
		menuFix = new JMenuItem(lang.getString("fix"));
		menuFix.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionFix();
			}
		});
		menuFix.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
		menuEdit.add(menuFix);
		
		menuFixPrevious = new JMenuItem(lang.getString("fixfromprevious"));
		menuFixPrevious.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionFixFromPrevious();
			}
		});
		menuFixPrevious.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
		menuEdit.add(menuFixPrevious);
		
		menuFixFollowing = new JMenuItem(lang.getString("fixfromfollowing"));
		menuFixFollowing.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionFixFromFollowing();
			}
		});
		menuFixFollowing.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, InputEvent.CTRL_MASK));
		menuEdit.add(menuFixFollowing);
		
		menuRotate = new JMenuItem(lang.getString("rotateimage180"));
		menuRotate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionRotateImage180();
			}
		});
		
		separator_1 = new JSeparator();
		menuEdit.add(separator_1);
		menuRotate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
		menuEdit.add(menuRotate);
		
		menuRotateAndFixPrevious = new JMenuItem(lang.getString("rotateandfixfromprevious"));
		menuRotateAndFixPrevious.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionRotateAndFixFromPrevious();
			}
		});
		
		menuSwap = new JMenuItem(lang.getString("swap"));
		menuSwap.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionSwap();
			}
		});
		menuSwap.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK));
		menuEdit.add(menuSwap);
		
		separator = new JSeparator();
		menuEdit.add(separator);
		menuRotateAndFixPrevious.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.CTRL_MASK));
		menuEdit.add(menuRotateAndFixPrevious);
		
		menuRotateAndFixFollowing = new JMenuItem(lang.getString("rotateandfixfromfollowing"));
		menuRotateAndFixFollowing.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionRotateAndFixFromFollowing();
			}
		});
		menuRotateAndFixFollowing.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_MASK));
		menuEdit.add(menuRotateAndFixFollowing);
		
		menuNavigate = new JMenu(lang.getString("navigate"));
		menuNavigate.setEnabled(false);
		menuBar.add(menuNavigate);
		
		menuNextProblem = new JMenuItem(lang.getString("nextproblem"));
		menuNextProblem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionNextProblem();
			}
		});
		menuNextProblem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, 0));
		menuNavigate.add(menuNextProblem);
		
		menuSelectAll = new JMenuItem(lang.getString("selectallproblems"));
		menuSelectAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionSelectAllProblems();
			}
		});
		menuSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
		menuNavigate.add(menuSelectAll);
		
		Dimension d = new Dimension(32, 32);
		btnLoadPdf.setMinimumSize(d);
		btnNextProblem.setMinimumSize(d);
		btnSave.setMinimumSize(d);
		btnSelectAllProblems.setMinimumSize(d);
		btnUpload.setMinimumSize(d);
		btnLoadPdf.setPreferredSize(d);
		btnNextProblem.setPreferredSize(d);
		btnSave.setPreferredSize(d);
		btnSelectAllProblems.setPreferredSize(d);
		btnUpload.setPreferredSize(d);
	}

	private void scrollToRow(int row) {
		JScrollBar bar = scrollPaneTable.getVerticalScrollBar();
		if(table.getRowCount() > 0) {
			int newIndex = Math.max(0, row-2);
			int newPosition = (int) (((float) bar.getMaximum() / (float) table.getRowCount()) * (float) newIndex);
			bar.setValue(newPosition);
		}		
	}
	
	private void initializeTable() {
		table = new PagesTable(moodle);
		scrollPaneTable.setViewportView(table);
		table.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseClicked(MouseEvent e) {

				if(moodle.getQr().isDoubleside() && table.getSelectedRow() % 2 != 0) {
					JOptionPane.showMessageDialog(frame, lang.getString("onlyevenrowsdoubleside"));
					return;
				}

				// Right click
				if(e.getButton() == MouseEvent.BUTTON3) {
					contextMenu.show(e.getComponent(), e.getX(), e.getY());
				}

				// Double click
				if(e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
					try {
						if(moodle.getPages().fixPageData(table.getSelectedRow(), frame)) {
							updateTableData(table.getSelectedRow());
						}
					} catch (Exception e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(frame, e1.getMessage());
					}
				}
			}
		});
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(table.getSelectedRow() >= 0) {
					loadSelectedRowPreview(table.getSelectedRow());
					menuEdit.setEnabled(true);
				} else {
					menuEdit.setEnabled(false);					
				}
			}
		});		
	}

	public void updateTableData(int row) {
		Object[] data = moodle.getPages().getRowData(row);
		table.updateData(data, row, moodle.getQr().isDoubleside());
	}

	private void executeCommand(Action command) {
		MoodleWorker worker = new MoodleWorker(moodle, 
				table.getSelectedRows(), 
				command);
		worker.addRowProcessedListener(new MoodleWorkerListener() {
			@Override
			public void stepPerformed(MoodleWorkerEvent e) {
				progress.getProgressBar().setValue(e.getCurrent());
				progress.getLblProgress().setText(lang.getString("processingpage") + " " + e.getOutput());
				scrollToRow((int) e.getOutput());
				updateTableData((int) e.getOutput());
				loadSelectedRowPreview((int) e.getOutput());
			}
			@Override
			public void processFinished(MoodleWorkerEvent e) {
				progress.setVisible(false);
			}
			@Override
			public void processStarted(MoodleWorkerEvent e) {
				progress.getProgressBar().setMaximum(e.getTotal());
				progress.getProgressBar().setMinimum(0);
			}
		});
		progress.setWorker(worker);
		progress.setLocationRelativeTo(frame);
		progress.startProcessing();
	}

	private void saveZipFiles(List<File> zipFiles) {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle(lang.getString("savepagesfile"));
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		chooser.setFileFilter(new FileFilter() {					
			@Override
			public String getDescription() {
				return "*.zip";
			}					
			@Override
			public boolean accept(File arg0) {
				if(arg0.getName().endsWith(".zip") || arg0.isDirectory())
					return true;
				return false;
			}
		});
		int retvalsave = chooser.showSaveDialog(frame);
		if(retvalsave == JFileChooser.APPROVE_OPTION) {
			String zipfilename = chooser.getSelectedFile().getAbsolutePath();
			if(zipfilename.endsWith(".zip")) {
				zipfilename = chooser.getSelectedFile().getAbsolutePath().substring(0, chooser.getSelectedFile().getAbsolutePath().length()-4);
			}
			int num = 1;
			for(File zip : zipFiles) {
				File dest = new File(zipfilename + num + ".zip");
				zip.renameTo(dest);
				num++;
			}
			JOptionPane.showMessageDialog(frame, lang.getString("done"));
		}
	}
	
	public JFrame getFrame() {
		return this.frame;
	}
	
	private void loadSelectedRowPreview(int row) {
		if(moodle.getPages().get(row) == null) {
			logger.error("Invalid row for preview:" + row);
		}
		String pageFilename = moodle.getPages().get(row).getFilename();
		String filename = moodle.getQr().getTempdirStringPath() + "/" + pageFilename + ".png";
		try {
			File imagefile = new File(filename);
			if(!imagefile.exists()) {
				JOptionPane.showMessageDialog(null, lang.getString("filenotfound") + " " + filename);
				return;
			}
			Image img = ImageIO.read(imagefile);
			int width = 640;
			if(imagePanel.getWidth() > 640)
				width = imagePanel.getWidth();
			int height = (int) (((float) width / (float) img.getWidth(null)) * (float) img.getHeight(null));
			imagePanel.getGraphics().drawImage(img, 0, 0, width, height, null);
		} catch (IOException e1) {
			e1.printStackTrace();
		}		
	}

	private void actionLoadPdf() {
		OptionsDialog dialog = new OptionsDialog(moodle);
		dialog.setModal(true);
		dialog.setLocationRelativeTo(frame);
		dialog.setVisible(true);
		if(dialog.isCancelled())
			return;
		moodle.getQr().setDoubleside(dialog.getDoubleSideSelected());
		File pdfFile = new File(dialog.getFilename().getText());
		PDFDocument pdfdoc = new PDFDocument();
		int pages = 0;
		try {
			pdfdoc.load(pdfFile);
			pages = pdfdoc.getPageCount();
		} catch (IOException | DocumentException ex) {
			JOptionPane.showMessageDialog(frame, lang.getString("unabletoopenfile") + " " + pdfFile.getName());
			ex.printStackTrace();
			return;
		}
		toolBar.setEnabled(false);

		progress.getProgressBar().setMaximum(pages);
		progress.getProgressBar().setMinimum(0);
		progress.getProgressBar().setValue(0);
		moodle.getQr().setPdffile(dialog.getFilename().getText());
		moodle.getQr().setTotalpages(pages);
		progress.setLocationRelativeTo(frame);
		progress.setWorker(moodle.getQr());
		moodle.clearPages();
		initializeTable();
		progress.startProcessing();		
	}
	
	private void actionSave() {
		ZipFile appZip = new ZipFile(moodle);
		progress.setLocationRelativeTo(frame);
		progress.setWorker(appZip);
		appZip.addProgressListener(new MoodleWorkerListener() {
			
			@Override
			public void processStarted(MoodleWorkerEvent e) {
				progress.getProgressBar().setMinimum(0);
				progress.getProgressBar().setMaximum(e.getTotal());
				progress.getProgressBar().setValue(0);
			}

			@Override
			public void stepPerformed(MoodleWorkerEvent e) {
				progress.getLblProgress().setText(e.getOutput().toString());
				progress.getProgressBar().setValue(e.getCurrent());
			}

			@Override
			public void processFinished(MoodleWorkerEvent e) {
				progress.setVisible(false);						
				ZipFile appZip = (ZipFile) e.getSource();
				zipFiles = appZip.getZipFiles();
			}
		});
		
		progress.startProcessing();
		saveZipFiles(zipFiles);		
	}
	
	private void actionUpload() {
		try {
			final UploadAnswersDialog dialog = new UploadAnswersDialog(moodle);
			dialog.setLocationRelativeTo(frame);
			dialog.setModal(true);
			dialog.setVisible(true);

			if(dialog.isCancelled())
				return;

			ZipFile appZip = new ZipFile(moodle);
			appZip.setDatalimit(2);
			appZip.addProgressListener(new MoodleWorkerListener() {
				
				@Override
				public void processStarted(MoodleWorkerEvent e) {
				}

				@Override
				public void stepPerformed(MoodleWorkerEvent e) {
				}

				@Override
				public void processFinished(MoodleWorkerEvent e) {
					ZipFile appZip = (ZipFile) e.getSource();
					zipFiles = appZip.getZipFiles();
				}
			});
			progress.setWorker(appZip);
			progress.setLocationRelativeTo(frame);
			progress.startProcessing();
			
			Activity activity = null;
			String newactivityname = lang.getString("defaultactivityname");
			boolean merge = true;

			if(!dialog.getChckbxNewActivity().isSelected() 
					&& dialog.getActivitiesComboBox().getSelectedItem() instanceof Activity) {
				activity = (Activity) dialog.getActivitiesComboBox().getSelectedItem();
				merge = !dialog.getChkMerge().isSelected();
			}

			if(dialog.getChckbxNewActivity().isSelected()) {
				newactivityname = dialog.getTxtActivityName().getText();
				merge = false;
			}

			Course course = (Course) moodle.getCourses().get(moodle.getCourses().keySet().toArray()[0]);
			btnSave.setEnabled(false);
			worker = new UploadWorker(
					moodle,
					activity,
					merge,
					newactivityname,
					zipFiles, 
					course.getId());

			worker.addProcessingListener(new MoodleWorkerListener() {
				@Override
				public void processStarted(MoodleWorkerEvent e) {
				}

				@Override
				public void stepPerformed(MoodleWorkerEvent e) {
					int currentBytes = e.getCurrent() / 1024;
					int totalBytes = e.getTotal() / 1024;
					String message = "Uploading " + currentBytes + "K -" + totalBytes + "K";
					uploadProgress.getProgressBar().setMaximum(e.getTotal());
					uploadProgress.getProgressBar().setMinimum(0);
					uploadProgress.getProgressBar().setValue(e.getCurrent());
					uploadProgress.getLblProgress().setText(message);
				}

				@Override
				public void processFinished(MoodleWorkerEvent e) {
					logger.debug("Upload finished event");
					btnUpload.setEnabled(false);
					menuUpload.setEnabled(false);
					btnSave.setEnabled(true);
					menuSave.setEnabled(true);
					uploadProgress.setVisible(false);
				}
			});

			progress.setWorker(worker);
			progress.setLocationRelativeTo(frame);
			progress.startProcessing();
		} catch (Exception e1) {
			e1.printStackTrace();
		}		
	}
	
	private void actionNextProblem() {
		int start = 0;
		if(table.getSelectedRow() >= 0)
			start = table.getSelectedRow();
		boolean problemdetected = false;
		for(int i=start+1; i<table.getRowCount(); i++) {
			Page page = moodle.getPages().get(i);
			if(page.isProblematic()) {
				table.setRowSelectionInterval(i, i);
				problemdetected = true;
				scrollToRow(i);
				break;
			}
		}
		if(!problemdetected) {
			JOptionPane.showMessageDialog(frame, lang.getString("nomoreproblems"));
		}		
	}
	
	private void actionSelectAllProblems() {
		boolean problemdetected = false;
		ListSelectionModel model = table.getSelectionModel();
		model.clearSelection();
		for(int i=0; i<table.getRowCount(); i++) {
			Page page = moodle.getPages().get(i);
			if(page.isProblematic()) {
				model.addSelectionInterval(i, i);
				problemdetected = true;
				scrollToRow(i);
			}
		}				
		if(!problemdetected) {
			JOptionPane.showMessageDialog(frame, lang.getString("nomoreproblems"));
		}		
	}
	
	private void actionFix() {
		for(int row : table.getSelectedRows()) {
			try {
				if(moodle.getPages().fixPageData(row, frame)) {
					updateTableData(row);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(frame, e1.getMessage());
			}
		}		
	}
	
	private void actionFixFromPrevious() {
		try {
			executeCommand(Action.FIX_FROM_PREVIOUS);
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(frame, ex.getMessage());
		}		
	}
	
	private void actionFixFromFollowing() {

		try {
			executeCommand(Action.FIX_FROM_FOLLOWING);
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(frame, ex.getMessage());
		}		
	}
	
	private void actionRotateImage180() {
		try {
			executeCommand(Action.ROTATE180);
		} catch (Exception e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(frame, e1.getMessage());
		}		
	}
	
	private void actionSwap() {
		try {
			executeCommand(Action.SWAPFRONTBACK);
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(frame, e1.getMessage());
			e1.printStackTrace();
		}		
	}
	
	private void actionRotateAndFixFromPrevious() {
		try {
			executeCommand(Action.ROTATE180ANDFIX);
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(frame, e1.getMessage());
			e1.printStackTrace();
		}		
	}
	
	private void actionRotateAndFixFromFollowing() {
		try {
			executeCommand(Action.ROTATE180ANDFIXFROMFOLLOWING);
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(frame, e1.getMessage());
			e1.printStackTrace();
		}		
	}
}
