package net.astesana.ajlib.swing.dialog;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import net.astesana.ajlib.swing.framework.Application;
import net.astesana.ajlib.utilities.FileUtils;
import net.astesana.ajlib.utilities.NullUtils;

/**
 * A better file chooser. <br>
 * Here are the improvements:
 * <ul>
 * <li>In save mode, a confirm dialog is displayed when the selected file
 * already exists.</li>
 * <li>Some classical problem are detected while validating the dialog.
 * <ul>
 * <li>In open mode, the file does not exist.</li>
 * <li>The application have no right to access the file.</li>
 * </ul>
 * </li>
 * <li>The getSelectedFile methods returns the selected file, even if the file
 * name field has been modified (It's strange but that method in JFileChooser
 * returns the last file selected in the file list until the "ok" button is
 * pressed. It sounds like a bug of JFileChooser)</li>
 * <li>setSelectedFile(null) clears the current selection.</li>
 * <li>The SELECTED_FILE_CHANGED_PROPERTY event is thrown accordingly to the
 * previous point.</li>
 * <br>
 * <br>
 * <b>Here is the bad news</b>: This component is design to allow selection of
 * individual files, not folder, not multiple files :-(
 * </ul>
 * 
 * @author Jean-Marc Astesana <BR>
 *         License : GPL v3
 */
public class FileChooser extends JFileChooser {
	private static final long serialVersionUID = 1L;
	private JTextField fileNameField;
	private File selectedFile;
	private Component fileNameLabel;
	private boolean selectionTestEnabled;

	public FileChooser() {
		this(null);
	}

	public FileChooser(String path) {
		super(path);
		this.selectionTestEnabled = true;
		this.selectedFile = null;
		fileNameField = null;
		try {
			Field field = getUI().getClass().getDeclaredField(
					"fileNameTextField"); //$NON-NLS-1$
			try {
				field.setAccessible(true);
				fileNameField = (JTextField) field.get(getUI());
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		} catch (NoSuchFieldException e) {
			// If there's no fileNameField ... we will suppose there's no
			// problem with this field updates !
		}

		try {
			Field field = getUI().getClass().getDeclaredField("fileNameLabel"); //$NON-NLS-1$
			try {
				field.setAccessible(true);
				fileNameLabel = (JLabel) field.get(getUI());
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		} catch (NoSuchFieldException e) {
			// If there's no fileNameLabel ... ok, then we will not do anything
			// with it !
		}

		PropertyChangeListener listener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				File old = selectedFile;
				File superSelected = FileChooser.super.getSelectedFile();
				String name = fileNameField != null ? fileNameField.getText()
						: superSelected.getName();
				File selectedFile = name.length() == 0 ? null : new File(
						FileChooser.super.getCurrentDirectory(), name);
				if (!NullUtils.areEquals(old, selectedFile)) {
					int pos = fileNameField != null ? fileNameField
							.getCaretPosition() : 0;
					firePropertyChange(SELECTED_FILE_CHANGED_PROPERTY, old,
							selectedFile);
					if (fileNameField != null) {
						if (pos > fileNameField.getText().length())
							pos = fileNameField.getText().length();
						fileNameField.setCaretPosition(pos);
					}
				}
			}
		};

		if (fileNameField != null) {
			new MyDocument(fileNameField);
			addPropertyChangeListener(MyDocument.TEXT_PROPERTY, listener);
		}
		addPropertyChangeListener(DIRECTORY_CHANGED_PROPERTY, listener);
	}

