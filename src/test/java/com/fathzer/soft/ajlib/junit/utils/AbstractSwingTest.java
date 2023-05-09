package com.fathzer.soft.ajlib.junit.utils;

import java.awt.AWTException;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;

import javax.swing.JFrame;

import org.junit.BeforeClass;

public abstract class AbstractSwingTest {
	protected static Robot robot;

	@BeforeClass
	public static void init() throws AWTException {
		if (!GraphicsEnvironment.isHeadless()) {
			robot = new Robot();
		}
	}
	
	protected static abstract class AbstractSwingWindow {
		private Throwable exception;
		
		public void initTest() throws InterruptedException, Throwable {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					build();
				}
			});
			synchronized (this) {
				wait();
			}
		    if (exception!=null) {
		    	throw exception;
		    }
		}

		private void build() {
			try {
				init();
			} catch (Throwable e) {
				exception = e;
			}
			synchronized (this) {
				notifyAll();
			}
		}
		
		private void init() {
			JFrame f = new JFrame();
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.setLocationRelativeTo(null);
			f.setLayout(new FlowLayout());
			populate(f);
			f.setVisible(true);
		}

		protected abstract void populate(JFrame f);
	}
}