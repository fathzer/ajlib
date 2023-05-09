package com.fathzer.soft.ajlib.swing.worker;

import javax.swing.JPanel;

/** A panel reporting the progress of a background task.
 * @see Worker
 */
@SuppressWarnings("serial")
public abstract class WorkInProgressPanel extends JPanel {
	public abstract Worker<?, ?> getWorker();
	public abstract void setSwingWorker(Worker<?, ?> worker);
}