package com.fathzer.soft.ajlib.swing.table;

import javax.swing.JTable;

/** A class that is able to set the selection of elements in a JTable.
 * <br>This could be seen very easy, but, unfortunately, there's some pitfalls:
 * You have to set "value is adjusting" to false before start selecting elements,
 * be aware of view/model index and don't forget to scroll the table to make the selection visible.
 * <br>This class takes care of this for you.
 * @param <T> The class of the elements in the table.
 * @author Jean-Marc Astesana
 * <BR>License: LGPL v3
 */
public abstract class JTableSelector<T> {
	/** The JTable that contains the selected elements.*/
	protected JTable table;
	
	/** Constructor
	 * @param table The JTable that contains the selected transactions.
	 */
	protected JTableSelector(JTable table) {
		this.table = table;
	}
	
	/** Selects some elements.
	 * @param elements The elements to select. If some elements do not exist in the table, they are ignored. 
	 */
	public void setSelectedTransactions(T[] elements) {
		table.getSelectionModel().setValueIsAdjusting(true);
		table.getSelectionModel().clearSelection();
		int firstViewRow = -1;
		for (int i = 0; i < elements.length; i++) {
			int row = getModelIndex(elements[i]);
			if (row>=0) {
				row = table.convertRowIndexToView(row);
				if ((firstViewRow<0) || (firstViewRow>row)) {
					firstViewRow = row;
				}
				table.getSelectionModel().addSelectionInterval(row, row);
			}
		}
		table.getSelectionModel().setValueIsAdjusting(false);
		table.scrollRectToVisible(table.getCellRect(firstViewRow, 0, true));
	}
	
	/** Gets the model index of an element.
	 * @param element an element
	 * @return the index of the element in the table model (a negative number is the element is not in the table).
	 */
	protected abstract int getModelIndex(T element);
}