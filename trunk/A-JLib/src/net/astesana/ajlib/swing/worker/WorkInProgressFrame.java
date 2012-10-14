package net.astesana.ajlib.swing.worker;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.SwingWorker.StateValue;

import net.astesana.ajlib.swing.Utils;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/** A progress dialog, that manages a long background task (a instance of Worker class).
 * <br>When the setVisible(true) method of the dialog is called, its task is launched in a SwingWorker thread.
 * <br>As the running computer may be faster that the developer thought, the long task is maybe a small and fast task !
 * So, instead of displaying immediately the dialog, we wait a little. If the long task completes during this time, the dialog is not displayed
 * (of course, the done method of the swingWorker is invoked).
 * <br>Once it is displayed, it remains visible for a minimum time (to prevent a flash effect if the task completes just after the pop up delay).
 * <br><br>By default, when the user clicks the frame close box, it cancels the task and immediately disposes the window.
 * If you set the frame default close operation to "do nothing" (with <code>this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE)</code>)
 * the close box does nothing at all. You can then listen to window closing event in order to do what you want.
 * @author Jean-Marc Astesana
 * <BR>License : GPL v3
 * @see Worker
 */
@SuppressWarnings("serial")
public class WorkInProgressFrame extends JDialog {
	private final class AutoClosePropertyChangeListener implements PropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(Worker.STATE_PROPERTY_NAME)) {
				if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
//					System.out.println ("End of worker detected at "+System.currentTimeMillis());
					WorkInProgressFrame.this.dispose();
				}
			}
		}
	}

	public static final int DEFAULT_DELAY = 500;
	public static final int DEFAULT_MINIMUM_TIME_VISIBLE = 1000;

	private WorkInProgressPanel progressPanel;
	
	private long setVisibleTime;
	private int minimumVisibleTime;
	private int delay;
	private Timer timer;
	private Worker<?, ?> worker;

	/**
	 * Constructor.
	 * @param owner The owner of the dialog
	 * @param title The title of the dialog.
	 * @param modality The modality of the dialog
	 * @param worker The worker that will be executed in background.
	 */
	public WorkInProgressFrame(Window owner, String title, ModalityType modality, Worker<?,?> worker) {
		super(owner, title, modality);
		this.delay = DEFAULT_DELAY;
		this.minimumVisibleTime = DEFAULT_MINIMUM_TIME_VISIBLE;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				if (getDefaultCloseOperation()!=JFrame.DO_NOTHING_ON_CLOSE) {
					// Cancel the task if the window is closing
					Worker<?, ?> worker = progressPanel.getWorker();
					if (!worker.isFinished()) worker.cancel(false);
					
					if (getDefaultCloseOperation()==JFrame.DISPOSE_ON_CLOSE) {
						forceDispose();
					}
				}
			}
		});
		
		this.worker = worker;
		buildContentPane();
		this.worker.addPropertyChangeListener(new AutoClosePropertyChangeListener());

		pack();
		if (owner!=null) Utils.centerWindow(this, owner);
	}

	private void buildContentPane() {
		progressPanel = getWorkInProgressPanel();
		setContentPane(progressPanel);
	}
	
	/** Gets the progress panel used by this dialog.
	 * @return a JPanel
	 * @see #buildProgressPanel()
	 */
	public final WorkInProgressPanel getWorkInProgressPanel() {
		if (progressPanel==null) {
			progressPanel = buildProgressPanel();
			progressPanel.setSwingWorker(worker);
		}
		return progressPanel;
	}

	/** Builds the progress panel.
	 * @return a new instance of DefaultWorkInProgressPanel. If you need a customized panel, you should override this method.
	 */
	protected WorkInProgressPanel buildProgressPanel() {
		return new DefaultWorkInProgressPanel();
	}
	
	/** Sets the delay before the dialog is shown.
	 * <BR>This class allows you to specify a delay in opening the dialog.
	 * @param delay The delay in ms. Long.MAX_VALUE to prevent the dialog to be shown.
	 * The default value is DEFAULT_DELAY.
	 * <BR>Note that this method must be called before calling setVisible with a true argument.
	 */
	public void setDelay(int delay) {
		this.delay = delay;
	}
	
	/** Sets the minimum time the dialog will be visible if it is shown.
	 * @param time The time in ms.
	 * The default value is DEFAULT_MINIMUM_TIME_VISIBLE.
	 * <BR>Note that this method must be called before calling setVisible with a true argument.
	 */
	public void setMinimumVisibleTime(int time) {
		this.minimumVisibleTime = time;
	}
	
	/** Sets the frame visible or not
	 * <br> if visible is true and the worker is pending, then the execute method is called.
	 * @param visible true to set the dialog visible.
	 * @see #execute()
	 */
	@Override
	public void setVisible(boolean visible) {
		if (visible) { // If we try to open the dialog, start the task if it is not already started
			execute();
		}
	}
	
	@Override
	public void dispose() {
		// remaining will contain the visibility remaining time (to satisfied the minimumVisibleTime attribute).
		// If the worker was cancelled, we assume that the user has cancelled the dialog ... so, minimumVisibleTime has no reason to be satisfied
		long remaining = this.worker.isCancelled()?0:minimumVisibleTime-(System.currentTimeMillis()-setVisibleTime);
		if (remaining>0) { // If the dialog is displayed for less than the minimum visible time ms, and the task was not cancelled
			// Wait for the user to see what happens ;-)
			Timer disposeTimer = new Timer((int) remaining, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					forceDispose();
				}
			});
			disposeTimer.setRepeats(false);
			disposeTimer.start();
