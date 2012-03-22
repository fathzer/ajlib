package net.astesana.ajlib.swing.demo;

import javax.swing.JPanel;

import net.astesana.ajlib.swing.framework.Application;

/** This is a simple demonstration application. */
public class AJLibDemo extends Application {
	public static final AJLibDemo DEMO = new AJLibDemo();
	
	private AJLibDemoPanel demoPanel;
	
	private AJLibDemo() {
		demoPanel = new AJLibDemoPanel();
	}
	
	@Override
	public String getName() {
		return "AJLibDemo";
	}

	@Override
	protected JPanel buildMainPanel() {
		return demoPanel;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DEMO.launch();
	}
	
	public static void setMessage(String text) {
		DEMO.demoPanel.setMessage(text);
	}
}
