package net.astesana.ajlib.swing.worker;

import javax.swing.Icon;
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

/** A default panel reporting the progress of a background task.
 * <br>This panel contains a message, a progress bar and a cancel button. 
 * @author Jean-Marc Astesana
 * <BR>License : GPL v3
 * @see Worker
 */
@SuppressWarnings("serial")
public class DefaultWorkInProgressPanel extends WorkInProgressPanel {
	private Worker<?, ?> worker;
	private JLabel message;
	private JProgressBar progressBar;
	private JButton btnCancel;
	private PropertyChangeListener workerListener;

	/**
	 * Constructor.
	 */
	public DefaultWorkInProgressPanel() {
		buildContent();

		workerListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(Worker.STATE_PROPERTY_NAME)) {
					getBtnCancel().setEnabled(evt.getNewValue().equals(SwingWorker.StateValue.STARTED));
					if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
						if (!worker.isCancelled()) {
							getProgressBar().setIndeterminate(false);
							getProgressBar().setValue(getProgressBar().getMaximum());
						}
					}
				} else if (evt.getPropertyName().equals(Worker.PROGRESS_PROPERTY_NAME)){
					long absoluteValue = ((Integer)evt.getNewValue())*worker.getPhaseLength()/100;
					getProgressBar().setValue((int)absoluteValue);
				} else if (evt.getPropertyName().equals(Worker.JOB_PHASE) || evt.getPropertyName().equals(Worker.PHASE_LENGTH)){
					initPhase();
				}
			}
		};
	}

	/** Builds the panel content.
	 * <br>You may override this method in order to implement your own panel.
	 * <br>Note that this method is called once during by the panel constructor.
	 */
	protected void buildContent() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		setLayout(gridBagLayout);
		
		GridBagConstraints gbc_message = new GridBagConstraints();
		gbc_message.insets = new Insets(5, 5, 5, 5);
		gbc_message.anchor = GridBagConstraints.WEST;
		gbc_message.gridwidth = 0;
		gbc_message.gridx = 0;
		gbc_message.gridy = 0;
		add(getLabel(), gbc_message);
		
		GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.weightx = 1.0;
		gbc_progressBar.insets = new Insets(0, 5, 5, 5);
		gbc_progressBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_progressBar.gridwidth = 0;
		gbc_progressBar.gridx = 0;
		gbc_progressBar.gridy = 1;
		add(getProgressBar(), gbc_progressBar);
		
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.insets = new Insets(0, 5, 5, 5);
		gbc_btnCancel.gridx = 0;
		gbc_btnCancel.gridy = 2;
		add(getBtnCancel(), gbc_btnCancel);
	}

	/* (non-Javadoc)
	 * @see net.astesana.ajlib.swing.worker.WorkInProgressPanel#getWorker()
	 */
	@Override
	public Worker<?, ?> getWorker() {
		return worker;
	}

	/* (non-Javadoc)
	 * @see net.astesana.ajlib.swing.worker.WorkInProgressPanel#setSwingWorker(net.astesana.ajlib.swing.worker.Worker)
	 */
	@Override
	public void setSwingWorker(Worker<?, ?> worker) {
		if (this.worker!=null) this.worker.removePropertyChangeListener(workerListener);
		this.worker = worker;
		this.worker.addPropertyChangeListener(workerListener);
		initPhase();
	}

	public void setMessage(String message) {
		this.message.setText(message);
	}
	
	public void setIcon(Icon icon) {
		this.message.setIcon(icon);
	}
	
	/** Inits the progress bar.
	 * <br>This method is called every time the worker phase or phase length changes (or when the worker itself changes).
	 * <br>The default implementation sets the progress bar in indeterminate state if the length of the worker's phase is <= 0.
	 * If it is > 0, then, it sets the maximum to the phase length and turns the StringPainted of the progress bar to true.
	 * <br>if the phase wording is null, the message text is unchanged.
	 * <br>You can override this method to change this behavior. 
	 */
	protected void initPhase() {
		if (worker.getPhase()!=null) setMessage(worker.getPhase());					
		int phaseLength = worker.getPhaseLength();
		JProgressBar pBar = getProgressBar();
		pBar.setIndeterminate(phaseLength<=0);
		pBar.setStringPainted(phaseLength>0);
		if (phaseLength>0) {
			pBar.setMaximum(phaseLength);
		} else {
			pBar.setValue(0);
		}
	}

	public JLabel getLabel() {
		if (message==null) {
			message = new JLabel(" "); //$NON-NLS-1$
		}
		return message;
	}

	public JProgressBar getProgressBar() {
		if (progressBar==null) {
			progressBar = new JProgressBar();
		}
		return progressBar;
	}
	
	public JButton getBtnCancel() {
		if (btnCancel==null) {
			btnCancel = new JButton(Application.getString("GenericButton.cancel", getLocale())); //$NON-NLS-1$
			btnCancel.setEnabled(false);
			getBtnCancel().addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (worker!=null) {
						worker.cancel(false);
					}
				}
			});
		}
		return btnCancel;
	}
}
