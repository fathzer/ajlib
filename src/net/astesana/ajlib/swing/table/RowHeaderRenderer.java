package net.astesana.ajlib.swing.table;

import java.awt.Component;
import javax.swing.table.TableCellRenderer;
import javax.swing.JTable;

/**
 * A table CellRenderer that renders row headers.
 */
public class RowHeaderRenderer implements TableCellRenderer {
	private TableCellRenderer renderer;

	public RowHeaderRenderer() {
	}

	private TableCellRenderer getRenderer() {
		if (renderer==null) {
			renderer = new JTable().getTableHeader().getDefaultRenderer();
		}
		return renderer;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
		return getRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
	}
}
