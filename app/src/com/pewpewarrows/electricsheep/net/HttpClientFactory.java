package com.pewpewarrows.electricsheep.net;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * 
 */
public class HttpClientFactory {
	/**
	 * Generate a thread-safe HttpClient that kills an unresponsive connection
	 * after a provided length of time.
	 * 
	 * TODO: After minSdkVersion is incremented from 7 to 8, replace the
	 * DefaultHttpClient with AndroidHttpClient and its associated methods. It
	 * handles thread safety, connection pooling, and other niceties for us.
	 * 
	 * @param timeout
	 *            After this number of milliseconds the HttpClient will kill its
	 *            operations, if it hasn't received anything yet.
	 * @return HttpClient
	 */
	public static HttpClient create(int timeout) {
		DefaultHttpClient client = new DefaultHttpClient();
		ClientConnectionManager mgr = client.getConnectionManager();
		HttpParams params = client.getParams();

		// Apply timeout to the various connection configurations
		HttpConnectionParams.setConnectionTimeout(params, timeout);
		HttpConnectionParams.setSoTimeout(params, timeout);
		ConnManagerParams.setTimeout(params, timeout);

		// Initiate thread safety
		return new DefaultHttpClient(new ThreadSafeClientConnManager(params,
				mgr.getSchemeRegistry()), params);
	}

	/**
	 * Destroy any active use of the provided HttpClient through its
	 * ConnectionManager.
	 */
	public static void shutdown(HttpClient client) {
		if (client.getConnectionManager() != null) {
			client.getConnectionManager().shutdown();
		}
	}
}
