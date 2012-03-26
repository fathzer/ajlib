package net.astesana.ajlib.swing;

import java.text.Normalizer;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

import net.astesana.ajlib.swing.widget.HTMLPane;

/** A class that searches for a text in a JTextComponent and highlights all occurrences of that text.
 *  <br>It could be used with an HTMLPane.
 *  <br>It supports case and diacritical sensitive/insensitive search.
 * @author Jean-Marc Astesana
 * <BR>License : GPL v3
 * <br>This code is inspired by an example from Kim Topley (ISBN: 0 13 083292 8 - Publisher: Prentice Hall)
 * @see HTMLPane
 */
public class TextSearcher {
  protected JTextComponent comp;
  protected Highlighter.HighlightPainter painter;
	private boolean caseSensitive;
	private boolean diacriticalSensitive;

  /** Constructor.
   * @param comp The text component in which to search
   */
  public TextSearcher(JTextComponent comp) {
    this.comp = comp;
    this.caseSensitive = false;
    this.diacriticalSensitive = false;
    setPainter(null);
  }
  
  /** Sets the highlighter's painter.
   * <br>By default, the DefaultHighlighter.DefaultPainter is used.
   * @param painter The painter, null to reset to default.
   */
  public void setPainter(Highlighter.HighlightPainter painter) {
  	this.painter = painter==null?DefaultHighlighter.DefaultPainter:painter;
  }

  /** Search for a word and return the offset of the first occurrence.
   * <br>Highlights are added for all occurrences found.
   * @param text The searched text
   * @return The index of the first
   */
  public int search(String text) {
    Highlighter highlighter = comp.getHighlighter();
    
    // Remove any existing highlights for last word
    highlighter.removeAllHighlights();

    if (text == null || text.equals("")) {
      return -1;
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

    // 3°) Search and highlight the searched text
    int lastIndex = 0;
    int wordSize = text.length();
    int firstOffset = -1;
    while ((lastIndex = content.indexOf(text, lastIndex)) != -1) {
      int endIndex = lastIndex + wordSize;
      try {
        highlighter.addHighlight(lastIndex, endIndex, painter);
      } catch (BadLocationException e) {
        // Nothing to do
      }
      if (firstOffset == -1) {
        firstOffset = lastIndex;
      }
      lastIndex = endIndex;
    }
    
    // 4°) Scroll the text pane in order to view the first occurence.
    if (firstOffset != -1) {
      try {
        comp.scrollRectToVisible(comp.modelToView(firstOffset));
      } catch (BadLocationException e) {
      }
    }

    return firstOffset;
  }

	/** Gets the normalized version of a text.
	 * <br>The normalized version is lowercased if the TextSearcher is not case sensitive and has no diacritical marks if it is diacritical insensitive.
	 * @param content
	 * @return a String
	 */
  private String normalize(String content) {
		if (!isCaseSensitive()) content = content.toLowerCase();
		if (!isDiacriticalSensitive()) content = Normalizer.normalize(content, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
		return content;
	}
  
  /** Tests whether the search is case sensitive or not.
   * @return true if the search is case sensitive.
   */
  public boolean isCaseSensitive() {
  	return this.caseSensitive;
  }
  
  /** Sets if the search is case sensitive or not.
   * <br>By default, the search is not case sensitive. 
   * @param caseSensitive true to have a case sensitive search
   */
  public void setCaseSentitive(boolean caseSensitive) {
  	this.caseSensitive = caseSensitive;
  }

  /** Tests whether the search is diacritical sensitive or not.
   * <br>Note that this method doesn't perform the search. In order to update the highlights, you may have to call the search method. 
   * @return true if the search is diacritical sensitive.
   * @see #search(String)
   */
	public boolean isDiacriticalSensitive() {
		return diacriticalSensitive;
	}

  /** Sets if the search is diacritical sensitive or not.
   * <br>By default, the search is not diacritical sensitive.
   * <br>Note that this method doesn't perform the search. In order to update the highlights, you may have to call the search method. 
   * @param diacriticalSensitive true to have a diacritical sensitive search
   * @see #search(String)
   */
	public void setDiacriticalSensitive(boolean diacriticalSensitive) {
		this.diacriticalSensitive = diacriticalSensitive;
	}
}