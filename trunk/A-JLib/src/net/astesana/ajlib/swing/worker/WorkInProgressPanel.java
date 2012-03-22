package net.astesana.ajlib.swing.worker;

import javax.swing.JPanel;
import javax.swing.SwingWorker;

import java.awt.GridBagLayout;
import javax.swing.JProgressBar;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import java.awt.Insets;
import javax.swing.JButton;

import net.astesana.ajlib.swing.framework.Application;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

@SuppressWarnings("serial")
/** A panel reporting the progress of a background task.
 * @author Jean-Marc Astesana
 * <BR>License : GPL v3
 * @see SwingWorkerJobAdapter
 */
public class WorkInProgressPanel extends JPanel {
	private SwingWorkerJobAdapter<?, ?> worker;
	private JLabel message;
	private JProgressBar progressBar;
	private JButton btnCancel;
	private PropertyChangeListener workerListener;

	/**
	 * Constructor.
	 */
	public WorkInProgressPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		setLayout(gridBagLayout);
		
		message = new JLabel(" "); //$NON-NLS-1$
		GridBagConstraints gbc_message = new GridBagConstraints();
		gbc_message.insets = new Insets(0, 0, 5, 0);
		gbc_message.anchor = GridBagConstraints.WEST;
		gbc_message.gridwidth = 0;
		gbc_message.gridx = 0;
		gbc_message.gridy = 0;
		add(message, gbc_message);
		
		progressBar = new JProgressBar();
		GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.weightx = 1.0;
		gbc_progressBar.insets = new Insets(0, 0, 5, 0);
		gbc_progressBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_progressBar.gridwidth = 0;
		gbc_progressBar.gridx = 0;
		gbc_progressBar.gridy = 1;
		add(progressBar, gbc_progressBar);
		
		btnCancel = new JButton(Application.getString("GenericButton.cancel")); //$NON-NLS-1$
		btnCancel.setEnabled(false);
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (worker!=null) {
					worker.cancel(false);
				}
			}
		});
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.gridx = 0;
		gbc_btnCancel.gridy = 2;
		add(btnCancel, gbc_btnCancel);

		workerListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(SwingWorkerJobAdapter.STATE_PROPERTY_NAME)) {
					btnCancel.setEnabled(evt.getNewValue().equals(SwingWorker.StateValue.STARTED));
					if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
						if (!worker.isCancelled()) {
							setMessage("done");
							getProgressBar().setIndeterminate(false);
							getProgressBar().setValue(getProgressBar().getMaximum());
						}
					}
				} else if (evt.getPropertyName().equals(SwingWorkerJobAdapter.PROGRESS_PROPERTY_NAME)){
					long absoluteValue = ((Integer)evt.getNewValue())*worker.getPhaseLength()/100;
					getProgressBar().setValue((int)absoluteValue);
				} else if (evt.getPropertyName().equals(SwingWorkerJobAdapter.JOB_PHASE)){
					setMessage((String)evt.getNewValue());					
					initProgressBar();
				}
			}
		};
	}

	public SwingWorkerJobAdapter<?, ?> getWorker() {
		return worker;
	}

	public void setSwingWorker(SwingWorkerJobAdapter<?, ?> worker) {
		if (this.worker!=null) this.worker.removePropertyChangeListener(workerListener);
		this.worker = worker;
		this.worker.addPropertyChangeListener(workerListener);
		initProgressBar();
	}

	public void setMessage(String message) {
		this.message.setText(message);
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}
	
	private void initProgressBar() {
		int phaseLength = worker.getPhaseLength();
		getProgressBar().setIndeterminate(phaseLength<0);
		if (worker.getPhaseLength()>0) getProgressBar().setMaximum(worker.getPhaseLength());
	}
}
