package net.astesana.ajlib.swing.demo;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class AJLibDemoPanel extends JPanel {

	private JLabel messageLabel;

	/**
	 * Create the panel.
	 */
	public AJLibDemoPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0};
		gridBagLayout.columnWeights = new double[]{1.0};
		setLayout(gridBagLayout);
		
		WorkerDemoPanel workerPanel = new WorkerDemoPanel();
		workerPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "net.astesana.ajlib.swing.worker", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_workerPanel = new GridBagConstraints();
		gbc_workerPanel.insets = new Insets(0, 0, 5, 0);
		gbc_workerPanel.anchor = GridBagConstraints.WEST;
		gbc_workerPanel.gridx = 0;
		gbc_workerPanel.gridy = 0;
		add(workerPanel, gbc_workerPanel);
		
		DateDemoPanel datePanel = new DateDemoPanel();
		datePanel.setBorder(new TitledBorder(null, "net.astesana.ajlib.swing.widget.date", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_datePanel = new GridBagConstraints();
		gbc_datePanel.insets = new Insets(0, 0, 5, 0);
		gbc_datePanel.fill = GridBagConstraints.BOTH;
		gbc_datePanel.gridx = 0;
		gbc_datePanel.gridy = 1;
		add(datePanel, gbc_datePanel);
		
		messageLabel = new JLabel(" ");
		GridBagConstraints gbc_messageLabel = new GridBagConstraints();
		gbc_messageLabel.insets = new Insets(5, 5, 0, 0);
		gbc_messageLabel.weightx = 1.0;
		gbc_messageLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_messageLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_messageLabel.gridx = 0;
		gbc_messageLabel.gridy = 2;
		add(messageLabel, gbc_messageLabel);

	}

	public void setMessage(String text) {
		messageLabel.setText(text);
	}
}
