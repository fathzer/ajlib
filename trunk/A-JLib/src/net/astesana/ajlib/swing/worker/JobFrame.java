package net.astesana.ajlib.swing.worker;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

@SuppressWarnings("serial")
public class JobFrame extends JFrame {
	private JPanel contentPane;
	private WorkInProgressPanel progressPanel;

	/**
	 * Create the frame.
	 */
	public JobFrame(final SwingWorkerJobAdapter<?, ?> swingAdapter) {
		super();
		swingAdapter.setJobFrame(this);
		setMinimumSize(new Dimension(320, 0));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				SwingWorker<?, ?> worker = progressPanel.getWorker();
				if (!worker.isDone()) worker.cancel(false);
			}
		});
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		contentPane.setLayout(gbl_contentPane);
		
		progressPanel = new WorkInProgressPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.weightx = 1.0;
		gbc_panel.insets = new Insets(5, 5, 5, 5);
		gbc_panel.gridwidth = 0;
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		contentPane.add(progressPanel, gbc_panel);
		
		progressPanel.setSwingWorker(swingAdapter);
		
		swingAdapter.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(SwingWorkerJobAdapter.STATE_PROPERTY_NAME)) {
					if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
						if (swingAdapter.isCancelled()) {
							JobFrame.this.dispose();
						}
					}
				}
			}
		});
		pack();
		swingAdapter.execute();
	}
}
