package net.astesana.ajlib.swing.dialog.urichooser;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URI;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import net.astesana.ajlib.swing.dialog.FileChooser;
import net.astesana.ajlib.swing.dialog.urichooser.AbstractURIChooserPanel;
import net.astesana.ajlib.swing.dialog.urichooser.URIChooser;

/** An AbstractURIChooserPanel that allows the user to select a file.
 * @author Jean-Marc Astesana
 * <BR>License : GPL v3
 */
@SuppressWarnings("serial")
public class FileChooserPanel extends JPanel implements AbstractURIChooserPanel {
	private FileChooser fileChooser;
	private URIChooser chooser;

	/** Constructor.
	 */
	public FileChooserPanel() {
		setLayout(new BorderLayout(0, 0));
		add(getFileChooser(), BorderLayout.CENTER);
		getFileChooser().addPropertyChangeListener(FileChooser.SELECTED_FILE_CHANGED_PROPERTY, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				File oldFile = (File) evt.getOldValue();
				File newFile = (File) evt.getNewValue();
				URI oldURI = oldFile==null?null:oldFile.toURI();
				URI newURI = newFile==null?null:newFile.toURI();
				firePropertyChange(SELECTED_URI_PROPERTY, oldURI, newURI);
			}
		});
	}
	
	@Override
	public String getName() {
		return "Computer";
	}

	@Override
	public String getTooltip() {
		return "Select this tab to save/read data to/from a local storage";
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(getClass().getResource("computer.png"));
	}

	private JFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new FileChooser() {
				public void approveSelection() {
					chooser.approveSelection();
				}
			};
			fileChooser.setControlButtonsAreShown(false);
		}
		return fileChooser;
	}

	@Override
	public URI getSelectedURI() {
		File selectedFile = getFileChooser().getSelectedFile();
		return selectedFile==null?null:selectedFile.toURI();
	}

	@Override
	public void setUp() {}

	@Override
	public void setDialogType(boolean save) {
		this.fileChooser.setDialogType(save?FileChooser.SAVE_DIALOG:FileChooser.OPEN_DIALOG);
	}

	@Override
	public void setURIChooser(URIChooser chooser) {
		this.chooser = chooser;
	}
}
