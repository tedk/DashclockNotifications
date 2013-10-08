package net.homeip.tedk.dashclocknotifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenReceiver extends BroadcastReceiver {

	public static volatile boolean locked = true;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
			locked = true;
		}
		if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
			if (locked) {
				locked = false;
				DashclockService.clearNotifications();
			}
		}
	}

}