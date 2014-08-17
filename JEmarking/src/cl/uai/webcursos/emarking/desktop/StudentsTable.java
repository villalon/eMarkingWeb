/**
 * 
 */
package cl.uai.webcursos.emarking.desktop;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.apache.log4j.Logger;

import cl.uai.webcursos.emarking.desktop.data.Moodle;
import cl.uai.webcursos.emarking.desktop.data.Student;

/**
 * @author jorgevillalon
 *
 */
public class StudentsTable extends JTable {

	private static Logger logger = Logger.getLogger(StudentsTable.class);
	private StudentsTableModel model;
	private StudentsTableCellRenderer renderer;
	private Moodle moodle;

	private final static Object[][] emptydata = {};

	private final static String[] columnNames = {
		"#",
		EmarkingDesktop.lang.getString("student"),
		EmarkingDesktop.lang.getString("pages")
	};

	/**
	 * 
	 */
	private static final long serialVersionUID = -2094514351707140215L;

	public StudentsTable(Moodle _moodle) {
		super(new StudentsTableModel(emptydata, columnNames));

		this.moodle = _moodle;
		this.renderer = new StudentsTableCellRenderer(moodle);
		this.setDefaultRenderer(Object.class, this.renderer);

		this.setAutoResizeMode(AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		this.setStudentsTableModel((StudentsTableModel) this.getModel());
	}

	public StudentsTableModel getPagesTableModel() {
		return model;
	}

	public void setStudentsTableModel(StudentsTableModel model) {
		this.model = model;
	}

	/**
	 * 
	 * @param data
	 * @param row
	 * @param doubleside
	 */
	public void updateData(Student student) {
		if(this.model.getRowCount() == 0) {
			for(int i=0; i<this.moodle.getStudents().size(); i++) {
				Student st = this.moodle.getStudentByRowNumber(i);
				this.model.addRow((Object[]) null);
				if(st != null) {
					this.setValueAt(st.getRownumber()+1, i, 0);
					this.setValueAt(st.getFullname(), i, 1);
					this.setValueAt(st.getPages(), i, 2);
				} else {
					logger.error("Invalid student");
				}
			}
		}
		int row = student.getRownumber();
		this.setValueAt(student.getRownumber()+1, row, 0);
		this.setValueAt(student.getFullname(), row, 1);
		this.setValueAt(student.getPages(), row, 2);

		// Resize columns code
		for(int i=0;i<this.model.getColumnCount();i++) {
			int width = 0;
			for (int mrow = 0; mrow < this.getRowCount(); mrow++) {
				TableCellRenderer renderer = this.getCellRenderer(mrow, i);
				Component comp = this.prepareRenderer(renderer, mrow, i);
				width = Math.max (comp.getPreferredSize().width, width);
			}
			this.getColumnModel().getColumn(i).setPreferredWidth(width);
		}
	}
}