	@Override
	protected void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		// Update the selectedFile attribute
		if (SELECTED_FILE_CHANGED_PROPERTY.equals(propertyName))
			selectedFile = (File) newValue;
		super.firePropertyChange(propertyName, oldValue, newValue);
	}

	@Override
	public File getSelectedFile() {
		// The JFileChooser getSelectedFile returns a wrong value when the file
		// name field is changed
		// So, we use an attribute updated when events that modify the selected
		// file occurs.
		return selectedFile;
	}

	@Override
	public void approveSelection() {
		if (selectionTestEnabled) {
			// Refuse:
			// The broken links
			// The non existing files in OPEN_DIALOG mode
			// Non readable/writable files
			// Ask what to do if the file exists and we are in SAVE_DIALOG mode
			File file = getSelectedFile();
			if (file != null) {
				if (getDialogType() == SAVE_DIALOG) {
					File canonical;
					try {
						canonical = FileUtils.getCanonical(file);
						if (canonical.exists()) {
							boolean cancel = showSaveDisplayQuestion(this);
							if (cancel) {
								// User doesn't want to overwrite the file
								return;
							}
						}
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
				String error = getDisabledCause();
				if (error != null) {
					JOptionPane.showMessageDialog(this, error,
							get("Generic.error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
					return;
				}
			}
		}
		super.approveSelection();
	}

	/**
	 * Activate/Deactivate the test made on the selected file during in
	 * approveSelection method. <br>
	 * The default value is true.
	 * 
	 * @param enabled
	 *            true to perform tests, false to skip them.
	 */
	public void setSelectionTestEnabled(boolean enabled) {
		this.selectionTestEnabled = enabled;
	}

	public String getDisabledCause() {
		File file = getSelectedFile();
		if (file == null)
			return null;
		try {
			if (getDialogType() == OPEN_DIALOG) {
				if (!file.exists()) {
					return get("openDialog.fileDoesntExist"); //$NON-NLS-1$
				} else {
					File canonical = FileUtils.getCanonical(file);
					if (!canonical.exists()) {
						return get("openDialog.targetDoesntExist"); //$NON-NLS-1$
					} else if (!FileUtils.isReadable(file)) {
						return get("openDialog.fileNotReadable"); //$NON-NLS-1$
					}
				}
			} else {
				File canonical = FileUtils.getCanonical(file);
				if (!FileUtils.isWritable(canonical)) {
					return get("saveDialog.fileNotWritable"); //$NON-NLS-1$
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	private String get(String key) {
		return Application.LOCALIZATION.getString(key, getLocale());
	}

	/**
	 * Displays a dialog to inform the user that a file is already existing and
	 * it will be overwritten if the user continue.
	 * 
	 * @param parent
	 *            determines the Frame in which the dialog is displayed; if
	 *            null, or if the parentComponent has no Frame, a default Frame
	 *            is used
	 * @return true if the user cancels
	 */
	public static boolean showSaveDisplayQuestion(Component parent) {
		String message = Application.LOCALIZATION.getString(
				"saveDialog.FileExist.message", parent.getLocale()); //$NON-NLS-1$
		return JOptionPane.showOptionDialog(parent,
				message,
				Application.LOCALIZATION.getString(
						"saveDialog.FileExist.title", parent.getLocale()), //$NON-NLS-1$
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
				null, null) == JOptionPane.NO_OPTION;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JFileChooser#setDialogType(int)
	 */
	@Override
	public void setDialogType(int dialogType) {
		if ((fileNameField != null) && (fileNameLabel != null)) {
			boolean visible = dialogType != JFileChooser.OPEN_DIALOG;
			fileNameField.setVisible(visible);
			fileNameLabel.setVisible(visible);
		}
		super.setDialogType(dialogType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JFileChooser#setSelectedFile(java.io.File)
	 */
	@Override
	public void setSelectedFile(File file) {
		if ((file == null) && (selectedFile != null)) {
			File parent = selectedFile.getParentFile();
			super.setSelectedFile(new File("")); //$NON-NLS-1$
			super.setCurrentDirectory(parent);
			selectedFile = null;
		} else {
			super.setSelectedFile(file);
		}
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
		public void replace(int offset, int length, String text,
				AttributeSet attrs) throws BadLocationException {
			String oldValue = field.getText();
			this.ignoreEvents = true;
			super.replace(offset, length, text, attrs);
			this.ignoreEvents = false;
			String newValue = field.getText();
			if (!oldValue.equals(newValue))
				firePropertyChange(TEXT_PROPERTY, oldValue, newValue);
		}

		@Override
		public void remove(int offs, int len) throws BadLocationException {
			String oldValue = field.getText();
			super.remove(offs, len);
			String newValue = field.getText();
			if (!ignoreEvents && !oldValue.equals(newValue))
				firePropertyChange(TEXT_PROPERTY, oldValue, newValue);
		}
	}
}
