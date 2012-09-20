package net.astesana.ajlib.swing.dialog;

import java.awt.Component;
import java.awt.Container;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.basic.BasicFileChooserUI;
import javax.swing.text.JTextComponent;

import net.astesana.ajlib.utilities.LocalizationData;

/** A file chooser with a confirm dialog when the selected file already exists
 * SaveAs mode.
 * @author Jean-Marc Astesana
 * <BR>License : GPL v3
 */
public class FileChooser extends JFileChooser {
	private static final long serialVersionUID = 1L;

	public FileChooser() {
		super();
		JTextComponent fileName = getFirstTextComponent(getComponents());
		System.out.println (fileName);
	}
	
	private static JTextComponent getFirstTextComponent(Component[] components) {
		for (Component component : components) {
			if (component instanceof Container) {
				Component subResult = getFirstTextComponent(((Container)component).getComponents());
				if (subResult!=null) return (JTextComponent) subResult;
			} else if (component instanceof JTextComponent) {
				return (JTextComponent) component;
			}
		}
		return null;
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
}
