package com.pewpewarrows.electricsheep;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Application;
import android.content.SharedPreferences;

import com.pewpewarrows.electricsheep.net.HttpClientFactory;
import com.pewpewarrows.electricsheep.shim.CompatShim;

/**
 * Generic Application object for your Application to extend. Provides some nice
 * defaults and convenience functions.
 * 
 * Please note that the following methods should be called from your own
 * onCreate() method, and are not automatically called here due to required
 * parameters that you must provide them:
 * 
 * -setupApp -makeCompatible
 */
public abstract class ESApplication extends Application {

	protected HttpClient mHttpClient;
	protected SharedPreferences mSettings;

	@Override
	public void onLowMemory() {
		super.onLowMemory();

		shutdownHttpClient();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();

		shutdownHttpClient();
	}

	/**
	 * Initialize any member variables of ESApplication.
	 * 
	 * @param name
	 *            A string to be assigned as the name of this application's
	 *            general prefereces. The name of the overall application is a
	 *            good choice.
	 */
	protected void setupApp(String name, int timeout) {
		mHttpClient = HttpClientFactory.create(timeout);
		mSettings = getSharedPreferences(name, MODE_PRIVATE);
	}

	/**
	 * Kick-off the compatibility shim, progressively enhancing this application
	 * depending on what Android SDK levels are available in the currently
	 * running environment.
	 * 
	 * @param debug
	 *            Enable options like StrictMode during development.
	 */
	protected void makeCompatible(boolean debug) {
		CompatShim.init(debug);
	}

	/**
	 * If the application's shared HttpClient exists, safely destroy it.
	 */
	private void shutdownHttpClient() {
		if (mHttpClient != null) {
			HttpClientFactory.shutdown(mHttpClient);
		}
	}

	public HttpClient getHttpClient() {
		return mHttpClient;
	}

	public SharedPreferences getSettings() {
		return mSettings;
	}

}
