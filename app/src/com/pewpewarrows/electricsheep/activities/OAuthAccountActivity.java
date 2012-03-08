package com.pewpewarrows.electricsheep.activities;

import java.io.UnsupportedEncodingException;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import com.pewpewarrows.electricsheep.log.Log;
import com.pewpewarrows.electricsheep.net.OAuth;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

/**
 * TODO: This desperately needs a better name!
 */
public abstract class OAuthAccountActivity extends AccountAuthenticatorActivity {

	private static final String TAG = OAuthAccountActivity.class
			.getSimpleName();

	public static final String PARAM_AUTHTOKEN_TYPE = "authTokenType";
	public static final String PARAM_ACCOUNT_TYPE = "accountType";
	public static final String PARAM_OAUTH_TOKEN_TYPE = "oAuthTokenType";
	public static final String PARAM_OAUTH_SECRET_TYPE = "oAuthSecretType";

	private AccountManager mAccountManager;
	private OAuth mOAuth;
	private String mAuthTokenType;

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

	protected OAuthRequestTask mOAuthRequestTask;
	protected OAuthTokenTask mOAuthTokenTask;
	protected OAuthUsernameTask mOAuthUsernameTask;
	protected String mVerifier;
	protected String mOAuthToken;
	protected String mOAuthSecret;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "OAuthAccountActivity onCreate()");
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

		Intent intent = getIntent();
		mAuthTokenType = intent.getStringExtra(PARAM_AUTHTOKEN_TYPE);
		mAccountType = intent.getStringExtra(PARAM_ACCOUNT_TYPE);
		mOAuthTokenType = intent.getStringExtra(PARAM_OAUTH_TOKEN_TYPE);
		mOAuthSecretType = intent.getStringExtra(PARAM_OAUTH_SECRET_TYPE);

		mOAuth = new OAuth(mConsumerKey, mConsumerSecret, mCallbackUrl);

		try {
			mOAuth.setupProvider(mRequestUrl, mAccessUrl, mAuthorizeUrl,
					mScope, mEncoding);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mOAuthRequestTask = new OAuthRequestTask();
		mOAuthRequestTask.execute();
	}

	/**
	 * Should be called after the OAuth authentication request returns, since
	 * this activity is tied to the callback URL.
	 */
	@Override
	public void onNewIntent(Intent intent) {
		Log.v(TAG, "OAuthAccountActivity onNewIntent()");
		super.onNewIntent(intent);

		Uri uri = intent.getData();

		if (uri != null && uri.getScheme().equals(mCallbackScheme)) {
			getAccessToken(uri);
		}
	}

	private void getRequestToken(String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url))
				.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
						| Intent.FLAG_ACTIVITY_NO_HISTORY
						| Intent.FLAG_FROM_BACKGROUND);
		this.startActivity(intent);
	}

	private void getAccessToken(Uri uri) {
		mVerifier = uri.getQueryParameter(oauth.signpost.OAuth.OAUTH_VERIFIER);

		mOAuthTokenTask = new OAuthTokenTask();
		mOAuthTokenTask.execute();
	}

	private void setupOAuthAccount(String username) {
		Account account = new Account(username, mAccountType);

		// TODO: should check the return code here for errors...
		mAccountManager.addAccountExplicitly(account, null, null);
		// ContentResolver.setSyncAutomatically(account,
		// ContactsContract.AUTHORITY, true);

		mAccountManager.setAuthToken(account, mOAuthTokenType, mOAuthToken);
		mAccountManager.setAuthToken(account, mOAuthSecretType, mOAuthSecret);

		Intent intent = new Intent();

		intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
		intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, mAccountType);

		if (mAuthTokenType != null) {
			if (mAuthTokenType.equals(mOAuthTokenType)) {
				intent.putExtra(AccountManager.KEY_AUTHTOKEN, mOAuthToken);
			} else if (mAuthTokenType.equals(mOAuthSecretType)) {
				intent.putExtra(AccountManager.KEY_AUTHTOKEN, mOAuthSecret);
			}
		}

		setAccountAuthenticatorResult(intent.getExtras());
		setResult(RESULT_OK, intent);
		finish();
	}

	protected abstract String getUsername(String oAuthToken, String oAuthSecret);

	public String getAccountType() {
		return mAccountType;
	}

	public String getOAuthTokenType() {
		return mOAuthTokenType;
	}

	public String getOAuthSecretType() {
		return mOAuthSecretType;
	}

	private void onOAuthRequestResult(String url) {
		mOAuthRequestTask = null;
		getRequestToken(url);
	}

	private void onOAuthTokenResult(String[] tokens) {
		mOAuthToken = tokens[0];
		mOAuthSecret = tokens[1];
		mOAuthUsernameTask = new OAuthUsernameTask();
		mOAuthUsernameTask.execute();

		mOAuthTokenTask = null;
	}

	private void onOAuthUsernameResult(String username) {
		setupOAuthAccount(username);
		mOAuthTokenTask = null;
	}

	private void onOAuthFailed() {
		mOAuthRequestTask = null;
		mOAuthTokenTask = null;
		mOAuthUsernameTask = null;
	}

	private class OAuthRequestTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			String url = null;
			
			try {
				url = mOAuth.getRequestToken();
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
			
			return url;
		}

		@Override
		protected void onPostExecute(String url) {
			if (url != null) {
				onOAuthRequestResult(url);
			} else {
				onOAuthFailed();
			}
		}

		@Override
		protected void onCancelled() {
			onOAuthFailed();
		}

	}

	private class OAuthTokenTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			String[] tokens = null;

			try {
				tokens = mOAuth.getAccessToken(mVerifier);
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

			return tokens;
		}

		@Override
		protected void onPostExecute(String[] tokens) {
			if (tokens != null) {
				onOAuthTokenResult(tokens);
			} else {
				onOAuthFailed();
			}
		}

		@Override
		protected void onCancelled() {
			onOAuthFailed();
		}

	}

	private class OAuthUsernameTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			return getUsername(mOAuthToken, mOAuthSecret);
		}

		@Override
		protected void onPostExecute(String username) {
			if (username != null) {
				onOAuthUsernameResult(username);
			} else {
				onOAuthFailed();
			}
		}

		@Override
		protected void onCancelled() {
			onOAuthFailed();
		}

	}

}
