package com.pewpewarrows.electricsheep.net;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * TODO: This desperately needs a better name!
 */
public abstract class OAuthAuthenticator extends AbstractAccountAuthenticator {

	protected Class mAccountActivityKlass;

	private final Context mContext;

	public OAuthAuthenticator(Context context) {
		super(context);

		mContext = context;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Bundle addAccount(AccountAuthenticatorResponse response,
			String accountType, String authTokenType,
			String[] requiredFeatures, Bundle options)
			throws NetworkErrorException {
		Intent intent = new Intent(mContext, mAccountActivityKlass);
//		intent.putExtra(AuthenticatorActivity.PARAM_AUTHTOKEN_TYPE,
//	            authTokenType);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
				response);
		
		Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);

		return bundle;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse response,
			Account account, Bundle options) throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Bundle editProperties(AccountAuthenticatorResponse response,
			String accountType) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle options)
			throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAuthTokenLabel(String authTokenType) {
		try {
			Method m = mAccountActivityKlass.getMethod("getOAuthTokenType", new Class[]{});
			String oAuthTokenType = (String) m.invoke(null);
			
			m = mAccountActivityKlass.getMethod("getOAuthSecretType", new Class[]{});
			String oAuthSecretType = (String) m.invoke(null);
			
			if (oAuthTokenType == authTokenType) {
				return "OAuth Token";
			} else if (oAuthSecretType == authTokenType) {
				return "OAuth Token Secret";
			}
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse response,
			Account account, String[] features) throws NetworkErrorException {
		Bundle result = new Bundle();
		result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle options)
			throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

}
