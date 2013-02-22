package net.astesana.ajlib.utilities;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.table.TableModel;

import net.astesana.ajlib.swing.table.TitledRowsTableModel;

/** A CSV Exporter.
 * @author Jean-Marc Astesana
 * <BR>License : GPL v3
 */
public class CSVExporter {
	private char separator;
	private boolean quoteCells;
	
	public CSVExporter(char separator, boolean quoteCells) {
		this.separator = separator;
		this.quoteCells = quoteCells;
	}
	
	private class CSVWriter {
		private boolean lineIsEmpty;
		private BufferedWriter writer;
		
		public CSVWriter(BufferedWriter writer) {
			this.writer = writer;
			this.lineIsEmpty = true;
		}
		
		public void writeCell(String cell) throws IOException {
			//FIXME Have a look at the cvs spec => quoting pay also be used if a cell contains a separator
			//FIXME If a cell contains a quote, this quote may be replaced by two quotes.
			if (!lineIsEmpty) writer.append(separator);
			lineIsEmpty = false;
			if (quoteCells) writer.append('"');
			writer.append(cell);
			if (quoteCells) writer.append('"');
		}
		
		public void newLine() throws IOException {
			writer.newLine();
			lineIsEmpty = true;
		}
	}

	public void export(BufferedWriter writer, TableModel model, boolean withTitles) throws IOException {
		TitledRowsTableModel titled = (model instanceof TitledRowsTableModel)?(TitledRowsTableModel)model:null;
		CSVWriter csv = new CSVWriter(writer);
		if (withTitles) {
			if (titled!=null) {
				for (int i = 0; i < titled.getTitlesColumnCount(); i++) {
					csv.writeCell(getRowTitleHeaders(i));
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
	}
	
	protected String getRowTitleHeaders(int titleColumnIndex) {
		return "";
	}

	/** Gets the representation of a cell content.
	 * <br>The default implementation returns value.toString() or an empty String if value is null. 
	 * @param rowIndex the row index of the cell.
	 * @param columnIndex the column index of the cell.
	 * @param value The value of the cell (returned by TableModel.getValueAt(rowIndex, columnIndex).
	 * @return The string representation of the value.
	 */
	protected String getExported(int rowIndex, int columnIndex, Object value) {
		if (value==null) return ""; //$NON-NLS-1$
		return value.toString();
	}

	public char getSeparator() {
		return this.separator;
	}
	
	public void setSeparator(char separator) {
		this.separator = separator;
	}

	/** Returns a formatter to use to output currencies.
	 * <br>Excel is very demanding on the number formats. It doesn't tolerate currency suffix or prefix, nor grouping separators.
	 * <br>This method returns a formatter that outputs strings that will be recognized as numbers by excel. 
	 * @param locale The locale.
	 * @return a NumberFormat instance
	 */
	public static NumberFormat getCurrencyFormater(Locale locale) {
		NumberFormat result = NumberFormat.getInstance(locale);
		if (result instanceof DecimalFormat) {
			// We don't use the currency instance, because it would have output some currency prefix or suffix, not very easy
			// to manipulate with an excel like application
			NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);
			result.setMinimumFractionDigits(currencyFormat.getMinimumFractionDigits());
			result.setMaximumFractionDigits(currencyFormat.getMaximumFractionDigits());
		}
		result.setGroupingUsed(false);
		return result;
	}
}
