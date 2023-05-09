package com.fathzer.soft.ajlib.swing.table;

import javax.swing.table.TableModel;

/** A TableModel that defines titles for its rows.
 * <br>This kind of model can be used with Table that automatically display titles when its model implements this interface.
 */
public interface TitledRowsTableModel extends TableModel {
	/** Gets the number of columns dedicated to the row titles.
	 * @return an positive integer
	 */
	public int getTitlesColumnCount();
	
	/** Gets a row title.
	 * @param rowIndex The index of the row
	 * @param columnIndex The index of the title column
	 * @return the title of the row
	 */
	public String getRowTitle(int rowIndex, int columnIndex);
}