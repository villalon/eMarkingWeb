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
import java.awt.FlowLayout;

import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.Toolkit;
import javax.swing.SwingConstants;

import cl.uai.webcursos.emarking.desktop.data.Course;
import cl.uai.webcursos.emarking.desktop.data.Moodle;
import cl.uai.webcursos.emarking.desktop.data.Student;

public class FixRowDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3193581699837376410L;
	private final JPanel contentPanel = new JPanel();
	private JLabel lblPageNumber;
	private Moodle moodle = null;
	
	/**
	 * @return the moodle
	 */
	public Moodle getMoodle() {
		return moodle;
	}

	/**
	 * @param moodle the moodle to set
	 */
	public void setMoodle(Moodle moodle) {
		this.moodle = moodle;
	}
	
	private boolean cancelled = false;
	private JComboBox<Student> studentsCombo;
	private JComboBox<Course> coursesCombo;
	private JComboBox<Integer> comboPageNumber;

	/**
	 * @return the cancelled
	 */
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * Create the dialog.
	 */
	public FixRowDialog(Moodle moodle) {
		setIconImage(Toolkit.getDefaultToolkit().getImage(FixRowDialog.class.getResource("/cl/uai/webcursos/emarking/desktop/resources/glyphicons_152_check.png")));
		setTitle(EmarkingDesktop.lang.getString("fixrow"));
		setMoodle(moodle);
		setBounds(100, 100, 451, 194);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblPageNumberTitle = new JLabel(EmarkingDesktop.lang.getString("pagenumber"));
			lblPageNumberTitle.setHorizontalAlignment(SwingConstants.RIGHT);
			lblPageNumberTitle.setBounds(10, 16, 104, 14);
			contentPanel.add(lblPageNumberTitle);
		}
		{
			lblPageNumber = new JLabel("N");
			lblPageNumber.setBounds(126, 16, 291, 14);
			contentPanel.add(lblPageNumber);
		}
		{
			JLabel lblNewLabel_1 = new JLabel(EmarkingDesktop.lang.getString("student"));
			lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
			lblNewLabel_1.setBounds(10, 45, 104, 14);
			contentPanel.add(lblNewLabel_1);
		}
		{
			JLabel lblCourseId = new JLabel(EmarkingDesktop.lang.getString("courseid"));
			lblCourseId.setHorizontalAlignment(SwingConstants.RIGHT);
			lblCourseId.setBounds(10, 74, 104, 14);
			contentPanel.add(lblCourseId);
		}
		{
			JLabel lblExamPage = new JLabel(EmarkingDesktop.lang.getString("exampage"));
			lblExamPage.setHorizontalAlignment(SwingConstants.RIGHT);
			lblExamPage.setBounds(10, 103, 104, 14);
			contentPanel.add(lblExamPage);
		}
		
		studentsCombo = new JComboBox<Student>();
		Student[] students = new Student[moodle.getStudents().size()];
		students = moodle.getStudents().values().toArray(students);
		ComboBoxModel<Student> model = new DefaultComboBoxModel<Student>(students);
		studentsCombo.setModel(model);
		studentsCombo.setBounds(124, 42, 300, 27);
		contentPanel.add(studentsCombo);
		{
			coursesCombo = new JComboBox<Course>();
			coursesCombo.setBounds(124, 71, 300, 27);
			Course[] courses = new Course[moodle.getCourses().size()];
			courses = moodle.getCourses().values().toArray(courses);
			ComboBoxModel<Course> coursesmodel = new DefaultComboBoxModel<Course>(courses);
			coursesCombo.setModel(coursesmodel);
			contentPanel.add(coursesCombo);
		}
		
		Integer[] items = new Integer[100];
		for(int i=1;i<items.length;i++) {
			items[i] = i;
		}
		comboPageNumber = new JComboBox<Integer>();
		comboPageNumber.setBounds(124, 100, 300, 27);
		comboPageNumber.setModel(new DefaultComboBoxModel<Integer>(items));
		
		contentPanel.add(comboPageNumber);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							int exampage = comboPageNumber.getSelectedIndex();
							if(exampage <= 0) {
								throw new Exception(EmarkingDesktop.lang.getString("invalidvalues"));
							}
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(null, ex.getMessage());
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
	}
	public JLabel getLblPageNumber() {
		return lblPageNumber;
	}
	public JComboBox<Student> getStudentsCombo() {
		return studentsCombo;
	}
	public JComboBox<Course> getCoursesCombo() {
		return coursesCombo;
	}
	public JComboBox<Integer> getComboPageNumber() {
		return comboPageNumber;
	}
}
