package com.fathzer.soft.ajlib.utilities;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/** CSV Writer.
 * <br>This class is able to write data in the CSV format.
 * <br>If you're looking for a CSV parser, I recommend using OpenCSV.
 * @author Jean-Marc Astesana
 * <BR>License: LGPL v3
 */
public class CSVWriter {
	private static final String BLANK = " ";
	
	private boolean lineIsEmpty;
	private BufferedWriter writer;
	private char separator;
	private char quote;
	private boolean alwaysQuoteCells;
	
	private String quoteSeq;
	private String doubleQuoteSeq;
	
	/** Constructor.
	 * <br>Builds a new instance with the following default attributes:<ul>
	 * <li>separator: ;</li>
	 * <li>quoting character: "</li>
	 * <li>alwaysQuote: false</li>
	 * </ul>
	 * @param writer The writer where to write the data
	 */
	public CSVWriter(Writer writer) {
		this.writer = writer instanceof BufferedWriter?(BufferedWriter)writer:new BufferedWriter(writer);
		this.lineIsEmpty = true;
		this.separator = ';';
		this.alwaysQuoteCells = false;
		this.setQuote('"');
	}
	
	public void writeCell(String cell) throws IOException {
		if (!lineIsEmpty) writer.append(separator);
		lineIsEmpty = false;
		if (cell==null) cell = new String();
		boolean quoteCells = alwaysQuoteCells || (cell.indexOf(separator)>=0) || (cell.indexOf(quote)>=0) || cell.startsWith(BLANK) || cell.endsWith(BLANK);
		cell = cell.replace(quoteSeq, doubleQuoteSeq);
		if (quoteCells) writer.append(quote);
		writer.append(cell);
		if (quoteCells) writer.append(quote);
	}
	
	public void newLine() throws IOException {
		writer.newLine();
		lineIsEmpty = true;
	}
	
	/** Ensures all the data is written to the underlying writer.
	 * @throws IOException
	 */
	public void flush() throws IOException {
		writer.flush();
	}

	/**
	 * @return the separator
	 */
	public char getSeparator() {
		return separator;
	}

	/**
	 * @param separator the separator to set
	 */
	public void setSeparator(char separator) {
		this.separator = separator;
	}

	/**
	 * @return the quote
	 */
	public char getQuote() {
		return quote;
	}

	/**
	 * @param quote the quote to set
	 */
	public void setQuote(char quote) {
		this.quote = quote;
		this.quoteSeq = new String(new char[]{quote});
		this.doubleQuoteSeq = this.quoteSeq + this.quoteSeq;
	}

	/**
	 * @return the alwaysQuoteCells
	 */
	public boolean isAlwaysQuoteCells() {
		return alwaysQuoteCells;
	}

	/**
	 * @param alwaysQuoteCells the alwaysQuoteCells to set
	 */
	public void setAlwaysQuoteCells(boolean alwaysQuoteCells) {
		this.alwaysQuoteCells = alwaysQuoteCells;
	}
	
	/** Returns a formatter to use to output decimal number.
	 * <br>Excel is very demanding on the number formats. It doesn't tolerate currency suffix or prefix, nor grouping separators.
	 * <br>This method returns a formatter that outputs strings that will be recognized as numbers by excel. 
	 * @param locale The locale.
	 * @return a NumberFormat instance
	 */
	public static NumberFormat getDecimalFormater(Locale locale) {
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