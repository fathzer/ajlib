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


import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

/** A panel that allows the selection of a file.
 * @author Jean-Marc Astesana
 * <BR>License: LGPL v3
 */
public class FileSelectionPane extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public static final String SELECTED_FILE_PROPERTY = "selectedFile"; //$NON-NLS-1$
	private JTextField textField;
	private File selectedFile;

	private TitledBorder border;
	private JLabel lblFile;

	/**
	 * Create the panel.
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
		GridBagConstraints gbcTextField = new GridBagConstraints();
		gbcTextField.weightx = 1.0;
		gbcTextField.insets = new Insets(0, 0, 0, 5);
		gbcTextField.fill = GridBagConstraints.HORIZONTAL;
		gbcTextField.gridx = 1;
		gbcTextField.gridy = 0;
		add(textField, gbcTextField);
		textField.setColumns(10);
		
		final JButton btnChange = new JButton(Application.getString("FileSelectionPanel.change", getLocale())); //$NON-NLS-1$
		btnChange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new FileChooser();
				chooser.setCurrentDirectory(new File(".")); //$NON-NLS-1$
				File file = chooser.showOpenDialog(Utils.getOwnerWindow(btnChange)) == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile() : null;
				if (file != null) {
					setSelectedFile(file);
				}
			}
		});
		GridBagConstraints gbcBtnChange = new GridBagConstraints();
		gbcBtnChange.gridx = 2;
		gbcBtnChange.gridy = 0;
		add(btnChange, gbcBtnChange);
	}

	public void setSelectedFile(File file) {
		if (!file.equals(selectedFile)) {
			File oldValue = this.selectedFile;
			this.selectedFile = file;
			textField.setText(file.getAbsolutePath());
			this.firePropertyChange(SELECTED_FILE_PROPERTY, oldValue, file);
		}
	}
	
	public File getSelectedFile() {
		return this.selectedFile;
	}
	
	public void setTitle(String title) {
		border.setTitle(title);
	}
	
	public void setLabel(String label) {
		lblFile.setText(label);
	}
}
