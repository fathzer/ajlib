package net.astesana.ajlib.swing.dialog.urichooser;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;

import javax.swing.JPanel;

import net.astesana.ajlib.swing.dialog.AbstractDialog;
import net.astesana.ajlib.swing.dialog.FileChooser;

@SuppressWarnings("serial")
public class URIChooserDialog extends AbstractDialog<URIChooser, URI> {
	public URIChooserDialog(Window owner, String title, AbstractURIChooserPanel[] choosers) {
		super(owner, title, new URIChooser(choosers));
	}

	@Override
	protected JPanel createCenterPane() {
		data.addPropertyChangeListener(URIChooser.URI_APPROVED_PROPERTY, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ((Boolean)evt.getNewValue()) {
					confirm();
				}
			}
		});
		addWindowListener(new WindowAdapter() {
			/* (non-Javadoc)
			 * @see java.awt.event.WindowAdapter#windowOpened(java.awt.event.WindowEvent)
			 */
			@Override
			public void windowOpened(WindowEvent e) {
				((AbstractURIChooserPanel)data.getSelectedComponent()).setUp();
			}
		});
		this.data.addPropertyChangeListener(AbstractURIChooserPanel.SELECTED_URI_PROPERTY, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				updateOkButtonEnabled();
			}
		});
		JPanel result = new JPanel(new BorderLayout());
		result.add(this.data, BorderLayout.CENTER);
		return result;
	}

	@Override
	protected URI buildResult() {
		return this.data.getSelectedURI();
	}

	@Override
	protected String getOkDisabledCause() {
		if (this.data.getSelectedURI()==null) return "This button is disabled because no file is selected";
		return null;
	}

	/* (non-Javadoc)
	 * @see net.astesana.ajlib.swing.dialog.AbstractDialog#confirm()
	 */
	@Override
	protected void confirm() {
		AbstractURIChooserPanel panel = (AbstractURIChooserPanel)this.data.getSelectedComponent();
		URI selectedURI = panel.getSelectedURI();
		if (selectedURI!=null && panel.exist(selectedURI) && data.isSaveDialogType()) {
			if (FileChooser.showSaveDisplayQuestion(this)) return;
		}
		super.confirm();
	}
	
	/** Sets the dialog's type.
	 * @param save true for a save dialog, false for an open dialog
	 */
	public void setSaveDialogType(boolean save) {
		data.setDialogType(save);
	}

	/** Shows the dialog and gets its result.
	 * @return an URI
	 */
	public URI showDialog() {
		setVisible(true);
		return getResult();
	}

	/** Sets the current uri.
	 * @param uri
	 */
	public void setSelectedURI(URI uri) {
		String scheme = uri.getScheme();
		for (int i=0; i<getComponentCount(); i++) {
			AbstractURIChooserPanel panel = (AbstractURIChooserPanel)data.getSelectedComponent();
			if (scheme.equals(panel.getScheme())) {
				panel.setSelectedURI(uri);
				break;
			}
		}
	}
}
