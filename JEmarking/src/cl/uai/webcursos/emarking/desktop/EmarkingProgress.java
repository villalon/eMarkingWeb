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

import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import javax.swing.JButton;

import org.apache.log4j.Logger;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EmarkingProgress extends JDialog {
	
	private static Logger logger = Logger.getLogger(EmarkingProgress.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5140842177243998750L;
	private JProgressBar progressBar;
	private JLabel lblProgress;
	private Runnable qr;
	private Thread th;
	private JButton btnCancel;

	/**
	 * Create the frame.
	 */
	public EmarkingProgress() {
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		setAlwaysOnTop(true);
		setIconImage(Toolkit.getDefaultToolkit().getImage(EmarkingProgress.class.getResource("/cl/uai/webcursos/emarking/desktop/resources/glyphicons_137_cogwheels.png")));
		setTitle(EmarkingDesktop.lang.getString("processingexam"));
		setBounds(100, 100, 450, 133);
		getContentPane().setLayout(null);
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		lblProgress = new JLabel(EmarkingDesktop.lang.getString("processingpages"));
		lblProgress.setBounds(10, 11, 414, 14);
		getContentPane().add(lblProgress);
		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setBounds(10, 36, 414, 23);
		getContentPane().add(progressBar);
		
		btnCancel = new JButton(EmarkingDesktop.lang.getString("cancel"));
		btnCancel.setBounds(165, 70, 89, 27);
		
		btnCancel.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				logger.debug("Cancelling operation");
				lblProgress.setText(EmarkingDesktop.lang.getString("canceloperation"));
				btnCancel.setEnabled(false);
				th.interrupt();
			}
		});
		
		getContentPane().add(btnCancel);

	}
	public void setWorker(Runnable qr) {
		this.qr = qr;
	}
	public void startProcessing() {
		btnCancel.setEnabled(true);
		th = new Thread(qr);
		th.start();
		this.setVisible(true);
	}
	public JProgressBar getProgressBar() {
		return progressBar;
	}
	public JLabel getLblProgress() {
		return lblProgress;
	}
}
