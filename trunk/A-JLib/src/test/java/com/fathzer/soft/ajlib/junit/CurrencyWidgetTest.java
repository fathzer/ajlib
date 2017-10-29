package com.fathzer.soft.ajlib.junit;

import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Test;

import com.fathzer.soft.ajlib.swing.widget.CurrencyWidget;

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
}
