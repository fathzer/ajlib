package net.astesana.ajlib.swing.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.SortOrder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class RowSorter<M extends TableModel> extends TableRowSorter<M> {
	private List<SortOrder> toggleSequence;
	
	public RowSorter() {
		super();
		this.toggleSequence = Arrays.asList(SortOrder.values());
	}

	public RowSorter(M model) {
		super(model);
		this.toggleSequence = Arrays.asList(SortOrder.values());
	}

	/* (non-Javadoc)
	 * @see javax.swing.DefaultRowSorter#toggleSortOrder(int)
	 */
	@Override
	public void toggleSortOrder(int column) {
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
    if (theKey==null) theKey = new SortKey(column, toggleSequence.get(0));
    futureKeys.add(0, theKey);
 try {
    super.setSortKeys(futureKeys);
 } catch (IllegalArgumentException e) {
	 System.out.println ("oups !!!");
 }
	}
	
	public void setToggleSequence(List<SortOrder> sequence) {
		if (sequence==null) {
			this.toggleSequence = Arrays.asList(SortOrder.values());
		} else if (sequence.size()==0) {
			throw new IllegalArgumentException("toggle sequence can't be empty");
		} else {
			this.toggleSequence = sequence;
		}
	}
}
