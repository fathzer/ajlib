package com.fathzer.soft.ajlib.swing.widget;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.fathzer.jlocal.Formatter;
import com.fathzer.soft.ajlib.swing.dialog.FileChooser;
import com.fathzer.soft.ajlib.swing.framework.Application;
import com.fathzer.soft.ajlib.swing.table.CSVExporter;
import com.fathzer.soft.ajlib.swing.table.Table;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/** A widget with a JTable and a button that is able to save it in csv format.
 * @author Jean-Marc Astesana
 * <BR>License: LGPL v3
 */ 
public class ExcelPane extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final String CSV_EXTENSION = ".csv";

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
		GridBagConstraints gbcScrollPane = new GridBagConstraints();
		gbcScrollPane.gridwidth = 0;
		gbcScrollPane.weighty = 1.0;
		gbcScrollPane.weightx = 1.0;
		gbcScrollPane.fill = GridBagConstraints.BOTH;
		gbcScrollPane.gridx = 0;
		gbcScrollPane.gridy = 0;
		add(getTable(), gbcScrollPane);
		GridBagConstraints gbcSaveButton = new GridBagConstraints();
		gbcSaveButton.insets = new Insets(5, 0, 0, 0);
		gbcSaveButton.anchor = GridBagConstraints.EAST;
		gbcSaveButton.gridx = 1;
		gbcSaveButton.gridy = 1;
		add(getSaveButton(), gbcSaveButton);
		JComponent extra = getExtraComponent();
		if (extra!=null) {
			GridBagConstraints gbcExtra = new GridBagConstraints();
			gbcExtra.insets = new Insets(5, 0, 0, 0);
			gbcExtra.anchor = GridBagConstraints.WEST;
			gbcExtra.gridx = 0;
			gbcExtra.gridy = 1;
			add(extra, gbcExtra);
		}
	}
	
	/** Gets an extra component displayed at left below the table.
	 * <br>By default, this method returns null.
	 * @return A JComponent or null if no component is provided.
	 */
	protected JComponent getExtraComponent() {
		return null;
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
			saveButton = new JButton(Application.getString("ExcelPane.save", getLocale())); //$NON-NLS-1$
			saveButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser chooser = new FileChooser(getInitialPath()) {
						private static final long serialVersionUID = 1L;
						public File getSelectedFile() {
							File f = super.getSelectedFile();
							if ((f!=null) && !f.getName().endsWith(CSV_EXTENSION)) {
								f = new File(f.getParent(), f.getName()+CSV_EXTENSION);
							}
							return f;
						}
					};
					chooser.setFileFilter(new FileNameExtensionFilter(Application.getString("ExcelPane.csv.wording", getLocale()),CSV_EXTENSION.substring(1)));
					File file = chooser.showSaveDialog(ExcelPane.this)==JFileChooser.APPROVE_OPTION?chooser.getSelectedFile():null;
					if (file!=null) {
						try {
							save(file);
						} catch(IOException ex) {
							String message = Formatter.format(Application.getString("ExcelPane.error.message", getLocale()), ex.toString()); //$NON-NLS-1$
							JOptionPane.showMessageDialog(ExcelPane.this, message, Application.getString("Generic.error", getLocale()), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
						}
					}
				}
			});
		}
		return saveButton;
	}
	
	/** Gets the initial path of the file chooser.
	 * <br>This method is called every time the save button is clicked.
	 * <br>By default, this path is null.
	 * @return The path or null that causes the file chooser to point to the user's default directory
	 */
	protected String getInitialPath() {
		return null;
	}
	
	/** Saves the pane content to a file.
	 * <br>This method is called every time the save button is clicked and a file is selected.
	 * <br>By default, the content is save to the file using the csv exporter.
	 * @param file The file selected by the user to save the data.
	 * @see #getCSVExporter()
	 */
	protected void save(File file) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		try {
			getCSVExporter().export(writer, getTable().getModel(), true);
			writer.flush();
		} finally {
			writer.close();
		}
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
	 * <br>The default implementation returns a default CSVExporter;
	 * @return a CSVExporter
	 * @see CSVExporter
	 */
	protected CSVExporter buildExporter() {
		return new CSVExporter();
	}
}