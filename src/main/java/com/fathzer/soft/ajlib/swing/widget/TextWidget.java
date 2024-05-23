package com.fathzer.soft.ajlib.swing.widget;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.TreeSet;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.fathzer.soft.ajlib.utilities.NullUtils;
import com.fathzer.soft.ajlib.utilities.TextMatcher;

/** A JTextField with a property that maps its text and the ability to define predefined values.
 * <br>I've found no way to track efficiently the modifications of the text of a JTextField ... so I developed this widget.
 * <br>DocumentListeners are intended to do it, unfortunately, when a text is replace in a field, the listener receive two events:<ol>
 * <li>One when the replaced text is removed.</li>
 * <li>One when the replacing text is inserted</li>
 * </ol>
 * The first event is ... simply absolutely misleading, it corresponds to a value that the text never had.
 * <br>Another problem with DocumentListener is that you can't modify the text into it (it throws IllegalStateException).
 * <br>Another way was to use KeyListeners ... but some key events are throw a long time (probably the key auto-repeat interval)
 * after the key was released. And others events (for example a click on an OK button) may occurs before the listener is informed of the change.
 * <br>This widget guarantees that no "ghost" property change is thrown !
 * <br><br>This class implements a popup menu that allows to select the field content into a list of predefined values.
 * <br>The difference with a JComboBox is the popup content and selection is updated when the user changes the text field content.
 * It always contains only predefined text that contains the current widget text.
 */
public class TextWidget extends JTextField {
	private static final long serialVersionUID = 1L;

	public static final String TEXT_PROPERTY = "text"; //$NON-NLS-1$
	/** A PropertyChangeEvent of this name is fired when a predefined value is selected.
	 * The values of the event are the field content.
	 */
	public static final String PREDEFINED_VALUE = "PREDEFINED_VALUE"; //$NON-NLS-1$
	
	private JPopupMenu popup;
	private JList<String> list;
	private String predefined=null;
	private String lastText=""; //$NON-NLS-1$
	private int unsortedMax;
	private String[] proposals;
	
	private static class UpperLineBorder extends AbstractBorder {
		private static final long serialVersionUID = 1L;
		protected Color lineColor;

