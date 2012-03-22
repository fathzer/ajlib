package net.astesana.ajlib.swing.demo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.astesana.ajlib.swing.Utils;
import net.astesana.ajlib.swing.worker.JobFrame;
import net.astesana.ajlib.swing.worker.Worker;

@SuppressWarnings("serial")
public class WorkerDemoPanel extends JPanel {
		
	/**
	 * Create the panel.
	 */
	public WorkerDemoPanel() {
		JButton btnStartANew = new JButton("Start a new background task");
		add(btnStartANew);
		btnStartANew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				WorkerSample worker = new WorkerSample();
				JobFrame jobFrame = new JobFrame(worker);
				jobFrame.setTitle("task n°"+worker.taskNumber);
				jobFrame.setSize(300, jobFrame.getSize().height);
				Utils.centerWindow(jobFrame, Utils.getOwnerWindow(WorkerDemoPanel.this));
				jobFrame.setVisible(true);
			}
		});
	}

	private static class WorkerSample extends Worker<Void, Void> {
		private static int globalTaskNumber;
		private int taskNumber;
		
		WorkerSample() {
			this.taskNumber = ++globalTaskNumber;
		}
		
		@Override
		protected Void doInBackground() throws Exception {
			setPhase("A task may define phases", -1);
			for (int i=0;i<30;i++) {
				Thread.sleep(100);
				if (isCancelled()) return null;
			}
			setPhase("Some may not have a fixed length", -1);
			for (int i=0;i<20;i++) {
				Thread.sleep(100);
				if (isCancelled()) return null;
			}
			int nb = 30;
			setPhase("Other may have defined length", nb);
			for (int i=0;i<nb;i++) {
				Thread.sleep(100);
				try {
					reportProgress(i);
					if (isCancelled()) return null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void done() {
			AJLibDemo.setMessage("Task n°"+taskNumber+" is finished");
		}
	}
}