//System.out.println ("Window will be closed after "+remaining+" ms");
		} else {
			forceDispose();
		}
	}

	private void forceDispose() {
//		System.out.println ("Window disposed at "+System.currentTimeMillis());
		super.dispose();
	}
	
	/** Executes the task.
	 * <br>Starts the worker, and, after getDelay() ms, sets the dialog visible (if the task is not yet completed).
	 * <br>Once the task is complete, this frame is closed (if it is visible, the method ensure that this frame remains visible at least setMinimumVisibleTime ms).
	 * <br>If this frame is modal, the calling thread is blocked until the task completes (and this frame is hiden).
	 */
	public void execute() {
		if (!worker.getState().equals(StateValue.PENDING)) return;
		// Start the job task.
//		System.out.println ("execute at "+System.currentTimeMillis()+". Delay="+this.delay);
		worker.execute();
		if (!isVisible()) {
			if (delay>0) { // If the window display should be delayed
				if (getModalityType().equals(ModalityType.MODELESS)) {
					// If the dialog is not modal, then create a timer to show the window and returns immediately 
					this.timer = new Timer(delay, new ActionListener() {
						public void actionPerformed(ActionEvent e) {
	//		 System.out.println ("Timer expired at "+System.currentTimeMillis());
							showIt();
						}
					});
					timer.setRepeats(false);
					timer.start();
	//				System.out.println ("Timer is set to "+delay);
				} else {
					// If the dialog is modal wait until the delay is expired or the task is completed (the method should not return immediately to conform with modal dialogs behavior
					try {
						synchronized (worker) {
							worker.wait(delay);
						}
//						System.out.println ("Wait expired at "+System.currentTimeMillis());
						showIt();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				showIt();
			}
		}
	}
	
	private synchronized void showIt() {
		setVisibleTime = System.currentTimeMillis(); // Remember when the dialog was displayed
//		System.out.println ("setVisible(true) at "+setVisibleTime+". Minimum="+this.minimumVisibleTime);
		if (!isVisible() && !worker.isFinished()) super.setVisible(true);
	}
	
	protected Worker<?,?> getWorker() {
		return this.worker;
	}

//	/** Sets the auto-dispose attribute.
//	 * <br>This attribute is true by default.
//	 * The auto-dispose set to true means that the window will be disposed when the worker will be completed.
//	 * @param auto true to have the window automatically closed when the worker completes.
//	 */
//	public void setAutoDispose(boolean auto) {
//		this.autoDispose = auto;
//	}

//	/** Sets a new worker.
//	 * <br>Used with setAutoDispose(false), this allow to chain workers in the same WorkInProgressFrame.
//	 * <br>The method launches the new worker.
//	 * @param worker The new worker
//	 * @throws IllegalStateException if the current worker is not done.
//	 */
//	private void setWorker(Worker<?, ?> worker) {
//		synchronized (this) {
//		}
//	}
}