		public UpperLineBorder(Color color) {
			lineColor = color;
		}

		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			g.setColor(lineColor);
			g.drawLine(x+1, y, x+width-2, y);
		}
	}
	
	private class MyRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			if (((String)value).length()==0) {
				value = " "; //$NON-NLS-1$
			}
			JComponent label = (JComponent) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if ((unsortedMax>0) && (index==unsortedMax)) {
				Border border = new UpperLineBorder(label.getForeground().brighter());
				label.setBorder(new CompoundBorder(border, label.getBorder()));
			}
			return label;
		}
	}

	/** Constructor.
	 */
	public TextWidget () {
		// Revision 244 of this method contains a not efficient try to implement command-V in textfield on MacOS
		this.setDocument(new MyDocument());
	}

	private void installPopup() {
		if (popup==null) {
			unsortedMax = Integer.MAX_VALUE;
			popup = new JPopupMenu();
			list = new JList<>(new PopupListModel());
			list.setAutoscrolls(true);
			list.setCellRenderer(new MyRenderer());
			popup.add(new JScrollPane(list));
			popup.setFocusable(false);
	
			// If the component looses the focus and the popup is shown, we have to hide the popup
			// The following FocusListener will do that
			addFocusListener(new FocusListener() { 
				@Override
				public void focusLost(FocusEvent e) {
					if (popup.isVisible() && !e.isTemporary() && (!list.equals(e.getOppositeComponent()))) {
						popup.setVisible(false);
						if (e.getOppositeComponent()!=null) {
							e.getOppositeComponent().requestFocus();
						}
					}
				}
				
				@Override
				public void focusGained(FocusEvent e) {
					// Nothing to do in this case
				}
			});
			// When the user clicks the list, we have to set the value clicked in the list, hide the popup, then transfer back the focus to the textField
			list.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getButton()==MouseEvent.BUTTON1) {
						setPredefined(list.getSelectedValue());
						popup.setVisible(false);
					}
				}
				@Override
				public void mouseReleased(MouseEvent e) {
					requestFocus();
				}
			});
			// When the user press the down arrow, show the popup, if it's hidden.
			// Up, down and enter key respectivly selects next element, selects previous element, set the textField to the selected element
			// When a key is type, and changed the content of the field, this adapter also refresh the list selected element
			addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_DOWN) { // down arrow key was pressed
						if (!popup.isVisible()) {
							showPopup();
						} else {
							int index = list.getSelectedIndex();
							if (index < list.getModel().getSize()) {
								index++;
								list.setSelectedIndex(index);
							}
						}
					} else if (e.getKeyCode() == KeyEvent.VK_UP) { // Up arrow key was pressed
						if (popup.isVisible()) {
							int index = list.getSelectedIndex();
							if (index > 0) {
								index--;
								list.setSelectedIndex(index);
							}
						}
					} else if (e.getKeyCode() == KeyEvent.VK_ENTER) { // Enter key pressed
						if (popup.isVisible()) {
							if (list.getSelectedIndex()>=0) {
								setPredefined(list.getSelectedValue());
							}
							popup.setVisible(false);
							e.consume();
						}
					}
				}
	
				@Override
				public void keyReleased(KeyEvent e) {
					String text = getText();
					if (!text.equals(lastText)) {
						fillModel(text);
						lastText = text;
						setPredefined((String)null);
						showPopup();
					}
				}
			});
		}
	}
	
	public TextWidget(int columns) {
		this();
		this.setColumns(columns);
	}

	private void setPredefined(String value) {
		if (value!=null) {
			lastText = value;
			setText(value);
		}
		if (!NullUtils.areEquals(predefined, value)) {
			String oldPredefined = predefined;
			predefined = value;
			this.firePropertyChange(PREDEFINED_VALUE, oldPredefined, predefined);
		}
	}

	/** Sets the predefined values allowed by the field.
	 * @param array The predefined values.
	 * @see #setPredefined(String[], int)
	 */
	public void setPredefined(String[] array) {
		setPredefined (array, Integer.MAX_VALUE);
	}

	/** Sets the predefined values allowed by the field.
	 * @param array The predefined values.
	 * @param unsortedMax Maximum number of unsorted values.
	 * <br>The values can be divided in two groups (which will be separated by a thin line).
	 * <ol>
	 * <li>A group of values, that matches the current typed text, sorted in the same order as in the array argument.</li>
	 * <li>A group of values, that matches the current typed text, sorted in alphabetical order.</li>
	 * </ol>
	 * <br>This argument contains the size of the first group. Use Integer.MAX_VALUE to always display values in the same order as in the array. 0 to always sorted values.
	 */
	public void setPredefined(String[] array, int unsortedMax) {
		installPopup();
		this.unsortedMax = unsortedMax;
		this.proposals = array==null ? null : array.clone();
		fillModel(this.getText());
	}

	private void showPopup() {
		if (list.getModel().getSize()!=0) {
			Dimension size = popup.getPreferredSize();
			if (getWidth()>size.width) {
				popup.setPreferredSize(new Dimension(getWidth(), size.height));
			}
			popup.show(TextWidget.this, 0, getHeight());
			requestFocus(false);
		} else {
			popup.setVisible(false);
		}
	}

	private void fillModel(String text) {
		TextMatcher matcher = new TextMatcher(TextMatcher.Kind.CONTAINS, text, false, false); //TODO Must match "starts with" ... to be implemented in TextMatcher
		ArrayList<String> okProbaSort = new ArrayList<>();
		TreeSet<String> okAlphabeticSort = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		for (String value : this.proposals) {
			if (matcher.matches(value)) {
				if (okProbaSort.size()<=unsortedMax) {
					okProbaSort.add(value);
				} else {
					okAlphabeticSort.add(value);
				}
			}
		}
		okProbaSort.addAll(okAlphabeticSort);
		((PopupListModel)list.getModel()).setValues(okProbaSort.toArray(new String[okProbaSort.size()]));
		
		if (0 != list.getSelectedIndex()) {
			list.setSelectedIndex(0);
		}
	}

	private static final class PopupListModel extends AbstractListModel<String> {
		private static final long serialVersionUID = 1L;
		String[] values;
		
		PopupListModel() {
			this.values = new String[0];
		}
		
		@Override
		public int getSize() {
			return values.length;
		}

		@Override
		public String getElementAt(int index) {
			return values[index];
		}

		public void setValues(String[] values) {
			int n = this.values.length;
			if (n>0) {
				this.values = new String[0];
				fireIntervalRemoved(this, 0, n);
			}
			this.values = values.clone();
			fireIntervalAdded(this, 0, this.values.length);
		}
	}
	
	private class MyDocument extends PlainDocument {
		private static final long serialVersionUID = 1L;
		private boolean ignoreEvents = false;
		
		@Override
		public void replace(int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
			String oldValue = TextWidget.this.getText();
			this.ignoreEvents = true;
			super.replace(offset, length, text, attrs);
			this.ignoreEvents = false;
			String newValue = TextWidget.this.getText();
			if (!oldValue.equals(newValue)) {
				TextWidget.this.firePropertyChange(TEXT_PROPERTY, oldValue, newValue);
			}
		}
		
		@Override
		public void remove(int offs, int len) throws BadLocationException {
			String oldValue = TextWidget.this.getText();
			super.remove(offs, len);
			String newValue = TextWidget.this.getText();
			if (!ignoreEvents && !oldValue.equals(newValue)) {
				TextWidget.this.firePropertyChange(TEXT_PROPERTY, oldValue, newValue);
			}
		}
	}

}
