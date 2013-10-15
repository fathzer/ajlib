package com.fathzer.soft.ajlib.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.util.Date;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/** A JTable CellRenderer easy to customize.
 * @author Jean-Marc Astesana (Licence GPL v3)
 */
public class CustomCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		row = table.convertRowIndexToModel(row);
		column = table.convertColumnIndexToModel(column);
		this.setHorizontalAlignment(getAlignment(table, value, isSelected, hasFocus, row, column));
		this.setBackground(getBackground(table, value, isSelected, hasFocus, row, column));
		this.setForeground(getForeground(table, value, isSelected, hasFocus, row, column));
		setValue(getValue(table, value, isSelected, hasFocus, row, column));
		return this;
	}
	
	/** Gets the alignment. 
	 * @param table The JTable.
	 * @param value The original value in the cell (not the replacement value returned by getReplacementValue).
	 * @param isSelected true if cell is selected.
	 * @param hasFocus true if cell has the focus.
	 * @param rowModel The row index in the model.
	 * @param columnModel The column index in the model.
	 * @return The alignment, an int in the set SwingConstants: LEFT, CENTER, RIGHT, LEADING or TRAILING.<br>
	 * By default it returns CENTER if value is an instance of Date, RIGHT if value is an instance of Double, Integer, Long or Float,
	 * LEFT in other cases. You may override this method to change this behavior.
	 */
	protected int getAlignment(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowModel, int columnModel) {
		int alignment = SwingConstants.LEFT;
		if (value instanceof Date) {
			alignment = SwingConstants.CENTER;
		} else if ((value instanceof Double) || (value instanceof Integer) || (value instanceof Long) || (value instanceof Float)) {
			alignment = SwingConstants.RIGHT;
		}
		return alignment;
	}

	/** Gets the replacement value of a cell.<br>
	 * This method allows you to change the value on the fly. By default, it returns the original value but you can override
	 * this method to change this behavior.
	 * @param table The JTable.
	 * @param value The value in the cell.
	 * @param isSelected true if cell is selected.
	 * @param hasFocus true if cell has the focus.
	 * @param rowModel The row index in the model.
	 * @param columnModel The column index in the model.
	 * @return The value that should be displayed in the cell.
	 */
	protected Object getValue(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowModel, int columnModel) {
		return value;
	}
	
	/** Gets the background color of a cell.<br>
	 * @param table The JTable.
	 * @param value The value in the cell.
	 * @param isSelected true if cell is selected.
	 * @param hasFocus true if cell has the focus.
	 * @param rowModel The row index in the model.
	 * @param columnModel The column index in the model.
	 * @return The background color of the cell. The default implementation returns the JTable default color.
	 */
	protected Color getBackground(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowModel, int columnModel) {
		return isSelected ? table.getSelectionBackground() : table.getBackground();
	}

	/** Gets the foreground color of a cell.<br>
	 * @param table The JTable.
	 * @param value The value in the cell.
	 * @param isSelected true if cell is selected.
	 * @param hasFocus true if cell has the focus.
	 * @param rowModel The row index in the model.
	 * @param columnModel The column index in the model.
	 * @return The foreground color of the cell. The default implementation returns the JTable default color.
	 */
	protected Color getForeground(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowModel, int columnModel) {
		return isSelected ? table.getSelectionForeground() : table.getForeground();
	}
}
