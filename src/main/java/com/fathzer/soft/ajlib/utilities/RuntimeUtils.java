package com.fathzer.soft.ajlib.utilities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public abstract class RuntimeUtils {
	private RuntimeUtils() {
		// To prevent class being instantiated
	}
	
	private static class UnexpectedException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public UnexpectedException(Throwable cause) {
			super("Unexpected exception", cause);
		}
		
	}
	
	public static int getJavaMajorVersion() {
		try {
			// Try with java 9 API
			final Method m = Runtime.class.getMethod("version");
			final Object version = m.invoke(null);
			return ((List<Integer>) version.getClass().getMethod("version").invoke(version)).get(0);
		} catch (NoSuchMethodException e) {		
			// Try parsing System.getProperty
			String version = System.getProperty("java.version");
			if (version.startsWith("1.")) {
				version = version.substring(2, 3);
			} else {
				final int dot = version.indexOf(".");
				if (dot != -1) {
					version = version.substring(0, dot);
				}
			}
			return Integer.parseInt(version);
		} catch (IllegalAccessException e) {
			throw new UnexpectedException(e);
		} catch (InvocationTargetException e) {
			throw new UnexpectedException(e);
		}
	}
}
