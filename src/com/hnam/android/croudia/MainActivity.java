package com.hnam.android.croudia;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.hnam.android.croudia.CroudiaApp.CrDialogListener;

public class MainActivity extends Activity {
	private Button mTestBtn;
	public String authUrl;
	private CroudiaApp mCroudia;
	private String  mRedirectUrl = "";
	private static final String mCunsomerKey = "eeb2e6a1bdd30b405f301633bab86c1fbb2946cb393c48ee2a8bb1bcbfbf985d";
	private static final String mSecretkey = "d3cf1af429666aaa80e2522bf299047d5ebb5f3ae1a6dee55d3f9c979cd086f7";
	private static final String mState = "77f094743bdb55c0f97";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mTestBtn = (Button)findViewById(R.id.testBtn);
		mTestBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onLoginClick();
			}
		});
		mCroudia = new CroudiaApp(this, mCunsomerKey, mSecretkey, mState);		
		mCroudia.setListener(mTwLoginDialogListener);		
	} 

	public void onLoginClick() {
		mCroudia.authorize();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private final CrDialogListener mTwLoginDialogListener = new CrDialogListener() {
		@Override
		public void onComplete(String value) {
		}
		
		@Override
		public void onError(String value) {
			mRedirectUrl = value;
			Log.e("Croudia Debug", mRedirectUrl);
			Toast.makeText(MainActivity.this, "Twitter connection failed", Toast.LENGTH_LONG).show();
		}
	};
}
