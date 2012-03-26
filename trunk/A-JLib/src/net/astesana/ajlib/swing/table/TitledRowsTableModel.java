package net.astesana.ajlib.swing.table;

import net.astesana.ajlib.swing.widget.ExcelPane;

/** A table model that defines row titles.
 * @see ExcelPane
 */
public interface TitledRowsTableModel {
	/** Gets a row title.
	 * @param rowIndex The index of the row
	 * @return the title of the row
	 */
	public String getRowName(int rowIndex);
}