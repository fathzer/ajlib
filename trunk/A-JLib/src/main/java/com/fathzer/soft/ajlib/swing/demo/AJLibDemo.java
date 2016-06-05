package com.fathzer.soft.ajlib.swing.demo;

import javax.swing.JPanel;

import com.fathzer.soft.ajlib.swing.framework.Application;

/** This is a simple demonstration application. */
public class AJLibDemo extends Application {
	public static final AJLibDemo DEMO = new AJLibDemo();
	
	private AJLibDemoPanel demoPanel;
	
	private AJLibDemo() {
	}
	
	@Override
	public String getName() {
		return "AJLibDemo";
	}

	@Override
	protected JPanel buildMainPanel() {
		if (demoPanel==null) {
			demoPanel = new AJLibDemoPanel();
		}
		return demoPanel;
	}

	/**
	 * @param args Program arguments
	 */
	public static void main(String[] args) {
		DEMO.launch();
	}
	
	public static void setMessage(String text) {
		DEMO.demoPanel.setMessage(text);
	}
}
