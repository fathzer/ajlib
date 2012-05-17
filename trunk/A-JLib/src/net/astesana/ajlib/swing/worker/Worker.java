package net.astesana.ajlib.swing.worker;

import javax.swing.SwingWorker;

import net.astesana.ajlib.utilities.NullUtils;

/** A Worker that defines phases that can have different lengths.
 * By default, a SwingWorker reports its progress with an integer between 0 and 100 (see method setProgress).
 * This class allows you to define any length then report the progress without having to convert it to percentage.
 * @author Jean-Marc Astesana
 * <BR>License : GPL v3
 * @see #reportProgress(int)
 */
public abstract class Worker<T,V> extends SwingWorker<T,V> {
	/** The state property name of the SwingWorker (Should have been defined in SwingWorker by Oracle ?) */
	public static String STATE_PROPERTY_NAME = "state"; //$NON-NLS-1$
	/** The progress property name of the SwingWorker (Should have been defined in SwingWorker by Oracle ?) */
	public static String PROGRESS_PROPERTY_NAME = "progress"; //$NON-NLS-1$
	public static final String JOB_PHASE = "phase"; //$NON-NLS-1$
	
	private String phase;
	private int phaseLength;

	/** Constructor.
	 *  <br>The phase is initialize to null and the length is set to -1 (indeterminate length).
	 */
	public Worker() {
		super();
		this.phase = null;
		this.phaseLength = -1;
	}
	
	/** Sets the phase.
	 * @param phase The phase title
	 * @param phaseLength The phase length (a negative number means the length is indeterminate).
	 */
	protected void setPhase(String phase, int phaseLength) {
		this.phaseLength = phaseLength;
		if (!NullUtils.areEquals(this.phase, phase)) {
			String old = this.phase;
			this.phase = phase;
			this.firePropertyChange(JOB_PHASE, old, phase);
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
		long percent = (progress*100)/phaseLength;
		super.setProgress((int)percent);
	}
}
