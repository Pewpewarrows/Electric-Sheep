package com.pewpewarrows.electricsheep.concurrency;

/**
 * TODO: Utils in a class-name is code smell. Figure out a better place to put
 * all of this.
 */
public class ConcurrentUtils {

	/**
	 * Executes the network requests on a separate thread.
	 * 
	 * @param runnable
	 *            The runnable instance containing network mOperations to be
	 *            executed.
	 */
	public static Thread performOnBackgroundThread(final Runnable runnable) {
		final Thread t = new Thread() {
			@Override
			public void run() {
				try {
					runnable.run();
				} finally {

				}
			}
		};

		t.start();

		return t;
	}

}
