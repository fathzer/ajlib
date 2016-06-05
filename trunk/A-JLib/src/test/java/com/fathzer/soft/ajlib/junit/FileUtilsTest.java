package com.fathzer.soft.ajlib.junit;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import com.fathzer.soft.ajlib.utilities.FileUtils;
import com.fathzer.soft.ajlib.utilities.StringUtils;

/** Tests for StringUtils class.
 * @see StringUtils
 */
public class FileUtilsTest {

	@Test
	public void testExtension() {
		assertEquals(".xml", FileUtils.getExtension(new File("x.xml")));
		assertEquals(null, FileUtils.getExtension(new File("")));
		assertEquals(".", FileUtils.getExtension(new File(".")));
		assertEquals("x", FileUtils.getRootName(new File("x.xml")));
		assertEquals("", FileUtils.getRootName(new File("")));
		assertEquals("", FileUtils.getRootName(new File(".")));
	}
}
