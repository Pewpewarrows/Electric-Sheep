package com.pewpewarrows.electricsheep.shim;

import android.os.Build;

/**
 * Singleton to progressively enhance an application with features that are only
 * available in newer versions of Android, while still allowing a compilation
 * minSdkVersion to be old.
 * 
 * Note that this is not a true singleton implementation, but merely uses the
 * same type of pattern to prevent init() from shim'ing compatibility multiple
 * times. The static inner class is also necessary to prevent the various other
 * non-empty shims from being loaded into memory at runtime, which would cause
 * an error due to them importing non-existant classes in specific SDK versions.
 * 
 * Assumes a minSdkVersion of at least 7.
 */
public abstract class CompatShim {
	static private CompatShim instance = null;

	/**
	 * Call this method once as early as possible in the application's setup
	 * phase. Ideally in the onCreate() method of your project's main
	 * Application object.
	 * 
	 * @param debug
	 *            Whether or not the application is in a "development" mode.
	 *            This mode is typically much slower and uses more memory, but
	 *            offers log and profiling data. Should not be true in a live,
	 *            production release.
	 */
	static public void init(boolean debug) {
		if (instance != null) {
			return;
		}

		/*
		 * Each shim extends the previous version's shim. This ensures that all
		 * compatibility fixes are applied down the chain.
		 * 
		 * TODO: Make these extendable and customizable by user's application.
		 */
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			instance = new IceCreamSandwichShim(debug);
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			instance = new HoneycombShim(debug);
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			instance = new GingerbreadShim(debug);
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			instance = new FroyoShim(debug);
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1) {
			instance = new EclairMr1Shim(debug);
		} else {
			instance = new EmptyShim();
		}
	}

	static class EmptyShim extends CompatShim {
		// intentionally left empty
	}
}
