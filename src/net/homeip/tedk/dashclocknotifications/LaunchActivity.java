package net.homeip.tedk.dashclocknotifications;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class LaunchActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String data = getIntent().getData().getHost();
		Log.d("LauchActivity", data);
		finish();
	}
	
}
