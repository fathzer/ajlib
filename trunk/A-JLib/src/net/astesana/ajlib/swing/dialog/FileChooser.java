package net.astesana.ajlib.swing.dialog;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import net.astesana.ajlib.swing.framework.Application;
import net.astesana.ajlib.utilities.FileUtils;
import net.astesana.ajlib.utilities.NullUtils;

/** A better file chooser.
 * <br>Here are the improvements:<ul>
 * <li>In save mode, a confirm dialog is displayed when the selected file already exists.</li>
 * <li>The getSelectedFile methods returns the selected file, even if the file name field has been modified
 * (It's strange but that method in JFileChooser returns the last file selected in the file list until the "ok"
 * button is pressed. It sounds like a bug of JFileChooser)</li>
 * <li>The SELECTED_FILE_CHANGED_PROPERTY event is thrown accordingly to the previous point.</li>
 * </ul>
 * @author Jean-Marc Astesana
 * <BR>License : GPL v3
 */
public class FileChooser extends JFileChooser {
	private static final long serialVersionUID = 1L;
	private JTextField fileNameField;
	private File selectedFile;

	public FileChooser() {
		this(null);
	}

	public FileChooser(String path) {
		super(path);
		this.selectedFile = null;
	  fileNameField = null;
		try {
			Field field = getUI().getClass().getDeclaredField("fileNameTextField"); //$NON-NLS-1$
			try {
				field.setAccessible(true);
				fileNameField = (JTextField) field.get(getUI());
			} catch (Throwable e) {
				throw new RuntimeException (e);
			}
		} catch (NoSuchFieldException e) {
			// If there's no fileNameField ... we will suppose there's no problem with this field updates !
		}

		if (fileNameField != null) {
			new MyDocument(fileNameField);
			addPropertyChangeListener(MyDocument.TEXT_PROPERTY, new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					File old = selectedFile;
					String name = fileNameField.getText();
					File selectedFile = name.length()==0 ? null : new File(FileChooser.super.getCurrentDirectory(), name);
					if (!NullUtils.areEquals(old, selectedFile)) {
						int pos = fileNameField.getCaretPosition();
						firePropertyChange(SELECTED_FILE_CHANGED_PROPERTY, old, selectedFile);
						if (pos>fileNameField.getText().length()) pos = fileNameField.getText().length();
						fileNameField.setCaretPosition(pos);
					}
				}
			});
		}
		
		addPropertyChangeListener(DIRECTORY_CHANGED_PROPERTY, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (selectedFile!=null) {
					File old = selectedFile;
					firePropertyChange(SELECTED_FILE_CHANGED_PROPERTY, old, new File(getCurrentDirectory(), selectedFile.getName()));
				}
			}
		});
	}
	
	@Override
	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		// Update the selectedFile attribute
		if (SELECTED_FILE_CHANGED_PROPERTY.equals(propertyName)) selectedFile = (File) newValue;
		super.firePropertyChange(propertyName, oldValue, newValue);
	}
	
	@Override
	public File getSelectedFile() {
		// The JFileChooser getSelectedFile returns a wrong value when the file name field is changed
		// So, we use an attribute updated when events that modify the selected file occurs.
		return selectedFile;
	}

	@Override
	public void approveSelection() {
		// Refuse:
		// The broken links
		// The non existing files in OPEN_DIALOG mode
		// Ask what to do if the file exists and we are in SAVE_DIALOG mode
		System.out.println ("approveSelection is called for "+super.getSelectedFile());
		File file = super.getSelectedFile();
		if (file!=null) {
			try {
				if (getDialogType() == OPEN_DIALOG) {
					if (!file.exists()) {
						JOptionPane.showMessageDialog(this, get("openDialog.fileDoesntExist"), get("Generic.error"), JOptionPane.ERROR_MESSAGE);  //$NON-NLS-1$//$NON-NLS-2$
						return;
					} else {
						File canonical = FileUtils.getCanonical(file);
						if (!canonical.exists()) {
							JOptionPane.showMessageDialog(this, get("openDialog.targetDoesntExist"), get("Generic.error"), JOptionPane.ERROR_MESSAGE);  //$NON-NLS-1$//$NON-NLS-2$
							return;
						}
					}
				} else {
					File canonical = FileUtils.getCanonical(file);
					if (canonical.exists()) {
						boolean cancel = showSaveDisplayQuestion(this);
						if (cancel) {
							// User doesn't want to overwrite the file
							return;
						}
					}
					if (!FileUtils.isWritable(canonical)) {
						JOptionPane.showMessageDialog(this, get("saveDialog.fileNotWritable"), get("Generic.error"), JOptionPane.ERROR_MESSAGE);  //$NON-NLS-1$//$NON-NLS-2$
						return;
					}
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		super.approveSelection();
	}
	
	private String get(String key) {
		return Application.LOCALIZATION.getString(key, getLocale());
	}

	/** Displays a dialog to inform the user that a file is already existing and it will be overwritten if the user continue. 
	 * @param parent determines the Frame in which the dialog is displayed; if null, or if the parentComponent has no Frame, a default Frame is used
	 * @return true if the user cancels
	 */
	public static boolean showSaveDisplayQuestion(Component parent) {
		String message = Application.LOCALIZATION.getString("saveDialog.FileExist.message", parent.getLocale()); //$NON-NLS-1$
		return JOptionPane.showOptionDialog(parent, message, Application.LOCALIZATION.getString("saveDialog.FileExist.title", parent.getLocale()), //$NON-NLS-1$
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null) == JOptionPane.NO_OPTION;
	}
	
	private class MyDocument extends PlainDocument {
		private static final long serialVersionUID = 1L;
		private static final String TEXT_PROPERTY = "text"; //$NON-NLS-1$
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
