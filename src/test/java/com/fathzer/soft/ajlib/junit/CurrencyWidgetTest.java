package com.fathzer.soft.ajlib.junit;

import static org.junit.Assert.*;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import org.junit.Test;

import com.fathzer.soft.ajlib.swing.widget.CurrencyWidget;
import com.fathzer.soft.ajlib.swing.widget.NumberWidget;

public class CurrencyWidgetTest {
	@Test
	public void test() {
		CurrencyWidget w = new CurrencyWidget(Locale.FRANCE);
		double value = 5000.0;
		w.setValue(value);
		assertEquals("5 000,00 €", w.getText());
		assertEquals(value, w.getValue(), 0.001);

		value = -5000.0;
		w.setValue(value);
		assertEquals("-5 000,00 €", w.getText());
		assertEquals(value, w.getValue(), 0.001);
	}
	
	private static class MyWidget extends NumberWidget {
		private static final long serialVersionUID = 1L;

		@Override
		public DecimalFormat patchJavaBug4510618(DecimalFormat format) {
			return super.patchJavaBug4510618(format);
		}
	}
	
	@Test
	public void fuckingJavaDecimalFormatTest() {
		DecimalFormat format = (DecimalFormat)NumberFormat.getCurrencyInstance(Locale.FRENCH);
		final String x = format.format(5000.0).replace((char)0x202F, ' ').replace((char)0x00A0, ' ');
		
		format = new MyWidget().patchJavaBug4510618(format);
		assertEquals(x, format.format(5000.0));
	}
}
