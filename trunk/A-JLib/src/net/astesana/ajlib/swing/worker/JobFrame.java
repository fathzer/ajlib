package net.astesana.ajlib.swing.worker;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/** TODO
 * @author Jean-Marc Astesana
 * <BR>License : GPL v3
 */
@SuppressWarnings("serial")
public class JobFrame extends JFrame {
	private static final long DEFAULT_DELAY = 500;
	private static final int MINIMUM_TIME_VISIBLE = 1000;

	private JPanel contentPane;
	private WorkInProgressPanel progressPanel;
	
	private long setVisibleTime;
	private long delay;
	private SwingWorker<Object, Void> showWorker;
	private Worker<?, ?> worker;

	/**
	 * Create the frame.
	 */
	public JobFrame(final Worker<?, ?> swingAdapter) {
		super();
		this.worker = swingAdapter;
		this.delay = DEFAULT_DELAY;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				// Cancel the tack if the window is closing
				SwingWorker<?, ?> worker = progressPanel.getWorker();
				if (!worker.isDone()) worker.cancel(false);
			}
		});
		
		buildContentPane();

		worker.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(Worker.STATE_PROPERTY_NAME)) {
					if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
						JobFrame.this.dispose();
					}
				}
			}
		});
		pack();
		// Start the job task.
		worker.execute();
	}


	private void buildContentPane() {
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		GridBagLayout gbl_contentPane = new GridBagLayout();
		contentPane.setLayout(gbl_contentPane);
		
		progressPanel = new WorkInProgressPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.weightx = 1.0;
		gbc_panel.insets = new Insets(5, 5, 5, 5);
		gbc_panel.gridwidth = 0;
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		contentPane.add(progressPanel, gbc_panel);
		
		progressPanel.setSwingWorker(worker);
		setContentPane(contentPane);
	}
	
	
	/** Sets the delay before the dialog is shown.
	 * <BR>This class allows you to specify a delay in opening the dialog.
	 * @param delay The delay in ms. Long.MAX_VALUE to prevent the dialog to be shown.
	 * The default value is 500 ms.
	 * <BR>Note that this method must be called before calling setVisible with a true argument.
	 */
	public void setDelay(long delay) {
		this.delay = delay;
	}
	
	@Override
	public void setVisible(boolean visible) {
		if (visible) { // If the dialog is opened
			// Start a thread that will delay the dialog display 
			this.showWorker = new SwingWorker<Object, Void>() {
				@Override
				protected Object doInBackground() throws Exception {
					Thread.sleep(delay);
					return null;
				}

				@Override
				protected void done() {
					// This method is called when the showWorker end.
					// This means it is time to display the dialog.
					if (!worker.isDone()) { // If the task is not finished (please note we talk about the main task of the dialog, not the "display timer" task)
						// If main task not ended, show the dialog
						doShow();
					}
				}
			};
			this.showWorker.execute();
		} else { // If the dialog is closed
			if (this.showWorker!=null) this.showWorker.cancel(true);
			long delay = MINIMUM_TIME_VISIBLE-(System.currentTimeMillis()-this.setVisibleTime);
			try {
				if (delay>0) { // If the dialog is displayed for less than 500 ms, wait for the user to see what happens ;-)
					Thread.sleep(delay);
				}
			} catch (InterruptedException e) {
			}
			super.setVisible(visible);
		}
	}

	private void doShow() {
		this.setVisibleTime = System.currentTimeMillis(); // Remember when the dialog was displayed
		super.setVisible(true);
	}
}
