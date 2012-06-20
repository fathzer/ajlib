package net.astesana.ajlib.swing.widget;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JButton;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;


import net.astesana.ajlib.swing.dialog.FileChooser;
import net.astesana.ajlib.swing.framework.Application;
import net.astesana.ajlib.swing.table.Table;
import net.astesana.ajlib.utilities.CSVExporter;

/** A widget with a JTable and a button that is able to save it in csv format.
 * @author Jean-Marc Astesana
 * <BR>License : GPL v3
 */ 
public class ExcelPane extends JPanel {
	private static final long serialVersionUID = 1L;

	private Table table;
	private JButton saveButton;

	private CSVExporter exporter;

	/**
	 * Creates the panel.
	 */
	public ExcelPane() {
		initialize();
	}

	/** Initializes the panel.
	 * <br>This method is called once by the constructor.
	 * <br>This implementation sets the layout to a new GridBagLayout and adds the table and the button to it.
	 * <br>You can override this method in order to create a custom panel (for example, if you want to change the button position). 
	 */
	protected void initialize() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		setLayout(gridBagLayout);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.weighty = 1.0;
		gbc_scrollPane.weightx = 1.0;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		add(getTable(), gbc_scrollPane);
		GridBagConstraints gbc_saveButton = new GridBagConstraints();
		gbc_saveButton.insets = new Insets(5, 0, 5, 0);
		gbc_saveButton.anchor = GridBagConstraints.EAST;
		gbc_saveButton.gridx = 0;
		gbc_saveButton.gridy = 1;
		add(getSaveButton(), gbc_saveButton);
	}

	/** Gets the table.
	 * @return a Table
	 */
	public final Table getTable() {
		if (table == null) {
			table = buildTable();
		}
		return table;
	}
	
	/** Gets the save button.
	 * @return a button
	 */
	public final JButton getSaveButton() {
		if (saveButton == null) {
			saveButton = new JButton(Application.getString("ExcelPane.save")); //$NON-NLS-1$
			saveButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser chooser = new FileChooser(null);
					File file = chooser.showSaveDialog(ExcelPane.this)==JFileChooser.APPROVE_OPTION?chooser.getSelectedFile():null;
					if (file!=null) {
						try {
							BufferedWriter writer = new BufferedWriter(new FileWriter(file));
							try {
								getCSVExporter().export(writer, getTable().getModel(), true);
							} finally {
								writer.close();
							}
						} catch(IOException ex) {
							String message = MessageFormat.format(Application.getString("ExcelPane.error.message"), ex.toString()); //$NON-NLS-1$
							JOptionPane.showMessageDialog(ExcelPane.this, message, Application.getString("ExcelPane.error.title"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
						}
					}
				}
			});
		}
		return saveButton;
	}
	
	/** Gets the CSVExporter used to export the data.
	 * @return a CSVExporter
	 */
	public final CSVExporter getCSVExporter() {
		if (this.exporter == null) {
			exporter = buildExporter();
		}
		return this.exporter;
	}

	/** Builds the table.
	 * <br>This method is called once.
	 * <br>You can override this method in order to create a customized Table.
	 * @return a Table
	 */
	protected Table buildTable() {
		return new Table();
	}
	
	/** Builds the table.
	 * <br>This method is called once.
	 * <br>The default implementation returns a new CSVEXporter(';', false);
	 * <br>You can override this method in order to create a customized Table.
	 * @return a CSVExporter
	 */
	protected CSVExporter buildExporter() {
		return new CSVExporter(';',false);
	}
}