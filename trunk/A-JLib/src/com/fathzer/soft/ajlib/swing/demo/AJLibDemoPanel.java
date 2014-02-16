package com.fathzer.soft.ajlib.swing.demo;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
public class AJLibDemoPanel extends JPanel {

	private JLabel messageLabel;

	/**
	 * Create the panel.
	 */
	public AJLibDemoPanel() {

		initialize();
	}
	private void initialize() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		setLayout(gridBagLayout);
		
		JTabbedPane panel = new JTabbedPane();
		GridBagConstraints gbcPanel = new GridBagConstraints();
		gbcPanel.weightx = 1.0;
		gbcPanel.weighty = 1.0;
		gbcPanel.fill = GridBagConstraints.BOTH;
		gbcPanel.gridx = 0;
		gbcPanel.gridy = 0;
		add(panel, gbcPanel);
		
		panel.add("widget", new WidgetsDemoPanel());
		
		panel.add("widget.date", new DateDemoPanel());
		
		panel.add("rotating label", new RotatingLabelPanel());

		panel.add("worker", new WorkerDemoPanel());

		panel.add("dialog", new DialogsPanel());

		messageLabel = new JLabel(" ");
		GridBagConstraints gbcMessageLabel = new GridBagConstraints();
		gbcMessageLabel.insets = new Insets(5, 5, 0, 5);
		gbcMessageLabel.weightx = 1.0;
		gbcMessageLabel.fill = GridBagConstraints.HORIZONTAL;
		gbcMessageLabel.anchor = GridBagConstraints.NORTHWEST;
		gbcMessageLabel.gridx = 0;
		gbcMessageLabel.gridy = 1;
		add(messageLabel, gbcMessageLabel);
	}

	public void setMessage(String text) {
		messageLabel.setText(text);
	}
}
