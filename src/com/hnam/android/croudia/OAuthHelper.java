package com.hnam.android.croudia;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;

public class OAuthHelper {
	// API Key
	private String mConsumerKey;
	private String mSecretKey;
	
	// Oauth
	private OAuthConsumer mConsumer;
	private OAuthProvider mProvider;
	
	private String mCallbackUrl;
	public static final String CALLBACK_URL = "my-activity://mywebsite.com/";
	
	public OAuthHelper(String consumerKey, String consumerSecret, String callbackUrl) {
		mConsumerKey = consumerKey;
		mSecretKey = consumerSecret;
		
		mConsumer = new CommonsHttpOAuthConsumer(mConsumerKey, mSecretKey);
		mProvider = new CommonsHttpOAuthProvider("https://api.croudia.com/oauth/authorize?response_type=code&client_id=aa0e1490f6fae629b25a9c20a4bc0&state=77f094743bdb55c0f97",
				"https://api.croudia.com/oauth/token",
			    "https://api.croudia.com/oauth/authorize");
		
		mProvider.setOAuth10a(true);
		mCallbackUrl = (callbackUrl == null ? OAuth.OUT_OF_BAND : callbackUrl);
	}
}
