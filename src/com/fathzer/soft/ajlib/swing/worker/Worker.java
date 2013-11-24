package com.fathzer.soft.ajlib.swing.worker;

import javax.swing.SwingWorker;

import com.fathzer.soft.ajlib.utilities.NullUtils;


/** A SwingWorker that defines phases that can have different lengths.
 * <br>By default, a SwingWorker reports its progress with an integer between 0 and 100 (see method setProgress).
 * This class allows you to define any length then report the progress without having to convert it to percentage.
 * <br>Another enhancement is the Worker notifies all the threads waiting for him when it finished is processing.
 * To perform that, we were obliged to override the doInBackground method of SwingWorker and made it final.
 * Developers should see doProcessing method has the equivalent of the SwingWorker.doInBackground  
 * @param <T> the result type returned by this {@code SwingWorker's}
 *        {@code doInBackground} and {@code get} methods
 * @param <V> the type used for carrying out intermediate results by this
 *        {@code SwingWorker's} {@code publish} and {@code process} methods
 * @author Jean-Marc Astesana
 * <BR>License: LGPL v3
 * @see #reportProgress(int)
 */
public abstract class Worker<T,V> extends SwingWorker<T,V> {
	/** The state property name of the SwingWorker (Should have been defined in SwingWorker by Oracle ?) */
	public static final String STATE_PROPERTY_NAME = "state"; //$NON-NLS-1$
	/** The progress property name of the SwingWorker (Should have been defined in SwingWorker by Oracle ?) */
	public static final String PROGRESS_PROPERTY_NAME = "progress"; //$NON-NLS-1$
	/** The job phase property name */
	public static final String JOB_PHASE = "phase"; //$NON-NLS-1$
	/** The phase length property name */
	public static final String PHASE_LENGTH = "length"; //$NON-NLS-1$
	
	private String phase;
	private int phaseLength;
	private boolean isFinished;

	/** Constructor.
	 *  <br>The phase is initialize to null and the length is set to -1 (indeterminate length).
	 */
	public Worker() {
		super();
		this.phase = null;
		this.phaseLength = -1;
		this.isFinished = false;
	}
	
	/** Sets the phase.
	 * @param phase The phase title
	 * @param phaseLength The phase length (a negative number means the length is indeterminate).
	 */
	protected void setPhase(String phase, int phaseLength) {
		if (!NullUtils.areEquals(this.phase, phase)) {
			String old = this.phase;
			this.phase = phase;
			this.firePropertyChange(JOB_PHASE, old, phase);
		}
		setPhaseLength(phaseLength);
	}
	
	/** Sets the phase length.
	 * @param phaseLength The phase length (a negative number means the length is indeterminate)
	 */
	protected void setPhaseLength(int phaseLength) {
		if (phaseLength!=this.phaseLength) {
			int old = this.phaseLength;
			this.phaseLength = phaseLength;
			this.firePropertyChange(PHASE_LENGTH, old, phase);
		}
	}
	
	/** Gets the current phase's title.
	 * @return a String or null if the phase was not set.
	 */
	public String getPhase() {
		return phase;
	}
	
	/** Gets the current phase's length.
	 * @return an integer (negative if the phase length is indeterminate).
	 */
	public int getPhaseLength() {
		return phaseLength;
	}

	/** Sets the progress of the current phase.
	 * <br>You should use this method instead of the super class setProgress method.
	 * @param progress
	 */
	public void reportProgress(int progress) {
		if (this.phaseLength<0) throw new IllegalArgumentException();
		if (progress>phaseLength) throw new IllegalArgumentException();
		long percent = phaseLength==0?100:(progress*100)/phaseLength;
		super.setProgress((int)percent);
	}

	/** Overrides the SwingWorker doInBackground method to force the worker
	 * to notify all threads waiting on this object (by invoking this.wait()) when the
	 * processing is finished. 
	 */
	@Override
	protected final T doInBackground() throws Exception {
		T result = doProcessing();
		synchronized (this) {
			this.isFinished = true;
			this.notifyAll();
		}
		return result;
	}
	
	/** Performs the task processing and gets its result.
	 * <br>This method is called by the doInBackground method. 
	 * @return The task result
	 * @throws Exception if a error occurs.
	 */
	protected abstract T doProcessing() throws Exception;
	
	/** Tests whether the doProcessing method has ended.
	 * <br>This method should be preferred to isDone when testing if the task is done after being awake from a
	 * this.wait() method, because it is guaranteed that this method returns true after the waiting object is awaked
	 * (isDone may still returns false, depending on the threads dispatching).
	 * @return true if the method ended, false if it is still running.
	 */
	public boolean isFinished() {
		return this.isFinished;
	}
}
