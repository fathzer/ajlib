package net.astesana.ajlib.swing.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.Scrollable;

@SuppressWarnings("serial")
class DefaultScrollablePanel extends JPanel implements Scrollable {
	DefaultScrollablePanel(Component component) {
		super(new BorderLayout());
		this.add(component);
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return this.getComponent(0).getPreferredSize();
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 10;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 40;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return true;
	}
}
