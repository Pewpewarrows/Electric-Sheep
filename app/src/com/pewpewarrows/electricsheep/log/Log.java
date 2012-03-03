package com.pewpewarrows.electricsheep.log;

/**
 * Lightweight wrapper around the built-in Android logging capability. Handles
 * variable logging thresholds at runtime, and dynamic placement of messages for
 * such functionality like error reporting.
 * 
 * TODO: Actually implement everything I described above.
 * TODO: Change method signatures to have Throwable tr before String msg, and
 * 		provide Object...args after msg to String.format() with.
 */
public class Log {
	
	private static boolean DEBUG = false;
	private static boolean debugSet = false;
	
	public static void setDebug(boolean debug) {
		// Prevent DEBUG from being toggled more than once.
		if (debugSet) {
			return;
		}
		
		debugSet = true;
		DEBUG = debug;
	}

	public static int v(String tag, String msg) {
		if (!DEBUG || !android.util.Log.isLoggable(tag, android.util.Log.VERBOSE)) {
			return 0;
		}
		return android.util.Log.v(tag, msg);
	}

	public static int v(String tag, String msg, Throwable tr) {
		if (!DEBUG || !android.util.Log.isLoggable(tag, android.util.Log.VERBOSE)) {
			return 0;
		}
		return android.util.Log.v(tag, msg, tr);
	}

	public static int d(String tag, String msg) {
		if (!DEBUG || !android.util.Log.isLoggable(tag, android.util.Log.DEBUG)) {
			return 0;
		}
		return android.util.Log.d(tag, msg);
	}

	public static int d(String tag, String msg, Throwable tr) {
		if (!DEBUG || !android.util.Log.isLoggable(tag, android.util.Log.DEBUG)) {
			return 0;
		}
		return android.util.Log.d(tag, msg, tr);
	}

	public static int i(String tag, String msg) {
		if (!android.util.Log.isLoggable(tag, android.util.Log.INFO)) {
			return 0;
		}
		return android.util.Log.i(tag, msg);
	}

	public static int i(String tag, String msg, Throwable tr) {
		if (!android.util.Log.isLoggable(tag, android.util.Log.INFO)) {
			return 0;
		}
		return android.util.Log.i(tag, msg, tr);
	}

	public static int w(String tag, String msg) {
		if (!android.util.Log.isLoggable(tag, android.util.Log.WARN)) {
			return 0;
		}
		return android.util.Log.w(tag, msg);
	}

	public static int w(String tag, Throwable tr) {
		if (!android.util.Log.isLoggable(tag, android.util.Log.WARN)) {
			return 0;
		}
		return android.util.Log.w(tag, tr);
	}

	public static int w(String tag, String msg, Throwable tr) {
		if (!android.util.Log.isLoggable(tag, android.util.Log.WARN)) {
			return 0;
		}
		return android.util.Log.w(tag, msg, tr);
	}

	public static int e(String tag, String msg) {
		if (!android.util.Log.isLoggable(tag, android.util.Log.ERROR)) {
			return 0;
		}
		return android.util.Log.e(tag, msg);
	}

	public static int e(String tag, String msg, Throwable tr) {
		if (!android.util.Log.isLoggable(tag, android.util.Log.ERROR)) {
			return 0;
		}
		return android.util.Log.e(tag, msg, tr);
	}

	// TODO: how to handle wtf() compatibility?

}
