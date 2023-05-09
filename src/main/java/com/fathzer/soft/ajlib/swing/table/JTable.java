package com.fathzer.soft.ajlib.swing.table;

import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;

/** A JTable that fixes the following bugs in the JTable and adds some functionalities.
 * <br>Bugs fixed:<ul>
 * <li>Unlike the original swing JTable class, the row height is set accordingly to the font (see setRowHeigth() method).
 * <br>The method setRowHeight is called in the constructor. You should call it again when changing the font.</li>
 * </ul>
 */
@SuppressWarnings("serial")
public class JTable extends javax.swing.JTable {
	public JTable() {
		super();
		setRowHeight();
	}
	
	public JTable(TableModel tableModel) {
		super(tableModel);
		setRowHeight();
	}
	
	/** Sets the row height according to the font size.
	 * <br>The row height is set to getFont().getSize()*4/3.
	 */
	public void setRowHeight() {
		setRowHeight(getFont().getSize()*4/3);
	}
	
	/** Sets the selection.
	 * @param selectedIndexes The indexes to select.<br>Please note that specifying non contiguous selection on a table that
	 * uses SINGLE_SELECTION mode may have unpredictable result.
	 * @throws IllegalArgumentException if an index is out of bounds.
	 */
	public void setSelectedIndexes(int[] selectedIndexes) {
		for (int i = 0; i < selectedIndexes.length; i++) {
			if ((selectedIndexes[i]<0) || (selectedIndexes[i]>=getRowCount())) {
				throw new IllegalArgumentException();
			}
		}
		ListSelectionModel selectionModel = getSelectionModel();
		selectionModel.setValueIsAdjusting(true);
		selectionModel.clearSelection();
		for (int i = 0; i < selectedIndexes.length; i++) {
			selectionModel.addSelectionInterval(selectedIndexes[i], selectedIndexes[i]);
		}
		selectionModel.setValueIsAdjusting(false);
	}
}
