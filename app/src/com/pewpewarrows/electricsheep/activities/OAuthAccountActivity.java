package com.pewpewarrows.electricsheep.activities;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;

/**
 * TODO: This desperately needs a better name!
 */
public abstract class OAuthAccountActivity extends AccountAuthenticatorActivity {

	private final int OAUTH_ACTIVITY_CODE = 1;

	private AccountManager mAccountManager;

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

		mAccountManager = AccountManager.get(this);

		Intent intent = new Intent(this, OAuthActivity.class);

		intent.putExtra(OAuthActivity.PARAM_CONSUMER_KEY, mConsumerKey);
		intent.putExtra(OAuthActivity.PARAM_CONSUMER_SECRET, mConsumerSecret);
		intent.putExtra(OAuthActivity.PARAM_CALLBACK_URL, mCallbackUrl);
		intent.putExtra(OAuthActivity.PARAM_REQUEST_URL, mRequestUrl);
		intent.putExtra(OAuthActivity.PARAM_ACCESS_URL, mAccessUrl);
		intent.putExtra(OAuthActivity.PARAM_AUTHORIZE_URL, mAuthorizeUrl);
		intent.putExtra(OAuthActivity.PARAM_SCOPE, mScope);
		intent.putExtra(OAuthActivity.PARAM_ENCODING, mEncoding);
		intent.putExtra(OAuthActivity.PARAM_CALLBACK_SCHEME, mCallbackScheme);

		startActivityForResult(intent, OAUTH_ACTIVITY_CODE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case OAUTH_ACTIVITY_CODE:
			setupOAuthAccount(resultCode, data);
			break;
		default:
			break;
		}
	}

	private void setupOAuthAccount(int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			// TODO: yell and complain
		}

		Bundle extras = data.getExtras();

		String oAuthToken = extras.getString(oauth.signpost.OAuth.OAUTH_TOKEN);
		String oAuthSecret = extras
				.getString(oauth.signpost.OAuth.OAUTH_TOKEN_SECRET);
		String username = getUsername();

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

	protected String getUsername() {
		return "";
	}

	public String getOAuthTokenType() {
		return mOAuthTokenType;
	}

	public String getOAuthSecretType() {
		return mOAuthSecretType;
	}

}
