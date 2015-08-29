package com.fathzer.soft.ajlib.swing.table;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.fathzer.soft.ajlib.utilities.ListUtils;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

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
	 * @param table The table that contains the moveable rows.
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
	 * @see ListUtils#move(java.util.List, int[], int)
	 */
	protected abstract void moveModelRows(int[] selectedRows, boolean up);
}
