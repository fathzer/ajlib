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
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.weightx = 1.0;
		gbc_panel.weighty = 1.0;
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		add(panel, gbc_panel);
		
		panel.add("widget", new WidgetsDemoPanel());
		
		panel.add("widget.date", new DateDemoPanel());
		
		panel.add("worker", new WorkerDemoPanel());

		panel.add("dialog", new DialogsPanel());

		messageLabel = new JLabel(" ");
		GridBagConstraints gbc_messageLabel = new GridBagConstraints();
		gbc_messageLabel.insets = new Insets(5, 5, 0, 5);
		gbc_messageLabel.weightx = 1.0;
		gbc_messageLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_messageLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_messageLabel.gridx = 0;
		gbc_messageLabel.gridy = 1;
		add(messageLabel, gbc_messageLabel);
	}

	public void setMessage(String text) {
		messageLabel.setText(text);
	}
}
