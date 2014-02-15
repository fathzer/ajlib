package com.fathzer.soft.ajlib.swing.widget;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.JLabel;

public class RotatingLabel extends JLabel {
	private static final long serialVersionUID = 1L;

	public static enum Direction {
		CLOCKWISE,
		COUNTERCLOCKWISE,
		UPSIDE_DOWN
	}

	private Direction direction;
	private boolean drawing;

	public RotatingLabel(Direction direction) {
		this.direction = direction;
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension preferredSize = super.getPreferredSize();
		return direction==Direction.UPSIDE_DOWN ? preferredSize : new Dimension(preferredSize.height, preferredSize.width);
	}

	@Override
	public Dimension getSize() {
		Dimension size = super.getSize();
		return drawing && direction!=Direction.UPSIDE_DOWN ? new Dimension(size.height, size.width) : size;
	}

	@Override
	public int getHeight() {
		return getSize().height;
	}

	@Override
	public int getWidth() {
		return getSize().width;
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D gr = (Graphics2D) g.create();

		if (direction == Direction.CLOCKWISE) {
			gr.transform(AffineTransform.getQuadrantRotateInstance(1));
			gr.translate(0, -getSize().getWidth());
		} else if (direction == Direction.COUNTERCLOCKWISE) {
			gr.translate(0, getSize().getHeight());
			gr.transform(AffineTransform.getQuadrantRotateInstance(-1));
		} else if (direction == Direction.UPSIDE_DOWN) {
			gr.transform(AffineTransform.getQuadrantRotateInstance(2, (double)getWidth()/2, (double)getHeight()/2));
		}

		this.drawing = true;
		super.paintComponent(gr);
		this.drawing = false;
	}
}
