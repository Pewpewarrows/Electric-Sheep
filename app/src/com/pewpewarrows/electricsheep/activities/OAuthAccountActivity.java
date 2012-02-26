package com.pewpewarrows.electricsheep.activities;

import java.io.UnsupportedEncodingException;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import com.pewpewarrows.electricsheep.net.OAuth;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * TODO: This desperately needs a better name!
 */
public abstract class OAuthAccountActivity extends AccountAuthenticatorActivity {

	private AccountManager mAccountManager;
	private OAuth mOAuth;

	protected String mConsumerKey;
	protected String mConsumerSecret;
	protected String mCallbackUrl;
	protected String mRequestUrl;
	protected String mAccessUrl;
	protected String mAuthorizeUrl;
	protected String mScope;
	protected String mEncoding;
	protected String mCallbackScheme;

	protected String mAccountType;
	protected String mOAuthTokenType;
	protected String mOAuthSecretType;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*
		 * Note for future self to save frustration:
		 * AccountAuthenticatorActivities cannot call startActivityForResult for
		 * some weird reason. Well, the call technically works (the activity
		 * starts), but this class is immediately given a response of
		 * RESULT_CANCELED, even while the new activity is all running fine and
		 * dandy. But that new activity cannot ever inform this one of its
		 * results, because this one already immediately received a
		 * RESULT_CANCELED from some phantom activity or something.
		 * 
		 * The normal startActivity call seems to work fine.
		 */

		mAccountManager = AccountManager.get(this);

		mOAuth = new OAuth(mConsumerKey, mConsumerSecret, mCallbackUrl);

		try {
			mOAuth.setupProvider(mRequestUrl, mAccessUrl, mAuthorizeUrl,
					mScope, mEncoding);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		getRequestToken();
	}

	/**
	 * Should be called after the OAuth authentication request returns, since
	 * this activity is tied to the callback URL.
	 */
	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		Uri uri = intent.getData();

		if (uri != null && uri.getScheme().equals(mCallbackScheme)) {
			getAccessToken(uri);
		}
	}

	private void getRequestToken() {
		try {
			String url = mOAuth.getRequestToken();
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url))
					.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
							| Intent.FLAG_ACTIVITY_NO_HISTORY
							| Intent.FLAG_FROM_BACKGROUND);
			this.startActivity(intent);
		} catch (OAuthMessageSignerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthNotAuthorizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getAccessToken(Uri uri) {
		String verifier = uri
				.getQueryParameter(oauth.signpost.OAuth.OAUTH_VERIFIER);

		try {
			String[] tokens = mOAuth.getAccessToken(verifier);

			setupOAuthAccount(tokens[0], tokens[1]);
		} catch (OAuthMessageSignerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthNotAuthorizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setupOAuthAccount(String oAuthToken, String oAuthSecret) {
		String username = getUsername(oAuthToken, oAuthSecret);

		Account account = new Account(username, mAccountType);

		mAccountManager.addAccountExplicitly(account, null, null);
		// ContentResolver.setSyncAutomatically(account,
		// ContactsContract.AUTHORITY, true);

		// TODO: Does this work?
		mAccountManager.setAuthToken(account, mOAuthTokenType, oAuthToken);
		mAccountManager.setAuthToken(account, mOAuthSecretType, oAuthSecret);

		Intent intent = new Intent();

		intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
		intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, mAccountType);
		// intent.putExtra(AccountManager.KEY_AUTHTOKEN, oAuthToken);

		setAccountAuthenticatorResult(intent.getExtras());
		setResult(RESULT_OK, intent);
		finish();
	}

	protected abstract String getUsername(String oAuthToken, String oAuthSecret);

	public String getOAuthTokenType() {
		return mOAuthTokenType;
	}

	public String getOAuthSecretType() {
		return mOAuthSecretType;
	}

}
