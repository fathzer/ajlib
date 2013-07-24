package net.astesana.ajlib.junit;

import static org.junit.Assert.*;

import java.util.Locale;

import net.astesana.ajlib.utilities.LocalizationData;

import org.junit.Test;

public class LocalizationDataTest {
	private static final String NAME = "name";

	@Test
	public void test() {
		LocalizationData loc = new LocalizationData("net.astesana.ajlib.junit.Resources");
		assertEquals (loc.getString(NAME, Locale.US), Locale.US.getLanguage());
		assertEquals (loc.getString(NAME, Locale.FRANCE), Locale.FRANCE.getLanguage());
	}

}
