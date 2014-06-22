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

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Hashtable;
import javax.swing.JCheckBox;
import javax.swing.JSeparator;

import cl.uai.webcursos.emarking.desktop.data.Activity;
import cl.uai.webcursos.emarking.desktop.data.Course;
import cl.uai.webcursos.emarking.desktop.data.Moodle;

public class UploadAnswersDialog extends JDialog {

	private static final long serialVersionUID = 2577755818098593404L;
	private final JPanel contentPanel = new JPanel();
	private boolean cancelled = true;
	private final JTextField txtActivityName;
	private Moodle moodle;
	private JCheckBox chckbxNewActivity;
	private JComboBox<Course> comboBoxCourses;
	private JComboBox<Activity> comboBox;
	private JCheckBox chkMerge;

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	/**
	 * Create the dialog.
	 */
	public UploadAnswersDialog(Moodle _moodle) {
		setTitle(EmarkingDesktop.lang.getString("uploadanswers"));
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(null);
		contentPanel.setBounds(0, 0, 450, 239);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);
		{
			txtActivityName = new JTextField();
			txtActivityName.setEnabled(false);
			txtActivityName.setBounds(208, 176, 236, 28);
			contentPanel.add(txtActivityName);
			txtActivityName.setColumns(10);
		}
		{
			JLabel lblActivityName = new JLabel(EmarkingDesktop.lang.getString("activityname"));
			lblActivityName.setHorizontalAlignment(SwingConstants.RIGHT);
			lblActivityName.setBounds(6, 182, 190, 16);
			contentPanel.add(lblActivityName);
		}
		
		this.moodle = _moodle;
		comboBox = new JComboBox<Activity>();
		Hashtable<Integer, Activity> activitieshash = null;
		try {
			activitieshash = moodle.retrieveEmarkingActivities(moodle.getCourses());
		} catch (Exception e1) {
			e1.printStackTrace();
			comboBox.setEnabled(false);
		}
		Activity[] activities = new Activity[activitieshash.size()];
		activities = activitieshash.values().toArray(activities);
		ComboBoxModel<Activity> model = new DefaultComboBoxModel<Activity>(activities);
		comboBox.setModel(model);
		comboBox.setBounds(208, 43, 236, 27);
		contentPanel.add(comboBox);
		
		comboBoxCourses = new JComboBox<Course>();
		Course[] courses = new Course[moodle.getCourses().size()];
		courses = moodle.getCourses().values().toArray(courses);
		ComboBoxModel<Course> coursesModel = new DefaultComboBoxModel<Course>(courses);
		comboBoxCourses.setModel(coursesModel);
		comboBoxCourses.setBounds(208, 43, 236, 27);
		contentPanel.add(comboBoxCourses);
				
		JLabel label = new JLabel(EmarkingDesktop.lang.getString("emarkingactivity"));
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setBounds(6, 47, 190, 16);
		contentPanel.add(label);
		
		chckbxNewActivity = new JCheckBox("");
		chckbxNewActivity.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxNewActivity.isSelected()) {
					comboBox.setEnabled(false);
					txtActivityName.setEnabled(true);
					chkMerge.setEnabled(false);
				} else {
					comboBox.setEnabled(true);					
					txtActivityName.setEnabled(false);
					chkMerge.setEnabled(true);
				}
			}
		});
		chckbxNewActivity.setBounds(208, 141, 128, 23);
		contentPanel.add(chckbxNewActivity);
		
		JLabel lblNewActivity = new JLabel(EmarkingDesktop.lang.getString("createactivity"));
		lblNewActivity.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewActivity.setBounds(6, 145, 190, 16);
		contentPanel.add(lblNewActivity);
		{
			chkMerge = new JCheckBox("");
			chkMerge.setBounds(208, 82, 128, 23);
			contentPanel.add(chkMerge);
		}
		{
			JLabel lblMerge = new JLabel(EmarkingDesktop.lang.getString("replaceanswers"));
			lblMerge.setHorizontalAlignment(SwingConstants.RIGHT);
			lblMerge.setBounds(6, 86, 190, 16);
			contentPanel.add(lblMerge);
		}
		
		JSeparator separator = new JSeparator();
		separator.setBounds(6, 117, 438, 12);
		contentPanel.add(separator);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(0, 239, 450, 39);
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(chckbxNewActivity.isSelected() && txtActivityName.getText().trim().length() < 3) {
							return;
						}
						cancelled = false;
						setVisible(false);
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
		
		if(activitieshash.size() == 0) {
			chckbxNewActivity.setSelected(true);
			chckbxNewActivity.setEnabled(false);
			comboBox.setEnabled(false);
			chkMerge.setEnabled(false);
			txtActivityName.setEnabled(true);
		}
	}
	public JTextField getTxtActivityName() {
		return txtActivityName;
	}
	public JCheckBox getChckbxNewActivity() {
		return chckbxNewActivity;
	}
	public JComboBox<Activity> getActivitiesComboBox() {
		return comboBox;
	}
	public JCheckBox getChkMerge() {
		return chkMerge;
	}
}
