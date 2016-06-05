package com.fathzer.soft.ajlib.swing.widget;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.fathzer.soft.ajlib.swing.Utils;
import com.fathzer.soft.ajlib.swing.dialog.FileChooser;
import com.fathzer.soft.ajlib.swing.framework.Application;
import com.fathzer.soft.ajlib.utilities.NullUtils;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

/** A panel that allows the selection of a file.
 * @author Jean-Marc Astesana
 * <BR>License: LGPL v3
 */
public class FileSelectionPane extends JPanel {
	private static final long serialVersionUID = 1L;
	/** The name of the property that is fired each time the selected file changes.*/
	public static final String SELECTED_FILE_PROPERTY = "selectedFile"; //$NON-NLS-1$
	
	private JTextField textField;
	private File selectedFile;

	private TitledBorder border;
	private JLabel lblFile;
	private JButton btnChange;

	/**
	 * Constructor.
	 */
	public FileSelectionPane() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		setLayout(gridBagLayout);
		border = new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null); //$NON-NLS-1$
		setBorder(border);
		
		lblFile = new JLabel(Application.getString("FileSelectionPanel.file", getLocale())); //$NON-NLS-1$
		GridBagConstraints gbcLblFile = new GridBagConstraints();
		gbcLblFile.anchor = GridBagConstraints.WEST;
		gbcLblFile.insets = new Insets(0, 0, 0, 5);
		gbcLblFile.gridx = 0;
		gbcLblFile.gridy = 0;
		add(lblFile, gbcLblFile);
		
		textField = new JTextField();
		textField.setEditable(false);
		textField.setFocusable(false);
		GridBagConstraints gbcTextField = new GridBagConstraints();
		gbcTextField.weightx = 1.0;
		gbcTextField.insets = new Insets(0, 0, 0, 5);
		gbcTextField.fill = GridBagConstraints.HORIZONTAL;
		gbcTextField.gridx = 1;
		gbcTextField.gridy = 0;
		add(textField, gbcTextField);
		textField.setColumns(10);
		
		GridBagConstraints gbcBtnChange = new GridBagConstraints();
		gbcBtnChange.gridx = 2;
		gbcBtnChange.gridy = 0;
		add(getBtn(), gbcBtnChange);
		
		setOpaque(false);
	}

	/** Sets the selected file.
	 * @param file The selected file (null to de-select file).
	 */
	public void setSelectedFile(File file) {
		if (!NullUtils.areEquals(file, selectedFile)) {
			File oldValue = this.selectedFile;
			this.selectedFile = file;
			textField.setText(file==null?"":file.getAbsolutePath()); //$NON-NLS-1$
			this.firePropertyChange(SELECTED_FILE_PROPERTY, oldValue, file);
		}
	}
	
	/** Gets the currently selected file.
	 * @return a File or null if no file is selected
	 */
	public File getSelectedFile() {
		return this.selectedFile;
	}
	
	/** Sets the title of the panel (It appears on the border of the panel)
	 * @param title The title
	 */
	public void setTitle(String title) {
		border.setTitle(title);
	}
	
	/** Sets the label before the File name field.
	 * @param label The label
	 */
	public void setLabel(String label) {
		lblFile.setText(label);
		lblFile.setVisible(!label.isEmpty());
	}
	
	/** Gets the button that allows the selection of the file in a dialog.
	 * @return a JButton
	 */
	public JButton getBtn() {
		if (btnChange==null) {
			btnChange = new JButton(Application.getString("FileSelectionPanel.change", getLocale())); //$NON-NLS-1$
			btnChange.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser chooser = new FileChooser();
					setUpFileChooser(chooser);
					File file;
					if (chooser.getDialogType()==JFileChooser.SAVE_DIALOG) {
						file = chooser.showSaveDialog(Utils.getOwnerWindow(btnChange)) == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile() : null;
					} else {
						file = chooser.showOpenDialog(Utils.getOwnerWindow(btnChange)) == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile() : null;
					}
					if (file != null) {
						setSelectedFile(file);
					}
				}
			});
		}
		return btnChange;
	}
	
	/** Customizes the file chooser.
	 * <br>This method is called every time before the file selection dialog is opened.
	 * <br>It can be used to set up the file filter or the change the default current directory.
	 * The default implementation sets the current directory to the directory of currently selected file or user's directory if no file is currently selected.
	 * The chooser's dialog type is JFileChooser.OPEN_DIALOG
	 * @param chooser The chooser used in the dialog.
	 */
	protected void setUpFileChooser(JFileChooser chooser) {
		if (getSelectedFile()!=null) {
			chooser.setCurrentDirectory(getSelectedFile().getParentFile());
		} else {
			chooser.setCurrentDirectory(new File(".")); //$NON-NLS-1$
		}
	}
}
