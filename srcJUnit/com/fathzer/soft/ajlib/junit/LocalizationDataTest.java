package com.fathzer.soft.ajlib.junit;

import static org.junit.Assert.*;

import java.util.Locale;


import org.junit.Test;

import com.fathzer.soft.ajlib.utilities.LocalizationData;

public class LocalizationDataTest {
	private static final String NAME = "name";

	@Test
	public void test() {
		LocalizationData loc = new LocalizationData("com.fathzer.soft.ajlib.junit.Resources");
		assertEquals (loc.getString(NAME, Locale.US), Locale.US.getLanguage());
		assertEquals (loc.getString(NAME, Locale.FRANCE), Locale.FRANCE.getLanguage());
	}

}
