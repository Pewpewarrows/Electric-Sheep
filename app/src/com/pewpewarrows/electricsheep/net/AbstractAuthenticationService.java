package com.pewpewarrows.electricsheep.net;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.pewpewarrows.electricsheep.log.Log;

import android.accounts.AbstractAccountAuthenticator;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

/**
 * This whole mess is to allow developers to simply extend this abstract service
 * and assign mAuthenticatorKlass to their real Authenticator class. The binding
 * and everything else is handled for them.
 */
abstract public class AbstractAuthenticationService extends Service {
	
	private static final String TAG = AbstractAuthenticationService.class.getSimpleName();

	private AbstractAccountAuthenticator mAuthenticator;
	@SuppressWarnings("rawtypes")
	protected Class mAuthenticatorKlass;

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate() {
		Log.v(TAG, "AbstractAuthenticationService onCreate()");
		
		try {
			/*
			 * All you need to know: makes a new instance of whatever class is
			 * in mAuthenticatorKlass, and assigns it to mAuthenticator
			 */
			Constructor<AbstractAccountAuthenticator> c = mAuthenticatorKlass
					.getConstructor(new Class[] { Context.class });
			// TODO: Should we be using getApplicationContext() instead of 'this'?
			mAuthenticator = (AbstractAccountAuthenticator) c
					.newInstance(new Object[] { this });
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDestroy() {
		Log.v(TAG, "AbstractAuthenticationService onDestroy()");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IBinder onBind(Intent intent) {
		Log.v(TAG, "AbstractAuthenticationService onBind()");
		return mAuthenticator.getIBinder();
	}

}
