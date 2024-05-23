package com.fathzer.soft.ajlib.swing.demo;

import java.awt.Dialog.ModalityType;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.fathzer.soft.ajlib.swing.Utils;
import com.fathzer.soft.ajlib.swing.worker.WorkInProgressFrame;
import com.fathzer.soft.ajlib.swing.worker.Worker;

import java.awt.GridBagConstraints;

@SuppressWarnings("serial")
public class WorkerDemoPanel extends JPanel {
	private JButton btnStartANew1;
		
	/**
	 * Create the panel.
	 */
	public WorkerDemoPanel() {
		initialize();
	}
	
	private void initialize() {
		setLayout(new GridBagLayout());
		JButton btnStartANew = getBtnStartANew();
		GridBagConstraints gbcBtnStartANew = new GridBagConstraints();
		gbcBtnStartANew.gridy = 0;
		gbcBtnStartANew.gridx = 0;
		add(btnStartANew, gbcBtnStartANew);
		GridBagConstraints gbcBtnStartANew1 = new GridBagConstraints();
		gbcBtnStartANew1.gridy = 1;
		gbcBtnStartANew1.gridx = 0;
		add(getBtnStartANew1(), gbcBtnStartANew1);
	}

	private static class WorkerSample extends Worker<Void, Void> {
		private static int globalTaskNumber;
		private int taskNumber;
		
		WorkerSample() {
			this.taskNumber = ++globalTaskNumber;
		}
		
		@Override
		protected Void doProcessing() throws Exception {
			setPhase("A task may define phases", -1);
			for (int i=0;i<40;i++) {
				Thread.sleep(50);
				if (isCancelled()) {
					return null;
				}
			}
			setPhase("Some may not have a fixed length", -1);
			for (int i=0;i<30;i++) {
				Thread.sleep(50);
				if (isCancelled()) {
					return null;
				}
			}
			int nb = 30;
			setPhase("Other may have a defined length", nb);
			for (int i=0;i<nb;i++) {
				Thread.sleep(50);
				reportProgress(i);
				if (isCancelled()) {
					return null;
				}
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
				WorkInProgressFrame jobFrame = new WorkInProgressFrame(Utils.getOwnerWindow(WorkerDemoPanel.this), "task n°"+worker.taskNumber, ModalityType.MODELESS, worker);
				jobFrame.setSize(300, jobFrame.getSize().height);
				jobFrame.setLocationRelativeTo(Utils.getOwnerWindow(WorkerDemoPanel.this));
				jobFrame.setVisible(true);
			}
		});
		return btnStartANew;
	}
	
	private JButton getBtnStartANew1() {
		if (btnStartANew1 == null) {
			btnStartANew1 = new JButton("Start a new modal background task");
			btnStartANew1.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					WorkerSample worker = new WorkerSample();
					WorkInProgressFrame jobFrame = new WorkInProgressFrame(Utils.getOwnerWindow(WorkerDemoPanel.this), "task n°"+worker.taskNumber, ModalityType.APPLICATION_MODAL, worker);
					jobFrame.setSize(300, jobFrame.getSize().height);
					jobFrame.setLocationRelativeTo(Utils.getOwnerWindow(WorkerDemoPanel.this));
					jobFrame.setMinimumVisibleTime(1500);
					jobFrame.setVisible(true);
				}
			});
		}
		return btnStartANew1;
	}
}
