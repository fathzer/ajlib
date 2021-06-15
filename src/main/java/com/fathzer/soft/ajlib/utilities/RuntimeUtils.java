package com.fathzer.soft.ajlib.utilities;

public abstract class RuntimeUtils {
	private RuntimeUtils() {
		// To prevent class being instantiated
	}
	
	public static int getJavaMajorVersion() {
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
	}
}
