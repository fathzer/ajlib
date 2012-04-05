package net.astesana.ajlib.swing;

import java.text.Normalizer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

import net.astesana.ajlib.swing.widget.HTMLPane;

/**
 * A class that searches for a text in a JTextComponent and highlights all
 * occurrences of that text. <br>
 * It could be used with an HTMLPane. <br>
 * It supports case and diacritical sensitive/insensitive search.
 * 
 * @author Jean-Marc Astesana <BR>
 *         License : GPL v3 <br>
 *         This code is inspired by an example from Kim Topley (ISBN: 0 13
 *         083292 8 - Publisher: Prentice Hall)
 * @see HTMLPane
 */
public class TextSearcher {
	protected JTextComponent comp;
	private boolean caseSensitive;
	private boolean diacriticalSensitive;
	private String text;
	private int[] offsets;
	private int searchedNormalizedLength;

	/**
	 * Constructor.
	 * 
	 * @param comp
	 *          The text component in which to search
	 */
	public TextSearcher(JTextComponent comp, String searchedText) {
		this.comp = comp;
		this.caseSensitive = false;
		this.diacriticalSensitive = false;
		this.text = searchedText;
		search();
	}

	/**
	 * Searches for a text and returns the offset of all the occurrences.
	 */
	private void search() {
		if (text == null || text.equals("")) {
			this.offsets = new int[0];
			return;
		}

		// Look for the text we are given
		// 1°) Extract a normalized form of the text in which to search
		String content = null;
		try {
			Document d = comp.getDocument();
			content = normalize(d.getText(0, d.getLength()));
		} catch (BadLocationException e) {
			// Cannot happen
			throw new RuntimeException(e);
		}

		// 2°) Get the normalized form of the searched text
		text = normalize(text);

		// 3°) Search for the searched text
		List<Integer> indexes = new LinkedList<Integer>();
		int lastIndex = 0;
		int size = 0;
		searchedNormalizedLength = text.length();
		while ((lastIndex = content.indexOf(text, lastIndex)) != -1) {
			indexes.add(lastIndex);
			size++;
			lastIndex = lastIndex + searchedNormalizedLength;
		}

		offsets = new int[size];
		Iterator<Integer> iter = indexes.iterator();
		for (int i = 0; i < offsets.length; i++) {
			offsets[i] = iter.next();
		}
	}

	/**
	 * Gets the normalized version of a text. <br>
	 * The normalized version is lowercased if the TextSearcher is not case
	 * sensitive and has no diacritical marks if it is diacritical insensitive.
	 * 
	 * @param content
	 * @return a String
	 */
	private String normalize(String content) {
		if (!isCaseSensitive()) content = content.toLowerCase();
		if (!isDiacriticalSensitive())
			content = Normalizer.normalize(content, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
		return content;
	}

	/**
	 * Tests whether the search is case sensitive or not.
	 * 
	 * @return true if the search is case sensitive.
	 */
	public boolean isCaseSensitive() {
		return this.caseSensitive;
	}

	/**
	 * Sets if the search is case sensitive or not. <br>
	 * By default, the search is not case sensitive. <br>
	 * This method updates the offsets attribute.
	 * @param caseSensitive true to have a case sensitive search
	 * @see #getOffsets()
	 */
	public void setCaseSentitive(boolean caseSensitive) {
		if (caseSensitive != this.caseSensitive) {
			this.caseSensitive = caseSensitive;
			search();
		}
	}

	/**
	 * Tests whether the search is diacritical sensitive or not.
	 * @return true if the search is diacritical sensitive.
	 */
	public boolean isDiacriticalSensitive() {
		return diacriticalSensitive;
	}

	/**
	 * Sets if the search is diacritical sensitive or not. <br>
	 * By default, the search is not diacritical sensitive. <br>
	 * This method updates the offsets attribute.
	 * @param diacriticalSensitive true to have a diacritical sensitive search
	 * @see #getOffsets()
	 */
	public void setDiacriticalSensitive(boolean diacriticalSensitive) {
		if (diacriticalSensitive != this.diacriticalSensitive) {
			this.diacriticalSensitive = diacriticalSensitive;
			search();
		}
	}

	/** Gets the offsets of the searched text.
	 * <br>These offsets can be passed to the highLight methods
	 * @return a int array (its dimension is 0 if the text was not found)
	 * @see #highLight(int, javax.swing.text.Highlighter.HighlightPainter)
	 * @see #highLight(int[], javax.swing.text.Highlighter.HighlightPainter)
	 */
	public int[] getOffsets() {
		return offsets;
	}

	/** highLights some portions of the text.
	 * @param offsets the start of the portions to highlight (these offsets are returned by getOffsets method).
	 * null to remove all highlights.
	 * @param painter The painter to be used (or null to use the default one).
	 * @throws BadLocationException if offsets are out of text bounds
	 * @see #getOffsets()
	 */
	public void highLight(int[] offsets, Highlighter.HighlightPainter painter) throws BadLocationException {
  	if (painter==null) painter = DefaultHighlighter.DefaultPainter;
		// Remove any existing highlights for last word
		Highlighter highlighter = comp.getHighlighter();
		highlighter.removeAllHighlights();
		if (offsets!=null) {
			for (int i = 0; i < offsets.length; i++) {
				highlighter.addHighlight(offsets[i], offsets[i] + this.searchedNormalizedLength, painter);
			}
			// Scroll the text pane in order to view the first occurence.
			if (offsets.length>0) comp.scrollRectToVisible(comp.modelToView(offsets[0]));
		}
	}

	/** highLights a portion of the text. 
	 * @param offset the start of the portion to highlight (one of the offsets returned by getOffsets method).
	 * @param painter The painter to be used (or null to use the default one).
	 * @throws BadLocationException if offset is out of text bounds
	 * @see #getOffsets()
	 */
	public void highLight(int offset, Highlighter.HighlightPainter painter) throws BadLocationException {
  	if (painter==null) painter = DefaultHighlighter.DefaultPainter;
		// Remove any existing highlights for last word
		Highlighter highlighter = comp.getHighlighter();
		highlighter.removeAllHighlights();
		highlighter.addHighlight(offset, offset + this.searchedNormalizedLength, painter);
		// Scroll the text pane in order to view the first occurence.
		comp.scrollRectToVisible(comp.modelToView(offset));
	}
}