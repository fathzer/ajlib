package com.fathzer.soft.ajlib.swing.table;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

/** An abstract widget composed of two buttons to move up or down selected rows of a JTable.
 * @see JTable
 * @author Jean-Marc Astesana
 * <BR>License: LGPL v3
 */
public abstract class AbstractTableRowMover extends JPanel {
	private static final long serialVersionUID = 1L;

	private JButton downButton;
	private JButton upButton;

	private JTable table;
	/**
	 * Creates the panel.
	 */
	public AbstractTableRowMover(JTable table) {
		this.table = table;
		initialize();
		
		getJTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}
				updateupDownEnabled();
			}
		});
	}
	private void initialize() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		setLayout(gridBagLayout);
		GridBagConstraints gbcUpButton = new GridBagConstraints();
		gbcUpButton.insets = new Insets(0, 0, 5, 0);
		gbcUpButton.weighty = 1.0;
		gbcUpButton.anchor = GridBagConstraints.SOUTH;
		gbcUpButton.gridx = 0;
		gbcUpButton.gridy = 0;
		add(getUpButton(), gbcUpButton);
		GridBagConstraints gbcDownButton = new GridBagConstraints();
		gbcDownButton.anchor = GridBagConstraints.NORTH;
		gbcDownButton.weighty = 1.0;
		gbcDownButton.gridx = 0;
		gbcDownButton.gridy = 1;
		add(getDownButton(), gbcDownButton);
	}

	private JButton getUpButton() {
		if (upButton == null) {
			upButton = new JButton("");
			upButton.setIcon(new ImageIcon(AbstractTableRowMover.class.getResource("/com/fathzer/soft/ajlib/swing/widget/up.png"))); //$NON-NLS-1$
			upButton.setEnabled(false);
			upButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int[] selectedRows = getJTable().getSelectedRows();
					moveModelRows(selectedRows, true);						
					offsetSelection(selectedRows,-1);
				}
			});
		}
		return upButton;
	}
	private JButton getDownButton() {
		if (downButton == null) {
			downButton = new JButton("");
			downButton.setIcon(new ImageIcon(AbstractTableRowMover.class.getResource("/com/fathzer/soft/ajlib/swing/widget/down.png"))); //$NON-NLS-1$
			downButton.setEnabled(false);
			downButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int[] selectedRows = getJTable().getSelectedRows();
					moveModelRows(selectedRows, false);
					offsetSelection(selectedRows,1);
				}
			});
		}
		return downButton;
	}
	
	private void updateupDownEnabled() {
		getUpButton().setEnabled(getJTable().getSelectedRow()>0);
		int [] rowsSelected=getJTable().getSelectedRows();
		getDownButton().setEnabled((rowsSelected.length!=0) && (rowsSelected[rowsSelected.length-1]!=getJTable().getRowCount()-1));
	}
	private void offsetSelection(int[] selectedRows, int offset) {
		for (int i = 0; i < selectedRows.length; i++) {
			selectedRows[i] = selectedRows[i]+offset;
		}
		getJTable().setSelectedIndexes(selectedRows);
	}
	
	protected JTable getJTable() {
		return this.table;
	}
	
	/** Moves the rows in the table model.
	 * <br>This method is called when the user clicks the up or down buttons.
	 * <br>It is mandatory that this method fire the proper model change event after updating the model.  
	 * @param selectedRows The moved rows
	 * @param up true if the rows are moved up, false if they are moved down.
	 */
	protected abstract void moveModelRows(int[] selectedRows, boolean up);
	
	/** Utility method that moves some indexes of a list one position up or down.
	 * <br>It should be useful to implement moveModelRows.
	 * @param list The list where to move the elements
	 * @param indexes The indexes of the elements to move (should be sorted in ascending order).
	 * @param up true to move up, false to move down.
	 */
	public static <T> void move(List<T> list, int[] indexes, boolean up) {
		if (up) {
			for (int i = 0; i < indexes.length; i++) {
				T elem1 = list.get(indexes[i]);
				T elem2 = list.get(indexes[i]-1);
				list.set(indexes[i]-1, elem1);
				list.set(indexes[i], elem2);
			}
		} else {
			List<T> tmp = new ArrayList<T>();
			for (int i=0;i<indexes.length;i++) {
				tmp.add(list.get(indexes[i]));
			}	
			for (int i=0;i<indexes.length;i++) {
				list.remove(indexes[i]-i);
			}
			for (int i=0;i<tmp.size();i++) {
				list.add(indexes[i]+1, tmp.get(i));
			}
		}
	}
}
