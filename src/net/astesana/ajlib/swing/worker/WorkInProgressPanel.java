package net.astesana.ajlib.swing.worker;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class WorkInProgressPanel extends JPanel {
	public abstract Worker<?, ?> getWorker();
	public abstract void setSwingWorker(Worker<?, ?> worker);
}