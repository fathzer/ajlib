package net.astesana.ajlib.swing.table;

import java.awt.Component;
import javax.swing.table.TableCellRenderer;
import javax.swing.JTable;

/**
 * A table CellRenderer that renders row headers.
 */
public class RowHeaderRenderer implements TableCellRenderer {
	private boolean hideSelection;
	private TableCellRenderer renderer;

	public RowHeaderRenderer(boolean hideSelection) {
		renderer = new JTable().getTableHeader().getDefaultRenderer();
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
		if (hideSelection) isSelected = false;
		return renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
	}
}
