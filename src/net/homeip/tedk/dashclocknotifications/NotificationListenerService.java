package net.homeip.tedk.dashclocknotifications;

import java.text.DateFormat;
import java.util.Date;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.view.accessibility.AccessibilityEvent;

public class NotificationListenerService extends AccessibilityService {

	private DateFormat dateFormat = null;
	private PackageManager pm = null;

	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();
		setUp();
	}

	public void setUp() {
		dateFormat = DateFormat.getTimeInstance(DateFormat.LONG);
		pm = getPackageManager();
		
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_USER_PRESENT);
		BroadcastReceiver br = new ScreenReceiver();
		registerReceiver(br, filter);
	}
	
	public void tearDown() {
		dateFormat = null;
		pm = null;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		tearDown();
		return super.onUnbind(intent);
	}

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		if(!ScreenReceiver.locked)
			return;
		if (event == null)
			return;
		Notification n = (Notification) event.getParcelableData();
		if (n == null)
			return;

		String text = n.tickerText == null || n.tickerText.toString().trim().length() == 0 ? null : n.tickerText.toString().trim();
		
		if (text == null)
			return; // ignore blank notifications (downloads, gps, keyboard, etc.)

		String time = dateFormat.format(new Date());
		String packageName = event.getPackageName().toString();
		String appName = null;
		try {
			appName = pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString();
		} catch (Exception e) {
			//Log.e("NotificationListenerService", "Could not load application name", e);
		}
		if (appName == null)
			appName = packageName;
		String num = Integer.toString(n.number);
//		Context c = getApplicationContext();
//		try {
//			c = createPackageContext(packageName, 0);
//		} catch (NameNotFoundException e) {
//			//Log.e("NotificationListenerService", "Could not load application context", e);
//		}
		int icon = n.icon;
//		ByteArrayOutputStream baos = null;
//		try {
//			baos = new ByteArrayOutputStream();
//			BitmapFactory.Options options = new BitmapFactory.Options();
//			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//			Bitmap iconBitmap = BitmapFactory.decodeResource(c.getResources(), n.icon, options);
//			Bitmap iconWithBackground = Bitmap.createBitmap(iconBitmap.getWidth(), iconBitmap.getHeight(), iconBitmap.getConfig());
//			Canvas background = new Canvas(iconWithBackground);
//			background.drawColor(Color.BLACK);
//			background.drawBitmap(iconBitmap, 0.f, 0.f, null);
//			iconWithBackground.compress(Bitmap.CompressFormat.PNG, 100, baos);
//			baos.flush();
//			icon = "data:image/png;base64," + Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP | Base64.URL_SAFE);
//		} catch (Exception e) {
//			//Log.e("NotificationListenerService", "Could not load icon", e);
//		} finally {
//			if (baos != null)
//				try {
//					baos.close();
//				} catch (Exception e) {
//				}
//		}
		PendingIntent intent = n.contentIntent;
		DashclockService.addNotification(new DashclockService.NotificationInfo(time, appName, text, num, icon, intent));
	}

	@Override
	public void onInterrupt() {
		// do nothing for now
	}

}
