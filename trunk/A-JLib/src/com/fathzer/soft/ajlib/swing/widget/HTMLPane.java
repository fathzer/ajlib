package com.fathzer.soft.ajlib.swing.widget;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.fathzer.soft.ajlib.swing.Browser;
import com.fathzer.soft.ajlib.swing.framework.Application;


/** A HTML component.
 * @author Jean-Marc Astesana
 * <BR>License: LGPL v3
 */
@SuppressWarnings("serial")
public class HTMLPane extends JScrollPane {
	private static final String HTML_START_TAG = "<html>"; //$NON-NLS-1$
	private static final String HTML_END_TAG = "</html>"; //$NON-NLS-1$
	private JTextPane textPane;
	private String contentType;

	/** Constructor.
	 */
	public HTMLPane () {
		super();
		this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.contentType = null;
		textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType()==HyperlinkEvent.EventType.ACTIVATED) {
					URL url = e.getURL();
					try {
						Browser.show(url.toURI(), HTMLPane.this, Application.getString("Generic.error", getLocale())); //$NON-NLS-1$
					} catch (URISyntaxException e2) {
						throw new RuntimeException();
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

	/** Sets the panel content.
	 * <br>If this panel content type is not set, the content type is determined by the content of the text.
	 * If it starts with "&lt;html&gt;" and ends with "&lt;/html&gt;", the panel assumes the content type is "text/html".
	 * <br>If not, the content type is assumed to "text/plain".
	 * <br>If the content type is set, this content type is used. It allows you to display an html code source,
	 * by invoking this.setContentType("text/plain") before calling this method.  
	 * @param text The text to be set.
	 * @see #setContentType(String)
	 */
	public void setContent (String text) {
		String type;
		if (this.contentType==null) {
			String trimmed = text.trim();
			boolean html = trimmed.length()>=HTML_START_TAG.length()+HTML_END_TAG.length();
			if (html) html = HTML_START_TAG.equalsIgnoreCase(trimmed.substring(0, HTML_START_TAG.length()));
			if (html) html = HTML_END_TAG.equalsIgnoreCase(trimmed.substring(trimmed.length() - HTML_END_TAG.length()));
			type = html?"text/html":"text/plain"; //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			type = this.contentType;
		}
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
	
	/** Gets the internal text pane. */
	public JTextPane getTextPane() {
		return this.textPane;
	}

	/** Sets the content type.
	 * <br>Please note that this method as no effect on the current content. It should only have an effect on future calls to setContent.
	 * @param contentType A content type or null for the content type to be automatically set when calling set content.
	 * @see #setContent(String)
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
		if (contentType!=null) getTextPane().setContentType(contentType);
	}
}