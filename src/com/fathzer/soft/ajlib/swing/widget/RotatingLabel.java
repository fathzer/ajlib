package com.fathzer.soft.ajlib.swing.widget;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.JLabel;

/** A JLabel that can be rotated.
 * @author Jean-Marc Astesana
 * <BR>License: LGPL v3
 */
public class RotatingLabel extends JLabel {
	private static final long serialVersionUID = 1L;

	private int angle;
	private boolean drawing;

	/** Constructor.
	 * <br>By default, the label contains no text, no icon and is not rotated.
	 */
	public RotatingLabel() {
		this.angle = 0;
	}
	
	/** Sets the angle of this label.
	 * @param angleDegrees The angle in degrees, between -179 and 180 degrees.
	 * @throws IllegalArgumentException if angle in not in range -179 to 180.
	 * @throws UnsupportedOperationException In the current version, only -90, 0, 90 and 180 are accepted. Other values throw this exception
	 */
	public void setRotation(int angleDegrees) {
		if (angleDegrees<-179 || angleDegrees>180) {
			throw new IllegalArgumentException();
		}
		if (angleDegrees!=-90 && angleDegrees!=0 && angleDegrees!=90 && angleDegrees!=180) {
			throw new UnsupportedOperationException(); 
		}
		angle = angleDegrees;
		revalidate();
		repaint();
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension preferredSize = super.getPreferredSize();
		double rad = Math.toRadians(angle);
		int w = (int) (Math.abs(preferredSize.width*Math.cos(rad)) + Math.abs(preferredSize.height*Math.sin(rad)));
		int h = (int) (Math.abs(preferredSize.width*Math.sin(rad)) + Math.abs(preferredSize.height*Math.cos(rad)));
		Dimension result = new Dimension(w, h);
//		System.out.println ("Angle: "+angle+", original pref size: "+preferredSize+" ,rotated: "+result);
		return result;
	}

	@Override
	public Dimension getSize() {
		Dimension size = super.getSize();
		if (!drawing) {
			return size;
		} else {
			double rad = Math.toRadians(angle);
			int w = (int) (Math.abs(size.width*Math.cos(rad)) + Math.abs(size.height*Math.sin(rad)));
			int h = (int) (Math.abs(size.width*Math.sin(rad)) + Math.abs(size.height*Math.cos(rad)));
			return new Dimension(w, h);
		}
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
		
		if (angle==90) {
			gr.transform(AffineTransform.getQuadrantRotateInstance(1));
			gr.translate(0, -getSize().getWidth());
		} else if (angle==-90) {
			gr.translate(0, getSize().getHeight());
			gr.transform(AffineTransform.getQuadrantRotateInstance(-1));
		} else if (angle==180) {
			gr.transform(AffineTransform.getQuadrantRotateInstance(2, (double)getWidth()/2, (double)getHeight()/2));
		} else if (angle!=0) {
			gr.transform(AffineTransform.getRotateInstance(Math.toRadians(angle), (double)getWidth()/2, (double)getHeight()/2));
//			gr.translate(Math.cos(angle)*getSize().getWidth(), Math.sin(angle)*getSize().getHeight());
		}

		this.drawing = true;
		super.paintComponent(gr);
		this.drawing = false;
	}
}
