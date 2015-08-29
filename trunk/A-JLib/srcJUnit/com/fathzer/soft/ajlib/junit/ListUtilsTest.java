package com.fathzer.soft.ajlib.junit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fathzer.soft.ajlib.utilities.ListUtils;
import com.fathzer.soft.ajlib.utilities.StringUtils;

/** Tests for StringUtils class.
 * @see StringUtils
 */
public class ListUtilsTest {
	private static Collection<Integer> list;
	
	@BeforeClass
	public static void init() {
		list = new ArrayList<Integer>();
		for (int i = 0; i < 10; i++) {
			list.add(i);
		}
		list = Collections.unmodifiableCollection(list);
	}
	
	@Test
	public void testOne() {
		List<Integer> tested = new ArrayList<Integer>(list);
		ListUtils.move(tested, 4, -2);
		assertIs(new int[]{0,1,4,2,3,5,6,7,8,9},tested);
		ListUtils.move(tested, 2, 2);
		assertIs(new int[]{0,1,2,3,4,5,6,7,8,9},tested);
	}
	
	@Test
	public void testMany() {
		List<Integer> tested = new ArrayList<Integer>(list);
		ListUtils.move(tested, new int[]{4,8,9}, -3);
		assertIs(new int[]{0,4,1,2,3,8,9,5,6,7},tested);
		ListUtils.move(tested, new int[]{1,5,6}, 3);
		assertIs(new int[]{0,1,2,3,4,5,6,7,8,9},tested);
	}
	
	private void assertIs(int[] expected, List<Integer> actual) {
		if (expected.length!=actual.size()) {
			err(expected, actual, "(different lengths");
		}
		for (int i = 0; i < expected.length; i++) {
			if (expected[i] != actual.get(i)) {
				String message = "(expected["+i+"]="+expected[i]+" is not actual["+i+"]="+actual.get(i)+")";
				err(expected, actual, message);
			}
		}
	}
	
	private void err(int[] expected, List<Integer> actual, String message) {
		throw new AssertionError("Expected <"+Arrays.toString(expected)+"> but was <"+actual+"> "+message);
	}
}
