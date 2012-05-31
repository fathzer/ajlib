package net.astesana.ajlib.swing.worker;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.SwingWorker.StateValue;
import javax.swing.border.EmptyBorder;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;
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
 * <br><br>By default, when the user clicks the frame close box, it cancels the task and disposes the window.
 * If you set the frame default close operation to "do nothing" (with <code>this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE)</code>)
 * the close box does nothing at all. You can then listen to window closing event in order to do want you want.
 * @author Jean-Marc Astesana
 * <BR>License : GPL v3
 * @see Worker
 */
@SuppressWarnings("serial")
public class WorkInProgressFrame extends JDialog {
	public static final int DEFAULT_DELAY = 500;
	public static final int DEFAULT_MINIMUM_TIME_VISIBLE = 1000;

	private JPanel contentPane;
	private WorkInProgressPanel progressPanel;
	
	private long setVisibleTime;
	private int minimumVisibleTime;
	private int delay;
	private Timer timer;
	private Worker<?, ?> worker;

	/**
	 * Constructor.
	 * <br>Default arguments : owner is null, title is an empty String and modality is ModalityType.MODELESS.
	 * @param worker The worker that will run in background.
	 * @see #WorkInProgressFrame(Window, String, ModalityType, Worker)
	 */
	public WorkInProgressFrame(Worker<?, ?> worker) {
		this(null,"",ModalityType.MODELESS, worker);
	}
	
	/**
	 * Constructor.
	 * @param owner The owner of the dialog
	 * @param title The title of the dialog.
	 * @param modality The modality of the dialog
	 * @param worker The worker that will be executed in background.
	 */
	public WorkInProgressFrame(Window owner, String title, ModalityType modality, Worker<?,?> worker) {
		super(owner, title, modality);
		this.worker = worker;
		this.delay = DEFAULT_DELAY;
		this.minimumVisibleTime = DEFAULT_MINIMUM_TIME_VISIBLE;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				if (getDefaultCloseOperation()!=JFrame.DO_NOTHING_ON_CLOSE) {
					// Cancel the task if the window is closing
					SwingWorker<?, ?> worker = progressPanel.getWorker();
					if (!worker.isDone()) worker.cancel(false);
				}
			}
		});
		
		buildContentPane();

		worker.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(Worker.STATE_PROPERTY_NAME)) {
					if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
						if (timer!=null) {
							timer.stop();
//System.out.println ("Timer is stopped at "+System.currentTimeMillis());
						}
						// remaining will contain the visibility remaining time (to satisfied the minimumVisibleTime attribute).
						// If the task was cancelled, we assume that the user has cancelled the dialog ... so, minimumVisibleTime has no reason to be satisfied
						long remaining = WorkInProgressFrame.this.worker.isCancelled()?0:minimumVisibleTime-(System.currentTimeMillis()-setVisibleTime);
						if (remaining>0) { // If the dialog is displayed for less than the minimum visible time ms, wait for the user to see what happens ;-)
							Timer disposeTimer = new Timer((int) remaining, new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									WorkInProgressFrame.this.dispose();
								}
							});
							disposeTimer.setRepeats(false);
							disposeTimer.start();
//System.out.println ("Window will be closed after "+remaining+" ms");
						} else {
							WorkInProgressFrame.this.dispose();
						}
					}
				}
			}
		});
		pack();
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
	 * @see #buildProgressPanel()
	 */
	public final WorkInProgressPanel getWorkInProgressPanel() {
		if (progressPanel==null) {
			progressPanel = buildProgressPanel();
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
		if (visible && !isVisible() && (worker.getState().equals(StateValue.PENDING))) { // If the dialog is opened
			execute();
		}
	}
	
	/** Executes the task.
	 * <br>Starts the worker, and, after getDelay() ms, sets the dialog visible (if the task is not yet completed).
	 * <br>Once the task is complete, this frame is closed (if it visible, the method ensure that this frame remains visible at least setMinimumVisibleTime ms).
	 * <br>If this frame is modal, the calling thread is blocked until the task completes (and this frame is hiden).
	 */
	public void execute() {
		// Start the job task.
		worker.execute();
		// We will give the illusion that the window is not visible ... but it will be (to have the modal property of modal dialog preserved)
		// The magic is to display the window ... outside of the screen
		final Point location = getLocation();
		final Dimension size = getSize();
		setLocation(Integer.MAX_VALUE, Integer.MAX_VALUE);
		setSize(0,0);
		this.timer = new Timer(delay, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//System.out.println ("Timer expired at "+System.currentTimeMillis());
				setVisibleTime = System.currentTimeMillis(); // Remember when the dialog was displayed
				setLocation(location);
				setSize(size);
				timer = null;
			}
		});
		timer.setRepeats(false);
		timer.start();
		super.setVisible(true);
	}
}
