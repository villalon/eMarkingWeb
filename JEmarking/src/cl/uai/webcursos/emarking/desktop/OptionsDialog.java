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
import javax.swing.JTabbedPane;
import javax.swing.border.LineBorder;
import javax.swing.JSpinner;

public class OptionsDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3424578643623876331L;

	private static Logger logger = Logger.getLogger(OptionsDialog.class);
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
	private JPanel panel;
	private JButton btnOpenPdfFile;
	private final JTextField omrtemplate;
	private JButton btnOpenOMRTemplate;
	private JSpinner spinnerOMRshapeSize;
	private JSpinner spinnerOMRdensity;
	private JSpinner spinnerOMRthreshold;
	private JSpinner spinnerAnonymousPercentage;
	private JSpinner spinnerAnonymousPercentageCustomPage;
	private JSpinner spinnerCustomPage;

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
		setBounds(100, 100, 707, 400);
		this.moodle = _moodle;
		this.moodle.loadProperties();
		getContentPane().setLayout(new BorderLayout());
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
							if(!f.exists() || f.isDirectory() || (!f.getPath().endsWith(".pdf") && !f.getPath().endsWith(".zip"))) {
								throw new Exception(EmarkingDesktop.lang.getString("invalidpdffile") + " " + filename.getText());								
							}
							if(omrtemplate.getText().trim().length() > 0) {
								File omrf = new File(omrtemplate.getText());
								if(!omrf.exists() || omrf.isDirectory() || (!omrf.getPath().endsWith(".xtmpl"))) {
									throw new Exception(EmarkingDesktop.lang.getString("invalidomrfile") + " " + omrtemplate.getText());								
								}
							}
							moodle.setLastfile(filename.getText());
							moodle.setDoubleside(chckbxDoubleSide.isSelected());
							moodle.setMaxthreads(Integer.parseInt(getMaxThreads().getSelectedItem().toString())); 
							moodle.setResolution(Integer.parseInt(getResolution().getSelectedItem().toString())); 
							moodle.setMaxzipsize(getMaxZipSize().getSelectedItem().toString());
							moodle.setOMRTemplate(omrtemplate.getText());
							moodle.setThreshold(Integer.parseInt(spinnerOMRthreshold.getValue().toString()));
							moodle.setDensity(Integer.parseInt(spinnerOMRdensity.getValue().toString()));
							moodle.setShapeSize(Integer.parseInt(spinnerOMRshapeSize.getValue().toString()));
							moodle.setAnonymousPercentage(Integer.parseInt(spinnerAnonymousPercentage.getValue().toString()));
							moodle.setAnonymousPercentageCustomPage(Integer.parseInt(spinnerAnonymousPercentageCustomPage.getValue().toString()));
							moodle.saveProperties();
							cancelled = false;
							setVisible(false);
						} catch (Exception ex) {
							ex.printStackTrace();
							JOptionPane.showMessageDialog(panel, EmarkingDesktop.lang.getString("invaliddatainform"));
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

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		panel = new JPanel();
		tabbedPane.addTab(EmarkingDesktop.lang.getString("general"), null, panel, null);
		panel.setLayout(null);

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		panel_2.setBounds(10, 11, 665, 131);
		panel.add(panel_2);
		panel_2.setLayout(null);

		JLabel lblPassword = new JLabel(EmarkingDesktop.lang.getString("password"));
		lblPassword.setBounds(10, 99, 109, 14);
		panel_2.add(lblPassword);
		lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);

		password = new JPasswordField();
		password.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				testConnection();
			}
		});
		password.setBounds(129, 96, 329, 20);
		panel_2.add(password);
		this.password.setText(this.moodle.getPassword());

		btnTestConnection = new JButton(EmarkingDesktop.lang.getString("connect"));
		btnTestConnection.setEnabled(false);
		btnTestConnection.setBounds(468, 93, 172, 27);
		panel_2.add(btnTestConnection);

		username = new JTextField();
		username.setBounds(129, 65, 329, 20);
		panel_2.add(username);
		username.setColumns(10);
		this.username.setText(this.moodle.getUsername());

		moodleurl = new JTextField();
		moodleurl.setBounds(129, 34, 329, 20);
		panel_2.add(moodleurl);
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
				if(!validator.isValid(moodleurl.getText()) || !moodleurl.getText().endsWith("/")) {
					moodleurl.setForeground(Color.RED);
					btnTestConnection.setEnabled(false);
				} else {
					moodleurl.setForeground(Color.BLACK);
					btnTestConnection.setEnabled(true);
				}
			}
		});

		// Initializing values from moodle configuration
		this.moodleurl.setText(this.moodle.getUrl());

		JLabel lblMoodleUrl = new JLabel(EmarkingDesktop.lang.getString("moodleurl"));
		lblMoodleUrl.setBounds(10, 37, 109, 14);
		panel_2.add(lblMoodleUrl);
		lblMoodleUrl.setHorizontalAlignment(SwingConstants.RIGHT);

		JLabel lblUsername = new JLabel(EmarkingDesktop.lang.getString("username"));
		lblUsername.setBounds(10, 68, 109, 14);
		panel_2.add(lblUsername);
		lblUsername.setHorizontalAlignment(SwingConstants.RIGHT);

		JLabel lblMoodleSettings = new JLabel(EmarkingDesktop.lang.getString("moodlesettings"));
		lblMoodleSettings.setBounds(10, 11, 230, 14);
		panel_2.add(lblMoodleSettings);
		btnTestConnection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				testConnection();
			}
		});

		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		panel_3.setBounds(10, 159, 666, 131);
		panel.add(panel_3);
		panel_3.setLayout(null);

		JLabel lblPdfFile = new JLabel(EmarkingDesktop.lang.getString("pdffile"));
		lblPdfFile.setBounds(0, 39, 119, 14);
		panel_3.add(lblPdfFile);
		lblPdfFile.setHorizontalAlignment(SwingConstants.RIGHT);

		JLabel lblScanned = new JLabel(EmarkingDesktop.lang.getString("scanned"));
		lblScanned.setBounds(0, 64, 119, 14);
		panel_3.add(lblScanned);
		lblScanned.setHorizontalAlignment(SwingConstants.RIGHT);

		chckbxDoubleSide = new JCheckBox(EmarkingDesktop.lang.getString("doubleside"));
		chckbxDoubleSide.setEnabled(false);
		chckbxDoubleSide.setBounds(125, 60, 117, 23);
		panel_3.add(chckbxDoubleSide);
		chckbxDoubleSide.setToolTipText(EmarkingDesktop.lang.getString("doublesidetooltip"));
		this.chckbxDoubleSide.setSelected(this.moodle.isDoubleside());

		filename = new JTextField();
		filename.setEnabled(false);
		filename.setBounds(129, 36, 329, 20);
		panel_3.add(filename);
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
				validateFileForProcessing(!btnTestConnection.isEnabled());
			}
		});
		this.filename.setText(this.moodle.getLastfile());

		btnOpenPdfFile = new JButton(EmarkingDesktop.lang.getString("openfile"));
		btnOpenPdfFile.setEnabled(false);
		btnOpenPdfFile.setBounds(468, 33, 172, 27);
		panel_3.add(btnOpenPdfFile);

		JLabel lblPdfFileSettings = new JLabel(EmarkingDesktop.lang.getString("filesettings"));
		lblPdfFileSettings.setBounds(10, 11, 230, 14);
		panel_3.add(lblPdfFileSettings);

		JLabel lblOMRtemplate = new JLabel(EmarkingDesktop.lang.getString("omrfile"));
		lblOMRtemplate.setHorizontalAlignment(SwingConstants.RIGHT);
		lblOMRtemplate.setBounds(0, 95, 119, 14);
		panel_3.add(lblOMRtemplate);

		omrtemplate = new JTextField();
		omrtemplate.setEnabled(false);
		omrtemplate.setText((String) null);
		omrtemplate.setColumns(10);
		omrtemplate.setBounds(129, 92, 329, 20);
		panel_3.add(omrtemplate);
		omrtemplate.setText(this.moodle.getOMRTemplate());

		btnOpenOMRTemplate = new JButton(EmarkingDesktop.lang.getString("openomrfile"));
		btnOpenOMRTemplate.setEnabled(false);
		btnOpenOMRTemplate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle(EmarkingDesktop.lang.getString("openfiletitle"));
				chooser.setDialogType(JFileChooser.OPEN_DIALOG);
				chooser.setFileFilter(new FileFilter() {					
					@Override
					public String getDescription() {
						return "*.xtmpl";
					}
					@Override
					public boolean accept(File arg0) {
						if(arg0.getName().endsWith(".xtmpl") || arg0.isDirectory())
							return true;
						return false;
					}
				});
				int retval = chooser.showOpenDialog(panel);
				if(retval == JFileChooser.APPROVE_OPTION) {
					omrtemplate.setText(chooser.getSelectedFile().getAbsolutePath());
				} else {
					return;
				}
			}
		});
		btnOpenOMRTemplate.setBounds(468, 89, 172, 27);
		panel_3.add(btnOpenOMRTemplate);
		btnOpenPdfFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okButton.setEnabled(false);
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle(EmarkingDesktop.lang.getString("openfiletitle"));
				chooser.setDialogType(JFileChooser.OPEN_DIALOG);
				chooser.setFileFilter(new FileFilter() {					
					@Override
					public String getDescription() {
						return "*.pdf, *.zip";
					}
					@Override
					public boolean accept(File arg0) {
						if(arg0.getName().endsWith(".zip") || arg0.getName().endsWith(".pdf") || arg0.isDirectory())
							return true;
						return false;
					}
				});
				int retval = chooser.showOpenDialog(panel);
				if(retval == JFileChooser.APPROVE_OPTION) {
					filename.setText(chooser.getSelectedFile().getAbsolutePath());
					okButton.setEnabled(true);
				} else {
					return;
				}
			}
		});

		JPanel panel_1 = new JPanel();
		tabbedPane.addTab(EmarkingDesktop.lang.getString("advanced"), null, panel_1, null);
		panel_1.setLayout(null);

		JPanel panel_4 = new JPanel();
		panel_4.setLayout(null);
		panel_4.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		panel_4.setBounds(10, 11, 665, 131);
		panel_1.add(panel_4);

		JLabel lblAdvancedOptions = new JLabel(EmarkingDesktop.lang.getString("advancedoptions"));
		lblAdvancedOptions.setBounds(10, 11, 233, 14);
		panel_4.add(lblAdvancedOptions);

		JLabel lblThreads = new JLabel(EmarkingDesktop.lang.getString("maxthreads"));
		lblThreads.setBounds(10, 38, 130, 14);
		panel_4.add(lblThreads);
		lblThreads.setHorizontalAlignment(SwingConstants.RIGHT);

		JLabel lblSomething = new JLabel(EmarkingDesktop.lang.getString("separatezipfiles"));
		lblSomething.setBounds(10, 73, 130, 14);
		panel_4.add(lblSomething);
		lblSomething.setHorizontalAlignment(SwingConstants.RIGHT);

		JLabel label = new JLabel(EmarkingDesktop.lang.getString("resolution"));
		label.setBounds(10, 105, 130, 14);
		panel_4.add(label);
		label.setHorizontalAlignment(SwingConstants.RIGHT);

		resolution = new JComboBox<Integer>();
		resolution.setBounds(150, 99, 169, 27);
		panel_4.add(resolution);
		resolution.setModel(new DefaultComboBoxModel<Integer>(new Integer[] {75, 100, 150, 300, 400, 500, 600}));
		resolution.setSelectedIndex(2);
		this.resolution.setSelectedItem(this.moodle.getQr().getResolution());		

		maxZipSize = new JComboBox<String>();
		maxZipSize.setBounds(150, 67, 169, 27);
		panel_4.add(maxZipSize);
		maxZipSize.setModel(new DefaultComboBoxModel<String>(new String[] {"<dynamic>", "2Mb", "4Mb", "8Mb", "16Mb", "32Mb", "64Mb", "128Mb", "256Mb", "512Mb", "1024Mb"}));
		maxZipSize.setSelectedIndex(6);
		this.maxZipSize.setSelectedItem(this.moodle.getMaxZipSizeString());

		maxThreads = new JComboBox<Integer>();
		maxThreads.setBounds(150, 32, 169, 27);
		panel_4.add(maxThreads);
		maxThreads.setModel(new DefaultComboBoxModel<Integer>(new Integer[] {2, 4, 8, 16}));
		maxThreads.setSelectedIndex(1);
		this.maxThreads.setSelectedItem(this.moodle.getQr().getMaxThreads());
		
		JPanel panel_5 = new JPanel();
		panel_5.setLayout(null);
		panel_5.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		panel_5.setBounds(10, 153, 665, 131);
		panel_1.add(panel_5);
		
		JLabel lblOMRoptions = new JLabel(EmarkingDesktop.lang.getString("omroptions"));
		lblOMRoptions.setBounds(10, 11, 233, 14);
		panel_5.add(lblOMRoptions);
		
		JLabel lblOMRthreshold = new JLabel(EmarkingDesktop.lang.getString("omrthreshold"));
		lblOMRthreshold.setHorizontalAlignment(SwingConstants.RIGHT);
		lblOMRthreshold.setBounds(10, 32, 130, 14);
		panel_5.add(lblOMRthreshold);
		
		JLabel lblShapeSize = new JLabel(EmarkingDesktop.lang.getString("omrshapesize"));
		lblShapeSize.setHorizontalAlignment(SwingConstants.RIGHT);
		lblShapeSize.setBounds(10, 99, 130, 14);
		panel_5.add(lblShapeSize);
		
		JLabel lblDensity = new JLabel(EmarkingDesktop.lang.getString("omrdensity"));
		lblDensity.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDensity.setBounds(10, 70, 130, 14);
		panel_5.add(lblDensity);
		
		spinnerOMRthreshold = new JSpinner();
		spinnerOMRthreshold.setBounds(150, 32, 169, 20);
		panel_5.add(spinnerOMRthreshold);
		spinnerOMRthreshold.setValue(this.moodle.getOMRthreshold());
		
		spinnerOMRdensity = new JSpinner();
		spinnerOMRdensity.setBounds(150, 67, 169, 20);
		panel_5.add(spinnerOMRdensity);
		spinnerOMRdensity.setValue(this.moodle.getOMRdensity());
		
		spinnerOMRshapeSize = new JSpinner();
		spinnerOMRshapeSize.setBounds(150, 99, 169, 20);
		panel_5.add(spinnerOMRshapeSize);
		spinnerOMRshapeSize.setValue(this.moodle.getOMRshapeSize());
		
		JLabel lblAnonymousPercentage = new JLabel("<html>"+EmarkingDesktop.lang.getString("anonymouspercentage")+"</html>");
		lblAnonymousPercentage.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAnonymousPercentage.setBounds(329, 32, 130, 27);
		panel_5.add(lblAnonymousPercentage);
		
		spinnerAnonymousPercentage = new JSpinner();
		spinnerAnonymousPercentage.setBounds(469, 32, 169, 20);
		panel_5.add(spinnerAnonymousPercentage);
		spinnerAnonymousPercentage.setValue(this.moodle.getAnonymousPercentage());
		
		JLabel lblAnonymousPercentageCustomPage = new JLabel("<html>"+EmarkingDesktop.lang.getString("anonymouspercentagecustompage")+"</html>");
		lblAnonymousPercentageCustomPage.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAnonymousPercentageCustomPage.setBounds(329, 70, 130, 27);
		panel_5.add(lblAnonymousPercentageCustomPage);
		
		spinnerAnonymousPercentageCustomPage = new JSpinner();
		spinnerAnonymousPercentageCustomPage.setBounds(469, 70, 169, 20);
		panel_5.add(spinnerAnonymousPercentageCustomPage);
		spinnerAnonymousPercentageCustomPage.setValue(this.moodle.getAnonymousPercentageCustomPage());
		
		JLabel lblCustomPage = new JLabel("<html>"+EmarkingDesktop.lang.getString("anonymouscustompage")+"</html>");
		lblCustomPage.setHorizontalAlignment(SwingConstants.RIGHT);
		lblCustomPage.setBounds(329, 99, 130, 27);
		panel_5.add(lblCustomPage);
		
		spinnerCustomPage = new JSpinner();
		spinnerCustomPage.setBounds(469, 99, 169, 20);
		panel_5.add(spinnerCustomPage);
		spinnerCustomPage.setValue(this.moodle.getAnonymousCustomPage());
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
	private void validateFileForProcessing(boolean activateOkButton) {
		File f = new File(filename.getText());
		if(!f.exists() || f.isDirectory() || (!f.getPath().endsWith(".pdf") && !f.getPath().endsWith(".zip"))) {
			filename.setForeground(Color.RED);
			okButton.setEnabled(false);
		} else {
			filename.setForeground(Color.BLACK);
			if(activateOkButton)
				okButton.setEnabled(true);
		}		
	}
	public String getOMRTemplate() {
		return this.omrtemplate.getText();
	}
	private void testConnection() {
		btnTestConnection.setEnabled(false);
		logger.debug("Testing Moodle connection");
		moodle.setUrl(moodleurl.getText());
		moodle.setUsername(username.getText());
		String _password = new String(password.getPassword());
		moodle.setPassword(_password);
		if(moodle.connect()) {
			btnTestConnection.setIcon(new ImageIcon(EmarkingDesktop.class.getResource("/cl/uai/webcursos/emarking/desktop/resources/glyphicons_206_ok_2.png")));
			btnTestConnection.setText(EmarkingDesktop.lang.getString("connectionsuccessfull"));
			filename.setEnabled(true);
			chckbxDoubleSide.setEnabled(true);
			btnOpenPdfFile.setEnabled(true);
			btnTestConnection.setEnabled(false);
			username.setEnabled(false);
			moodleurl.setEnabled(false);
			password.setEnabled(false);
			btnOpenOMRTemplate.setEnabled(true);
			omrtemplate.setEnabled(true);
			validateFileForProcessing(true);
		} else {
			JOptionPane.showMessageDialog(panel, EmarkingDesktop.lang.getString("connectionfailed"));					
			btnTestConnection.setEnabled(true);
		}		
	}
}
