package com.fathzer.soft.ajlib.swing.widget;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.GridBagLayout;

import javax.swing.JButton;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;

import com.fathzer.soft.ajlib.swing.Utils;
import com.fathzer.soft.ajlib.swing.dialog.FileChooser;
import com.fathzer.soft.ajlib.swing.framework.Application;
import com.fathzer.soft.ajlib.utilities.NullUtils;

/** An abstract widget composed of four buttons to open, save, save as and create new file.
 * @author Jean-Marc Astesana
 * <BR>License: LGPL v3
 */
public abstract class AbstractFileSelector extends JPanel {
	private static final long serialVersionUID = 1L;
	/** A property bind with the file selected in this panel.*/
	public static final String SELECTED_FILE_PROPERTY = "SELECTED_FILE";
	/** A property bind with the changes of the data managed by this panel.*/
	public static final String CHANGED_PROPERTY = "CHANGED";
	/** A property bind with the emptiness the data managed by this panel.*/
	public static final String EMPTY_PROPERTY = "EMPTY";

	private JButton btnOpen;
	private JButton btnNew;
	private JButton btnSave;
	private JButton btnSaveAs;
	
	private File file;
	private boolean isChanged;

	/**
	 * Creates the panel.
	 */
	protected AbstractFileSelector() {
		initialize();
		this.isChanged = false;
	}
	private void initialize() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		setLayout(gridBagLayout);
		GridBagConstraints gbcBtnOpen = new GridBagConstraints();
		gbcBtnOpen.gridx = 0;
		gbcBtnOpen.gridy = 0;
		add(getBtnOpen(), gbcBtnOpen);
		GridBagConstraints gbcBtnNew = new GridBagConstraints();
		gbcBtnNew.gridx = 1;
		gbcBtnNew.gridy = 0;
		add(getBtnNew(), gbcBtnNew);
		GridBagConstraints gbcBtnSave = new GridBagConstraints();
		gbcBtnSave.gridx = 2;
		gbcBtnSave.gridy = 0;
		add(getBtnSave(), gbcBtnSave);
		GridBagConstraints gbcBtnSaveAs = new GridBagConstraints();
		gbcBtnSaveAs.weightx = 1.0;
		gbcBtnSaveAs.anchor = GridBagConstraints.WEST;
		gbcBtnSaveAs.gridx = 3;
		gbcBtnSaveAs.gridy = 0;
		add(getBtnSaveAs(), gbcBtnSaveAs);
	}

	protected JButton getBtnOpen() {
		if (btnOpen == null) {
			btnOpen = new JButton(Application.getString("FileSelector.open", getLocale())); //$NON-NLS-1$
			btnOpen.setToolTipText(Application.getString("FileSelector.open.tooltip", getLocale())); //$NON-NLS-1$
			btnOpen.setIcon(new ImageIcon(AbstractFileSelector.class.getResource("Open.png"))); //$NON-NLS-1$
			btnOpen.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (!lastChanceToSave()) {
						return;
					}
					JFileChooser chooser = new FileChooser();
					if (getSelectedFile()!=null) {
						chooser.setCurrentDirectory(getSelectedFile().getParentFile());
					}
					File file = chooser.showOpenDialog(Utils.getOwnerWindow(btnOpen)) == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile() : null;
					if (file != null && read(file)) {
						setFile(file);
						setEmpty(false);
					}
				}
			});
		}
		return btnOpen;
	}
	protected JButton getBtnNew() {
		if (btnNew == null) {
			btnNew = new JButton(Application.getString("FileSelector.new", getLocale())); //$NON-NLS-1$
			btnNew.setIcon(new ImageIcon(AbstractFileSelector.class.getResource("New.png"))); //$NON-NLS-1$
			btnNew.setToolTipText(Application.getString("FileSelector.new.tooltip", getLocale())); //$NON-NLS-1$
			btnNew.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (!lastChanceToSave()) {
						return;
					}
					newFile();
					setEmpty(true);
					setFile(null);
				}
			});
		}
		return btnNew;
	}
	protected JButton getBtnSave() {
		if (btnSave == null) {
			btnSave = new JButton(Application.getString("FileSelector.save", getLocale())); //$NON-NLS-1$
			btnSave.setIcon(new ImageIcon(AbstractFileSelector.class.getResource("Save.png"))); //$NON-NLS-1$
			btnSave.setToolTipText(Application.getString("FileSelector.save.tooltip", getLocale())); //$NON-NLS-1$
			btnSave.setEnabled(false);
			btnSave.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					File file = getSelectedFile();
					if (file==null) {
						JFileChooser chooser = new FileChooser();
						file = chooser.showSaveDialog(Utils.getOwnerWindow(btnSave)) == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile() : null;
					}
					if (file != null && save(file)) {
						setFile(file);
					}
				}
			});
		}
		return btnSave;
	}
	protected JButton getBtnSaveAs() {
		if (btnSaveAs == null) {
			btnSaveAs = new JButton(Application.getString("FileSelector.saveAs", getLocale())); //$NON-NLS-1$
			btnSaveAs.setIcon(new ImageIcon(AbstractFileSelector.class.getResource("SaveAs.png"))); //$NON-NLS-1$
			btnSaveAs.setToolTipText(Application.getString("FileSelector.saveAs.tooltip", getLocale())); //$NON-NLS-1$
			btnSaveAs.setEnabled(false);
			btnSaveAs.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser chooser = new FileChooser();
					if (getSelectedFile()!=null) {
						chooser.setCurrentDirectory(getSelectedFile().getParentFile());
					}
					File file = chooser.showSaveDialog(Utils.getOwnerWindow(btnSave)) == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile() : null;
					if (file != null && save(file)) {
						setFile(file);
					}
				}
			});
		}
		return btnSaveAs;
	}
	
	private void updateButtons(boolean isEmpty) {
		getBtnSaveAs().setEnabled(!isEmpty);
		getBtnSave().setEnabled(!isEmpty && isChanged());
	}
	
	/** Informs this panel that the data was changed since last save.
	 * <br>This method should be called when the data state changes.
	 * <br>Please note that when data is successfully read, saved, or created using this panel buttons, changed state is automatically set to false. 
	 * @param isChanged true if dated as been modified since last call to save method.
	 * @see #save(File) 
	 */
	public void setChanged(boolean isChanged) {
		if (isChanged!=this.isChanged) {
			boolean oldIsChanged = this.isChanged;
			this.isChanged = isChanged;
			updateButtons(isEmpty());
			firePropertyChange(CHANGED_PROPERTY, oldIsChanged, isChanged);
		}
	}

	/** Informs this panel that the data was changed since last save.
	 * <br>This method should be called when the data state changes.
	 * <br>Please note that when data is successfully read, empty state is automatically set to false. 
	 * <br>Please note that when data is successfully create, empty state is automatically set to true. 
	 * @param isEmpty true if data is empty.
	 */
	public void setEmpty(boolean isEmpty) {
		if (isEmpty!=this.isEmpty()) {
			boolean oldIsEmpty = isEmpty(); 
			updateButtons(isEmpty);
			firePropertyChange(EMPTY_PROPERTY, oldIsEmpty, isEmpty);
		}
	}

	/** This method is called before changing the selected file.
	 * <br>If current data is not saved yet, this method asks the user what to do (ignore, save, cancel).
	 * <br>You can call this method if needed, for example, when the window containing this widget is closing.
	 * @return true if the process can continue, false to keep everything unchanged
	 */
	public boolean lastChanceToSave() {
		if (!getBtnSave().isEnabled()) {
			// If save button is enabled, there's nothing to save
			return true;
		}
		String[] options = new String[]{getBtnSave().getText(),Application.getString("GenericButton.ignore", getLocale()),
				Application.getString("GenericButton.cancel", getLocale())}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		int n = JOptionPane.showOptionDialog(Utils.getOwnerWindow(this),
					getUnsavedQuestion(),
			    Application.getString("FileSelector.unsavedChanges", getLocale()), //$NON-NLS-1$
			    JOptionPane.YES_NO_CANCEL_OPTION,
			    JOptionPane.WARNING_MESSAGE,
			    null,     //do not use a custom Icon
			    options,  //the titles of buttons
			    options[2]); //default button title
		if (n==-1 || n==2) {
			return false;
		} else if (n==0) {
			if (getSelectedFile()==null) {
				JFileChooser chooser = new FileChooser();
				File file = chooser.showSaveDialog(Utils.getOwnerWindow(this)) == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile() : null;
				if (file != null) {
					boolean ok =  save(file);
					if (ok) {
						setFile(file);
					}
					return ok;
				} else {
					return false;
				}
			}
		}
		return true;
	}
	
	/** Gets the question to ask to the user when lastChanceToSave is called and data has unsaved changes.
	 * <br>Subclasses may override this method to customize the question.
	 * @return The question to ask.
	 */
	protected String getUnsavedQuestion() {
		return Application.getString("FileSelector.unsavedChanges.question", getLocale()); //$NON-NLS-1$
	}

	/** Reads a file.
	 * <br>This method is called when the user clicks the <b>"open"</b> button.
	 * @param file The file to read.
	 * @return true if the file was successfully read, false if the file reading failed.
	 */
	protected abstract boolean read(File file);

	/** Creates a new empty data set.
	 * <br>This method is called when the user clicks the <b>"new"</b> button.
	 */
	protected abstract void newFile();
	
	/** Saves the data to a file.
	 * <br>This method is called when the user clicks the "Save" or "Save as" buttons.
	 * @param file The file where to save the data.
	 * @return true if the save succeeds.
	 */
	protected abstract boolean save(File file);
	
	/** Gets the current edited file.
	 * @return The selected file or null if no file is selected.
	 */
	public File getSelectedFile() {
		return this.file;
	}
	
	/** Sets the selected file.
	 * <br>If current file has unmodified changes, a dialog asks the user if he wants to save the changes.
	 * @param file The file to read or null to clear the data and select no file
	 */
	public void setSelectedFile(File file) {
		if (!NullUtils.areEquals(this.file, file)) {
			if (!lastChanceToSave()) {
				return;
			}
			if (file == null && lastChanceToSave()) {
				newFile();
				setFile(null);
			} else if (read(file)) {
				setFile(file);
			}
		}
	}
	
	private void setFile(File file) {
		File old = this.file;
		this.file = file;
		firePropertyChange(SELECTED_FILE_PROPERTY, old, file);
		setChanged(false);
	}

	public boolean isEmpty() {
		return !getBtnSaveAs().isEnabled();
	}
	
	public boolean isChanged() {
		return isChanged;
	}
}