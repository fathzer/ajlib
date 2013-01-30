package net.astesana.ajlib.swing.table;

import java.awt.Component;

import javax.swing.table.TableModel;

/** A JTable that fix the following bug in the JTable.
 * <br>Bugs fixed :<ul>
 * <li>The row height is not set accordingly to the font (more generally, to the renderers height).
 * <br>When the table is first layed out, its row height is set to the maximum size of its renderers</li>
 * </ul>
 * @author Jean-Marc Astesana
 * Licence GPL v3
 */
@SuppressWarnings("serial")
public class JTable extends javax.swing.JTable {
	private boolean rowHeightInited;

	public JTable() {
		super();
	}
	
	public JTable(TableModel tableModel) {
		super(tableModel);
	}

	@Override
	public void doLayout() {
		if (!rowHeightInited && getRowCount()>0) {
			int height = 1;
			for (int column = 0; column < getColumnCount(); column++) {
				Component renderer = this.prepareRenderer(getCellRenderer(0, column), 0, column);
				height = Math.max(height, renderer.getPreferredSize().height);
			}
			setRowHeight(height);
			rowHeightInited = true;
		}
		super.doLayout();
	}

}
