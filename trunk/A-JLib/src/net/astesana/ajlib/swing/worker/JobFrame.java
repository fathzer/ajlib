package net.astesana.ajlib.swing.worker;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/** A progress dialog, that manages a long background task.
 * <br>When the dialog is constructed, its task is launch in a SwingWorker thread.
 * <br>As the running computer may be faster that the developer thought, the long task is maybe a small and fast task !
 * So, instead of displaying immediately the dialog, we wait a little. If the long task completes during this time, the dialog is not displayed
 * (of course, the done method of the swingWorker is invoked).
 * <br>Once it is displayed, it remains visible for a minimum time (to prevent a flash effect if the search completes just after the pop up delay).
 * @author Jean-Marc Astesana
 * <BR>License : GPL v3
 */
@SuppressWarnings("serial")
public class JobFrame extends JDialog {
	public static final long DEFAULT_DELAY = 500;
	public static final long DEFAULT_MINIMUM_TIME_VISIBLE = 1000;

	private JPanel contentPane;
	private WorkInProgressPanel progressPanel;
	
	private long setVisibleTime;
	private long minimumVisibleTime;
	private long delay;
	private SwingWorker<Object, Void> showWorker;
	private Worker<?, ?> worker;

	/**
	 * Constructor.
	 * <br>Default arguments : owner is null, title is an empty String and modality is ModalityType.MODELESS.
	 * @param worker The worker that will run in background.
	 * @see #JobFrame(Window, String, ModalityType, Worker)
	 */
	public JobFrame(Worker<?, ?> worker) {
		this(null,"",ModalityType.MODELESS, worker);
	}
	
	/**
	 * Constructor.
	 * @param owner The owner of the dialog
	 * @param title The title of the dialog.
	 * @param modality The modality of the dialog
	 * @param worker The worker that will be executed in background.
	 */
	public JobFrame(Window owner, String title, ModalityType modality, Worker<?,?> worker) {
		super(owner, title, modality);
		this.worker = worker;
		this.delay = DEFAULT_DELAY;
		this.minimumVisibleTime = DEFAULT_MINIMUM_TIME_VISIBLE;
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
		
		progressPanel = getWorkInProgressPanel();
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
	
	/** Gets the progress panel used by this dialog.
	 * @return a JPanel
	 */
	public WorkInProgressPanel getWorkInProgressPanel() {
		if (progressPanel==null) {
			progressPanel = new DefaultWorkInProgressPanel();
		}
		return progressPanel;
	}
	
	/** Sets the delay before the dialog is shown.
	 * <BR>This class allows you to specify a delay in opening the dialog.
	 * @param delay The delay in ms. Long.MAX_VALUE to prevent the dialog to be shown.
	 * The default value is DEFAULT_DELAY.
	 * <BR>Note that this method must be called before calling setVisible with a true argument.
	 */
	public void setDelay(long delay) {
		this.delay = delay;
	}
	
	/** Sets the minimum time the dialog will be visible if it is shown.
	 * @param time The time in ms.
	 * The default value is DEFAULT_MINIMUM_TIME_VISIBLE.
	 * <BR>Note that this method must be called before calling setVisible with a true argument.
	 */
	public void setMinimumVisibleTime(long time) {
		this.minimumVisibleTime = time;
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
		} else { // If the dialog is made invisible
			if (this.showWorker!=null) this.showWorker.cancel(true);
			long delay = this.minimumVisibleTime-(System.currentTimeMillis()-this.setVisibleTime);
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
