package com.fathzer.soft.ajlib.swing.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.SortOrder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/** A subclass of javax.swing.TableRowSorter that allows the user to deselect sort keys.
 * <br>The TableRowSorter receives user's clicks on the table header and manage the sort keys and order.
 * Unfortunately, when the user click a column, the row sorter toggle the sort order (ascending/descending),
 * but provides no way to deselect the sort key.
 * <br>This class makes a cycle between ASCENDING, DESCENDING and UNSORTED when the user clicks a column header.
 * <br>Using this class is simple as:
 * <br><code>
 * &nbsp;&nbsp;JTable table;<br>
 * &nbsp;&nbsp;...<br>
 * &nbsp;&nbsp;table.setRowSorter&lt;TableModel&gt;(new RowSorter(table.getModel()))<br>
 * </code>
 * @param <M> The type of the table model
 */
public class RowSorter<M extends TableModel> extends TableRowSorter<M> {
	private List<SortOrder> toggleSequence;
	
	/** Constructor.
	 */
	public RowSorter() {
		super();
		this.toggleSequence = Arrays.asList(SortOrder.values());
	}

	/** Constructor.
	 * @param model The table's model.
	 */
	public RowSorter(M model) {
		super(model);
		this.toggleSequence = Arrays.asList(SortOrder.values());
	}

	/* (non-Javadoc)
	 * @see javax.swing.DefaultRowSorter#toggleSortOrder(int)
	 */
	@Override
	public void toggleSortOrder(int column) {
		if (!isSortable(column)) {
			return;
		}
		List<? extends SortKey> sortKeys = getSortKeys();
		ArrayList<SortKey> futureKeys = new ArrayList<SortKey>();
		SortKey theKey = null;
		for (SortKey sortKey : sortKeys) {
			if (sortKey.getColumn()==column) {
				int index = toggleSequence.indexOf(sortKey.getSortOrder());
				if ((index<0) || (index==toggleSequence.size()-1)) {
					index = 0;
				} else {
					index++;
				}
				theKey = new SortKey(column, toggleSequence.get(index));
			} else {
				futureKeys.add(sortKey);
			}
		}
		if (theKey == null) {
			theKey = new SortKey(column, toggleSequence.get(0));
		}
		if (!theKey.getSortOrder().equals(SortOrder.UNSORTED)) {
			futureKeys.add(0, theKey);
		} else {
			// One might be tempted to remove the key from the sort key list, it
			// would not be a good idea
			// If the UNSORTED is not at the end of the toggleSequence, it would
			// broke the sequence
			// Example: ASCENDING, UNSORTED, DESCENDING would leave DESCENDING
			// unreachable
			// Instead of removing the key, we will put it to the lowest
			// priority.
			futureKeys.add(theKey);
		}
		super.setSortKeys(futureKeys);
	}
	
	/** Sets the toggle sequence.
	 * <br>By default, the sequence is ASCENDING, DESCENDING and UNSORTED
	 * @param sequence the toggle sequence, a non empty list of SortOrder. Null to restore the default order.
	 * Please note that if a SorterOrder occurs twice in the list, the behavior of this class is unpredictable.
	 * @throws IllegalArgumentException if the sequence is empty
	 */
	public void setToggleSequence(List<SortOrder> sequence) {
		if (sequence==null) {
			this.toggleSequence = Arrays.asList(SortOrder.values());
		} else if (sequence.isEmpty()) {
			throw new IllegalArgumentException("toggle sequence can't be empty");
		} else {
			this.toggleSequence = sequence;
		}
	}
}
