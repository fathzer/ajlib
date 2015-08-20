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

/** An abstract widget composed of four buttons to open, save, save as and create new file.
 * @author Jean-Marc Astesana
 * <BR>License: LGPL v3
 */
public abstract class AbstractFileSelector extends JPanel {
	private static final long serialVersionUID = 1L;

	private JButton btnOpen;
	private JButton btnNew;
	private JButton btnSave;
	private JButton btnSaveAs;

	/**
	 * Creates the panel.
	 */
	public AbstractFileSelector() {
		initialize();
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

	private JButton getBtnOpen() {
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
					if (getCurrentFile()!=null) {
						chooser.setCurrentDirectory(getCurrentFile().getParentFile());
					}
					File file = chooser.showOpenDialog(Utils.getOwnerWindow(btnOpen)) == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile() : null;
					if (file != null) {
						open(file);
					}
				}
			});
		}
		return btnOpen;
	}
	private JButton getBtnNew() {
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
				}
			});
		}
		return btnNew;
	}
	private JButton getBtnSave() {
		if (btnSave == null) {
			btnSave = new JButton(Application.getString("FileSelector.save", getLocale())); //$NON-NLS-1$
			btnSave.setIcon(new ImageIcon(AbstractFileSelector.class.getResource("Save.png"))); //$NON-NLS-1$
			btnSave.setToolTipText(Application.getString("FileSelector.save.tooltip", getLocale())); //$NON-NLS-1$
			btnSave.setEnabled(false);
			btnSave.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					File file = getCurrentFile();
					if (file==null) {
						JFileChooser chooser = new FileChooser();
						file = chooser.showSaveDialog(Utils.getOwnerWindow(btnSave)) == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile() : null;
					}
					if (file != null) {
						save(file);
					}
				}
			});
		}
		return btnSave;
	}
	private JButton getBtnSaveAs() {
		if (btnSaveAs == null) {
			btnSaveAs = new JButton(Application.getString("FileSelector.saveAs", getLocale())); //$NON-NLS-1$
			btnSaveAs.setIcon(new ImageIcon(AbstractFileSelector.class.getResource("SaveAs.png"))); //$NON-NLS-1$
			btnSaveAs.setToolTipText(Application.getString("FileSelector.saveAs.tooltip", getLocale())); //$NON-NLS-1$
			btnSaveAs.setEnabled(false);
			btnSaveAs.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser chooser = new FileChooser();
					if (getCurrentFile()!=null) {
						chooser.setCurrentDirectory(getCurrentFile().getParentFile());
					}
					File file = chooser.showSaveDialog(Utils.getOwnerWindow(btnSave)) == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile() : null;
					if (file != null) {
						save(file);
					}
				}
			});
		}
		return btnSaveAs;
	}
	
	/** Sets the data state.
	 * <br>This method should be called when the data state changes.
	 * @param notEmpty true if data is not empty.
	 * @param needToBeSaved true if dated as been modified since last call to save method.
	 * @see #save(File) 
	 */
	public void setDataState(boolean notEmpty, boolean needToBeSaved) {
		getBtnSaveAs().setEnabled(notEmpty);
		getBtnSave().setEnabled(notEmpty && needToBeSaved);
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
		String[] options =new String[]{getBtnSave().getText(),Application.getString("GenericButton.ignore", getLocale()),
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
			if (getCurrentFile()==null) {
				JFileChooser chooser = new FileChooser();
				File file = chooser.showSaveDialog(Utils.getOwnerWindow(this)) == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile() : null;
				if (file != null) {
					return save(file);
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

	/** Opens a file.
	 * <br>This method is called when the user clicks the <b>"open"</b> button.
	 * @param file The file to open.
	 * @return true if the file was successfully opened, false if the file opening failed.
	 */
	protected abstract boolean open(File file);

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
	protected abstract File getCurrentFile();
}