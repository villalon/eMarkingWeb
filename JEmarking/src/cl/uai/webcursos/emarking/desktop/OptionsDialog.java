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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JCheckBox;
import javax.swing.ImageIcon;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.validator.routines.UrlValidator;
import org.apache.log4j.Logger;

import cl.uai.webcursos.emarking.desktop.data.Moodle;

import java.awt.Toolkit;

import javax.swing.SwingConstants;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class OptionsDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3424578643623876331L;
	
	private static Logger logger = Logger.getLogger(OptionsDialog.class);
	
	private final JPanel contentPanel = new JPanel();
	private JCheckBox chckbxDoubleSide;
	private boolean cancelled = false;
	private final JTextField username;
	private final JPasswordField password;
	private final JTextField moodleurl;
	private final JTextField filename;
	private final Moodle moodle;
	private final JButton btnTestConnection;
	private JButton okButton;
	private JComboBox<String> maxZipSize;
	private JComboBox<Integer> maxThreads;
	private JComboBox<Integer> resolution;

	/**
	 * @return the cancelled
	 */
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * Create the dialog.
	 */
	public OptionsDialog(Moodle _moodle) {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				cancelled = true;
			}
		});
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(OptionsDialog.class.getResource("/cl/uai/webcursos/emarking/desktop/resources/glyphicons_439_wrench.png")));
		setTitle(EmarkingDesktop.lang.getString("emarkingoptions"));
		setModal(true);
		setBounds(100, 100, 666, 397);
		this.moodle = _moodle;
		this.moodle.loadProperties();
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		chckbxDoubleSide = new JCheckBox(EmarkingDesktop.lang.getString("doubleside"));
		chckbxDoubleSide.setBounds(146, 95, 117, 23);
		chckbxDoubleSide.setToolTipText(EmarkingDesktop.lang.getString("doublesidetooltip"));
		
		username = new JTextField();
		username.setBounds(146, 258, 329, 20);
		username.setColumns(10);
		
		JLabel lblUsername = new JLabel(EmarkingDesktop.lang.getString("username"));
		lblUsername.setHorizontalAlignment(SwingConstants.RIGHT);
		lblUsername.setBounds(10, 261, 130, 14);
		
		JLabel lblPassword = new JLabel(EmarkingDesktop.lang.getString("password"));
		lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPassword.setBounds(10, 297, 130, 14);
		
		password = new JPasswordField();
		password.setBounds(146, 294, 329, 20);
		
		JLabel lblScanned = new JLabel(EmarkingDesktop.lang.getString("scanned"));
		lblScanned.setHorizontalAlignment(SwingConstants.RIGHT);
		lblScanned.setBounds(10, 98, 130, 14);
		
		JLabel lblMoodleUrl = new JLabel(EmarkingDesktop.lang.getString("moodleurl"));
		lblMoodleUrl.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMoodleUrl.setBounds(10, 134, 130, 14);
		
		moodleurl = new JTextField();
		moodleurl.setBounds(146, 131, 329, 20);
		moodleurl.setColumns(10);
		moodleurl.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				warn();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				warn();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				warn();
			}
			
			private void warn() {
				UrlValidator validator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
				if(!validator.isValid(moodleurl.getText())) {
					moodleurl.setForeground(Color.RED);
				} else {
					moodleurl.setForeground(Color.BLACK);
				}
			}
		});
		
		contentPanel.setLayout(null);
		contentPanel.add(lblMoodleUrl);
		contentPanel.add(moodleurl);
		contentPanel.add(lblScanned);
		contentPanel.add(chckbxDoubleSide);
		contentPanel.add(lblPassword);
		contentPanel.add(password);
		contentPanel.add(lblUsername);
		contentPanel.add(username);
		
		JLabel lblPdfFile = new JLabel(EmarkingDesktop.lang.getString("pdffile"));
		lblPdfFile.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPdfFile.setBounds(10, 62, 130, 14);
		contentPanel.add(lblPdfFile);
		
		filename = new JTextField();
		filename.setBounds(146, 59, 329, 20);
		contentPanel.add(filename);
		filename.setColumns(10);
		filename.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				warn();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				warn();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				warn();
			}
			
			private void warn() {
				File f = new File(filename.getText());
				if(!f.exists() || f.isDirectory() || !f.getPath().endsWith(".pdf")) {
					filename.setForeground(Color.RED);
				} else {
					filename.setForeground(Color.BLACK);
				}
			}
		});
		
		JButton btnOpenPdfFile = new JButton(EmarkingDesktop.lang.getString("openfile"));
		btnOpenPdfFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle(EmarkingDesktop.lang.getString("openfiletitle"));
				chooser.setDialogType(JFileChooser.OPEN_DIALOG);
				chooser.setFileFilter(new FileFilter() {					
					@Override
					public String getDescription() {
						return "*.pdf";
					}					
					@Override
					public boolean accept(File arg0) {
						if(arg0.getName().endsWith(".pdf") || arg0.isDirectory())
							return true;
						return false;
					}
				});
				int retval = chooser.showOpenDialog(contentPanel);
				if(retval == JFileChooser.APPROVE_OPTION) {
					filename.setText(chooser.getSelectedFile().getAbsolutePath());
				} else {
					return;
				}
			}
		});
		btnOpenPdfFile.setBounds(487, 57, 172, 27);
		contentPanel.add(btnOpenPdfFile);
		
		btnTestConnection = new JButton(EmarkingDesktop.lang.getString("connect"));
		btnTestConnection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.debug("Testing Moodle connection");
				btnTestConnection.setEnabled(false);
				moodle.setUrl(moodleurl.getText());
				moodle.setUsername(username.getText());
				String _password = new String(password.getPassword());
				moodle.setPassword(_password);
				if(moodle.connect()) {
					btnTestConnection.setIcon(new ImageIcon(EmarkingDesktop.class.getResource("/cl/uai/webcursos/emarking/desktop/resources/glyphicons_206_ok_2.png")));
					btnTestConnection.setText(EmarkingDesktop.lang.getString("connectionsuccessfull"));
					okButton.setEnabled(true);
				} else {
					JOptionPane.showMessageDialog(contentPanel, EmarkingDesktop.lang.getString("connectionfailed"));					
					btnTestConnection.setEnabled(true);
				}
			}
		});
		btnTestConnection.setBounds(487, 292, 172, 27);
		contentPanel.add(btnTestConnection);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton(EmarkingDesktop.lang.getString("ok"));
				okButton.setEnabled(false);
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							UrlValidator validator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
							if(!validator.isValid(moodleurl.getText())) {
								throw new Exception(EmarkingDesktop.lang.getString("invalidmoodleurl")+ " " + moodleurl.getText());
							}
							File f = new File(filename.getText());
							if(!f.exists() || f.isDirectory() || !f.getPath().endsWith(".pdf")) {
								throw new Exception(EmarkingDesktop.lang.getString("invalidpdffile") + " " + filename.getText());								
							}
							moodle.setLastfile(filename.getText());
							moodle.setDoubleside(chckbxDoubleSide.isSelected());
							moodle.setMaxthreads((int) getMaxThreads().getSelectedItem()); 
							moodle.setResolution((int) getResolution().getSelectedItem()); 
							moodle.setMaxzipsize(getMaxZipSize().getSelectedItem().toString());
							moodle.saveProperties();
							cancelled = false;
							setVisible(false);
						} catch (Exception ex) {
							ex.printStackTrace();
							JOptionPane.showMessageDialog(contentPanel, EmarkingDesktop.lang.getString("invaliddatainform"));
						}
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton(EmarkingDesktop.lang.getString("cancel"));
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cancelled = true;
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		JLabel lblSomething = new JLabel(EmarkingDesktop.lang.getString("separatezipfiles"));
		lblSomething.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSomething.setBounds(10, 165, 130, 14);
		contentPanel.add(lblSomething);
		
		JLabel lblThreads = new JLabel(EmarkingDesktop.lang.getString("maxthreads"));
		lblThreads.setHorizontalAlignment(SwingConstants.RIGHT);
		lblThreads.setBounds(10, 195, 130, 14);
		contentPanel.add(lblThreads);
		
		maxZipSize = new JComboBox<String>();
		maxZipSize.setModel(new DefaultComboBoxModel<String>(new String[] {"<dynamic>", "2Mb", "4Mb", "8Mb", "16Mb", "32Mb", "64Mb", "128Mb", "256Mb", "512Mb", "1024Mb"}));
		maxZipSize.setSelectedIndex(6);
		maxZipSize.setBounds(146, 160, 169, 27);
		contentPanel.add(maxZipSize);
		
		maxThreads = new JComboBox<Integer>();
		maxThreads.setModel(new DefaultComboBoxModel<Integer>(new Integer[] {2, 4, 8, 16}));
		maxThreads.setSelectedIndex(1);
		maxThreads.setBounds(146, 190, 169, 27);
		contentPanel.add(maxThreads);
		
		JLabel label = new JLabel(EmarkingDesktop.lang.getString("resolution"));
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setBounds(10, 226, 130, 14);
		contentPanel.add(label);
		
		resolution = new JComboBox<Integer>();
		resolution.setModel(new DefaultComboBoxModel<Integer>(new Integer[] {75, 100, 150, 300, 400, 500, 600}));
		resolution.setSelectedIndex(2);
		resolution.setBounds(146, 221, 169, 27);
		contentPanel.add(resolution);
		
		// Initializing values from moodle configuration
		this.moodleurl.setText(this.moodle.getUrl());
		this.username.setText(this.moodle.getUsername());
		this.filename.setText(this.moodle.getLastfile());
		this.chckbxDoubleSide.setSelected(this.moodle.isDoubleside());
		this.password.setText(this.moodle.getPassword());
		this.maxZipSize.setSelectedItem(this.moodle.getMaxZipSizeString());
		this.maxThreads.setSelectedItem(this.moodle.getQr().getMaxThreads());
		this.resolution.setSelectedItem(this.moodle.getQr().getResolution());		
	}
	
	public boolean getDoubleSideSelected() {
		return chckbxDoubleSide.isSelected();
	}
	public void setDoubleSideSelected(boolean selected) {
		chckbxDoubleSide.setSelected(selected);
	}
	public String getUsername() {
		return username.getText();
	}
	public void setUsername(String text) {
		username.setText(text);
	}
	public JPasswordField getPassword() {
		return password;
	}
	public JTextField getMoodleUrl() {
		return moodleurl;
	}
	public JTextField getFilename() {
		return filename;
	}
	public JComboBox<String> getMaxZipSize() {
		return maxZipSize;
	}
	public JComboBox<Integer> getMaxThreads() {
		return maxThreads;
	}
	public JComboBox<Integer> getResolution() {
		return resolution;
	}
}
