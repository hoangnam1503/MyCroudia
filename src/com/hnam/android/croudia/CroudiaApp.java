package com.hnam.android.croudia;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;

public class CroudiaApp {
	private String mConsumerKey;
	private String mSecretKey;
	private CommonsHttpOAuthConsumer mHttpOauthConsumer;
	private CommonsHttpOAuthProvider mHttpOauthProvider;
	public static final String CALLBACK_URL = "MyCroudia://callback";
	private ProgressDialog mProgressDlg;
	private Context mContext;
	private static final String TAG = "CroudiaApp";
	private CrDialogListener mListener;
	
	public CroudiaApp(Context context, String consumerKey, String secretKey) {
		this.mContext = context;
		mProgressDlg = new ProgressDialog(context);
		mProgressDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		mConsumerKey = consumerKey;
		mSecretKey = secretKey;
		
		mHttpOauthConsumer = new CommonsHttpOAuthConsumer(mConsumerKey, mSecretKey);
		mHttpOauthProvider = new CommonsHttpOAuthProvider("https://api.croudia.com/oauth/authorize?response_type=code&client_id=eeb2e6a1bdd30b405f301633bab86c1fbb2946cb393c48ee2a8bb1bcbfbf985d&state=77f094743bdb55c0f97",
				"https://api.croudia.com/oauth/token",
			    "https://api.croudia.com/oauth/authorize");
	}
	
	public void authorize() {
		mProgressDlg.setMessage("Initializing...");
		mProgressDlg.show();
		
		new Thread() {
			public void run() {
				String authUrl = "";
				int what = 1;
				
				try {
					authUrl = "https://api.croudia.com/oauth/authorize?response_type=code&client_id=eeb2e6a1bdd30b405f301633bab86c1fbb2946cb393c48ee2a8bb1bcbfbf985d&state=77f094743bdb55c0f97";
					what = 0;
					Log.e(TAG, "Request token url " + authUrl);
				} catch (Exception e) {
					Log.e(TAG, "Failed to get request token");
					e.printStackTrace();
				}
				mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0, authUrl));
			}
		}.start();
	}

	public void processToken(String callbackUrl)  {
		mProgressDlg.setMessage("Finalizing ...");
		mProgressDlg.show();
		
		final String verifier = getVerifier(callbackUrl);

		new Thread() {
			@Override
			public void run() {
				int what = 1;
				
				try {
					mHttpOauthProvider.retrieveAccessToken(mHttpOauthConsumer, verifier);
			        
			        what = 0;
				} catch (Exception e){
					Log.d(TAG, "Error getting access token");
					
					e.printStackTrace();
				}
				
				mHandler.sendMessage(mHandler.obtainMessage(what, 2, 0));
			}
		}.start();
	}

	private String getVerifier(String callbackUrl) {
		String verifier	 = "";
		
		try {
			callbackUrl = callbackUrl.replace("twitterapp", "http");
			
			URL url 		= new URL(callbackUrl);
			String query 	= url.getQuery();
		
			String array[]	= query.split("&");

			for (String parameter : array) {
	             String v[] = parameter.split("=");
	             
	             if (URLDecoder.decode(v[0]).equals(oauth.signpost.OAuth.OAUTH_VERIFIER)) {
	            	 verifier = URLDecoder.decode(v[1]);
	            	 break;
	             }
	        }
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return verifier;
	}

	public void setListener(CrDialogListener listener) {
		mListener = listener;
	}
	
	private void showLoginDialog(String url) {
		final CrDialogListener listener = new CrDialogListener() {
			@Override
			public void onComplete(String value) {
				processToken(value);
			}
			
			@Override
			public void onError(String value) {
				mListener.onError("Failed opening authorization page");
			}
		};
		
		new CroudiaDialog(mContext, url, listener).show();
	}
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mProgressDlg.dismiss();
			
			if (msg.what == 1) {
				if (msg.arg1 == 1)
					mListener.onError("Error getting request token");
				else
					mListener.onError("Error getting access token");
			} else {
				if (msg.arg1 == 1)
					showLoginDialog((String) msg.obj);
				else
					mListener.onComplete("");
			}
		}
	};
	
	public interface CrDialogListener {
		public void onComplete(String value);		
		
		public void onError(String value);
	}
}
