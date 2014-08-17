/**
 * 
 */
package cl.uai.webcursos.emarking.desktop;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import cl.uai.webcursos.emarking.desktop.data.Moodle;

/**
 * @author jorgevillalon
 *
 */
public class PagesTable extends JTable {

	private PagesTableModel model;
	private PagesTableCellRenderer renderer;
	private Moodle moodle;
	
	private final static Object[][] emptydata = {};
	
	private final static String[] columnNames = {
			"#",
			EmarkingDesktop.lang.getString("student"),
			EmarkingDesktop.lang.getString("course"),
			EmarkingDesktop.lang.getString("page")
	};
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2094514351707140215L;

	public PagesTable(Moodle _moodle) {
		super(new PagesTableModel(emptydata, columnNames));
		
		this.moodle = _moodle;
		this.renderer = new PagesTableCellRenderer(moodle);
		this.setDefaultRenderer(Object.class, this.renderer);

		this.setAutoResizeMode(AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		this.setPagesTableModel((PagesTableModel) this.getModel());
	}

	public PagesTableModel getPagesTableModel() {
		return model;
	}

	public void setPagesTableModel(PagesTableModel model) {
		this.model = model;
	}
	
	/**
	 * 
	 * @param data
	 * @param row
	 * @param doubleside
	 */
	public void updateData(Object[] data, int row, boolean doubleside) {

		for(int i=0;i<data.length;i++) {
			this.setValueAt(data[i], row, i);
			int width = 0;
			for (int mrow = 0; mrow < this.getRowCount(); mrow++) {
			     TableCellRenderer renderer = this.getCellRenderer(mrow, i);
			     Component comp = this.prepareRenderer(renderer, mrow, i);
			     width = Math.max (comp.getPreferredSize().width, width);
			}
		    this.getColumnModel().getColumn(i).setPreferredWidth(width);
		}
		
		if(doubleside && row % 2 == 0) {
			updateData(data, row + 1, doubleside);
		}
	}
}
