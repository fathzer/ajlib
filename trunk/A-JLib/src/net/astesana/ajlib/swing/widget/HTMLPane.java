package net.astesana.ajlib.swing.widget;

import java.awt.Desktop;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/** A HTML component.
 * @author Jean-Marc Astesana
 * <BR>License : GPL v3
 */
@SuppressWarnings("serial")
public class HTMLPane extends JScrollPane {
	private static final String HTML_START_TAG = "<html>"; //$NON-NLS-1$
	private static final String HTML_END_TAG = "</html>"; //$NON-NLS-1$
	private JTextPane textPane;

	public HTMLPane () {
		super();
		this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType()==HyperlinkEvent.EventType.ACTIVATED) {
					URL url = e.getURL();
					try {
						Desktop.getDesktop().browse(url.toURI()); 
					} catch (IOException e1) {
						System.err.println("Attempted to read a bad URL: " + url);//FIXME These exceptions must be thrown //$NON-NLS-1$
					} catch (URISyntaxException e2) {
						e2.printStackTrace();
					}
				}
			}
		});
		this.setViewportView(textPane);
	}
	
	public HTMLPane (URL url) throws IOException {
		this();
		setContent(url);
	}

	public HTMLPane (String text) {
		this();
		setContent(text);
	}

	public void setContent (String text) {
		boolean html = text.length()>=HTML_START_TAG.length()+HTML_END_TAG.length();
		if (html) html = HTML_START_TAG.equalsIgnoreCase(text.substring(0, HTML_START_TAG.length()));
		if (html) html = HTML_END_TAG.equalsIgnoreCase(text.substring(text.length() - HTML_END_TAG.length()));
		String type = html?"text/html":"text/plain"; //$NON-NLS-1$ //$NON-NLS-2$
		textPane.setContentType(type);
		// We should not use textPane.setText because it scrolls the textPane to the end of the text
		try {
			textPane.read(new StringReader(text), type);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void setContent (URL url) throws IOException {
		if (url != null) {
			textPane.setPage(url);
		}
	}
	
	public JTextPane getTextPane() {
		return this.textPane;
	}
}