package net.astesana.ajlib.swing.worker;

import javax.swing.SwingWorker;

import net.astesana.ajlib.utilities.NullUtils;

public abstract class Worker<T,V> extends SwingWorker<T,V> {
	/** The state property name of the SwingWorker (Should have been defined in SwingWorker by Oracle ?) */
	public static String STATE_PROPERTY_NAME = "state"; //$NON-NLS-1$
	/** The progress property name of the SwingWorker (Should have been defined in SwingWorker by Oracle ?) */
	public static String PROGRESS_PROPERTY_NAME = "progress"; //$NON-NLS-1$
	public static final String JOB_PHASE = "phase"; //$NON-NLS-1$
	
	private String phase;
	private int phaseLength;

	public Worker() {
		super();
		this.phase = null;
		this.phaseLength = -1;
	}
	
	protected void setPhase(String phase, int phaseLength) {
		if (!NullUtils.areEquals(this.phase, phase)) {
			String old = this.phase;
			this.phase = phase;
			this.phaseLength = phaseLength;
			this.firePropertyChange(JOB_PHASE, old, phase);
		}
	}
	
	public int getPhaseLength() {
		return phaseLength;
	}

	public void reportProgress(int progress) {
		if (this.phaseLength<0) throw new IllegalArgumentException();
		if (progress>phaseLength) throw new IllegalArgumentException();
		long percent = (progress*100)/phaseLength;
		super.setProgress((int)percent);
	}
}
