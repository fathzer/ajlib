package com.fathzer.soft.ajlib.swing.table;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

import javax.swing.table.TableModel;

import com.fathzer.soft.ajlib.utilities.CSVWriter;


/** A specialized CSV Exporter that exports TableModels
 */
public class CSVExporter {
	
	public CSVExporter() {
		super();
	}
	
	public void export(Writer writer, TableModel model, boolean withTitles) throws IOException {
		CSVWriter csv = buildCSVWriter(writer);
		TitledRowsTableModel titled = (model instanceof TitledRowsTableModel)?(TitledRowsTableModel)model:null;
		if (withTitles) {
			if (titled!=null) {
				for (int i = 0; i < titled.getTitlesColumnCount(); i++) {
					csv.writeCell("");
				}
			}
			for (int i = 0; i < model.getColumnCount(); i++) {
				csv.writeCell(model.getColumnName(i));
			}
			csv.newLine();
		}
		for (int rowIndex = 0; rowIndex < model.getRowCount(); rowIndex++) {
			if (titled!=null) {
				for (int i = 0; i < titled.getTitlesColumnCount(); i++) {
					csv.writeCell(titled.getRowTitle(rowIndex, i));
				}
			}
			for (int columnIndex = 0; columnIndex < model.getColumnCount(); columnIndex++) {
				Object value = model.getValueAt(rowIndex, columnIndex);
				csv.writeCell(getExported(rowIndex, columnIndex, value));
			}
			csv.newLine();
		}
		csv.newLine();
		csv.flush();
	}

	protected CSVWriter buildCSVWriter(Writer writer) {
		return new CSVWriter(writer);
	}

	/** Gets the representation of a cell content.
	 * <br>The default implementation returns value.toString() or an empty String if value is null. 
	 * @param rowIndex the row index of the cell.
	 * @param columnIndex the column index of the cell.
	 * @param value The value of the cell (returned by TableModel.getValueAt(rowIndex, columnIndex).
	 * @return The string representation of the value.
	 * @see CSVWriter#getDecimalFormater(Locale)
	 */
	protected String getExported(int rowIndex, int columnIndex, Object value) {
		if (value==null) {
			return ""; //$NON-NLS-1$
		}
		return value.toString();
	}
}
