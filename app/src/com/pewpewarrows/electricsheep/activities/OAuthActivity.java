package com.pewpewarrows.electricsheep.activities;

import java.io.UnsupportedEncodingException;

import com.pewpewarrows.electricsheep.net.OAuth;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;

public class OAuthActivity extends Activity {

	public static final String PARAM_CONSUMER_KEY = "consumerKey";
	public static final String PARAM_CONSUMER_SECRET = "consumerSecret";
	public static final String PARAM_CALLBACK_URL = "callbackUrl";
	public static final String PARAM_REQUEST_URL = "requestUrl";
	public static final String PARAM_ACCESS_URL = "accessUrl";
	public static final String PARAM_AUTHORIZE_URL = "authorizeUrl";
	public static final String PARAM_SCOPE = "scope";
	public static final String PARAM_ENCODING = "encoding";
	public static final String PARAM_CALLBACK_SCHEME = "callbackScheme";

	private OAuth mOAuth;

	private String mConsumerKey;
	private String mConsumerSecret;
	private String mCallbackUrl;
	private String mRequestUrl;
	private String mAccessUrl;
	private String mAuthorizeUrl;
	private String mScope;
	private String mEncoding;
	private String mCallbackScheme;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO: android:launchMode="singleTask" vs "singleTop"

		Bundle extras = getIntent().getExtras();
		
		if (extras == null) {
			// TODO: yell and complain
		}

		mConsumerKey = extras.getString(PARAM_CONSUMER_KEY);
		mConsumerSecret = extras.getString(PARAM_CONSUMER_SECRET);
		mCallbackUrl = extras.getString(PARAM_CALLBACK_URL);
		mRequestUrl = extras.getString(PARAM_REQUEST_URL);
		mAccessUrl = extras.getString(PARAM_ACCESS_URL);
		mAuthorizeUrl = extras.getString(PARAM_AUTHORIZE_URL);
		mScope = extras.getString(PARAM_SCOPE);
		mEncoding = extras.getString(PARAM_ENCODING);
		mCallbackScheme = extras.getString(PARAM_CALLBACK_SCHEME);

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

			Intent result = new Intent();
			
			result.putExtra(oauth.signpost.OAuth.OAUTH_TOKEN, tokens[0]);
			result.putExtra(oauth.signpost.OAuth.OAUTH_TOKEN_SECRET, tokens[1]);
			
			// TODO: handle if parent, set its results instead?
			setResult(RESULT_OK, result);
			finish();
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
}
