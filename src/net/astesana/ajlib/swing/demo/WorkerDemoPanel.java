package net.astesana.ajlib.swing.demo;

import java.awt.Dialog.ModalityType;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.astesana.ajlib.swing.Utils;
import net.astesana.ajlib.swing.worker.WorkInProgressFrame;
import net.astesana.ajlib.swing.worker.Worker;
import java.awt.GridBagConstraints;

@SuppressWarnings("serial")
public class WorkerDemoPanel extends JPanel {
	private JButton btnStartANew_1;
		
	/**
	 * Create the panel.
	 */
	public WorkerDemoPanel() {
		initialize();
	}
	
	private void initialize() {
		setLayout(new GridBagLayout());
		JButton btnStartANew = getBtnStartANew();
		GridBagConstraints gbc_btnStartANew = new GridBagConstraints();
		gbc_btnStartANew.gridy = 0;
		gbc_btnStartANew.gridx = 0;
		add(btnStartANew, gbc_btnStartANew);
		GridBagConstraints gbc_btnStartANew_1 = new GridBagConstraints();
		gbc_btnStartANew_1.gridy = 1;
		gbc_btnStartANew_1.gridx = 0;
		add(getBtnStartANew_1(), gbc_btnStartANew_1);
		
		JButton btnNewButton = new JButton("Start a debug task (no phase name)");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WorkerSample worker = new AnonymousWorker();
				WorkInProgressFrame jobFrame = new WorkInProgressFrame(worker);
				jobFrame.setTitle("Anonymous task n°"+worker.taskNumber);
				jobFrame.setSize(300, jobFrame.getSize().height);
				Utils.centerWindow(jobFrame, Utils.getOwnerWindow(WorkerDemoPanel.this));
				jobFrame.setVisible(true);
			}
		});
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.anchor = GridBagConstraints.NORTH;
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 2;
		add(btnNewButton, gbc_btnNewButton);
		btnNewButton.setVisible(false);
	}
	
	private static class AnonymousWorker extends WorkerSample {
		@Override
		protected Void doInBackground() throws Exception {
			// First ... forget to define the phase
			Thread.sleep(2000);
			// Then, define a phase length ... but no name
			setPhase(null, 1000);
			for (int i = 0; i < 1000; i++) {
				Thread.sleep(2);
				reportProgress(i);
			}
			return null;
		}
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
			setPhase("Other may have a defined length", nb);
			for (int i=0;i<nb;i++) {
				Thread.sleep(100);
				reportProgress(i);
				if (isCancelled()) return null;
			}
			return null;
		}

		@Override
		protected void done() {
			AJLibDemo.setMessage("Task n°"+taskNumber+" is finished");
		}
	}
	
	private JButton getBtnStartANew() {
		JButton btnStartANew = new JButton("Start a new background task");
		btnStartANew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				WorkerSample worker = new WorkerSample();
				WorkInProgressFrame jobFrame = new WorkInProgressFrame(worker);
				jobFrame.setTitle("task n°"+worker.taskNumber);
				jobFrame.setSize(300, jobFrame.getSize().height);
				Utils.centerWindow(jobFrame, Utils.getOwnerWindow(WorkerDemoPanel.this));
				jobFrame.setVisible(true);
			}
		});
		return btnStartANew;
	}
	
	private JButton getBtnStartANew_1() {
		if (btnStartANew_1 == null) {
			btnStartANew_1 = new JButton("Start a new modal background task");
			btnStartANew_1.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					WorkerSample worker = new WorkerSample();
					WorkInProgressFrame jobFrame = new WorkInProgressFrame(Utils.getOwnerWindow(WorkerDemoPanel.this), "task n°"+worker.taskNumber, ModalityType.APPLICATION_MODAL, worker);
					jobFrame.setSize(300, jobFrame.getSize().height);
					Utils.centerWindow(jobFrame, Utils.getOwnerWindow(WorkerDemoPanel.this));
					jobFrame.setVisible(true);
				}
			});
		}
		return btnStartANew_1;
	}
}
