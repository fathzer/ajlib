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
//		JButton btnStartChained = getBtnChained();
//		GridBagConstraints gbc_btnStartChained = new GridBagConstraints();
//		gbc_btnStartChained.anchor = GridBagConstraints.NORTH;
//		gbc_btnStartChained.gridx = 0;
//		gbc_btnStartChained.gridy = 2;
//		add(btnStartChained, gbc_btnStartChained);
	}

//	private JButton getBtnChained() {
//		JButton btnStartChained = new JButton("Start many sequential tasks in one JobFrame");
//		btnStartChained.addActionListener(new ActionListener() {
//			private  WorkInProgressFrame jobFrame;
//			public void actionPerformed(ActionEvent e) {
////				final Worker<Void, Void> worker = new AnonymousWorker("First phase");
//				final Worker<Void, Void> worker = new Worker<Void, Void>() {
//					@Override
//					protected Void doProcessing() throws Exception {
//						System.out.println ("end of background task at "+System.currentTimeMillis());
//						return null;
//					}
//				};
//				jobFrame = new WorkInProgressFrame(Utils.getOwnerWindow(WorkerDemoPanel.this), "Chained tasks", ModalityType.APPLICATION_MODAL, worker);
////				jobFrame = new WorkInProgressFrame(Utils.getOwnerWindow(WorkerDemoPanel.this), "Chained tasks", ModalityType.APPLICATION_MODAL, null);
//				jobFrame.setSize(300, jobFrame.getSize().height);
//				jobFrame.setAutoDispose(false);
//				jobFrame.setDelay(1000);
//				worker.addPropertyChangeListener(new PropertyChangeListener() {
//					@Override
//					public void propertyChange(PropertyChangeEvent evt) {
//						if (Worker.STATE_PROPERTY_NAME.equals(evt.getPropertyName())) {
//							if (StateValue.DONE.equals(evt.getNewValue())) {
//								if (worker.isCancelled()) {
//									jobFrame.dispose();
//									return;
//								}
//								String[] phases = new String[]{"Second phase","Another phase"};
//								Component comp = jobFrame.isVisible()?jobFrame:WorkerDemoPanel.this;
//								int answer = JOptionPane.showOptionDialog(comp, "<html>What phase should I execute?</html>", "First phase done",
//										JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, phases, phases[0]);
//								if (answer>=0) {
//									doSecondPhase(phases[answer]);
//								} else {
//									jobFrame.dispose();
//								}
//							}
//						}
//					}
//				});
//				jobFrame.setVisible(true);
//			}
//			
//			private void doSecondPhase(String title) {
//				Worker<Void, Void> worker = new AnonymousWorker(title);
//				worker.addPropertyChangeListener(new PropertyChangeListener() {
//					@Override
//					public void propertyChange(PropertyChangeEvent evt) {
//						if (Worker.STATE_PROPERTY_NAME.equals(evt.getPropertyName())) {
//							if (StateValue.DONE.equals(evt.getNewValue())) {
//								jobFrame.dispose();
//							}
//						}
//					}
//				});
//				jobFrame.setWorker(worker);
//				jobFrame.execute();
//			}
//		});
//		return btnStartChained;
//	}
	
//	private static class AnonymousWorker extends Worker<Void, Void> {
//		private String title;
//		private AnonymousWorker(String title) {
//			this.title = title;
//		}
//		@Override
//		protected Void doProcessing() throws Exception {
//			// Then, define a phase length ... but no name
//			setPhase(title, 1000);
//			for (int i = 0; i < 1000; i++) {
//				Thread.sleep(2);
//				reportProgress(i);
//			}
//			return null;
//		}
//	}

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
				if (isCancelled()) return null;
			}
			setPhase("Some may not have a fixed length", -1);
			for (int i=0;i<30;i++) {
				Thread.sleep(50);
				if (isCancelled()) return null;
			}
			int nb = 30;
			setPhase("Other may have a defined length", nb);
			for (int i=0;i<nb;i++) {
				Thread.sleep(50);
				reportProgress(i);
				if (isCancelled()) return null;
			}
			return null;
		}

		@Override
		protected void done() {
			AJLibDemo.setMessage("Task n°"+taskNumber+" is finished");
		}

		@Override
		protected void setPhase(String phase, int phaseLength) {
//			System.out.println (phase+" -> "+phaseLength);
			super.setPhase(phase, phaseLength);
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
	
	private JButton getBtnStartANew_1() {
		if (btnStartANew_1 == null) {
			btnStartANew_1 = new JButton("Start a new modal background task");
			btnStartANew_1.addActionListener(new ActionListener() {
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
		return btnStartANew_1;
	}
}
