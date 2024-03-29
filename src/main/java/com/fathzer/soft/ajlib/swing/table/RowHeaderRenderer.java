package com.fathzer.soft.ajlib.swing.table;

import java.awt.Component;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.JTable;

/**
 * A table CellRenderer that renders row headers.
 */
public class RowHeaderRenderer implements TableCellRenderer {
	private TableCellRenderer renderer;

	public RowHeaderRenderer() {
		renderer = new JTable().getTableHeader().getDefaultRenderer();
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
		try {
			return renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
		} catch (NullPointerException e) {
			// Bug workaround: Under some unclear circumstances (Launch Application, Menu File/New, then choose Windows look and feel, then Windows Classic,
			// then Nimbus, then, open a non empty file ... a NullPointerException occurred :-(
			return new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
		}
	}
}
