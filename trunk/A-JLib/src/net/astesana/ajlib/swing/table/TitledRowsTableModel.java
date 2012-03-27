package net.astesana.ajlib.swing.table;

import javax.swing.table.TableModel;

/** A TableModel that defines titles for its rows.
 * <br>This kind of model can be used with Table that automatically display titles when its model implements this interface.
 * @author Jean-Marc Astesana
 * <BR>License : GPL v3
 * @see Table
 */
public interface TitledRowsTableModel extends TableModel {
	/** Gets a row title.
	 * @param rowIndex The index of the row
	 * @return the title of the row
	 */
	public String getRowName(int rowIndex);
}