package net.astesana.ajlib.swing.table;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

class RowModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	private TitledRowsTableModel model;
	
	RowModel(TitledRowsTableModel model) {
		this.model = model;
		model.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				//FIXME Be more precise ?
				fireTableDataChanged();
			}
		});
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return model.getRowTitle(rowIndex, columnIndex);
	}

	@Override
	public int getRowCount() {
		return model.getRowCount();
	}

	@Override
	public int getColumnCount() {
		return model.getTitlesColumnCount();
	}
}