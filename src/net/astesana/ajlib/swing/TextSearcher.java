package net.astesana.ajlib.swing;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

/** A class that searches for a word in a document and highlights all occurrences of that word.
 *  <br>It could be used with an HTMLPane.
 *  This code is based on an example by By Kim Topley (ISBN: 0 13 083292 8 - Publisher: Prentice Hall)  
 * @author Jean-Marc Astesana
 * <BR>License : GPL v3
 */
public class TextSearcher {
  protected JTextComponent comp;
  protected Highlighter.HighlightPainter painter;

  /** Constructor.
   * @param comp The text component in which to search
   */
  public TextSearcher(JTextComponent comp) {
    this.comp = comp;
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
    int firstOffset = -1;
    Highlighter highlighter = comp.getHighlighter();
    
    // Remove any existing highlights for last word
    highlighter.removeAllHighlights();

    if (text == null || text.equals("")) {
      return -1;
    }

    // Look for the text we are given - insensitive search
    String content = null;
    try {
      Document d = comp.getDocument();
      content = d.getText(0, d.getLength()).toLowerCase();
    } catch (BadLocationException e) {
      // Cannot happen
      return -1;
    }

    text = text.toLowerCase();
    int lastIndex = 0;
    int wordSize = text.length();

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

    return firstOffset;
  }
}