package net.astesana.ajlib.swing.table;

import javax.swing.JPanel;

import java.awt.Dimension;
import javax.swing.JScrollPane;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import net.astesana.ajlib.swing.Utils;
import java.awt.BorderLayout;

/** JTable has a lot of lacks, this class adds the ability for a table to have row titles.
 * @author Jean-Marc Astesana
 * <BR>License : GPL v3
 */ 
public class Table extends JPanel {
	private static final long serialVersionUID = 1L;

	private JScrollPane scrollPane;
	private JTable table;
	private JTable rowView;

	/**
	 * Constructor.
	 */
	public Table() {
		initialize();
	}
	
	protected void initialize() {
		setLayout(new BorderLayout(0, 0));
		add(getScrollPane());
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
	    scrollPane.setRowHeaderView(getRowJTable());
			scrollPane.setViewportView(getJTable());
		}
		return scrollPane;
	}
	
	/** Gets the internal table that is used to display table rows.
	 * <br>You can override this method in order to create a customized row table.
	 * @return a JTable
	 */
	public final JTable getRowJTable() {
		if (rowView==null) {
			rowView = new JTable();
			rowView.setDefaultRenderer(Object.class, new RowHeaderRenderer());
			rowView.setFocusable(false);
			rowView.setCellSelectionEnabled(false);
			setRowViewSize(rowView);
		}
		return rowView;
	}
	
	private void setRowViewSize(final JTable rowView) {
		int width = (rowView.getColumnCount()>0)?Utils.packColumn(rowView, 0, 2):0;
		Dimension d = rowView.getPreferredScrollableViewportSize();
		d.width = width;
		rowView.setPreferredScrollableViewportSize(d);
	}

	/** Gets the internal JTable.
	 * @return a JTable
	 * @see #buildJTable()
	 */
	public final JTable getJTable() {
		if (table == null) {
			table = buildJTable();
			if (table.getModel() instanceof TitledRowsTableModel) {
				installModelInRowJTable((TitledRowsTableModel) table.getModel());
			}
		}
		return table;
	}
	
	/** Builds the internal JTable.
	 * <br>This table doesn't not contains the row titles.
	 * <br>This method is called once and creates the internal JTable.
	 * <br>It is useful to customize the table (for example to change its CellRenderer).
	 * <br>So, you can override this method in order to create a customized table.
	 * @return a JTable
	 */
	protected JTable buildJTable() {
		return new JTable();
	}
	
	/** Sets the table model.
	 * <br>Please note that you should not modify directly the model of the internal JTable.
	 * It would results in having the row titles not updated.
	 * @param model The model. If this model implements TitledRowsTableModel, the table will have row titles.
	 * @see TitledRowsTableModel
	 */
	public void setModel(TableModel model) {
		table.setModel(model);
		if (model instanceof TitledRowsTableModel) {
			installModelInRowJTable((TitledRowsTableModel) model);
		}
	}

	private void installModelInRowJTable(TitledRowsTableModel model) {
		final TableModel rowHeaderModel = new RowModel(model);
		getRowJTable().setModel(rowHeaderModel);
		rowHeaderModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				setRowViewSize(rowView);
			}
		});
		setRowViewSize(getRowJTable());
	}
	
	/** Gets the table model.
	 * @return a TableModel
	 */
	public TableModel getModel() {
		return this.getJTable().getModel();
	}

	/** Gets the default row height of this table.
	 * @return an integer.
	 */
	public int getRowHeight() {
		return this.getJTable().getRowHeight();
	}

	/** Sets the default row height of this table.
	 * <br>This method sets the row height of the main table and its title table. 
	 * @param rowHeight The new row height
	 */
	public void setRowHeight(int rowHeight) {
		this.getJTable().setRowHeight(rowHeight);
		this.getRowJTable().setRowHeight(rowHeight);
	}
}