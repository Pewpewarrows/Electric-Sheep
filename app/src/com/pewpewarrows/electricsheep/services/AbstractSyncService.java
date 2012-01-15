package com.pewpewarrows.electricsheep.services;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

/**
 * This whole mess is to allow developers to simply extend this abstract service
 * and assign mSyncAdapterKlass to their real SyncAdapter class. The binding and
 * everything else is handled for them.
 */
public abstract class AbstractSyncService extends Service {

	private static final Object sSyncAdapterLock = new Object();
	private static AbstractThreadedSyncAdapter sSyncAdapter = null;
	protected Class mSyncAdapterKlass;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate() {
		// TODO: super.onCreate(); ?

		synchronized (sSyncAdapterLock) {
			if (sSyncAdapter == null) {
				try {
					/*
					 * All you need to know: makes a new instance of whatever
					 * class is in mSyncAdapterKlass, and assigns it to
					 * sSyncAdapter
					 */
					Constructor<AbstractThreadedSyncAdapter> c = mSyncAdapterKlass
							.getConstructor(new Class[] { Context.class,
									Boolean.TYPE });
					sSyncAdapter = (AbstractThreadedSyncAdapter) c
							.newInstance(new Object[] {
									getApplicationContext(), true });
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IBinder onBind(Intent intent) {
		// TODO: super.onBind(intent); ?

		return sSyncAdapter.getSyncAdapterBinder();
	}

}
