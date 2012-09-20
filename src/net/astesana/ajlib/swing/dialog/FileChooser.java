package net.astesana.ajlib.swing.dialog;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.reflect.Field;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import net.astesana.ajlib.utilities.LocalizationData;

/** A better file chooser.
 * <br>Here are the improvements:<ul>
 * <li>In save mode, a confirm dialog is displayed when the selected file already exists.</li>
 * </ul>
 * @author Jean-Marc Astesana
 * <BR>License : GPL v3
 */
public class FileChooser extends JFileChooser {
//TODO fix JFileChooser getSelectedFile bug.
	private static final long serialVersionUID = 1L;
	private JTextField fileNameField;

	public FileChooser() {
		super();
		
	  fileNameField = null;
		try {
			Field field = getUI().getClass().getDeclaredField("fileNameTextField");
			try {
				field.setAccessible(true);
				fileNameField = (JTextField) field.get(getUI());
				new MyDocument(fileNameField);
			} catch (Throwable e) {
				System.err.println ("Error accessing the field "+e);
			}
		} catch (NoSuchFieldException e) {
			System.out.println ("no such field");
		}
		System.out.println ("With field "+fileNameField);

		addPropertyChangeListener(MyDocument.TEXT_PROPERTY, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
			//TODO Fire SELECTED_FILE_CHANGED_PROPERTY when filename field changes
				System.out.println (fileNameField.getText());
			}
		});
	}

	public FileChooser(String path) {
		super(path);
	}

	@Override
	public void approveSelection() {
		File file = getSelectedFile();
		if ((getDialogType() == SAVE_DIALOG) && (file != null) && file.exists()) {
			boolean cancel = showSaveDisplayQuestion(file);
			if (cancel) {
				// User doesn't want to overwrite the file
				return;
			}
		}
		super.approveSelection();
	}

	/** Displays a dialog to inform the user that a file is already existing and it will be overwritten if the user continue. 
	 * @param file The selected file
	 * @return true if the user cancels
	 */
	private boolean showSaveDisplayQuestion(File file) {
		String message = LocalizationData.DEFAULT.getString("saveDialog.FileExist.message"); //$NON-NLS-1$
		return JOptionPane.showOptionDialog(this, message, LocalizationData.DEFAULT.getString("saveDialog.FileExist.title"), //$NON-NLS-1$
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null) == JOptionPane.NO_OPTION;
	}
	
	private class MyDocument extends PlainDocument {
		private static final long serialVersionUID = 1L;
		private static final String TEXT_PROPERTY = "text";
		private boolean ignoreEvents = false;
		private JTextField field;
		
		private MyDocument(JTextField field) {
			this.field = field;
			field.setDocument(this);
		}
		
		@Override
		public void replace(int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
			String oldValue = field.getText();
			this.ignoreEvents = true;
			super.replace(offset, length, text, attrs);
			this.ignoreEvents = false;
			String newValue = field.getText();
			if (!oldValue.equals(newValue)) firePropertyChange(TEXT_PROPERTY, oldValue, newValue);
		}
		
		@Override
		public void remove(int offs, int len) throws BadLocationException {
			String oldValue = field.getText();
			super.remove(offs, len);
			String newValue = field.getText();
			if (!ignoreEvents && !oldValue.equals(newValue)) firePropertyChange(TEXT_PROPERTY, oldValue, newValue);
		}
	}

}
