package com.pewpewarrows.electricsheep.shim;

import android.os.StrictMode;

public class GingerbreadShim extends FroyoShim {
	public GingerbreadShim(boolean debug) {
		super(debug);
		
		// Log and kill the application for violating StrictMode during
		// development
		if (debug) {
			// .penaltyDialog() is also an option
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectAll().penaltyLog().penaltyDeath().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectAll().penaltyLog().penaltyDeath().build());
		}
	}
}
