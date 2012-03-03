package com.pewpewarrows.electricsheep.net;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

public class OAuth {

	private OAuthConsumer mConsumer;
	private OAuthProvider mProvider;

	private String mCallbackUrl;

	/**
	 * Generates a usable OAuth helper object. setupProvider() must be called in
	 * order to fully use the helper methods.
	 * 
	 * @param consumerKey
	 * @param consumerSecret
	 * @param callbackUrl
	 */
	public OAuth(String consumerKey, String consumerSecret, String callbackUrl) {
		mConsumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
		mCallbackUrl = (callbackUrl == null ? oauth.signpost.OAuth.OUT_OF_BAND
				: callbackUrl);
	}

	/**
	 * Additional required setup for the OAuth provider to not have a giant
	 * method signature on the OAuthHelper constructor.
	 * 
	 * TODO: Better way to handle not having a giant constructor?
	 * 
	 * @param requestUrl
	 * @param accessUrl
	 * @param authUrl
	 * @param scope
	 * @param encoding
	 * @throws UnsupportedEncodingException
	 */
	public void setupProvider(String requestUrl, String accessUrl,
			String authUrl, String scope, String encoding)
			throws UnsupportedEncodingException {
		mProvider = new CommonsHttpOAuthProvider(requestUrl + "?scope="
				+ URLEncoder.encode(scope, encoding), accessUrl, authUrl);
		mProvider.setOAuth10a(true);
	}

	/**
	 * Generates the full authentication URL from the provider and consumer.
	 * 
	 * @return
	 * @throws OAuthMessageSignerException
	 * @throws OAuthNotAuthorizedException
	 * @throws OAuthExpectationFailedException
	 * @throws OAuthCommunicationException
	 */
	public String getRequestToken() throws OAuthMessageSignerException,
			OAuthNotAuthorizedException, OAuthExpectationFailedException,
			OAuthCommunicationException {
		String authUrl = mProvider
				.retrieveRequestToken(mConsumer, mCallbackUrl);
		return authUrl;
	}

	/**
	 * Returns an array of the extracted token and secret from the OAuth
	 * consumer.
	 * 
	 * @param verifier
	 * @return
	 * @throws OAuthMessageSignerException
	 * @throws OAuthNotAuthorizedException
	 * @throws OAuthExpectationFailedException
	 * @throws OAuthCommunicationException
	 */
	public String[] getAccessToken(String verifier)
			throws OAuthMessageSignerException, OAuthNotAuthorizedException,
			OAuthExpectationFailedException, OAuthCommunicationException {
		mProvider.retrieveAccessToken(mConsumer, verifier);

		return new String[] { mConsumer.getToken(), mConsumer.getTokenSecret() };
	}

	/**
	 * Obtains an OAuthConsumer that uses existing authenticated secret keys and
	 * tokens, which can then be used to sign any HttpRequest.
	 * 
	 * @param consumerKey
	 * @param consumerSecret
	 * @param oAuthToken
	 * @param oAuthSecret
	 * @return
	 */
	public static OAuthConsumer createConsumer(String consumerKey,
			String consumerSecret, String oAuthToken, String oAuthSecret) {
		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(consumerKey,
				consumerSecret);
		consumer.setTokenWithSecret(oAuthToken, oAuthSecret);

		return consumer;
		// Usage: consumer.sign(request)
	}

}
