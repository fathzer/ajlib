package com.fathzer.soft.ajlib.junit;

import static org.junit.Assert.*;

import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;

import org.junit.Test;

import com.fathzer.soft.ajlib.junit.utils.AbstractSwingTest;
import com.fathzer.soft.ajlib.swing.widget.date.DateField;
import com.fathzer.soft.ajlib.swing.widget.date.DateWidget;

public class DateWidgetTest extends AbstractSwingTest {
	
	@Test
  public void test() throws Throwable {
		final DateWidgetFrame test = new DateWidgetFrame();
		test.initTest();
		assertTrue(test.widget.isContentValid());
		assertTrue(test.widget.getDateField().isContentValid());
		MyListener widgetListener = new MyListener();
		test.widget.addPropertyChangeListener(DateWidget.CONTENT_VALID_PROPERTY, widgetListener);
		MyListener fieldListener = new MyListener();
		test.widget.getDateField().addPropertyChangeListener(DateField.CONTENT_VALID_PROPERTY, fieldListener);
    robot.keyPress(KeyEvent.VK_X);
    robot.keyRelease(KeyEvent.VK_X);
    robot.delay(50);
    assertEquals("x", test.widget.getDateField().getText());
    assertFalse(test.widget.isContentValid());
    assertFalse(test.widget.getDateField().isContentValid());
    widgetListener.reset();
    fieldListener.reset();
    robot.keyPress(KeyEvent.VK_BACK_SPACE);
    robot.keyRelease(KeyEvent.VK_BACK_SPACE);
    robot.delay(50);
    assertTrue(test.widget.getDateField().getText().isEmpty());
    assertTrue(test.widget.isContentValid());
    assertTrue(fieldListener.changed);
    assertTrue(widgetListener.changed);
  }

	private static final class MyListener implements PropertyChangeListener {
		private boolean changed;

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			changed=true;
		}

		public void reset() {
			changed = false;
		}
	}

	private static class DateWidgetFrame extends AbstractSwingWindow {
		private DateWidget widget;
		
		protected void populate(JFrame f) {
			widget = new DateWidget();
			widget.setDate(null);
			f.add(widget);
			f.pack();
		}
	}
}