package com.fathzer.soft.ajlib.swing.demo;

import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.JSlider;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.fathzer.soft.ajlib.swing.widget.RotatingLabel;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

public class RotatingLabelPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private JPanel panel;
	private JSlider slider;
	private RotatingLabel westLabel;
	private RotatingLabel southLabel;
	private RotatingLabel northLabel;
	private RotatingLabel eastLabel;
	private RotatingLabel rotatingLabel;
	private JPanel panel_1;

	public RotatingLabelPanel() {
		initialize();
	}
	private void initialize() {
		setLayout(new BorderLayout(0, 0));
		add(getPanel(), BorderLayout.CENTER);
		add(getSlider(), BorderLayout.SOUTH);
	}

	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new BorderLayout(0, 0));
			panel.add(getWestLabel(), BorderLayout.WEST);
			panel.add(getSouthLabel(), BorderLayout.SOUTH);
			panel.add(getNorthLabel(), BorderLayout.NORTH);
			panel.add(getEastLabel(), BorderLayout.EAST);
			panel.add(getPanel_1(), BorderLayout.CENTER);
		}
		return panel;
	}
	private JSlider getSlider() {
		if (slider == null) {
			slider = new JSlider();
			slider.setValue(0);
			slider.setMinimum(-179);
			slider.setMaximum(180);
			slider.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					try {
						getRotatingLabel().setRotation(slider.getValue());
					} catch (UnsupportedOperationException e1) {
						System.out.println ("angle not supported "+slider.getValue()); //TODO
					}
				}
			});
		}
		return slider;
	}
	private JLabel getWestLabel() {
		if (westLabel == null) {
			westLabel = new RotatingLabel();
			westLabel.setText("-90\u00B0");
			westLabel.setRotation(-90);
			westLabel.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return westLabel;
	}
	private JLabel getSouthLabel() {
		if (southLabel == null) {
			southLabel = new RotatingLabel();
			southLabel.setText("180\u00B0");
			southLabel.setRotation(180);
			southLabel.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return southLabel;
	}
	private JLabel getNorthLabel() {
		if (northLabel == null) {
			northLabel = new RotatingLabel();
			northLabel.setText("No rotation");
			northLabel.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return northLabel;
	}
	private JLabel getEastLabel() {
		if (eastLabel == null) {
			eastLabel = new RotatingLabel();
			eastLabel.setText("90\u00B0");
			eastLabel.setRotation(90);
			eastLabel.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return eastLabel;
	}
	private RotatingLabel getRotatingLabel() {
		if (rotatingLabel == null) {
			rotatingLabel = new RotatingLabel();
			rotatingLabel.setText("Rotate me with the slider below");
			rotatingLabel.setBackground(Color.ORANGE);
			rotatingLabel.setOpaque(true);
			rotatingLabel.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return rotatingLabel;
	}
	private JPanel getPanel_1() {
		if (panel_1 == null) {
			panel_1 = new JPanel();
			GridBagLayout gbl_panel_1 = new GridBagLayout();
			panel_1.setLayout(gbl_panel_1);
			GridBagConstraints gbcRotatingLabel = new GridBagConstraints();
			gbcRotatingLabel.weighty = 1.0;
			gbcRotatingLabel.gridx = 1;
			gbcRotatingLabel.gridy = 0;
			panel_1.add(getRotatingLabel(), gbcRotatingLabel);
		}
		return panel_1;
	}
}
